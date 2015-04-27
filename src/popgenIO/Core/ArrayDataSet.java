/* popgenIO
 * Copyright (c) 2011, 2012, 2013, Yee Whye Teh, Charles Blundell and Lloyd T. Elliott
 */

package popgenIO.Core;

import java.util.List;
import java.io.Serializable;

public interface ArrayDataSet<X> extends Serializable {
	
	public abstract ArrayDataSet<X> clone();
	/**
	 * data[s] values: 0 : homozygous 0 1 : heterozygous 2 : homozygous 1 -1 :
	 * missing
	 * 
	 * @param name
	 * @param data
	 */
	public abstract Genotype addGenotype(String name);
	public abstract Genotype addGenotype(String name, X[] data);
	public abstract Genotype addGenotype(X[] data);

	public abstract Diplotype addDiplotype(String name);
	public abstract Diplotype addDiplotype(String name, X[] data);
	public abstract Diplotype addDiplotype(X[] data);

	public abstract Haplotype addHaplotype(String name);
	public abstract Haplotype addHaplotype(String name, X data);
	public abstract Haplotype addHaplotype(X data);
	public abstract Site addSite(GlobalSite site);
	
	public abstract int numSites();
	public abstract int numGenotypes();
	public abstract int numDiplotypes();
	public abstract int numHaplotypes();
	public abstract int numSequences();

	public abstract List<Site> getSites();
	public abstract List<Genotype> getGenotypes();
	public abstract List<Haplotype> getHaplotypes();
	public abstract List<Diplotype> getDiplotypes();

	public abstract Site getSite(String name);
	public abstract Genotype getGenotype(String name);
	public abstract Diplotype getDiplotype(String name);
	public abstract Haplotype getHaplotype(String name);
	
	public abstract Site localize(GlobalSite site);
	
	public abstract boolean isObserved(Site ss, Genotype gg);
	public abstract boolean isObserved(Site ss, Haplotype hh);
	public abstract boolean isObserved(Site ss, Diplotype dd);
	public abstract boolean isHeterozygous(Site ss, Genotype gg);

	public abstract void setObserved(Site ss, Genotype gg, boolean obs);
	public abstract void setObserved(Site ss, Haplotype hh, boolean obs);
	public abstract void setObserved(Site ss, Diplotype dd, boolean obs);

	public abstract X get(Site ss, Genotype gg);

	public abstract X get(Site ss, Diplotype dd);
	
	public abstract byte getAllele(Site ss, Diplotype dd, int phase);
	
	public abstract byte getAllele(Site ss, Haplotype hh);

	public abstract void set(Site ss, Genotype gg, X val);
	
	public abstract void set(Site ss, Diplotype dd, X val);

	public abstract void set(Site ss, Haplotype hh, byte val);

	public GenotypeValue getGenotypeValue(Site site, Genotype gg);


	public ArrayDataSet<X> combine(ArrayDataSet<X> other);
	public abstract ArrayDataSet<X> filter(List<GlobalSite> sites);
	public abstract ArrayDataSet<X> filterSequences(List<Sequence> sequences);

}
