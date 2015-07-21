/* popgenIO
 * Copyright (c) 2011, 2012, 2013, Yee Whye Teh, Charles Blundell, Lloyd T. Elliott, Derek Aguiar and Barbara Engelhardt
 */

package popgenIO.Formats;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import libnp.util.Pair;

public class MergeFile {

	public static Pair<List<String>,List<String>> read(String merge_filename) throws Exception {

		List<String> mosaic_filenames = new ArrayList<String>();
		List<String> vcfs_filenames = new ArrayList<String>();

		BufferedReader in = new BufferedReader(new FileReader(merge_filename));

		String line = null;
		while ((line = in.readLine()) != null) {
			String[] parts = line.split("\t");
			mosaic_filenames.add(parts[0]);
			vcfs_filenames.add(parts[1]);
		}
		
		in.close();

		return new Pair<List<String>,List<String>>(mosaic_filenames,vcfs_filenames);
	}
}
