/**
 * Classname
 * 
 * version 0.1
 *
 * Jun 25, 2015
 * 
 * The contents of this file are subject to the terms of the GNU
 * General Public License Version 3. For more information visit 
 * http://www.gnu.org/licenses/gpl.html
 */
package popgenIO.Formats;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ouroboros
	Haplotype cluster graphs are encoded with a format as follows.
	The line number gives a unique identifier for the cluster. 
	Each line is space delimited and contains the number of variants contained 
	in the cluster, for each variant, the allele counts are delimited by commas, 
	then transition probabilities (edges) to other states follow.
 */
public class ClusterGraphFile {

	String fileName;
	int line_no;
	String line = null;
	String[] parts = null;
	String[] parts2 = null;
	List<List<Double>> returned_parts;
	int num_variants, start, end;
	BufferedReader in = null;
	
	public ClusterGraphFile(String fileName) {
		super();
		line_no = 0;
		this.fileName = fileName;
	}

	public void init() throws IOException {
		in = Files.newBufferedReader(Paths.get(fileName),
				Charset.forName("UTF-8"));
		line = in.readLine();
		String[] parts = line.split(" ");
		num_variants = Integer.parseInt(parts[0]);
		start = Integer.parseInt(parts[1]);
		end = Integer.parseInt(parts[2]);
	}

	public List<List<Double>> getNextState() throws IOException {
		if((line=in.readLine())==null) {
			return null;
		}
		// id start length 
		returned_parts = new ArrayList<List<Double>>();
		returned_parts.add(new ArrayList<Double>()); // cluster info
		returned_parts.add(new ArrayList<Double>()); // allele info
		returned_parts.add(new ArrayList<Double>()); // edge info
		parts = line.split(" ");
		returned_parts.get(0).add(Double.parseDouble(parts[0])); // id
		returned_parts.get(0).add(Double.parseDouble(parts[1])); // start 
		returned_parts.get(0).add(Double.parseDouble(parts[2])); // num alleles
		returned_parts.get(0).add(Double.parseDouble(parts[3])); // ploidy
		for (int i = 0; i < returned_parts.get(0).get(2); i++) {
			parts2 = parts[i+4].split(",");
			for (int j = 0; j < parts2.length; j++) {
				returned_parts.get(1).add(Double.parseDouble(parts2[j])); // allele count				
			}
		}
		int index = (int) (returned_parts.get(0).get(2) + 4);
		int num_of_edges = Integer.parseInt(parts[index]);
		for (int i = 0; i < num_of_edges; i++) {
			parts2 = parts[index+i+1].split(":");
			returned_parts.get(2).add(Double.parseDouble(parts2[0])); // id
			returned_parts.get(2).add(Double.parseDouble(parts2[1])); // prob
		}
		return returned_parts;
	}
	
	public void close() throws IOException {
		in.close();
	}
}
