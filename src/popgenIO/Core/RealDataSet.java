/* popgenIO
 * Copyright (c) 2011, 2012, 2013, Yee Whye Teh, Charles Blundell and Lloyd T. Elliott
 */

package popgenIO.Core;

import java.util.*;

public abstract class RealDataSet implements DataSet<Double> {
	BitSet observed = null;
	double[][] allele = null;
	int numsequences = 0;
	int numsites = 0;
	int index;
	Site[] sites;
	List<Genotype> genotypes;
	List<Diplotype> diplotypes;
	List<Haplotype> haplotypes;

	public RealDataSet(int numsites, int numsequences) {
		this.numsites = numsites;
		this.numsequences = numsequences;
		observed = new BitSet(numsequences * numsites);
		allele = new double[numsequences][numsites];
		
		genotypes = new ArrayList();
		diplotypes = new ArrayList();
		haplotypes = new ArrayList();
		
	}

	public RealDataSet(RealDataSet data) {
		this.numsites = data.numsites;
		this.numsequences = data.numsequences;
		this.observed = (BitSet) data.observed.clone();
		this.allele = (double[][]) data.allele.clone();
	}

	@Override
	public int numSites() {
		return numsites;
	}

	@Override
	public int numSequences() {
		return numsequences;
	}
	
	@Override
	public List<Genotype> getGenotypes() {
		return genotypes;
	}

	@Override
	public List<Diplotype> getDiplotypes() {
		return diplotypes;
	}
	

	@Override
	public List<Haplotype> getHaplotypes() {
		return haplotypes;
	}
	
	@Override
	public RealDataSet clone() {
		assert false : "Not implemented";
		return null;
		// return new RealDataSet(this);
	}
	
	final private boolean isObserved(int sid, int qid) {
		return observed.get(index(sid, qid));
	}

	final int index(int sid, int qid) {
		assert sid >= 0 && sid < numsites;
		assert qid >= 0 && qid < numsequences;
		return sid + (qid * numSites());
	}

	final private Double getAllele(int sid, int qid) {
		if (!isObserved(sid, qid)) {
			return null;
		} else {
			return allele[sid][qid];
		}
	}

	final private void setAllele(int sid, int qid, Double aa) {
		if (aa == null) {
			setObserved(sid, qid, false);
		} else {
			allele[sid][qid] = aa;
		}
	}


	final private void setObserved(int sid, int qid, boolean aa) {
		observed.set(index(sid, qid), aa);
	}
	
	@Override
	public Genotype addGenotype(String name, Double[][] data) {
		Genotype genotype = new Genotype(name, index);
		for (int t = 0; t < numsites; t++) {
			if (data[t][0] != null) {
				assert data[t][1] != null;
				setAllele(t, index, data[t][0]);
				setAllele(t, index+1, data[t][1]);
				
				setObserved(t, index, true);
				setObserved(t, index+1, true);
			} else {
				setObserved(t, index, false);
				setObserved(t, index, false);
			}
		}
		index += 2;
		genotypes.add(genotype);
		return genotype;
	}

	@Override
	public Genotype addGenotype(Double[][] data) {
		return addGenotype(String.format("G%06d", index), data);
	}

	@Override
	public Diplotype addDiplotype(String name, Double[][] data) {
		Diplotype diplotype = new Diplotype(name, index);
		for (int t = 0; t < numsites; t++) {
			if (data[t][0] != null) {
				assert data[t][1] != null;
				setAllele(t, index, data[t][0]);
				setAllele(t, index+1, data[t][1]);
				
				setObserved(t, index, true);
				setObserved(t, index+1, true);
			} else {
				setObserved(t, index, false);
				setObserved(t, index+1, false);
			}
		}
		index += 2;
		diplotypes.add(diplotype);
		return diplotype;
	}

	@Override
	public Diplotype addDiplotype(Double[][] data) {
		return addDiplotype(String.format("D%06d", index), data);
	}

	@Override
	public Haplotype addHaplotype(String name, Double[] data) {
		Haplotype haplotype = new Haplotype(name, index);
		for (int t = 0; t < numsites; t++) {
			if (data[t] != null) {
				setAllele(t, index, data[t]);
			} else {
				setObserved(t, index, false);
			}
		}
		index += 1;
		haplotypes.add(haplotype);
		return haplotype;
	}

	@Override
	public Haplotype addHaplotype(Double[] data) {
		return addHaplotype("H%06d", data);
	}

	@Override
	public int numGenotypes() {
		return genotypes.size();
	}

	@Override
	public int numDiplotypes() {
		return diplotypes.size();
	}

	@Override
	public int numHaplotypes() {
		return haplotypes.size();
	}

	@Override
	public List<Site> getSites() {
		assert false : "Not implemented.";
		return null;
		//return sites;
	}


	@Override
	public boolean isObserved(Site ss, Genotype gg) {
		return isObserved(ss.getIndex(), gg.getIndex());
	}

	@Override
	public boolean isObserved(Site ss, Haplotype hh) {
		return isObserved(ss.getIndex(), hh.getIndex());
	}

	@Override
	public boolean isObserved(Site ss, Diplotype dd) {
		return isObserved(ss.getIndex(), dd.getIndex());
	}

	@Override
	public void setObserved(Site ss, Genotype gg, boolean obs) {
		setObserved(ss.getIndex(), gg.getIndex(), obs);
		setObserved(ss.getIndex(), gg.getIndex()+1, obs);
	}

	@Override
	public void setObserved(Site ss, Haplotype hh, boolean obs) {
		setObserved(ss.getIndex(), hh.getIndex(), obs);
	}

	@Override
	public void setObserved(Site ss, Diplotype dd, boolean obs) {
		setObserved(ss.getIndex(), dd.getIndex(), obs);
		setObserved(ss.getIndex(), dd.getIndex()+1, obs);
		
	}

	@Override
	public Double[] get(Site ss, Genotype gg) {
		if (!isObserved(ss.getIndex(), gg.getIndex())) {
			return new Double[] { null, null };
			
		} else {
			return new Double[]{
					getAllele(ss.getIndex(), gg.getIndex()),
					getAllele(ss.getIndex(), gg.getIndex()+1)};
		}
	}

	@Override
	public Double[] get(Site ss, Diplotype dd) {
		if (!isObserved(ss.getIndex(), dd.getIndex())) {
			return new Double[] { null, null };
		} else {
			return new Double[]{
					getAllele(ss.getIndex(), dd.getIndex()),
					getAllele(ss.getIndex(), dd.getIndex()+1)};
		}
		
	}

	@Override
	public Double get(Site ss, Diplotype dd, int phase) {
		if (!isObserved(ss.getIndex(), dd.getIndex())) {
			return null;
		} else {
			return getAllele(ss.getIndex(), dd.getIndex()+phase);
		}
		
	}

	@Override
	public Double get(Site ss, Haplotype hh) {
		if (!isObserved(ss.getIndex(), hh.getIndex())) {
			return null;
		} else {
			return getAllele(ss.getIndex(), hh.getIndex());
		}
	}

	@Override
	public void set(Site ss, Genotype gg, Double[] val) {
		if (val == null || val[0] == null) {
			setObserved(ss.getIndex(), gg.getIndex(), false);
			setObserved(ss.getIndex(), gg.getIndex()+1, false);
		} else {
			setAllele(ss.getIndex(), gg.getIndex(), val[0]);
			setAllele(ss.getIndex(), gg.getIndex()+1, val[1]);
		}
		
		
	}

	@Override
	public void set(Site ss, Diplotype dd, Double[] val) {
		if (val == null || val[0] == null) {
			setObserved(ss.getIndex(), dd.getIndex(), false);
			setObserved(ss.getIndex(), dd.getIndex()+1, false);
		} else {
			setAllele(ss.getIndex(), dd.getIndex(), val[0]);
			setAllele(ss.getIndex(), dd.getIndex()+1, val[1]);
		}
		
	}

	@Override
	public void set(Site ss, Haplotype hh, Double val) {
		if (val == null) {
			setObserved(ss.getIndex(), hh.getIndex(), false);
		} else {
			setAllele(ss.getIndex(), hh.getIndex(), val);
		}
	}

	@Override
	public GenotypeValue getGenotypeValue(Site ss, Genotype gg) {
		return null;
	}
}
