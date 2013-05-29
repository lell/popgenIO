/* popgenIO
 * Copyright (c) 2011, 2012, 2013, Yee Whye Teh, Charles Blundell and Lloyd T. Elliott
 */

package programs;


import java.util.*;
import java.io.*;

import popgenIO.Core.DataSet;
import popgenIO.Formats.BeagleFile;
import popgenIO.Formats.FlatFile;

public class convert {

	static Scanner open_input(String input_file) throws Exception {
		if (input_file.equals("-")) {
			return new Scanner(System.in);
		}
		Scanner scan = null;
		try {
			scan = new Scanner(new BufferedReader(new FileReader(input_file)));
			scan.useLocale(Locale.US);
		} catch (Exception ee) {
			System.out.println("SNP: Unable to open input file " + input_file
					+ ": " + ee.getMessage());
			if (scan != null) {
				scan.close();
			}
			throw ee;
		}
		return scan;
	}

	static PrintStream open_output(String output_file) throws Exception {
		if (output_file.equals("-")) {
			return System.out;
		}
		PrintStream output = null;
		try {
			output = new PrintStream(output_file);
		} catch (Exception ee) {
			System.out.println("Unable to open output file " + output_file
					+ ": " + ee.getMessage());
			if (output != null) {
				output.close();
			}
			throw ee;
		}
		return output;
	}

	enum Format {
		Flat, Beagle;
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 4) {
			System.err
			.println("Usage: convert input_format input_file output_format output_file");
			System.err.println("Formats available: Phase Haplotype SNP");
			System.err.println("Use - for standard input or output.");
			System.exit(1);
		}
		Format input_format = Format.valueOf(args[0]);
		String input = args[1];
		Format output_format = Format.valueOf(args[2]);
		String output = args[3];

		DataSet<Boolean> dataset = null;

		switch (input_format) {
		case Flat:
			dataset = FlatFile.read(input);
			break;
		case Beagle:
			dataset = BeagleFile.read(input);
			break;
		default:
			throw new Error("Unrecognized format: " + args[0]);
		}
		switch (output_format) {
		case Flat:
			FlatFile.write(dataset, output);
			break;
		case Beagle:
			BeagleFile.write(dataset, output);
			break;
		default:
			throw new Error("Unrecognized format: " + args[2]);
		}

	}

}
