/* popgenIO
 * Copyright (c) 2011, 2012, 2013, Yee Whye Teh, Charles Blundell and Lloyd T. Elliott
 */

package popgenIO.Core;

public class GenotypeValue {
	public boolean isUnobserved;
	public boolean isObserved;
	public boolean isHomozygous;
	public boolean isHeterozygous;
	public int allele;
	public GenotypeValue(int allele) {
		this.allele = allele;
		switch(allele) {
		case -1:
			this.isUnobserved = true;
			this.isObserved = false;
			this.isHomozygous = false;
			this.isHeterozygous = false;
			break;
			
		case 0:
			this.isUnobserved = false;
			this.isObserved = true;
			this.isHomozygous = true;
			this.isHeterozygous = false;
			break;
			
		case 1:
			this.isUnobserved = false;
			this.isObserved = true;
			this.isHomozygous = false;
			this.isHeterozygous = true;
			break;
			
		case 2:
			this.isUnobserved = false;
			this.isObserved = true;
			this.isHomozygous = true;
			this.isHeterozygous = false;
			break;
			
		default:
			System.err.println("Unknown allele: " + allele);
			System.exit(-1);
			break;
		}
	}
}
