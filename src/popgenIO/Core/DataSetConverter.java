package popgenIO.Core;

import libnp.mcmc.collectors.Collectable;
import libnp.mcmc.collectors.Collector;
import libnp.mcmc.collectors.MatrixCollector;

public class DataSetConverter implements Collectable {

	private Collectable cc;
	
	public DataSetConverter(Collectable cc) {
		this.cc = cc;
	}
	
	private Integer[][] convert(DataSet<Boolean> data) {
		int N = data.numSequences();
		int T = data.numSites();
		Integer[][] matrix = new Integer[N][T];
		int hid = 0;
		for (Haplotype hh : data.getHaplotypes()) {
			int sid = 0;
			for (Site ss : data.getSites()) {
				Boolean val = data.get(ss, hh);
				if (val == null) {
					matrix[hid][sid] = -1;
				} else if (val) {
					matrix[hid][sid] = 1;
				} else {
					matrix[hid][sid] = 0;
				}
				sid++;
			}
			hid++;
		}
		for (Diplotype dd : data.getDiplotypes()) {
			int sid = 0;
			for (Site ss : data.getSites()) {
				Boolean[] val = data.get(ss, dd);
				if (val[0] == null) {
					matrix[hid][sid] = -1;
				} else if (val[0]) {
					matrix[hid][sid] = 1;
				} else {
					matrix[hid][sid] = 0;
				}
				if (val[1] == null) {
					matrix[hid+1][sid] = -1;
				} else if (val[1]) {
					matrix[hid+1][sid] = 1;
				} else {
					matrix[hid+1][sid] = 0;
				}
				sid++;
			}
			hid+=2;
		}
		return matrix;
	}
	@Override
	public Object get(String property_name) {
		return convert((DataSet)cc.get(property_name));
	}

	@Override
	public Object get(String property_name, Object arg) {
		return convert((DataSet)cc.get(property_name, arg));
	}

}
