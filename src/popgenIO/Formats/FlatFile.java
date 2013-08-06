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

import popgenIO.Core.DataSet;
import popgenIO.Core.BitDataSet;
import popgenIO.Core.Haplotype;
import popgenIO.Core.Site;
	    
public class FlatFile {

	// This should load ANY kind of .phase file (spaces, names, prefixes) or just a square matrix of 0/1/? as long as the names aren't like 010101 (can have #sites = 100)
	public static DataSet read(Scanner fr) {
		List<String> lines = new ArrayList();
		int numsites = 0;
		
		// Strip the header + collect all the lines with haps on them
		while(fr.hasNext()) {
			String line = fr.nextLine();
			line = line.replaceAll("-1", "?");
			line = line.replaceAll("\\s+", "");

			boolean hap = true;
			for (int t = 0; t < line.length(); t++) {
				char c = line.charAt(t);
				if (! (c == '0' || c == '1' || c == '?')) {
					hap = false;
					break;
				}
			}
			if (hap) {
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
		
		DataSet data = new BitDataSet(numsites, numSequences);
		data.addSites(arange(numsites));
		for (int i = 0; i < numSequences; i++) {
			Boolean[] haplotype = new Boolean[numsites];
			for (int t = 0; t < numsites; t++) {
				switch (lines.get(i).charAt(t)) {
				case '0':
					haplotype[t] = false;
					break;
				case '1':
					haplotype[t] = true;
					break;
				case '?':
					haplotype[t] = null;
					break;
				default:
					System.err.println("Unknown character in file: " + new Integer(lines.get(i).charAt(t)));
					System.exit(-1);	
				}
			}
			data.addHaplotype(haplotype);
		}
		return data;
	}
	
	public static DataSet read(String filename) {
		Scanner fr;
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
		
		return read(fr);
	}

	public static void write(DataSet<Boolean> data, BufferedWriter output) {
		int numsequences = data.numSequences();
		int numsites = data.numSites();
		try {
			for (Haplotype haplotype : data.getHaplotypes()) {
				for (Site site : data.getSites()) {
					if (!data.isObserved(site, haplotype)) {
						output.write("?");
					} else if (data.get(site, haplotype)) {
						output.write("1");
					} else {
						output.write("0");
					}
				}
				output.write("\n");
			}
			output.close();
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public static void write(DataSet<Boolean> data, String filename) {
		try {
			write(data, new BufferedWriter(new FileWriter(new File(filename))));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
