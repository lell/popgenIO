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

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * @author Ouroboros
 *
 */
public class GenericFile {

	String filename;
	BufferedWriter writer;
	
	
	public GenericFile(String filename) {
		super();
		this.filename = filename;
	}

	/**
	 * @param map
	 * @param traj_to_group
	 * @param mosaic_file
	 * @throws IOException 
	 */
	public void writeLine(String line) throws IOException {
		writer.write(line+System.getProperty("line.separator"));
	}

	/**
	 * @throws IOException 
	 * 
	 */
	public void init() throws IOException {
		writer = new BufferedWriter(new java.io.FileWriter(filename));
	}

	/**
	 * @throws IOException 
	 * 
	 */
	public void close() throws IOException {
		writer.flush();
		writer.close();
	}

}
