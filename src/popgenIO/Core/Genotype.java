/* popgenIO
 * Copyright (c) 2011, 2012, 2013, Yee Whye Teh, Charles Blundell and Lloyd T. Elliott
 */

package popgenIO.Core;

import java.io.Serializable;

public class Genotype extends SequenceImpl implements Serializable {
	Genotype(String name, int index) {
		super(name, index);
	}

	@Override
	public boolean isHaplotype() {
		return false;
	}

	@Override
	public boolean isGenotype() {
		return true;
	}

	@Override
	public boolean isDiplotype() {
		return false;
	}
	
	@Override
	public Genotype clone() {
		return new Genotype(this.getName(), this.getIndex());
	}

}
