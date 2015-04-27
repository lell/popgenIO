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
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

import popgenIO.Core.ArrayDataSet;
import popgenIO.Core.BitDataSet;
import popgenIO.Core.DataSet;
import popgenIO.Core.GlobalSite;
import popgenIO.Core.IntDataSet;
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

	public static ArrayDataSet read(String filename, String contig) throws Exception {
		return read(filename,contig,-1,-1);
	}

	// TODO: fix for higher ploidys
	/**
	 * We force the specification of a contig name because we want to enforce processing a single chromosome at a time.
	 * This is good practice in general for parallelizing runs, but also, it's more work to process multiple chromosomes
	 * at once. Does not support phased groups in VCF file. Assumes one phased group.
	 * @param filename
	 * @param contig
	 * @return
	 * @throws Exception
	 */
	public static ArrayDataSet read(String filename, String contig, int random_number_genotypes, int random_number_haplotypes) throws Exception {
		if(!(new File(filename).exists()))
			filename = getVCFFile(filename);

		//TODO: add check if number of alleles at any site is greater than Byte.maxval
		
		boolean[] isGenotype = null;
		String[] parts=null, parts2 = null;
		int num_individuals=0;
		int minPosition = 0, maxPosition = Integer.MAX_VALUE;
		// Assumes VCF is sorted (per specification)
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		int line_counter = 0;
		int genotype_index = 0;
		List<String> sequence_ids = new ArrayList<String>();
		String line = reader.readLine();
		int pos = -1;
		do  {
			if(!line.startsWith("#")) {
				if(line.trim().equals(""))
					continue;

				// figure out if its a genotype (anything with a heterozygous entry is a genotype
				// variant line
				parts = line.split("\t");

				// If we want to correctly parse the alleles, we need to change Site from char[] to String[]
				for (int i = 9; i < parts.length; i++) {
					if(parts[i].contains("/"))
						isGenotype[i]=true;
				}
				if(line_counter==0) {
					pos = Integer.parseInt(parts[1]);
					minPosition = new Integer(pos);
					// format column, figure out where the genotype is
					parts2 = parts[8].split(":");
					for (int i = 0; i < parts2.length; i++) {
						parts[i].equals("GT");
						genotype_index = i;
					}
				}

				line_counter++;
			} else if (line.startsWith("##")) {
				continue;
			} else if (line.startsWith("#")) {
				// header, get individuals... the first column is always CHROM, so it's fine to skip
				parts = line.split("\t");
				num_individuals = parts.length-9;
				isGenotype = new boolean[num_individuals];

				for (int i = 9; i < parts.length; i++) {
					sequence_ids.add(parts[i]);					
				}
			} 
		} while ((line = reader.readLine()) != null);

		List<Integer> indices = new ArrayList<Integer>();
		for (int i = 0; i < num_individuals; i++) {
			indices.add(i);
		}
		if(random_number_genotypes!=-1 && random_number_haplotypes!=-1)  {
			Collections.shuffle(indices);
		}
		maxPosition = new Integer(pos);
		reader.close();

		Site[] sites = new Site[line_counter];
		int site_cnt = 0;
		String chromosome = null;
		if((2 * sequence_ids.size())>Short.MAX_VALUE) 
			throw new IllegalArgumentException("Technical limitation: Can only have a maximum of " + Short.MAX_VALUE + " individuals. Found " + 2 * sequence_ids.size());
		
		ArrayDataSet<byte[]> gds = new IntDataSet(sites.length, 2 * sequence_ids.size());

		for (int i = 0; i < isGenotype.length; i++) {
			if(isGenotype[i]) {
				gds.addGenotype(sequence_ids.get(i));
			} else {
				gds.addHaplotype(sequence_ids.get(i) + "A");
				gds.addHaplotype(sequence_ids.get(i) + "B");
			}
		}

		reader.close();
		reader = new BufferedReader(new FileReader(filename));
		line = null;

		// indexed by [number of genotypes/diplotypes][which sequence (doesn't matter for genotypes)][number of sites]
		//int[][][] genotypes_and_diplotypes = new int[num_individuals][2][line_counter];
		line_counter=0;

		while ((line=reader.readLine())!=null) {
			if (line.startsWith("#") || line.trim().equals("")) {
				// header information we ignore
				continue;			
			} else {
				// variant line
				parts = line.split("\t");
				if(chromosome==null) 
					chromosome = parts[0];
				else if(!chromosome.equals(parts[0])) {
					reader.close();
					throw new ParseError("Found more than one contig in the VCF file. Please filter all contigs except one.");
				}

				// If we want to correctly parse the alleles, we need to change Site from char[] to String[]
				int varpos = Integer.parseInt(parts[1]);
				String varname = parts[2];
				if(varname.equals("."))
					varname = "VAR_" + chromosome + "_" + varpos;
				sites[site_cnt] = new Site(site_cnt, normalize(varpos,(double)minPosition,(double)maxPosition), 
						varname, new int[]{0,1});
				GlobalSite currentSite = sites[site_cnt++].globalize();
				gds.addSite(currentSite);
				for (int i = 9; i < parts.length; i++) {
					// inside specification of genotypes
					String genotype = parts[i].split(":")[genotype_index];
					parts2 = genotype.split("\\||/");

					if(genotype.contains("|")) {
						// phased
						// Probably should be diplotype here, code largely supports haplotypes 
						// only for inference (trajectories, etc...) should probably change in future
						gds.set(gds.getSites().get(line_counter), gds.getHaplotype(sequence_ids.get(i-9) + "A"), getGenotypeVal(parts2[0].charAt(0)));
						gds.set(gds.getSites().get(line_counter), gds.getHaplotype(sequence_ids.get(i-9) + "B"), getGenotypeVal(parts2[1].charAt(0)));
					} else if(genotype.contains("/")) {
						// unphased
						gds.set(gds.getSites().get(line_counter), gds.getGenotype(sequence_ids.get(i-9)), 
								new byte[] {getGenotypeVal(parts2[0].charAt(0)),getGenotypeVal(parts2[1].charAt(0))});
					} else {
						reader.close();
						throw new ParseError("Malformed genotype " + genotype);
					}
				}
				line_counter++;
			}
		}

		/*if(random_number_genotypes>=0 && random_number_haplotypes>=0) {
			for (int i = 0; i < num_individuals; i++) {
				if(isGenotype[indices.get(i)]) { 
					if(random_number_genotypes>0) {
						gds.addGenotype(sequence_ids.get(indices.get(i)), genotypes_and_diplotypes[indices.get(i)]);
						random_number_genotypes--;
					}
				} else {
					// Probably should be diplotype here, code largely supports haplotypes 
					// only for inference (trajectories, etc...) should probably change in future
					if(random_number_haplotypes==0 && random_number_genotypes>0) {
						printHap(genotypes_and_diplotypes[indices.get(i)][0],System.err);
						printHap(genotypes_and_diplotypes[indices.get(i)][1],System.err);
						gds.addGenotype(sequence_ids.get(indices.get(i)), genotypes_and_diplotypes[indices.get(i)]);
						random_number_genotypes--;
					}
					if(random_number_haplotypes>0) {
						gds.addHaplotype(sequence_ids.get(indices.get(i)) + "A", genotypes_and_diplotypes[indices.get(i)][0]);
						random_number_haplotypes--;
					}
					if(random_number_haplotypes>0) {
						gds.addHaplotype(sequence_ids.get(indices.get(i)) + "B", genotypes_and_diplotypes[indices.get(i)][1]);
						random_number_haplotypes--;
					}
				}
			}
			if(random_number_genotypes>0) {
				System.err.println("WARNING: Didn't find enough genotypes in the data. Requested " + 
						(random_number_genotypes+gds.getGenotypes().size()) + 
						" but only found " + gds.getGenotypes().size()+".");
			}
			if(random_number_haplotypes>0) {
				System.err.println("WARNING: Didn't find enough haplotypes in the data. Requested " + 
						(random_number_haplotypes+gds.getHaplotypes().size()) + 
						" but only found " + gds.getHaplotypes().size()+".");
			}
		} */
		return gds;
	}

	/**
	 * @param booleans
	 * @param err
	 */
	private static void printHap(int[] hap, PrintStream err) {
		for (int i : hap) {
			err.print(i);
		}
		System.err.println();
	}

	private static byte getGenotypeVal(char allele) {
		if(allele == '.') 
			return -1;
		
		int a = Character.getNumericValue(allele);
		if(a>Byte.MAX_VALUE)
			throw new IllegalArgumentException("Expected a byte encoding for allele, found " + allele);
		else return (byte)Character.getNumericValue(allele);
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
