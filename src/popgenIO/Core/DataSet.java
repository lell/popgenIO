/* popgenIO
 * Copyright (c) 2011, 2012, 2013, Yee Whye Teh, Charles Blundell and Lloyd T. Elliott
 */

package popgenIO.Core;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.io.Serializable;

public interface DataSet<X> extends Serializable {
	
	public abstract DataSet<X> clone();
	/**
	 * data[s] values: 0 : homozygous 0 1 : heterozygous 2 : homozygous 1 -1 :
	 * missing
	 * 
	 * @param name
	 * @param data
	 */
	public abstract Genotype addGenotype(String name, X[][] data);
	public abstract Genotype addGenotype(X[][] data);

	public abstract Diplotype addDiplotype(String name, X[][] data);
	public abstract Diplotype addDiplotype(X[][] data);
	
	public abstract Haplotype addHaplotype(String name, X[] data);
	public abstract Haplotype addHaplotype(X[] data);
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

	public abstract X[] get(Site ss, Genotype gg);

	public abstract X[] get(Site ss, Diplotype dd);
	
	public abstract X get(Site ss, Diplotype dd, int phase);
	
	public abstract X get(Site ss, Haplotype hh);

	public abstract void set(Site ss, Genotype gg, X[] val);
	
	public abstract void set(Site ss, Diplotype dd, X[] val);

	public abstract void set(Site ss, Haplotype hh, X val);

	public GenotypeValue getGenotypeValue(Site site, Genotype gg);


	public DataSet<X> combine(DataSet<X> other);
	public abstract DataSet<X> filter(List<GlobalSite> sites);
	public abstract DataSet<X> filterSequences(List<Sequence> sequences);

}
