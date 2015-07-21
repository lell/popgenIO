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

public class MosaicFile {


	/**
	 * 
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public static short[][] read(String filename, int num_individuals, int num_sites) throws Exception {
		short[][] mosaic = new short[num_individuals][num_sites];
		
		String line = null;
		BufferedReader in = new BufferedReader(new FileReader(filename));
		
		int line_ctr = 0;
		while((line=in.readLine())!=null) {
			String[] parts = line.split(" ");
			for (int i = 0; i < parts.length; i++) {
				mosaic[i][line_ctr]=Short.parseShort(parts[i]);
			}
			line_ctr++;
		}
		
		in.close();
		
		return mosaic;
	}
}
