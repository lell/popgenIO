package popgenIO.Core;

import libnp.mcmc.collectors.Collectable;
import libnp.mcmc.collectors.Collector;
import libnp.mcmc.collectors.MatrixCollector;

public class DataSetConverter implements Collectable {

	private Collectable cc;
	
	public DataSetConverter(Collectable cc) {
		this.cc = cc;
	}
	
	private Byte[][] convert(ArrayDataSet<byte[]> data) {
		int N = data.numSequences();
		int T = data.numSites();
		Byte[][] matrix = new Byte[N][T];
		int hid = 0;
		for (Haplotype hh : data.getHaplotypes()) {
			int sid = 0;
			for (Site ss : data.getSites()) {
				byte val = data.getAllele(ss, hh);
				matrix[hid][sid] = val;
				sid++;
			}
			hid++;
		}
		for (Diplotype dd : data.getDiplotypes()) {
			int sid = 0;
			for (Site ss : data.getSites()) {
				byte[] val = data.get(ss, dd);
				matrix[hid][sid] = val[0];
				matrix[hid+1][sid] = val[1];
				sid++;
			}
			hid+=2;
		}
		return matrix;
	}
	@Override
	public Object get(String property_name) {
		return convert((ArrayDataSet)cc.get(property_name));
	}

	@Override
	public Object get(String property_name, Object arg) {
		return convert((ArrayDataSet)cc.get(property_name, arg));
	}

}
