package unit;

import static org.junit.Assert.*;
import static popgenIO.Paths.getTestDirectory;
import static libnp.util.Operation.dump;
import static libnp.util.Operation.undump;

import java.io.File;

import org.junit.Test;

import popgenIO.Core.ArrayDataSet;
import popgenIO.Core.DataSet;
import popgenIO.Formats.FlatFile;

public class TestCombine {

	@Test
	public void test_combine01() {
		String testdir = getTestDirectory() + "/popgenIO/TestCombine/";
		new File(testdir).mkdirs();
		
		String study = "101\n111\n010\n";
		String study_sites = "1 S1 0/1\n3 S3 0/1\n5 S5 0/1\n";
		String study_samples = "HS01\nHS02\nHS03\n";
		
		String ref = "1011101\n0100010\n1101101\n";
		String ref_sites = "0 S0 0/1\n1 S1 0/1\n2 S2 0/1\n3 S3 0/1\n4 S4 0/1\n5 S5 0/1\n6 S6 0/1\n";
		String ref_samples = "HR01\nHR02\nHR03\n";
		
		dump(testdir + "/study", study);
		dump(testdir + "/study.sites", study_sites);
		dump(testdir + "/study.samples", study_samples);
		dump(testdir + "/ref", ref);
		dump(testdir + "/ref.sites", ref_sites);
		dump(testdir + "/ref.samples", ref_samples);
		
		ArrayDataSet<byte[]> study_data = FlatFile.read(testdir + "/study");
		ArrayDataSet<byte[]> ref_data = FlatFile.read(testdir + "/ref");
		
		ArrayDataSet<byte[]> combined = ref_data.combine(study_data);
		
		FlatFile.write(combined, testdir + "/combined");
		
		assertTrue(undump(testdir + "/combined").equals(
				ref + "?1?0?1?\n?1?1?1?\n?0?1?0?\n"));
		
		assertTrue(undump(testdir + "/combined.sites").equals(ref_sites));
	}

}
