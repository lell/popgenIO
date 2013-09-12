/* popgenIO
 * Copyright (c) 2011, 2012, 2013, Yee Whye Teh, Charles Blundell and Lloyd T. Elliott
 */

package popgenIO.Formats;

import java.util.*;
import java.util.zip.*;
import java.io.*;

import popgenIO.Core.DataSet;
import popgenIO.Core.BitDataSet;
import popgenIO.Core.Genotype;
import popgenIO.Core.Haplotype;
import popgenIO.Core.Site;

import libnp.statistics.Generator;
import static libnp.util.Operation.removeExtention;

public class BeagleFile {
	public static class ParseError extends Exception {
		private static final long serialVersionUID = 1L;

		ParseError(String msg) {
			super(msg);
		}
	};

	public static String getSnpFile(String filename) {
		if (new File(filename).exists()) {
			return filename;
		} else if (new File(filename + ".bgl").exists()) {
			return filename + ".bgl";
		} else if (new File(filename + ".bgl.gz").exists()) {
			return filename + ".bgl.gz";
		} else {
			return null;
		}
	}

	public static String getMarkerFile(String filename) {
		if (new File(removeExtention(filename) + ".markers.gz").exists()) {
			return removeExtention(filename) + ".markers.gz";
		} else if (new File(removeExtention(filename) + ".markers").exists()) {
			return removeExtention(filename) + ".markers";
		} else if (new File(filename + ".markers.txt.gz").exists()) {
			return removeExtention(filename) + ".markers.txt.gz";
		} else if (new File(removeExtention(filename) + ".markers.txt").exists()) {
			return removeExtention(filename) + ".markers.txt";	
		} else if (new File("markers.gz").exists()) {
			return "markers.gz";
		} else if (new File("markers").exists()) {
			return "markers";	
		} else if (new File("markers.txt.gz").exists()) {
			return "markers.txt.gz";	
		} else if (new File("markers.txt").exists()) {
			return "markers.txt";	
		} else {
			return null;
		}
	}

	public static DataSet read(String filename) throws Exception {
		String snpfile = getSnpFile(filename);
		String markerfile = getMarkerFile(filename);

		Map<String, Map<String, Integer[]>> snps = readSNPs(snpfile);
		Map<String, Integer[]> markermap = readMarkers(markerfile);
		
		List<String> sequences = new ArrayList<String>(snps.keySet());
		Collections.sort(sequences);
		Site[] sites = makeSiteMap(markermap);
		DataSet<Boolean> gds = new BitDataSet(sites.length, 2 * snps.size());
		
		for (Site site : sites) {
			gds.addSite(site.globalize());
		}
		
		for (String sequence_name : sequences) {
			if (sequence_name.startsWith("H")) {
				Map<String, Integer[]> sequence = snps.get(sequence_name);
				Boolean[] allele_data0 = new Boolean[sites.length];
				Boolean[] allele_data1 = new Boolean[sites.length];
				int i = 0;
				for (Site site : sites) {

					String marker = site.getName();
					if (!sequence.containsKey(marker)) {
						// unobserved
						allele_data0[i] = null;
						allele_data1[i] = null;
					} else {
						allele_data0[i] = sequence.get(marker)[0] == site.getAlleles()[0];
						allele_data1[i] = sequence.get(marker)[1] == site.getAlleles()[0];
					}
					i++;
				}
				gds.addHaplotype(sequence_name, allele_data0);
				gds.addHaplotype(sequence_name, allele_data1);

			} else if (sequence_name.startsWith("G") || sequence_name.startsWith("D")) {
				Map<String, Integer[]> sequence = snps.get(sequence_name);
				Boolean[][] allele_data = new Boolean[sites.length][2];
				int i = 0;
				for (Site site : sites) {
					String marker = site.getName();
					if (!sequence.containsKey(marker)) {
						// unobserved
						allele_data[i][0] = null;
						allele_data[i][1] = null;
					} else {
						allele_data[i][0] = sequence.get(marker)[0] == site.getAlleles()[0];
						allele_data[i][1] = sequence.get(marker)[1] == site.getAlleles()[0];
					}
					i++;
				}
				gds.addGenotype(sequence_name, allele_data);
			} else {
				throw new ParseError("Unknown sequnce name: " + sequence_name);
			}
		}
		return gds;
	}

	private static Site[] makeSiteMap(final Map<String, Integer[]> markermap) {
		ArrayList<Site> sites = new ArrayList<Site>();
		List<String> markers = new ArrayList<String>(markermap.keySet());
		Collections.sort(markers, new Comparator<String>() {
			@Override
			public int compare(String sa, String sb) {
				return markermap.get(sa)[0] - markermap.get(sb)[0];
			}
		});
		int index = 0;
		double min_pos = markermap.get(markers.get(0))[0];
		double max_pos = markermap.get(markers.get(markers.size() - 1))[0];
		for (String marker : markers) {
			Integer[] values = markermap.get(marker);
			// normalize position into [0,1]
			double position = (values[0] - min_pos) / (max_pos - min_pos);
			char[] allele_chars = { (char) (int) values[1],
					(char) (int) values[2] };

			sites.add(new Site(index, position, marker, allele_chars));
			index++;
		}
		Site[] sitemap = new Site[sites.size()];
		sites.toArray(sitemap);
		return sitemap;
	}

	/* returns a mapping from marker names to marker positions */
	private static Map<String, Integer[]> readMarkers(String mapfile)
			throws Exception {
		Scanner scan = new Scanner(new BufferedReader(new FileReader(mapfile)));
		Map<String, Integer[]> markermap = new HashMap<String, Integer[]>();
		while (scan.hasNext()) {
			String marker = scan.next();
			if (marker.startsWith("#")) {
				scan.nextLine();
				continue;
			}
			Integer position = scan.nextInt();
			Integer[] values = new Integer[3];
			markermap.put(marker, values);
			values[0] = position;
			Scanner allele_chars = new Scanner(scan.nextLine());
			String token = allele_chars.next();
			assert token.length() == 1;
			values[1] = (int) token.charAt(0);
			token = allele_chars.next();
			assert token.length() == 1;
			values[2] = (int) token.charAt(0);
		}
		scan.close();
		return markermap;
	}

	/* returns, for each sequence, a mapping from marker name to the allele */
	private static Map<String, Map<String, Integer[]>> readSNPs(
			String snpfilename) throws Exception {
		Scanner scan;
		List<String> sequence_names = new ArrayList<String>();

		scan = tryOpenSNP(snpfilename, sequence_names);

		Map<String, Map<String, Integer[]>> alleles = new HashMap<String, Map<String, Integer[]>>();
		for (String sequence_name : sequence_names) {
			alleles.put(sequence_name, new HashMap<String, Integer[]>());
		}

		while (scan.hasNext()) {
			String type = scan.next();
			if (type.startsWith("#") || type.equals("A")) {
				// harmless to skip these lines
				scan.nextLine();
				continue;
			}
			if (!type.equals("M")) {
				throw new ParseError("only marker types are supported, not `"
						+ type + "'");
			}

			String marker = scan.next();

			Scanner extract_sequences = new Scanner(scan.nextLine());
			for (String sequence_name : sequence_names) {
				for (int ii = 0; ii < 2; ii++) {
					String allele = extract_sequences.next();
					if (allele.length() != 1) {
						throw new ParseError(
								"Allele's are one character not, `" + allele
								+ "'");
					}
					Map<String, Integer[]> seq_alleles = alleles
							.get(sequence_name);
					int value = allele.charAt(0);
					if (seq_alleles.containsKey(marker)) {
						seq_alleles.get(marker)[1] = value;
					} else {
						Integer[] unphased = new Integer[2];
						unphased[0] = value;
						unphased[1] = 0;
						seq_alleles.put(marker, unphased);
					}
				}
			}
		}
		scan.close();
		return alleles;
	}

	private static Scanner tryOpenSNP(String snpfilename,
			List<String> sequence_names) throws Exception {
		Scanner scan;
		Scanner header;
		try {
			scan = new Scanner(new BufferedReader(new InputStreamReader(
					new GZIPInputStream(new FileInputStream(snpfilename)))));
			header = new Scanner(new BufferedReader(new InputStreamReader(
					new GZIPInputStream(new FileInputStream(snpfilename)))));

		} catch (IOException ioe) {
			// try again without gzip...
			scan = new Scanner(new BufferedReader(new FileReader(snpfilename)));
			header = new Scanner(
					new BufferedReader(new FileReader(snpfilename)));
		}
		if (parseFirstLine(header, sequence_names)) {
			// if we parsed something from the first line, then skip it as the
			// header
			scan.nextLine();
		}
		header.close();
		return scan;
	}

	private static boolean parseFirstLine(Scanner scan,
			List<String> sequence_names) throws Exception {
		/*
		 * this is a somewhat stricter interpretation of the beagle format, but
		 * it simplifies the code.. technically the first line need not be the
		 * header.
		 */
		String token = scan.next();
		while (token.startsWith("#")) {
			token = scan.nextLine();
		}

		if (token.equals("M")) {
			// no header so just count the number of sequences on this line and
			// make up some names.
			scan.next(); // skip id
			Scanner rest = new Scanner(scan.nextLine());
			int i = 0;
			for (; rest.hasNext(); rest.next()) {
				sequence_names.add("Seq" + Integer.toString(i));
				// if we're phasing then we assume that adjacent columns
				// represent common genotypes, so we give those columns the same
				// name.
				if (!rest.hasNext()) {
					throw new ParseError(
							"phased data comes in pairs of columns");
				}
				rest.next();
				i += 2;
			}
			return false;
		}

		if (!token.equals("I")) {
			throw new ParseError(
					"first line must have a header starting with I, not `"
							+ token + "'");
		}

		token = scan.next();
		if (!token.equals("id")) {
			throw new ParseError("second column must be id, not `" + token
					+ "'");
		}

		Scanner sequence_headers = new Scanner(scan.nextLine());

		for (; sequence_headers.hasNext(); sequence_headers.next()) {
			String name = sequence_headers.next();
			sequence_names.add(name);
			/*
			 * if (name.startsWith("H")) { sequence_names.add(name.substring(0,
			 * name.length()-1)); } else { sequence_names.add(name); }
			 */
		}

		return true;
	}

	public static void write(DataSet gds, String filename)
			throws Exception {
		
		writeMarkers(filename + ".markers.gz", gds);
		writeSNPs(filename + ".bgl.gz", gds);
	}

	private static void writeMarkers(String mapfile, DataSet<Boolean> gds)
			throws Exception {
		
		PrintStream out = new PrintStream(mapfile);
		/*
		 * format: <marker name> <position> <allele0> <allele1> ... <alleleN>
		 * where N = 1 for SNPs
		 */

		for (Site site : gds.getSites()) {
			out.print(site.getName());
			out.print(" ");
			out.print(String.format("%7f", site.getPosition()));
			out.print(" ");
			out.print(site.getAlleles()[0]);
			out.print(" ");
			out.print(site.getAlleles()[1]);
			out.println();
		}
		out.close();
	}

	private static void writeSNPs(String snpfile, DataSet<Boolean> gds) throws Exception {
		
		PrintStream out = new PrintStream(new GZIPOutputStream(
				new FileOutputStream(snpfile)));

		// header
		out.print("I id");
		for (Genotype geno : gds.getGenotypes()) {
			out.print(" ");
			out.print(geno.getName());
			out.print(" ");
			out.print(geno.getName());
		}

		for (Haplotype hh : gds.getHaplotypes()) {

			String name = hh.getName();
			out.print(" ");
			out.print(name.substring(0, name.length() - 1));
		}

		out.println();
		Generator gen = new Generator();
		for (Site site : gds.getSites()) {
			out.print("M ");
			out.print(site.getName());
			for (Genotype geno : gds.getGenotypes()) {
				out.print(" ");
				Boolean[] alleles = gds.get(site, geno);
				if (alleles[0] == null) {
					assert alleles[1] == null;
					out.print("? ? ");
				} else {
					int a0 = alleles[0]?1:0;
					int a1 = alleles[1]?1:0;

					// beagle cares about order for initialisation, so randomize it.
					if (gen.nextBoolean()) {
						out.print(site.getAlleles()[a0]);
						out.print(" ");
						out.print(site.getAlleles()[a1]);
					} else {
						out.print(site.getAlleles()[a1]);
						out.print(" ");
						out.print(site.getAlleles()[a0]);						
					}
				}
			}

			for (Haplotype hh : gds.getHaplotypes()) {
				out.print(" ");
				Boolean allele = gds.get(site, hh);
				if (allele == null) {
					out.print("?");
				} else {
					out.print(site.getAlleles()[allele?1:0]);
				}
			}
			out.println();
		}
		out.close();
	}
};
