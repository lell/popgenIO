/* popgenIO
 * Copyright (c) 2011, 2012, 2013, Yee Whye Teh, Charles Blundell and Lloyd T. Elliott
 */

package popgenIO.Core;

import java.util.HashMap;
import java.util.Map;
import static libnp.util.Float.compareFloats;

public class GlobalSite extends AbstractSite implements Comparable {
	public GlobalSite(double position, String name, char[] alleles) {
		assert position >= 0;
		assert name != null;
		assert alleles != null;
		assert alleles.length > 0; // must be polymorphic
		this.setPosition(position);
		this.setName(name);
		this.setAlleles(alleles);
	}
}
