package unit;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

import popgenIO.Core.ArrayDataSet;
import popgenIO.Core.BitDataSet;
import popgenIO.Core.DataSet;
import popgenIO.Core.Haplotype;
import popgenIO.Core.Site;
import popgenIO.Formats.FlatFile;
import static popgenIO.Paths.getTestDirectory;
import static libnp.util.Operation.dump;
import static libnp.util.Operation.undump;

public class TestFlatFile {
	
	@Test
	public void test_sites01() {
		String testdir = getTestDirectory() + "/popgenIO/TestFlatFile/";
		(new File(testdir)).mkdirs();
		String fin = testdir + "/tst01_in";
		String main_content = "001\n100\n101\n???\n";
		String site_content = "1 rs001 A/T\n2 rs002 G/C\n3 rs003 A/C\n";
		
		dump(fin, main_content);
		dump(fin + ".sites", site_content);
		
		ArrayDataSet<byte[]> data = FlatFile.read(fin);
		
		String fout = testdir + "/tst01_out";
		FlatFile.write(data, fout);
		
		assertTrue(main_content.equals(undump(fout)));
		assertTrue(undump(fout + ".sites"), site_content.equals(undump(fout + ".sites")));
	}
	
	
	@Test
	public void test_samples01() {
		String testdir = getTestDirectory() + "/popgenIO/TestFlatFile/";
		(new File(testdir)).mkdirs();
		String fin = testdir + "/tsm01_in";
		String main_content = "001\n100\n101\n???\n";
		String sample_content = "H01\nH02\nH03\nH04\n";
		
		dump(fin, main_content);
		dump(fin + ".samples", sample_content);
		
		ArrayDataSet<byte[]> data = FlatFile.read(fin);
		
		String fout = testdir + "/tsm01_out";
		FlatFile.write(data, fout);
		
		assertTrue(main_content.equals(undump(fout)));
		assertTrue(undump(fout + ".samples"), sample_content.equals(undump(fout + ".samples")));
	}


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
		
		ArrayDataSet<byte[]> bds = FlatFile.read(testdir + "out");
		assertTrue(bds.numSites() == data[0].length);
		assertTrue(bds.numSequences() == data.length);
		
		for (Haplotype hh : bds.getHaplotypes()) {
			for (Site ss : bds.getSites()) {
				byte allele = bds.getAllele(ss, hh);
				if (allele == -1) {
					assertTrue(data[hh.getIndex()][ss.getIndex()] == -1);
				} else if (allele == 0) {
					assertTrue(data[hh.getIndex()][ss.getIndex()] == 0);
				} else if (allele == 1) {
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
