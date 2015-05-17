/* popgenIO
 * Copyright (c) 2011, 2012, 2013, Yee Whye Teh, Charles Blundell and Lloyd T. Elliott
 */

package popgenIO.Core;

import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

import static libnp.util.Float.compareFloats;

public abstract class AbstractSite implements Comparable, Serializable {
	private double position;
	private String name;
	private byte[] alleles;

	public double getPosition() {
		return position;
	}
	
	public String getName() {
		return name;
	}

	public byte[] getAlleles() {
		return alleles;
	}

	public void setPosition(double position) {
		this.position = position;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAlleles(byte[] alleles){
		this.alleles = alleles;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		final AbstractSite other = (AbstractSite) obj;
		if (this.name != other.getName()) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(Object obj) {
		final AbstractSite other = (AbstractSite) obj;	
		return ((Double)position).compareTo((Double)(other.getPosition()));
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
