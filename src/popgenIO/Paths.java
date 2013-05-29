/* popgenIO
 * Copyright (c) 2011, 2012, 2013, Yee Whye Teh, Charles Blundell and Lloyd T. Elliott
 */

package popgenIO;

public class Paths {

	public static String getHome() {
		return System.getProperty("user.home");
	}
	
	public static String getNcbiDirectory() {
		return getHome() + "/data/ncbi/";
	}

	public static String getNcbiDirectory(String filename) {
		return getNcbiDirectory() + filename;
	}

	public static String getSeattleDirectory() {
		return getHome() + "/data/seattle/";
	}

	public static String getSeattleDirectory(String filename) {
		return getSeattleDirectory() + filename;
	}

	public static String getExperimentDirectory() {
		return getHome() + "/data/experiment/";
	}

	public static String getExperimentsDirectory(String filename) {
		return getExperimentDirectory() + filename;
	}

	public static String getTestDirectory() {
		return getHome() + "/test/";
	}
	
	public static String getTestDirectory(String filename) {
		return getTestDirectory() + filename;
	}

}
