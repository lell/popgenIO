/* popgenIO
 * Copyright (c) 2011, 2012, 2013, Yee Whye Teh, Charles Blundell and Lloyd T. Elliott
 */

package popgenIO.Core;

import java.io.Serializable;

public abstract class SequenceImpl implements Comparable<Sequence>, Sequence, Serializable, Cloneable {
	private String name;
	private int index;

	SequenceImpl(String name, int index) {
		this.name = name;
		this.index = index;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SequenceImpl other = (SequenceImpl) obj;
		if (this.index != other.index) {
			return false;
		}
		return true;
	}


	@Override
	public int compareTo(Sequence ss) {
		assert ss != null;
		assert getClass() == ss.getClass();
		return ((Integer) index).compareTo(ss.getIndex());
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
