/* popgenIO
 * Copyright (c) 2011, 2012, 2013, Yee Whye Teh, Charles Blundell and Lloyd T. Elliott
 */

package popgenIO.Core;

import java.util.HashMap;
import java.util.Map;
import static libnp.util.Float.compareFloats;

public class Site implements Comparable {
	private int index;
	private Double position;
	private String name;
	private char[] alleles;

	public Site(int index) {
		this(index, null, null, new char[]{ '0', '1' });
	}
	
	public Site(int index, Double position) {
		this(index, position, null, new char[]{ '0', '1' });
	}

	public Site(int index, Double position, String name) {
		this(index, position, name, new char[]{ '0', '1' });
	}

	public Site(int index, Double position, String name, char[] alleles) {
		this.index = index;
		this.position = position;
		this.name = name;
		this.alleles = alleles;
	}

	public static Site[] copyArray(Site[] sites) {
		Site[] newSites = new Site[sites.length];
		int i = 0;
		for (Site site : sites) {
			Site newSite = new Site(i, site.position, site.name, site.alleles);
			newSites[i++] = newSite;
		}
		return newSites;
	}

	public int getIndex() {
		return index;
	}

	public double getPosition() {
		return position;
	}
	
	public String getName() {
		return name;
	}

	public char[] getAlleles() {
		return alleles;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}

	public void setPosition(double position) {
		this.position = position;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAlleles(char[] alleles){
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
		final Site other = (Site) obj;
		if (this.index != other.index) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(Object other) {
		if (position != null) {
			return ((Double)getPosition()).compareTo(
					(Double)(((Site)other).getPosition()));
		} else {
			return ((Integer)getIndex()).compareTo(
					(Integer)(((Site)other).getIndex()));
		}
	}
}
