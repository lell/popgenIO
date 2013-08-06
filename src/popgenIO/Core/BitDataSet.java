/* popgenIO
 * Copyright (c) 2011, 2012, 2013, Yee Whye Teh, Charles Blundell and Lloyd T. Elliott
 */

package popgenIO.Core;

import static libnp.util.Operation.arange;

import java.util.*;

public class BitDataSet implements DataSet<Boolean> {
	BitSet observed = null;
	BitSet allele = null;
	int numsequences = 0;
	int numsites = 0;
	int index;
	Site[] sites;
	List<Genotype> genotypes;
	List<Diplotype> diplotypes;
	List<Haplotype> haplotypes;

	public BitDataSet(int numsites, int numsequences) {
		this.numsites = numsites;
		this.numsequences = numsequences;
		observed = new BitSet(numsequences * numsites);
		allele = new BitSet(numsequences * numsites);
		
		genotypes = new ArrayList();
		diplotypes = new ArrayList();
		haplotypes = new ArrayList();
	}

	public BitDataSet(BitDataSet data) {
		this.numsites = data.numsites;
		this.numsequences = data.numsequences;
		
		this.observed = (BitSet) data.observed.clone();
		this.allele = (BitSet) data.allele.clone();
		this.sites = Site.copyArray(data.getSites());

		genotypes = new ArrayList();
		diplotypes = new ArrayList();
		haplotypes = new ArrayList();
		
		for (Genotype genotype : data.getGenotypes()) {
			genotypes.add(genotype.clone());
		}
		for (Diplotype diplotype: data.getDiplotypes()) {
			diplotypes.add(diplotype.clone());
		}
		for (Haplotype haplotype : data.getHaplotypes()) {
			haplotypes.add(haplotype.clone());
		}
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
	public BitDataSet clone() {
		return new BitDataSet(this);
	}
	
	public static BitDataSet unobserved(int T, int N) {
		BitDataSet data = new BitDataSet(T, N);
		data.addSites(arange(T));
		
		for (int i = 0; i < N; i++) {
			Boolean[] haplotype = new Boolean[T];
			for (int t = 0; t < T; t++) {
				haplotype[t] = null;
			}
			data.addHaplotype(haplotype);
		}
		return data;
	}
	
	final private boolean isObserved(int sid, int qid) {
		return observed.get(index(sid, qid));
	}

	final int index(int sid, int qid) {
		assert sid >= 0 && sid < numsites;
		assert qid >= 0 && qid < numsequences;
		return sid + (qid * numSites());
	}

	final private Boolean getAllele(int sid, int qid) {
		if (!isObserved(sid, qid)) {
			return null;
		} else {
			return allele.get(index(sid, qid));
		}
	}

	final private void setAllele(int sid, int qid, Boolean aa) {
		if (aa == null) {
			setObserved(sid, qid, false);
		} else {
			allele.set(index(sid, qid), aa);
			setObserved(sid, qid, true);
		}
	}


	final private void setObserved(int sid, int qid, boolean aa) {
		observed.set(index(sid, qid), aa);
	}
	
	@Override
	public void addSites(Site[] sites) {
		assert sites == null;
		this.sites = sites;
	}
	
	@Override
	public void addSites(double[] positions) {
		sites = new Site[numsites];
		assert numsites == positions.length;
		for (int sid = 0; sid < numsites; sid++) {
			sites[sid] = new Site(sid, positions[sid]);
		}
	}

	@Override
	public void addSites(double[] positions, String[] names) {
		for (int sid = 0; sid < numsites; sid++) {
			sites[sid] = new Site(sid, positions[sid], names[sid]);
		}
	}
	
	
	@Override
	public Genotype addGenotype(String name, Boolean[][] data) {
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
	public Genotype addGenotype(Boolean[][] data) {
		return addGenotype(String.format("G%06d", index), data);
	}

	@Override
	public Diplotype addDiplotype(String name, Boolean[][] data) {
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
	public Diplotype addDiplotype(Boolean[][] data) {
		return addDiplotype(String.format("D%06d", index), data);
	}

	@Override
	public Haplotype addHaplotype(String name, Boolean[] data) {
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
	public Haplotype addHaplotype(Boolean[] data) {
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
	public Site[] getSites() {
		return sites;
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
	public Boolean[] get(Site ss, Genotype gg) {
		if (!isObserved(ss.getIndex(), gg.getIndex())) {
			return new Boolean[] { null, null };
			
		} else {
			return new Boolean[]{
					getAllele(ss.getIndex(), gg.getIndex()),
					getAllele(ss.getIndex(), gg.getIndex()+1)};
		}
	}

	@Override
	public Boolean[] get(Site ss, Diplotype dd) {
		if (!isObserved(ss.getIndex(), dd.getIndex())) {
			return new Boolean[] { null, null };
		} else {
			return new Boolean[]{
					getAllele(ss.getIndex(), dd.getIndex()),
					getAllele(ss.getIndex(), dd.getIndex()+1)};
		}
		
	}

	@Override
	public Boolean get(Site ss, Diplotype dd, int phase) {
		if (!isObserved(ss.getIndex(), dd.getIndex())) {
			return null;
		} else {
			return getAllele(ss.getIndex(), dd.getIndex()+phase);
		}
		
	}

	@Override
	public Boolean get(Site ss, Haplotype hh) {
		if (!isObserved(ss.getIndex(), hh.getIndex())) {
			return null;
		} else {
			return getAllele(ss.getIndex(), hh.getIndex());
		}
	}

	@Override
	public void set(Site ss, Genotype gg, Boolean[] val) {
		if (val == null || val[0] == null) {
			setObserved(ss.getIndex(), gg.getIndex(), false);
			setObserved(ss.getIndex(), gg.getIndex()+1, false);
		} else {
			setAllele(ss.getIndex(), gg.getIndex(), val[0]);
			setAllele(ss.getIndex(), gg.getIndex()+1, val[1]);
		}
		
		
	}

	@Override
	public void set(Site ss, Diplotype dd, Boolean[] val) {
		if (val == null || val[0] == null) {
			setObserved(ss.getIndex(), dd.getIndex(), false);
			setObserved(ss.getIndex(), dd.getIndex()+1, false);
		} else {
			setAllele(ss.getIndex(), dd.getIndex(), val[0]);
			setAllele(ss.getIndex(), dd.getIndex()+1, val[1]);
		}
		
	}

	@Override
	public void set(Site ss, Haplotype hh, Boolean val) {
		if (val == null) {
			setObserved(ss.getIndex(), hh.getIndex(), false);
		} else {
			setAllele(ss.getIndex(), hh.getIndex(), val);
		}
	}

	@Override
	public GenotypeValue getGenotypeValue(Site ss, Genotype gg) {
		if (!isObserved(ss, gg)) {
			return new GenotypeValue(-1);
		} else {
			return new GenotypeValue((getAllele(ss.getIndex(), gg.getIndex())?1:0)
					+ (getAllele(ss.getIndex(), gg.getIndex()+1)?1:0));
		}
	}

	@Override
	public DataSet<Boolean> filter(Site[] sites) {
		BitDataSet other = new BitDataSet(sites.length, this.numSequences());

		other.addSites(Site.copyArray(sites));
		Site[] other_sites = other.getSites();
		
		for (Genotype genotype : getGenotypes()) {
			Genotype other_genotype = genotype.clone();
			other.genotypes.add(other_genotype);
			for (int sid = 0; sid < sites.length; sid++) {
				Site site = sites[sid];
				Site other_site = other_sites[sid];
				if (this.isObserved(site, genotype)) {
					other.setObserved(other_site, other_genotype, true);
					other.set(other_site, other_genotype,
							get(site, genotype));
				} else {
					other.setObserved(other_site, other_genotype, false);
				}
			}
		}
		for (Diplotype diplotype : getDiplotypes()) {
			Diplotype other_diplotype = diplotype.clone();
			other.diplotypes.add(other_diplotype);
			
			for (int sid = 0; sid < sites.length; sid++) {
				Site site = sites[sid];
				Site other_site = other_sites[sid];
				if (this.isObserved(site, diplotype)) {
					other.setObserved(other_site, other_diplotype, true);
					other.set(other_site, other_diplotype,
							get(site, diplotype));
				} else {
					other.setObserved(other_site, other_diplotype, false);
				}
			}
		}
		
		for (Haplotype haplotype : getHaplotypes()) {
			Haplotype other_haplotype = haplotype.clone();
			other.haplotypes.add(other_haplotype);
			
			for (int sid = 0; sid < sites.length; sid++) {
				Site site = sites[sid];
				Site other_site = other_sites[sid];
				if (this.isObserved(site, haplotype)) {
					other.setObserved(other_site, other_haplotype, true);
					other.set(other_site, other_haplotype,
							get(site, haplotype));
				} else {
					other.setObserved(other_site, other_haplotype, false);
				}
			}
		}
		return other;
	}
}
