/* popgenIO
 * Copyright (c) 2011, 2012, 2013, Yee Whye Teh, Charles Blundell and Lloyd T. Elliott
 */

package popgenIO.Core;

public class GenotypeIntValue {
	public boolean[] isUnobserved;
	public boolean isHomozygous;
	public int[] alleles;
	public GenotypeIntValue(int[] alleles) {
		this.alleles = alleles;
		isUnobserved = new boolean[alleles.length];
		isHomozygous = true;
		int last_seen_allele=-1;

		if(alleles[0]==-1) {
			this.isUnobserved[0] = true;
		} else last_seen_allele = alleles[0];
		for (int i = 1; i < alleles.length; i++) {
			if(alleles[i]==-1)
				this.isUnobserved[i] = true;
			else if(last_seen_allele==-1) {
				last_seen_allele=alleles[i];
			} else if(alleles[i]!=last_seen_allele) {
				isHomozygous=false;
			} 
		}

	}
}
