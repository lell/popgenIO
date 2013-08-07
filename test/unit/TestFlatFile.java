package unit;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

import popgenIO.Core.BitDataSet;
import popgenIO.Core.DataSet;
import popgenIO.Core.Haplotype;
import popgenIO.Core.Site;
import popgenIO.Formats.FlatFile;
import static popgenIO.Paths.getTestDirectory;

public class TestFlatFile {

	public void TestRead(String name, String file, int[][] data) {
		String testdir = getTestDirectory() + "/popgenIO/" + "TestFlatFile" + "/" + name + "/";
		(new File(testdir)).mkdirs();
		BufferedWriter fp;
		try {
			fp = new BufferedWriter(new FileWriter(testdir + "out"));
			fp.write(file);
			fp.close();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		
		DataSet<Boolean> bds = FlatFile.read(testdir + "out");
		assertTrue(bds.numSites() == data[0].length);
		assertTrue(bds.numSequences() == data.length);
		
		for (Haplotype hh : bds.getHaplotypes()) {
			for (Site ss : bds.getSites()) {
				Boolean allele = bds.get(ss, hh);
				if (allele == null) {
					assertTrue(data[hh.getIndex()][ss.getIndex()] == -1);
				} else if (allele == false) {
					assertTrue(data[hh.getIndex()][ss.getIndex()] == 0);
				} else if (allele = true) {
					assertTrue(data[hh.getIndex()][ss.getIndex()] == 1);
				}
			}
		}
	}
	
	@Test
	public void test_flat01() {
		TestRead("flat01",
				"01\n10\n",
				new int[][] {
					{0, 1},
					{1, 0}});
	}
	
	@Test
	public void test_flat02() {
		/* Make sure that a "10" in the header is not interpreted as a haplotype
		 */
		TestRead("flat02",
				"2\n10\nP 1 2 3 4 5 6 7 8 9 10\nSSSSSSSSSS\n0000101011\n0000101100\n",
				new int[][] {
					{0, 0, 0, 0, 1, 0, 1, 0, 1, 1},
					{0, 0, 0, 0, 1, 0, 1, 1, 0, 0}});
	}

}
