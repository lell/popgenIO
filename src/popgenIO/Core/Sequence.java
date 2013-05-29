/* popgenIO
 * Copyright (c) 2011, 2012, 2013, Yee Whye Teh, Charles Blundell and Lloyd T. Elliott
 */

package popgenIO.Core;

import java.io.Serializable;

public interface Sequence extends Comparable<Sequence>, Serializable {

	public boolean isHaplotype();

	public boolean isGenotype();

	public boolean isDiplotype();

	public String getName();

	public void setName(String name);

	public int getIndex();

	public void setIndex(int index);

}
