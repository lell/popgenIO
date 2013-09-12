/* popgenIO
 * Copyright (c) 2011, 2012, 2013, Yee Whye Teh, Charles Blundell and Lloyd T. Elliott
 */

package popgenIO.Align;

import java.util.*;
import java.util.zip.GZIPInputStream;


import static libnp.util.Float.compareFloats;

import java.io.*;

import popgenIO.Paths;
import popgenIO.Core.DataSet;
import popgenIO.Core.Genotype;
import popgenIO.Core.Site;
import popgenIO.Core.GlobalSite;

public class Alignment {
	public static<T> DataSet<T> align(DataSet<T> gds) throws Exception {
		List<Integer> rsids = new ArrayList();
		Map<Integer, GlobalSite> rsidKeyed = new HashMap();

		for (Site site : gds.getSites()) {
			int rsid = getRsid(site.getName());
			if (rsid <= 0) {
				continue;
			}
			rsidKeyed.put(rsid, site.globalize());
			rsids.add(rsid);
		}

		Scanner scan = new Scanner(
				new BufferedReader(
				new InputStreamReader(
				new GZIPInputStream(
				new FileInputStream(
				Paths.getNcbiDirectory("b135_SNPChrPosOnRef_37_3.bcp.gz"))))));
		
		int sid = 0;
		while (sid < rsids.size() && scan.hasNext()) {
			String line = scan.nextLine();
			String[] cols = line.trim().split("\\s+");
			int rsid = Integer.valueOf(cols[0]);
			double position = Double.valueOf(cols[2]);
			
			if (rsid == rsids.get(sid)) {
				rsidKeyed.get(rsid).setPosition(position);
				sid++;
			} else if (rsid > rsids.get(sid)) {
				rsidKeyed.remove(rsid);
				sid++;
			}
		}
		
		List<GlobalSite> sites = new ArrayList();
		sites.addAll(rsidKeyed.values());
		
		Collections.sort(sites);
		return gds.filter(sites);
	}

	private static int getRsid(String s) {
		int rsid;
		try {
			rsid = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			rsid = -1;
		}
		
		if (rsid > 0) {
			return rsid;
		}
		
		if (!s.startsWith("rs") || !s.substring(2).matches("^\\d+$")) {
			return -1;
		} else {
			return Integer.parseInt(s.substring(2));
		}
	}
}
