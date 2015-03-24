/* popgenIO
 * Copyright (c) 2011, 2012, 2013, Yee Whye Teh, Charles Blundell and Lloyd T. Elliott
 */

package popgenIO.Core;

import java.util.BitSet;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;
import java.io.Serializable;

public class IntDataSet implements DataSet<Integer>, Serializable {
	BitSet observed = null;
	List<Integer> allele = null;
	int numsequences = 0;
	int numsites = 0;
	int index;
	int site_index = 0;
	
	SortedMap<String, Site> sites;
	LinkedHashMap<String, Genotype> genotypes;
	LinkedHashMap<String, Diplotype> diplotypes;
	LinkedHashMap<String, Haplotype> haplotypes;

	public IntDataSet(int numsites, int numsequences) {
		this.numsites = numsites;
		this.numsequences = numsequences;
		observed = new BitSet(numsequences * numsites);
		allele = new ArrayList<Integer>(numsequences * numsites);
		
		sites = new TreeMap();
		genotypes = new LinkedHashMap();	
		diplotypes = new LinkedHashMap();		
		haplotypes = new LinkedHashMap();
	}

	public IntDataSet(IntDataSet data) {
		this(data.numsites, data.numsequences);
		
		this.observed = (BitSet) data.observed.clone();
		this.allele = new ArrayList<Integer>(data.numsequences * data.numsites);
		Collections.copy(this.allele, data.allele);
		for (Site site : data.getSites()) {
			addSite(site.globalize());
		}
		
		for (Genotype genotype : data.getGenotypes()) {
			genotypes.put(genotype.getName(), genotype.clone());
		}
		for (Diplotype diplotype: data.getDiplotypes()) {
			diplotypes.put(diplotype.getName(), diplotype.clone());
		}
		for (Haplotype haplotype : data.getHaplotypes()) {
			haplotypes.put(haplotype.getName(), haplotype.clone());
		}
	}
	
	@Override
	public Site localize(GlobalSite site) {
		return sites.get(site.getName());
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
		List<Genotype> ggs = new ArrayList();
		ggs.addAll(genotypes.values());
		return ggs;
	}

	@Override
	public List<Diplotype> getDiplotypes() {
		List<Diplotype> dds = new ArrayList();
		dds.addAll(diplotypes.values());
		return dds;
	}

	@Override
	public List<Haplotype> getHaplotypes() {
		List<Haplotype> hhs = new ArrayList();
		hhs.addAll(haplotypes.values());
		return hhs;
	}
	
	@Override
	public List<Site> getSites() {
		List<Site> sss = new ArrayList();
		sss.addAll(sites.values());
		return sss;
	}
	
	@Override
	public Site getSite(String name) {
		return sites.get(name);
	}
	
	@Override
	public Genotype getGenotype(String name) {
		return genotypes.get(name);
	}

	@Override
	public Diplotype getDiplotype(String name) {
		return diplotypes.get(name);
	}

	@Override
	public Haplotype getHaplotype(String name) {
		return haplotypes.get(name);
	}
	
	@Override
	public Site addSite(GlobalSite site) {
		assert site != null;
		Site localized = new Site(site_index++, site);
		sites.put(site.getName(), localized);
		return localized;
	}
	
	@Override
	public IntDataSet clone() {
		return new IntDataSet(this);
	}
	
	public static IntDataSet unobserved(int T, int N) {
		IntDataSet data = new IntDataSet(T, N);
		for (int t = 0; t < T; t++) {
			data.addSite(new Site(t).globalize());
		}
		
		for (int i = 0; i < N; i++) {
			Integer[] haplotype = new Integer[T];
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

	final private Integer getAllele(int sid, int qid) {
		if (!isObserved(sid, qid)) {
			return null;
		} else {
			return allele.get(index(sid, qid));
		}
	}

	final private void setAllele(int sid, int qid, Integer aa) {
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
	public Genotype addGenotype(String name, Integer[][] data) {
		if (name == null) {
			name = String.format("G%06d", index);
		}
		Genotype genotype = new Genotype(name, index);
		for (int t = 0; t < numsites; t++) {
			if (data[0][t] != null) {
				assert data[1][t] != null;
				setAllele(t, index, data[0][t]);
				setAllele(t, index+1, data[1][t]);
				
				setObserved(t, index, true);
				setObserved(t, index+1, true);
			} else {
				setObserved(t, index, false);
				setObserved(t, index, false);
			}
		}
		index += 2;
		genotypes.put(name, genotype);
		return genotype;
	}

	@Override
	public Genotype addGenotype(Integer[][] data) {
		return addGenotype(null, data);
	}

	@Override
	public Diplotype addDiplotype(String name, Integer[][] data) {
		if (name == null) {
			name = String.format("D%06d", index);
		}
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
		diplotypes.put(name, diplotype);
		return diplotype;
	}

	@Override
	public Diplotype addDiplotype(Integer[][] data) {
		return addDiplotype(null, data);
	}

	@Override
	public Haplotype addHaplotype(String name, Integer[] data) {
		if (name == null) {
			name = String.format("H%06d", index);
		}
		Haplotype haplotype = new Haplotype(name, index);
		for (int t = 0; t < numsites; t++) {
			if (data[t] != null) {
				setAllele(t, index, data[t]);
			} else {
				setObserved(t, index, false);
			}
		}
		index += 1;
		haplotypes.put(name, haplotype);
		return haplotype;
	}

	@Override
	public Haplotype addHaplotype(Integer[] data) {
		return addHaplotype(null, data);
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
	public Integer[] get(Site ss, Genotype gg) {
		if (!isObserved(ss.getIndex(), gg.getIndex())) {
			return new Integer[] { null, null };
			
		} else {
			return new Integer[]{
					getAllele(ss.getIndex(), gg.getIndex()),
					getAllele(ss.getIndex(), gg.getIndex()+1)};
		}
	}

	@Override
	public Integer[] get(Site ss, Diplotype dd) {
		if (!isObserved(ss.getIndex(), dd.getIndex())) {
			return new Integer[] { null, null };
		} else {
			return new Integer[]{
					getAllele(ss.getIndex(), dd.getIndex()),
					getAllele(ss.getIndex(), dd.getIndex()+1)};
		}
		
	}

	@Override
	public Integer get(Site ss, Diplotype dd, int phase) {
		if (!isObserved(ss.getIndex(), dd.getIndex())) {
			return null;
		} else {
			return getAllele(ss.getIndex(), dd.getIndex()+phase);
		}
		
	}

	@Override
	public Integer get(Site ss, Haplotype hh) {
		if (!isObserved(ss.getIndex(), hh.getIndex())) {
			return null;
		} else {
			return getAllele(ss.getIndex(), hh.getIndex());
		}
	}

	@Override
	public void set(Site ss, Genotype gg, Integer[] val) {
		if (val == null || val[0] == null) {
			setObserved(ss.getIndex(), gg.getIndex(), false);
			setObserved(ss.getIndex(), gg.getIndex()+1, false);
		} else {
			setAllele(ss.getIndex(), gg.getIndex(), val[0]);
			setAllele(ss.getIndex(), gg.getIndex()+1, val[1]);
		}
		
		
	}

	@Override
	public void set(Site ss, Diplotype dd, Integer[] val) {
		if (val == null || val[0] == null) {
			setObserved(ss.getIndex(), dd.getIndex(), false);
			setObserved(ss.getIndex(), dd.getIndex()+1, false);
		} else {
			setAllele(ss.getIndex(), dd.getIndex(), val[0]);
			setAllele(ss.getIndex(), dd.getIndex()+1, val[1]);
		}
		
	}

	@Override
	public void set(Site ss, Haplotype hh, Integer val) {
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
			return new GenotypeValue((getAllele(ss.getIndex(), gg.getIndex()))
					+ (getAllele(ss.getIndex(), gg.getIndex()+1)));
		}
	}

	@Override
	public DataSet<Integer> combine(DataSet<Integer> other) {
		SortedSet<GlobalSite> combined = new TreeSet();
		
		for (Site site : getSites()) {
			combined.add(site.globalize());
		}

		for (Site site : other.getSites()) {
			combined.add(site.globalize());
		}
		
		DataSet<Integer> data = new IntDataSet(combined.size(), numSequences() + other.numSequences());
		
		for (GlobalSite site : combined) {
			data.addSite(site);
		}

		for (Genotype gg : getGenotypes()) {
			Integer[][] x = new Integer[combined.size()][2];
			for (int t = 0 ; t < combined.size(); t++) {
				x[t] = new Integer[] { null, null };
			}
			
			for (GlobalSite site : combined) {
				Site ss = localize(site);
				if (ss != null) {
					x[data.localize(site).getIndex()] = get(ss, gg);
				}
			}
			data.addGenotype(gg.getName(), x);
		}
		
		for (Diplotype dd : getDiplotypes()) {
			Integer[][] x = new Integer[combined.size()][2];
			for (int t = 0 ; t < combined.size(); t++) {
				x[t] = new Integer[] { null, null };
			}
			for (GlobalSite site : combined) {
				Site ss = localize(site);
				if (ss != null) {
					x[data.localize(site).getIndex()] = get(ss, dd);
				}
			}
			data.addDiplotype(dd.getName(), x);
		}
		
		for (Haplotype hh : getHaplotypes()) {
			Integer[] x = new Integer[combined.size()];
			for (int t = 0 ; t < combined.size(); t++) {
				x[t] = null;
			}
			for (GlobalSite site : combined) {
				Site ss = localize(site);
				if (ss != null) {
					x[data.localize(site).getIndex()] = get(ss, hh);
				}
			}
			data.addHaplotype(hh.getName(), x);
		}

		
		for (Genotype gg : other.getGenotypes()) {
			Integer[][] x = new Integer[combined.size()][2];
			for (int t = 0 ; t < combined.size(); t++) {
				x[t] = new Integer[] { null, null };
			}
			for (GlobalSite site : combined) {
				Site ss = other.localize(site);
				if (ss != null) {
					x[data.localize(site).getIndex()] = other.get(ss, gg);
				}
			}
			data.addGenotype(gg.getName(), x);
		}
		
		for (Diplotype dd : other.getDiplotypes()) {
			Integer[][] x = new Integer[combined.size()][2];
			for (int t = 0 ; t < combined.size(); t++) {
				x[t] = new Integer[] { null, null };
			}
			for (GlobalSite site : combined) {
				Site ss = other.localize(site);
				if (ss != null) {
					x[data.localize(site).getIndex()] = other.get(ss, dd);
				}
			}
			data.addDiplotype(dd.getName(), x);
		}
		
		for (Haplotype hh : other.getHaplotypes()) {
			Integer[] x = new Integer[combined.size()];
			for (int t = 0 ; t < combined.size(); t++) {
				x[t] = null;
			}
			for (GlobalSite site : combined) {
				Site ss = other.localize(site);
				if (ss != null) {
					x[data.localize(site).getIndex()] = other.get(ss, hh);
					
				}
			}
			data.addHaplotype(hh.getName(), x);
		}
		
		return data;
	}
	
	@Override
	public DataSet<Integer> filterSequences(List<Sequence> sequences) {
		IntDataSet data = new IntDataSet(this.numSites(), sequences.size());
		for (Site site : this.getSites()) {
			data.addSite(site.globalize());
		}
		
		for (Sequence hh : sequences) {
			Integer x[] = new Integer[data.numSites()];
			for (Site site : getSites()) {
				Haplotype other = this.getHaplotype(hh.getName());
				Site ss = data.localize(site.globalize());
				x[ss.getIndex()] = get(site, other);
			}
			data.addHaplotype(hh.getName(), x);
		}
		return data;
		
	}

	@Override
	public DataSet<Integer> filter(List<GlobalSite> filtered_sites) {
		IntDataSet other = new IntDataSet(filtered_sites.size(), this.numSequences());
		for (GlobalSite site : filtered_sites) {
			other.addSite(site);
		}

		for (Genotype gg : getGenotypes()) {
			Integer x[][] = new Integer[other.numSites()][2];
			for (Site site : other.getSites()) {
				Site ss = localize(site.globalize());
				x[site.getIndex()] = get(ss, gg);
			}
			other.addGenotype(gg.getName(), x);
		}
		for (Diplotype dd : getDiplotypes()) {
			Integer x[][] = new Integer[other.numSites()][2];
			for (Site site : other.getSites()) {
				Site ss = localize(site.globalize());
				x[site.getIndex()] = get(ss, dd);
			}
			other.addDiplotype(dd.getName(), x);
		}
		
		for (Haplotype hh : getHaplotypes()) {
			Integer x[] = new Integer[other.numSites()];
			for (Site site : other.getSites()) {
				Site ss = localize(site.globalize());
				x[site.getIndex()] = get(ss, hh);
			}
			other.addHaplotype(hh.getName(), x);
		}
		return other;
	}

	@Override
	public boolean isHeterozygous(Site ss, Genotype gg) {
		return get(ss,gg)[0]!=get(ss,gg)[1];
	}
}
