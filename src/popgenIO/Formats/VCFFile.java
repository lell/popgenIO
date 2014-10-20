/* popgenIO
 * Copyright (c) 2011, 2012, 2013, Yee Whye Teh, Charles Blundell, Lloyd T. Elliott, Derek Aguiar and Barbara Engelhardt
 */

package popgenIO.Formats;

import static libnp.util.Operation.removeExtention;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

import popgenIO.Core.BitDataSet;
import popgenIO.Core.DataSet;
import popgenIO.Core.Site;
import popgenIO.Formats.BeagleFile.ParseError;

public class VCFFile {
	
	public static String getVCFFile(String filename) throws FileNotFoundException {
		if (new File(removeExtention(filename) + ".vcf").exists()) {
			return removeExtention(filename) + ".vcf";
		} else if (new File(removeExtention(filename) + ".vcf.gz").exists()) {
			return removeExtention(filename) + ".vcf.gz";
		} else if (new File(filename + ".vcf").exists()) {
			return filename + ".vcf";
		} else if (new File(filename + ".vcf.gz").exists()) {
			return filename + ".vcf.gz";
		} else if (new File(filename).exists()) {
			return filename;
		} else {
			throw new FileNotFoundException(filename);
		}
	}
	
	/**
	 * We force the specification of a contig name because we want to enforce processing a single chromosome at a time.
	 * This is good practice in general for parallelizing runs, but also, it's more work to process multiple chromosomes
	 * at once. Does not support phased groups in VCF file. Assumes one phased group.
	 * @param filename
	 * @param contig
	 * @return
	 * @throws Exception
	 */
	public static DataSet read(String filename, String contig) throws Exception {
		if(!(new File(filename).exists()))
			filename = getVCFFile(filename);
		Scanner scan = tryOpen(filename);
		int minPosition = 0, maxPosition = Integer.MAX_VALUE, num_individuals = 0;
		// Assumes VCF is sorted (per specification)
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		int line_counter = 0;
		String line = null;
		int pos = -1;
		while ((line = reader.readLine()) != null) {
			if(!line.startsWith("#")) {
				if(line.trim().equals(""))
					continue;
				String[] parts = line.split("\t");
				num_individuals = parts.length-9;
				pos = Integer.parseInt(parts[1]);
				if(line_counter==0)
					minPosition = new Integer(pos);
				line_counter++;
				
			}
		}
		maxPosition = new Integer(pos);
		reader.close();
		
		Site[] sites = new Site[line_counter];
		int genotype_index = 0, site_cnt = 0;
		List<String> sequence_ids = new ArrayList<String>();
		String chromosome = null;
		DataSet<Boolean> gds = null;
		boolean[] isGenotype = new boolean[num_individuals];
		// indexed by [number of genotypes/diplotypes][which sequence (doesn't matter for genotypes)][number of sites]
		Boolean[][][] genotypes_and_diplotypes = new Boolean[num_individuals][2][line_counter];
		line_counter=0;
		
		while (scan.hasNext()) {
			String type = scan.next();
			if (type.startsWith("##")) {
				// there is valuable information here, we chose not to use it
				scan.nextLine();
				continue;			
			} else if (type.startsWith("#")) {
				// header, get individuals... the first column is always CHROM, so it's fine to skip
				Scanner line_scanner = new Scanner(scan.nextLine());
				int number_columns = 1;
				int number_sites = 0;
				while(line_scanner.hasNext()) {
					String column = line_scanner.next();
					number_columns++;
					if(number_columns==9) {
						// format column, figure out where the genotype is
						String[] parts = column.split(":");
						for (int i = 0; i < parts.length; i++) {
							parts[i].equals("GT");
							genotype_index = i;
						}
					} else if(number_columns>9) {
						sequence_ids.add(column);
					} 
				}
			} else {
				// variant line
				Scanner line_scanner = new Scanner(scan.nextLine());
				int number_columns = 1;
				if(chromosome==null) 
					chromosome = type;
				else if(!chromosome.equals(type)) {
					throw new ParseError("Found more than one contig in the VCF file. Please filter all contigs except one.");
				}
				
				if(gds == null) {
					gds = new BitDataSet(sites.length, 2 * sequence_ids.size());
				}

				// If we want to correctly parse the alleles, we need to change Site from char[] to String[]
				int varpos = line_scanner.nextInt();
				String varname = line_scanner.next();
				if(varname.equals("."))
					varname = "VAR_" + chromosome + "_" + varpos;
				sites[site_cnt] = new Site(site_cnt, normalize(varpos,(double)minPosition,(double)maxPosition), 
						varname, new char[]{'0','1'});
				number_columns+=2;
				gds.addSite(sites[site_cnt++].globalize());

				while(line_scanner.hasNext()) {
					String next_token = line_scanner.next();
					if(number_columns>8) {
						// inside specification of genotypes
						String genotype = next_token.split(":")[genotype_index];
						if(genotype.contains("|")) {
							// phased
						} else if(genotype.contains("/")) {
							// unphased
							isGenotype[number_columns-9]=true;
						} else {
							throw new ParseError("Malformed genotype " + genotype);
						}
						String[] alleles = genotype.split("\\||/");
						genotypes_and_diplotypes[number_columns-9][0][line_counter]=getGenotypeVal(alleles[0].charAt(0));
						genotypes_and_diplotypes[number_columns-9][1][line_counter]=getGenotypeVal(alleles[1].charAt(0));
					}
					number_columns++;
				}
				line_counter++;
			}
		}
		for (int i = 0; i <  sequence_ids.size(); i++) {
			if(isGenotype[i]) { 
				gds.addGenotype(sequence_ids.get(i), genotypes_and_diplotypes[i]);
				//gds.addDiplotype(sequence_ids.get(i), genotypes_and_diplotypes[i]); // for the phased diplotypes
				//gds.addHaplotype(sequence_ids.get(i) + "A", genotypes_and_diplotypes[i][0]); // diplotypes aren't really supported...
				//gds.addHaplotype(sequence_ids.get(i) + "B", genotypes_and_diplotypes[i][1]);
			} else {
				// Probably should be diplotype here, code largely supports haplotypes 
				// only for inference (trajectories, etc...) should probably change in future
				gds.addHaplotype(sequence_ids.get(i) + "A", genotypes_and_diplotypes[i][0]);
				gds.addHaplotype(sequence_ids.get(i) + "B", genotypes_and_diplotypes[i][1]);
			}
		}
		return gds;
	}
	
	private static Boolean getGenotypeVal(char allele) throws ParseError {
		if(allele == '0') 
			return false;
		else if(allele == '1') 
			return true;
		else if(allele == '.') 
			return null;
		else throw new ParseError("Did not understand allele " + allele + " in the VCF file.");
	}

	private static double normalize(double val, double low_bound, double up_bound) {
		return (val-low_bound)/(up_bound-low_bound);
	}

	private static Scanner tryOpen(String filename) throws Exception {
		Scanner scan;
		try {
			scan = new Scanner(new BufferedReader(new InputStreamReader(
					new GZIPInputStream(new FileInputStream(filename)))));

		} catch (IOException ioe) {
			// try again without gzip...
			scan = new Scanner(new BufferedReader(new FileReader(filename)));
		}
		
		
		return scan;
	}
}
