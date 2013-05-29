/* popgenIO
 * Copyright (c) 2011, 2012, 2013, Yee Whye Teh, Charles Blundell and Lloyd T. Elliott
 */

package popgenIO.Core;

public class Diplotype extends SequenceImpl {
	Diplotype(String name, int index) {
		super(name, index);
	}

	@Override
	public boolean isHaplotype() {
		return false;
	}

	@Override
	public boolean isGenotype() {
		return false;
	}

	@Override
	public boolean isDiplotype() {
		return true;
	}
	
	@Override
	public Diplotype clone() {
		return new Diplotype(this.getName(), this.getIndex());
	}
}

