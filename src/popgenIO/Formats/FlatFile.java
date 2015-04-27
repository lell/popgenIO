/* popgenIO
 * Copyright (c) 2011, 2012, 2013, Yee Whye Teh, Charles Blundell and Lloyd T. Elliott
 */

package popgenIO.Formats;


import static libnp.util.Operation.arange;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

import static java.lang.Math.round;
import popgenIO.Core.ArrayDataSet;
import popgenIO.Core.DataSet;
import popgenIO.Core.BitDataSet;
import popgenIO.Core.Genotype;
import popgenIO.Core.Haplotype;
import popgenIO.Core.IntDataSet;
import popgenIO.Core.Site;
import static libnp.util.Float.compareFloats;
	    
public class FlatFile {

	// This should load ANY kind of .phase file (spaces, names, prefixes) or just a square matrix of 0/1/? as long as the names aren't like 010101 (can have #sites = 100)
	public static ArrayDataSet read(Scanner fr, Scanner fst, Scanner fsm) {
		List<String> lines = new ArrayList();
		int numsites = 0;
		
		// Strip the header + collect all the lines with haps on them
		while(fr.hasNext()) {
			String line = fr.nextLine();
			line = line.replaceAll("-1", "?");
			line = line.replaceAll("NaN", "?");
			line = line.replaceAll("nan", "?");
			
			// If we save a real matrix from matlab with '-ascii', we get 1.000 and 0.000 for 1 and 0
			line = line.replaceAll("1\\.[0]+", "1");
			line = line.replaceAll("0\\.[0]+", "0");
			line = line.replaceAll("2\\.[2]+", "2");
			line = line.replaceAll("\\s+", "");

			boolean valid_seq = true;
			for (int t = 0; t < line.length(); t++) {
				char c = line.charAt(t);
				if (! (c == '0' || c == '1' || c == '?' || c == '2')) {
					valid_seq = false;
					break;
				}
			}
			
			if (valid_seq) {
				if (line.length() != numsites) {
					lines.clear();
				}
				numsites = line.length();
				lines.add(line);
			}
		}
		
		assert lines.size()>0;
		assert numsites == lines.get(0).length();
		int numSequences = lines.size();
		ArrayDataSet data = new IntDataSet(numsites, numSequences);
		
		if (fst != null) {
			for (int t = 0; t < numsites; t++) {
				String[] cols = fst.nextLine().trim().split("\\s");
				double position = t;
				String name = null;
				int[] alleles = null;
				
				if (cols.length >= 1) {
					position = Double.parseDouble(cols[0]);
				}
				
				if (cols.length >= 2) {
					name = cols[1];
				} 

				if (cols.length >= 3) {
					assert cols[2].length() == 3;
					alleles = new int[] { Character.getNumericValue(cols[2].charAt(0)), Character.getNumericValue(cols[2].charAt(2)) };
				}
				data.addSite(new Site(t, position, name, alleles).globalize());
			}
			
		} else {
			for (int t = 0; t < numsites; t++) {
				data.addSite(new Site(t).globalize());
			}
		}
		
		List<String> names = new ArrayList();
		if (fsm != null) {
			for (int i = 0; i < numSequences; i++) {
				String[] cols = fsm.nextLine().trim().split("\\s");
				assert cols.length == 1;
				names.add(cols[0]);
			}
			fsm.close();
		}
		
		for (int i = 0; i < numSequences; i++) {
			Boolean[] haplotype = new Boolean[numsites];
			Boolean[][] genotype = new Boolean[2][numsites];
			boolean isGenotype = false;
			for (int t = 0; t < numsites; t++) {
				switch (lines.get(i).charAt(t)) {
				case '0':
					haplotype[t] = false;
					genotype[0][t] = false;
					genotype[1][t] = false;
					break;
				case '1':
					haplotype[t] = true;
					genotype[0][t] = true;
					genotype[1][t] = true;
					break;
				case '?':
					haplotype[t] = null;
					genotype[0][t] = null;
					genotype[1][t] = null;
					break;
				case '2':
					isGenotype = true;
					genotype[0][t] = true;
					genotype[1][t] = false;
					break;
				default:
					System.err.println("Unknown character in file: " + lines.get(i).charAt(t) + " " + new Integer(lines.get(i).charAt(t)));
					System.exit(-1);	
				}
			}
			if (!names.isEmpty()) {
				if(isGenotype)
					data.addGenotype(names.get(i), genotype);
				else data.addHaplotype(names.get(i), haplotype);
			} else {
				if(isGenotype)
					data.addGenotype(genotype);
				else data.addHaplotype(haplotype);
			}
		}
		return data;
	}
	
	public static ArrayDataSet read(String filename) {
		Scanner fr;
		Scanner fst = null;
		Scanner fsm = null;
		
		try {
			fr = new Scanner(new BufferedReader(new InputStreamReader(
					new GZIPInputStream(new FileInputStream(filename)))));

		} catch (IOException ioe) {
			// try again without gzip...
			try {
				fr = new Scanner(new BufferedReader(new FileReader(filename)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(-1);
				return null;
			}
		}
		
		if (new File(filename + ".sites").exists()) {
			try {
				fst = new Scanner(new BufferedReader(new InputStreamReader(
						new GZIPInputStream(new FileInputStream(filename + ".sites")))));

			} catch (IOException ioe) {
				// try again without gzip...
				try {
					fst = new Scanner(new BufferedReader(new FileReader(filename + ".sites")));
				} catch (FileNotFoundException e) {
					fr.close();
					e.printStackTrace();
					System.exit(-1);
					return null;
				}
			}
		}
		

		if (new File(filename + ".samples").exists()) {
			try {
				fsm = new Scanner(new BufferedReader(new InputStreamReader(
						new GZIPInputStream(new FileInputStream(filename + ".samples")))));

			} catch (IOException ioe) {
				// try again without gzip...
				try {
					fsm = new Scanner(new BufferedReader(new FileReader(filename + ".samples")));
				} catch (FileNotFoundException e) {
					fr.close();
					e.printStackTrace();
					System.exit(-1);
					return null;
				}
			}
		}
		
		return read(fr, fst, fsm);
	}

	public static void write(ArrayDataSet<byte[]> data, BufferedWriter output, BufferedWriter bst, BufferedWriter bsm) {
		int numsequences = data.numSequences();
		int numsites = data.numSites();
		try {
			for (Haplotype haplotype : data.getHaplotypes()) {
				for (Site site : data.getSites()) {
					if (!data.isObserved(site, haplotype)) {
						output.write("?");
					} else 
						output.write(data.getAllele(site, haplotype));

				}
				output.write("\n");
			}
			for (Genotype genotype : data.getGenotypes()) {
				for (Site site : data.getSites()) {
					if (!data.isObserved(site, genotype)) {
						output.write("?");
					} else if (data.get(site, genotype)[0]==data.get(site, genotype)[1]) {
						output.write(data.get(site, genotype)[0]);
					} else {
						output.write("1");
					}
				}
			}
			output.close();
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		if (bst != null) {
			try {
				for (Site site : data.getSites()) {
					Object position = (Double)(site.getPosition());
					if (compareFloats((Double)position, round((Double)position), 1e-10) == 0) {
						position = (Integer)((int)((double)position));
					}
					String name = site.getName();
					String alleles = site.getAlleles()[0] + "/" + site.getAlleles()[1];
					bst.write(position + " " + name + " " + alleles + "\n");
				}
				bst.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
		
		if (bsm != null) {
			try {
				for (Haplotype haplotype : data.getHaplotypes()) {
					bsm.write(haplotype.getName() + "\n");
				}
				for (Genotype genotype : data.getGenotypes()) {
					bsm.write(genotype.getName() + "\n");
				}
				bsm.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}

	public static void write(ArrayDataSet<byte[]> data, String filename) {
		try {
			write(data,
					new BufferedWriter(new FileWriter(new File(filename))),
					new BufferedWriter(new FileWriter(new File(filename + ".sites"))),
					new BufferedWriter(new FileWriter(new File(filename + ".samples"))));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
