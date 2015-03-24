/* popgenIO
 * Copyright (c) 2011, 2012, 2013, Yee Whye Teh, Charles Blundell and Lloyd T. Elliott
 */

package popgenIO.Core;

public class GenotypeValue {
	public boolean isObserved;
	public boolean isHomozygous;
	public int allele;
	public GenotypeValue(int allele) {
		this.allele = allele;
		switch(allele) {
		case -1:
			this.isObserved = false;
			this.isHomozygous = false;
			break;
			
		case 0:
			this.isObserved = true;
			this.isHomozygous = true;
			break;
			
		case 1:
			this.isObserved = true;
			this.isHomozygous = false;
			break;
			
		case 2:
			this.isObserved = true;
			this.isHomozygous = true;
			break;
			
		default:
			System.err.println("Unknown allele: " + allele);
			System.exit(-1);
			break;
		}
	}
}
