/* popgenIO
 * Copyright (c) 2011, 2012, 2013, Yee Whye Teh, Charles Blundell and Lloyd T. Elliott
 */

package popgenIO.Manager;

import popgenIO.Core.DataSet;
import popgenIO.Core.Diplotype;
import popgenIO.Core.Genotype;
import popgenIO.Core.Haplotype;
import popgenIO.Core.Site;

import java.io.Serializable;

public interface Manager<X> extends Serializable {

	public DataSet<X> getAllData();

	public DataSet<X> getTrainingSet();

	public DataSet<X> getTestSet();

	public DataSet<X> getPredictedSet();

	public double getChanceAccuracy();

	public double getPredictionAccuracy();

	public double getPredictionAccuracy(DataSet<X> predictions);

	public void setPredictionMethod(int method);

	public int getPredictionMethod();

	public X[] getPrediction(Site ss, Genotype gg);
	
	public X[] getPrediction(Site ss, Diplotype dd);

	public X getPrediction(Site ss, Haplotype hh);

	public double[] getProbabilities(Site ss, Genotype gg);

	double[] getProbabilities(Site ss, Diplotype dd);

	public double[] getProbabilities(Site ss, Haplotype hh);

	public boolean isPredictable(Site ss, Genotype gg);

	public boolean isPredictable(Site ss, Diplotype dd);

	public boolean isPredictable(Site ss, Haplotype hh);

	public boolean isPredictable(Genotype gg);

	public boolean isPredictable(Diplotype gg);

	public boolean isPredictable(Haplotype hh);

	public int numPredictableGenotypes();

	public int numPredictableHaplotypes();

	public void collect(Site ss, Genotype gg, double homo0, double hetero,
			double homo1);

	public void collect(Site ss, Haplotype hh, double prob0, double prob1);

	public double[] getAllelicR2(int[] numSamples);

	public void getConcordance(int[] levels, int[] correct, int[] counts);

	public double getMeanAllele(Site ss);

}
