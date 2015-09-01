/* popgenIO
 * Copyright (c) 2011, 2012, 2013, Yee Whye Teh, Charles Blundell and Lloyd T. Elliott
 */

package popgenIO.Core;

import java.util.BitSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;
import java.io.Serializable;

public class IntDataSet implements ArrayDataSet<byte[]>, Serializable {
	private static final long serialVersionUID = -6752732948863408884L;
	byte[][] allele = null;
	int numsequences = 0;
	int numsites = 0;
	int index;
	int site_index = 0;
	private int max_alleles_at_any_site = 0;
	private int maxPosition=Integer.MAX_VALUE, minPosition=0;

	SortedMap<String, Site> sites;
	LinkedHashMap<String, Genotype> genotypes;
	LinkedHashMap<String, Diplotype> diplotypes;
	LinkedHashMap<String, Haplotype> haplotypes;

	List<Site> sss;;
	List<Haplotype> hhs;
	List<Diplotype> dds;
	List<Genotype> ggs;

	public IntDataSet(int numsites, int numsequences, int minPosition, int maxPosition) {
		this.numsites = numsites;
		this.numsequences = numsequences;
		this.minPosition=minPosition;
		this.maxPosition=maxPosition;
		allele = new byte[numsequences][numsites];
		sites = new TreeMap();
		sss = new ArrayList<Site>();
		genotypes = new LinkedHashMap();	
		diplotypes = new LinkedHashMap();		
		haplotypes = new LinkedHashMap();
		hhs = new ArrayList<Haplotype>();
		dds = new ArrayList<Diplotype>();
		ggs = new ArrayList<Genotype>();
	}

	public IntDataSet(IntDataSet data) {
		this(data.numsites, data.numsequences, data.getMinPosition(), data.getMaxPosition());

		this.max_alleles_at_any_site = data.max_alleles_at_any_site;
		this.allele = new byte[data.numsequences][data.numsites];
		for (int i = 0; i < data.allele.length; i++) {
			this.allele[i]=data.allele[i];
		}

		for (Site site : data.getSites()) {
			addSite(site.globalize());
		}

		for (Genotype genotype : data.getGenotypes()) {
			Genotype cloned = genotype.clone();
			genotypes.put(genotype.getName(), cloned);
			ggs.add(cloned);
		}
		for (Diplotype diplotype: data.getDiplotypes()) {
			Diplotype cloned = diplotype.clone();
			diplotypes.put(diplotype.getName(), cloned);
			dds.add(cloned);
		}
		for (Haplotype haplotype : data.getHaplotypes()) {
			Haplotype cloned = haplotype.clone();
			haplotypes.put(haplotype.getName(), cloned);
			hhs.add(cloned);
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
		return ggs;
	}

	@Override
	public List<Diplotype> getDiplotypes() {
		return dds;
	}

	@Override
	public List<Haplotype> getHaplotypes() {
		return hhs;
	}

	@Override
	public List<Site> getSites() {
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
		if(localized.getAlleles().length>max_alleles_at_any_site)
			max_alleles_at_any_site = localized.getAlleles().length;
		sites.put(site.getName(), localized);
		sss.add(localized);
		return localized;
	}

	@Override
	public IntDataSet clone() {
		return new IntDataSet(this);
	}

	public static IntDataSet unobserved(int T, int N, int min, int max) {
		IntDataSet data = new IntDataSet(T, N, min, max);
		for (int t = 0; t < T; t++) {
			data.addSite(new Site(t).globalize());
		}

		for (int i = 0; i < N; i++) {
			byte[] haplotype = new byte[T];
			for (int t = 0; t < T; t++) {
				haplotype[t] = -1;
			}
			data.addHaplotype(haplotype);
		}
		return data;
	}

	final private boolean isObserved(int individual, int site) {
		return allele[individual][site]!=-1;
	}

	final int index(int sid, int qid) {
		assert sid >= 0 && sid < numsites;
		assert qid >= 0 && qid < numsequences;
		return sid + (qid * numSites());
	}

	final private byte getAllele(int individual, int site) {
		return allele[individual][site];
	}

	final private void setAllele(int individual, int site, byte aa) {
		allele[individual][site]=aa;
	}

	/*final private void addAllele(int sid, int qid, int aa) {
		if (aa < 0) {
			setObserved(sid, qid, false);
		} else {
			allele[sid][qid]=aa;
			setObserved(sid, qid, true);
		}
	}*/


	/*final private void setObserved(int sid, int qid, boolean aa) {
		observed.set(index(sid, qid), aa);
	}*/

	@Override
	public Genotype addGenotype(String name) {
		Genotype genotype = new Genotype(name, index);
		index += 2;
		genotypes.put(name, genotype);
		ggs.add(genotype);
		return genotype;
	}

	@Override
	public Haplotype addHaplotype(String name) {
		Haplotype haplotype = new Haplotype(name, index);
		index ++;
		haplotypes.put(name, haplotype);
		hhs.add(haplotype);
		return haplotype;
	}

	@Override
	public Diplotype addDiplotype(String name) {
		Diplotype diplotype = new Diplotype(name, index);
		index += 2;
		diplotypes.put(name, diplotype);
		dds.add(diplotype);
		return diplotype;
	}

	@Override
	public Genotype addGenotype(String name, byte[][] data) {
		// expand the allele array by the size of this sequence
		expandArrayByX(allele,2);
		if (name == null) {
			name = String.format("G%06d", index);
		}
		Genotype genotype = new Genotype(name, index);
		for (int t = 0; t < numsites; t++) {
			setAllele(index, t, data[t][0]);
			setAllele(index+1, t, data[t][1]);
		}
		index += 2;
		genotypes.put(name, genotype);
		ggs.add(genotype);
		return genotype;
	}

	@Override
	public Genotype addGenotype(byte[][] data) {
		return addGenotype(null, data);
	}

	@Override
	public Diplotype addDiplotype(String name, byte[][] data) {
		// expand the allele array by the size of this sequence
		expandArrayByX(allele,2);
		if (name == null) {
			name = String.format("D%06d", index);
		}
		Diplotype diplotype = new Diplotype(name, index);
		for (int t = 0; t < numsites; t++) {
			setAllele(index, t, data[t][0]);
			setAllele(index+1, t, data[t][1]);
		}
		index += 2;
		diplotypes.put(name, diplotype);
		dds.add(diplotype);
		return diplotype;
	}

	private void expandArrayByX(byte[][] array, int x) {
		byte[][] newArray = new byte[array.length+x][array[0].length];
		for (int i = 0; i < array.length; i++) {
			System.arraycopy(array[i], 0, newArray[i], 0, array[0].length);			
		}
		array=newArray;
	}

	@Override
	public Diplotype addDiplotype(byte[][] data) {
		return addDiplotype(null, data);
	}

	@Override
	public Haplotype addHaplotype(String name, byte[] data) {
		// expand the allele array by the size of this sequence
		expandArrayByX(allele,1);
		if (name == null) {
			name = String.format("H%06d", index);
		}
		Haplotype haplotype = new Haplotype(name, index);
		for (int t = 0; t < numsites; t++) {
			setAllele(index, t, data[t]);
		}
		index += 1;
		haplotypes.put(name, haplotype);
		hhs.add(haplotype);
		return haplotype;
	}

	@Override
	public Haplotype addHaplotype(byte[] data) {
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
		return isObserved(gg.getIndex(),ss.getIndex());
	}

	@Override
	public boolean isObserved(Site ss, Haplotype hh) {
		return isObserved(hh.getIndex(),ss.getIndex());
	}

	@Override
	public boolean isObserved(Site ss, Diplotype dd) {
		return isObserved(dd.getIndex(),ss.getIndex());
	}

	@Override
	public void setObserved(Site ss, Genotype gg, boolean obs) {
		if(!obs) {
			allele[ss.getIndex()][gg.getIndex()]=-1;
			allele[ss.getIndex()][gg.getIndex()+1]=-1;
		}

	}

	@Override
	public void setObserved(Site ss, Haplotype hh, boolean obs) {
		if(!obs)
			allele[ss.getIndex()][hh.getIndex()]=-1;
	}

	@Override
	public void setObserved(Site ss, Diplotype dd, boolean obs) {
		if(!obs) {
			allele[ss.getIndex()][dd.getIndex()]=-1;
			allele[ss.getIndex()][dd.getIndex()+1]=-1;
		}
	}

	@Override
	public byte[] get(Site ss, Genotype gg) {		
		return new byte[]{
				getAllele( gg.getIndex(),ss.getIndex()),
				getAllele(gg.getIndex()+1,ss.getIndex())};
	}

	@Override
	public byte[] get(Site ss, Diplotype dd) {
		return new byte[]{
				getAllele( dd.getIndex(),ss.getIndex()),
				getAllele(dd.getIndex()+1,ss.getIndex())};		
	}

	@Override
	public byte getAllele(Site ss, Diplotype dd, int phase) {
		return getAllele( dd.getIndex()+phase,ss.getIndex());
	}

	@Override
	public byte getAllele(Site ss, Haplotype hh) {
		return getAllele(hh.getIndex(),ss.getIndex());
	}

	@Override
	public void set(Site ss, Genotype gg, byte[] val) {
		if (val == null) {
			setObserved(ss, gg, false);
		} else {
			setAllele(gg.getIndex(),sites.get(ss.getName()).getIndex(),val[0]);
			setAllele(gg.getIndex()+1,sites.get(ss.getName()).getIndex(), val[1]);
		}


	}

	@Override
	public void set(Site ss, Diplotype dd, byte[] val) {
		if (val == null) {
			setObserved(ss, dd, false);
		} else {
			setAllele(dd.getIndex(), sites.get(ss.getName()).getIndex(), val[0]);
			setAllele(dd.getIndex()+1, sites.get(ss.getName()).getIndex(), val[1]);
		}

	}

	@Override
	public void set(Site ss, Haplotype hh, byte val) {
		setAllele(hh.getIndex(), sites.get(ss.getName()).getIndex(), val);
	}

	@Override
	public GenotypeValue getGenotypeValue(Site ss, Genotype gg) {
		if (!isObserved(ss, gg)) {
			return new GenotypeValue(-1);
		} else {
			return new GenotypeValue((getAllele(gg.getIndex(),ss.getIndex()))
					+ (getAllele(gg.getIndex()+1,ss.getIndex())));
		}
	}

	@Override
	public ArrayDataSet<byte[]> combine(ArrayDataSet<byte[]> other) {
		SortedSet<GlobalSite> combined = new TreeSet();
		Set<String> addedSites = new HashSet<String>();

		int minPosition = Math.min(this.getMinPosition(), other.getMinPosition());
		int maxPosition = Math.max(this.getMaxPosition(), other.getMaxPosition());

		for (Site site : getSites()) {
			if(!addedSites.contains(site.getName())) {
				addedSites.add(site.getName());
				site.setPosition(((((double)this.getMinPosition()+((double)this.getMaxPosition()-(double)this.getMinPosition())*site.getPosition()))-(double)minPosition)/(double)(maxPosition-minPosition));
				combined.add(site.globalize());
			}
		}
		
		for (Site site : other.getSites()) {
			if(!addedSites.contains(site.getName())) {
				addedSites.add(site.getName());
				site.setPosition(((((double)other.getMinPosition()+((double)other.getMaxPosition()-(double)other.getMinPosition())*site.getPosition()))-(double)minPosition)/(double)(maxPosition-minPosition));
				combined.add(site.globalize());
			}
		}

		Set<Haplotype> merged_ids = new HashSet<Haplotype>();
		merged_ids.addAll(this.getHaplotypes());
		merged_ids.addAll(other.getHaplotypes());


		Set<Genotype> merged_genotype_ids = new HashSet<Genotype>();
		merged_genotype_ids.addAll(this.getGenotypes());
		merged_genotype_ids.addAll(other.getGenotypes());

		Set<Diplotype> merged_diplotype_ids = new HashSet<Diplotype>();
		merged_diplotype_ids.addAll(this.getDiplotypes());
		merged_diplotype_ids.addAll(other.getDiplotypes());

		ArrayDataSet<byte[]> data = new IntDataSet(combined.size(), 
				merged_ids.size() + 2*merged_genotype_ids.size() + 2*merged_diplotype_ids.size(),minPosition,maxPosition);

		for (GlobalSite site : combined) {
			data.addSite(site);
		}		

		for (Haplotype hh : getHaplotypes()) {
			byte[] x = new byte[combined.size()];
			for (int t = 0 ; t < combined.size(); t++) {
				x[t] = -1;
			}
			for (GlobalSite site : combined) {
				Site ss = localize(site);
				if (ss != null) {
					x[data.localize(site).getIndex()] = getAllele(ss, hh);
				}
			}
			data.addHaplotype(hh.getName(), x); 
		}
		
		for (Diplotype dd : getDiplotypes()) {
			byte[][] x = new byte[combined.size()][2];
			for (int t = 0 ; t < combined.size(); t++) {
				x[t] = new byte[] { -1, -1 };
			}
			for (GlobalSite site : combined) {
				Site ss = localize(site);
				if (ss != null) {
					x[data.localize(site).getIndex()] = get(ss, dd);
				}
			}
			data.addDiplotype(dd.getName(), x);
		}
		
		for (Genotype gg : getGenotypes()) {
			byte[][] x = new byte[combined.size()][2];
			for (int t = 0 ; t < combined.size(); t++) {
				x[t] = new byte[] { -1, -1 };
			}

			for (GlobalSite site : combined) {
				Site ss = localize(site);
				if (ss != null) {
					x[data.localize(site).getIndex()] = get(ss, gg);
				}
			}
			data.addGenotype(gg.getName(), x);
		}		

		for (Haplotype hh : other.getHaplotypes()) {
			if(!this.haplotypes.containsKey(hh.getName())) {
				byte[] x = new byte[combined.size()];
				for (int t = 0 ; t < combined.size(); t++) {
					x[t] = -1;
				}
				for (GlobalSite site : combined) {
					Site ss = other.localize(site);
					if (ss != null) {
						x[data.localize(site).getIndex()] = other.getAllele(ss, hh);

					}
				}
				data.addHaplotype(hh.getName(), x);
			} else {
				for (GlobalSite site : combined) {
					Site ss = other.localize(site);
					if (ss != null) {
						data.set(ss, hh, other.getAllele(ss, hh));
					}
				}
			}
		}
		
		for (Diplotype dd : other.getDiplotypes()) {
			byte[][] x = new byte[combined.size()][2];
			for (int t = 0 ; t < combined.size(); t++) {
				x[t] = new byte[] { -1, -1 };
			}
			for (GlobalSite site : combined) {
				Site ss = other.localize(site);
				if (ss != null) {
					x[data.localize(site).getIndex()] = other.get(ss, dd);
				}
			}
			data.addDiplotype(dd.getName(), x);
		}
		
		
		for (Genotype gg : other.getGenotypes()) {
			if(!this.genotypes.containsKey(gg.getName())) {
				byte[][] x = new byte[combined.size()][2];
				for (int t = 0 ; t < combined.size(); t++) {
					x[t] = new byte[] { -1, -1 };
				}
				for (GlobalSite site : combined) {
					Site ss = other.localize(site);
					if (ss != null) {
						x[data.localize(site).getIndex()] = other.get(ss, gg);
					}
				}
				data.addGenotype(gg.getName(), x);
			} else {
				for (GlobalSite site : combined) {
					Site ss = other.localize(site);
					if (ss != null) {
						data.set(ss, gg, other.getGenotypeValue(ss, gg).getGenotype());
					}
				}
			}
		}

		int sind = 0;
		for (GlobalSite site : combined) {
			data.localize(site).setIndex(sind++);
		}

		return data;
	}

	@Override
	public ArrayDataSet<byte[]> filterSequences(List<Sequence> sequences) {
		IntDataSet data = new IntDataSet(this.numSites(), sequences.size(),this.minPosition, this.maxPosition);
		for (Site site : this.getSites()) {
			data.addSite(site.globalize());
		}

		for (Sequence hh : sequences) {
			byte[] x = new byte[data.numSites()];
			for (Site site : getSites()) {
				Haplotype other = this.getHaplotype(hh.getName());
				Site ss = data.localize(site.globalize());
				x[ss.getIndex()] = getAllele(site, other);
			}
			data.addHaplotype(hh.getName(), x);
		}
		return data;

	}

	@Override
	public ArrayDataSet<byte[]> filter(List<GlobalSite> filtered_sites) {
		IntDataSet other = new IntDataSet(filtered_sites.size(), this.numSequences(),this.minPosition, this.maxPosition);
		for (GlobalSite site : filtered_sites) {
			other.addSite(site);
		}

		for (Genotype gg : getGenotypes()) {
			byte x[][] = new byte[other.numSites()][2];
			for (Site site : other.getSites()) {
				Site ss = localize(site.globalize());
				x[site.getIndex()] = get(ss, gg);
			}
			other.addGenotype(gg.getName(), x);
		}
		for (Diplotype dd : getDiplotypes()) {
			byte x[][] = new byte[other.numSites()][2];
			for (Site site : other.getSites()) {
				Site ss = localize(site.globalize());
				x[site.getIndex()] = get(ss, dd);
			}
			other.addDiplotype(dd.getName(), x);
		}

		for (Haplotype hh : getHaplotypes()) {
			byte x[] = new byte[other.numSites()];
			for (Site site : other.getSites()) {
				Site ss = localize(site.globalize());
				x[site.getIndex()] = getAllele(ss, hh);
			}
			other.addHaplotype(hh.getName(), x);
		}
		return other;
	}

	@Override
	public boolean isHeterozygous(Site ss, Genotype gg) {
		return get(ss,gg)[0]!=get(ss,gg)[1];
	}

	public int getMaxAlleles() {
		return max_alleles_at_any_site;
	}

	/* (non-Javadoc)
	 * @see popgenIO.Core.ArrayDataSet#setMinPosition(int)
	 */
	@Override
	public void setMinPosition(int pos) {
		this.minPosition=pos;
	}

	/* (non-Javadoc)
	 * @see popgenIO.Core.ArrayDataSet#setMaxPosition(int)
	 */
	@Override
	public void setMaxPosition(int pos) {
		this.maxPosition=pos;
	}

	/* (non-Javadoc)
	 * @see popgenIO.Core.ArrayDataSet#getMinPosition()
	 */
	@Override
	public int getMinPosition() {
		return minPosition;
	}

	/* (non-Javadoc)
	 * @see popgenIO.Core.ArrayDataSet#getMaxPosition()
	 */
	@Override
	public int getMaxPosition() {
		return maxPosition;
	}

	/* (non-Javadoc)
	 * @see popgenIO.Core.ArrayDataSet#getAlleleFreq(int, byte)
	 */
	@Override
	public float getAlleleFreq(int site, byte allele) {
		float cnt = 0f, all = 0f;
		for (int i = 0; i < this.allele.length; i++) {
			if(this.allele[i][site]==allele)
				cnt++;
			all++;
		}
		return cnt/all;
	}
}
