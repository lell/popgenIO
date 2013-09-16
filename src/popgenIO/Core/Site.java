/* popgenIO
 * Copyright (c) 2011, 2012, 2013, Yee Whye Teh, Charles Blundell and Lloyd T. Elliott
 */

package popgenIO.Core;

import java.util.HashMap;
import java.util.Map;

import java.io.Serializable;

import static java.lang.Math.round;

import static libnp.util.Float.compareFloats;

public class Site extends AbstractSite implements Serializable {
	private int index;

	public Site(int index) {
		this(index, index, null, null);
		assert getName() != null;
	}
	
	public Site(int index, double position) {
		this(index, position, null, null);
		assert getName() != null;
	}

	public Site(int index, double position, String name) {
		this(index, position, name, null);
		assert getName() != null;
	}

	public Site(int index, GlobalSite site) {
		this(index, site.getPosition(), site.getName(), site.getAlleles());
		assert getName() != null;
	}
	
	public Site(int index, double position, String name, char[] alleles) {
		if (name == null) {
			if (compareFloats(position, round(position), 1e-10) == 0) {
				name = String.format("S%010d", (int)position);
			} else {
				name = String.format("S%3.7f", position);
			}
		}
		
		if (alleles == null) {
			alleles = new char[]{ '0', '1' };
		}
		this.setPosition(position);
		this.setName(name);
		this.setAlleles(alleles);
		assert index >= 0;
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
	
	public GlobalSite globalize() {
		return new GlobalSite(getPosition(), getName(), getAlleles());
	}
}
