/* popgenIO
 * Copyright (c) 2011, 2012, 2013, Yee Whye Teh, Charles Blundell and Lloyd T. Elliott
 */

package popgenIO.Core;

public class Haplotype extends SequenceImpl {
	Haplotype(String name, int index) {
		super(name, index);
	}

	@Override
	public boolean isHaplotype() {
		return true;
	}

	@Override
	public boolean isGenotype() {
		return false;
	}

	@Override
	public boolean isDiplotype() {
		return false;
	}
	
	@Override
	public Haplotype clone() {
		return new Haplotype(this.getName(), this.getIndex());
	}
}
