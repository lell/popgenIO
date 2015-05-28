/* popgenIO
 * Copyright (c) 2011, 2012, 2013, Yee Whye Teh, Charles Blundell and Lloyd T. Elliott
 */

package popgenIO.Manager;

import java.util.*;
import java.io.Serializable;

import popgenIO.Core.ArrayDataSet;
import popgenIO.Core.DataSet;
import popgenIO.Core.Diplotype;
import popgenIO.Core.Genotype;
import popgenIO.Core.Haplotype;
import popgenIO.Core.Site;


abstract class AbstractIntManager implements ArrayManager<byte[]>, Cloneable, Serializable {
	protected ArrayDataSet<byte[]> allData;
	// these are the prediction probabilities for genotypes+haplotypes/sites.
	private HashMap<Genotype, HashMap<Site, Double>> probgenotype00,
			probgenotype11, probgenotype01;
	
	private HashMap<Diplotype, HashMap<Site, Double>> probdiplotype00,
		 probdiplotype01, probdiplotype10, probdiplotype11;
	
	private HashMap<Haplotype, HashMap<Site, Double>> probhaplotype1,
			probhaplotype0;

	public int predictionMethod = 1;

	public AbstractIntManager(ArrayDataSet<byte[]> dataset) {
		allData = dataset;
		clearPredictions();
	}

	@Override
	protected AbstractIntManager clone() {
		try {
			AbstractIntManager other = (AbstractIntManager) super.clone();
			other.allData = this.allData.clone();
			other.probgenotype00 = (HashMap<Genotype, HashMap<Site, Double>>) this.probgenotype00
					.clone();
			
			for (Genotype gg : other.probgenotype00.keySet()) {
				other.probgenotype00.put(gg,
						(HashMap<Site, Double>) this.probgenotype00.get(gg)
								.clone());
			}
			other.probgenotype01 = (HashMap<Genotype, HashMap<Site, Double>>) this.probgenotype01
					.clone();
			for (Genotype gg : other.probgenotype01.keySet()) {
				other.probgenotype01.put(gg,
						(HashMap<Site, Double>) this.probgenotype01.get(gg)
								.clone());
			}
			other.probgenotype11 = (HashMap<Genotype, HashMap<Site, Double>>) this.probgenotype11
					.clone();
			for (Genotype gg : other.probgenotype11.keySet()) {
				other.probgenotype11.put(gg,
						(HashMap<Site, Double>) this.probgenotype11.get(gg)
								.clone());
			}
			
			other.probdiplotype00 = (HashMap<Diplotype, HashMap<Site, Double>>) this.probdiplotype00
					.clone();
			
			for (Diplotype gg : other.probdiplotype00.keySet()) {
				other.probdiplotype00.put(gg,
						(HashMap<Site, Double>) this.probdiplotype00.get(gg)
								.clone());
			}
			other.probdiplotype01 = (HashMap<Diplotype, HashMap<Site, Double>>) this.probdiplotype01
					.clone();
			for (Diplotype gg : other.probdiplotype01.keySet()) {
				other.probdiplotype01.put(gg,
						(HashMap<Site, Double>) this.probdiplotype01.get(gg)
								.clone());
			}
			other.probdiplotype10 = (HashMap<Diplotype, HashMap<Site, Double>>) this.probdiplotype10
					.clone();
			for (Diplotype gg : other.probdiplotype10.keySet()) {
				other.probdiplotype10.put(gg,
						(HashMap<Site, Double>) this.probdiplotype10.get(gg)
								.clone());
			}
			
			other.probdiplotype11 = (HashMap<Diplotype, HashMap<Site, Double>>) this.probdiplotype11
					.clone();
			for (Diplotype gg : other.probdiplotype11.keySet()) {
				other.probdiplotype11.put(gg,
						(HashMap<Site, Double>) this.probdiplotype11.get(gg)
								.clone());
			}
			
			
			other.probhaplotype0 = (HashMap<Haplotype, HashMap<Site, Double>>) this.probhaplotype0
					.clone();
			for (Haplotype hh : other.probhaplotype0.keySet()) {
				other.probhaplotype0.put(hh,
						(HashMap<Site, Double>) this.probhaplotype0.get(hh)
								.clone());
			}
			other.probhaplotype1 = (HashMap<Haplotype, HashMap<Site, Double>>) this.probhaplotype1
					.clone();
			for (Haplotype hh : other.probhaplotype1.keySet()) {
				other.probhaplotype1.put(hh,
						(HashMap<Site, Double>) this.probhaplotype1.get(hh)
								.clone());
			}
			return other;
		} catch (CloneNotSupportedException e) {
			throw new Error("clone is not supported?!");
		}
	}

	protected void clearPredictions() {
		probgenotype00 = new HashMap<Genotype, HashMap<Site, Double>>();
		probgenotype11 = new HashMap<Genotype, HashMap<Site, Double>>();
		probgenotype01 = new HashMap<Genotype, HashMap<Site, Double>>();
		for (Genotype gg : allData.getGenotypes()) {
			clearPredictions(gg);
		}
		

		probdiplotype00 = new HashMap<Diplotype, HashMap<Site, Double>>();
		probdiplotype01 = new HashMap<Diplotype, HashMap<Site, Double>>();
		probdiplotype10 = new HashMap<Diplotype, HashMap<Site, Double>>();
		probdiplotype11 = new HashMap<Diplotype, HashMap<Site, Double>>();

		for (Diplotype dd : allData.getDiplotypes()) {
			clearPredictions(dd);
		}
		
		probhaplotype1 = new HashMap<Haplotype, HashMap<Site, Double>>();
		probhaplotype0 = new HashMap<Haplotype, HashMap<Site, Double>>();
		for (Haplotype hh : allData.getHaplotypes()) {
			clearPredictions(hh);
		}
	}

	protected void clearPredictions(Genotype gg) {
		probgenotype00.put(gg, new HashMap<Site, Double>());
		probgenotype11.put(gg, new HashMap<Site, Double>());
		probgenotype01.put(gg, new HashMap<Site, Double>());
	}

	protected void clearPredictions(Haplotype hh) {
		probhaplotype0.put(hh, new HashMap<Site, Double>());
		probhaplotype1.put(hh, new HashMap<Site, Double>());
	}
	
	protected void clearPredictions(Diplotype dd) {
		probdiplotype00.put(dd, new HashMap<Site, Double>());
		probdiplotype01.put(dd, new HashMap<Site, Double>());
		probdiplotype10.put(dd, new HashMap<Site, Double>());
		probdiplotype11.put(dd, new HashMap<Site, Double>());
	}


	protected void clearPredictions(Site ss) {
		for (Genotype gg : probgenotype00.keySet()) {
			Map<Site, Double> pred = probgenotype00.get(gg);
			if (pred.containsKey(ss)) {
				pred.remove(ss);
				probgenotype11.get(gg).remove(ss);
				probgenotype01.get(gg).remove(ss);
			}
		}
		for (Haplotype hh : probhaplotype0.keySet()) {
			Map<Site, Double> pred = probhaplotype0.get(hh);
			if (pred.containsKey(ss)) {
				pred.remove(ss);
				probhaplotype1.get(hh).remove(ss);
			}
		}
	}
	
	protected void wantPrediction(Site ss, Genotype gg) {
		assert allData.getGenotypes().contains(gg);
		if (probgenotype00.get(gg).containsKey(ss)) {
			return;
		}
		probgenotype00.get(gg).put(ss, 0.0);
		probgenotype01.get(gg).put(ss, 0.0);
		probgenotype11.get(gg).put(ss, 0.0);
	}
	

	protected void wantPrediction(Site ss, Diplotype dd) {
		assert allData.getDiplotypes().contains(dd);
		if (probdiplotype00.get(dd).containsKey(ss)) {
			return;
		}
		probdiplotype01.get(dd).put(ss, 0.0);
		probdiplotype10.get(dd).put(ss, 0.0);
		probdiplotype01.get(dd).put(ss, 0.0);
		probdiplotype11.get(dd).put(ss, 0.0);
	}

	protected void wantPrediction(Site ss, Haplotype hh) {
		assert allData.getHaplotypes().contains(hh);
		if (probhaplotype0.get(hh).containsKey(ss)) {
			return;
		}
		probhaplotype0.get(hh).put(ss, 0.0);
		probhaplotype1.get(hh).put(ss, 0.0);
	}
	
	/* TODO: Add phasing results into collector framework */
	protected void wantPhase(Site ss, Genotype gg) {
		assert allData.getGenotypes().contains(gg);

		if (probdiplotype00.get(gg).containsKey(ss)) {
			return;
		}
		probdiplotype00.get(gg).put(ss, 0.0);
		probdiplotype01.get(gg).put(ss, 0.0);
		probdiplotype10.get(gg).put(ss, 0.0);
		probdiplotype11.get(gg).put(ss, 0.0);
	}
	
	@Override
	public boolean isPredictable(Site ss, Genotype gg) {
		return probgenotype00.containsKey(gg)
				&& probgenotype00.get(gg).containsKey(ss);
	}

	
	@Override
	public boolean isPredictable(Site ss, Diplotype dd) {
		return probdiplotype00.containsKey(dd)
				&& probdiplotype00.get(dd).containsKey(ss);
	}

	@Override
	public boolean isPredictable(Site ss, Haplotype hh) {
		return probhaplotype0.containsKey(hh)
				&& probhaplotype0.get(hh).containsKey(ss);
	}

	@Override
	public boolean isPredictable(Genotype gg) {
		return probgenotype00.containsKey(gg)
				&& probgenotype00.get(gg).size() > 0;
	}
	
	@Override
	public boolean isPredictable(Diplotype dd) {
		return probdiplotype00.containsKey(dd)
				&& probdiplotype00.get(dd).size() > 0;
	}

	@Override
	public boolean isPredictable(Haplotype hh) {
		return probhaplotype0.containsKey(hh)
				&& probhaplotype0.get(hh).size() > 0;
	}
	
	@Override
	public boolean isPhaseable(Site ss, Genotype gg) {
		return probdiplotype00.containsKey(allData.getDiplotype(gg.getName()))
				&& probdiplotype00.get(allData.getDiplotype(gg.getName())).containsKey(ss);
	}
	
	@Override
	public boolean isHeterozygous(Site ss, Genotype gg) {
		return allData.get(ss, gg)[0]!=allData.get(ss, gg)[1];		
	}

	@Override
	public int numPredictableHaplotypes() {
		int num = 0;
		for (Haplotype hh : allData.getHaplotypes()) {
			if (isPredictable(hh)) {
				num++;
			}
		}
		return num;
	}

	@Override
	public int numPredictableGenotypes() {
		int num = 0;
		for (Genotype gg : allData.getGenotypes()) {
			if (isPredictable(gg)) {
				num++;
			}
		}
		return num;
	}

	@Override
	public void collect(Site ss, Genotype gg, double homo0, double hetero,
			double homo1) {
		HashMap<Site, Double> map;
		map = probgenotype00.get(gg);
		assert map.get(ss) != null : "Genotype " + gg.getName() + " at site "
				+ ss.getPosition() + " was null";
		map.put(ss, map.get(ss) + homo0);
		map = probgenotype11.get(gg);
		map.put(ss, map.get(ss) + homo1);
		map = probgenotype01.get(gg);
		map.put(ss, map.get(ss) + hetero);
	}
	

	@Override
	public void collect(Site ss, Diplotype dd, double d00, double d01, double d10, double d11) {
		HashMap<Site, Double> map;
		map = probdiplotype00.get(dd);
		assert map.get(ss) != null : "Diplotype " + dd.getName() + " at site "
				+ ss.getPosition() + " was null";
		map.put(ss, map.get(ss) + d00);
		map = probdiplotype01.get(dd);
		map.put(ss, map.get(ss) + d01);
		map = probdiplotype10.get(dd);
		map.put(ss, map.get(ss) + d10);
		map = probdiplotype11.get(dd);
		map.put(ss, map.get(ss) + d11);
	}

	/**
	 * e.g. Setting marginal probabilities
	 */
	@Override
	public void collect(Site ss, Haplotype hh, double prob0, double prob1) {
		HashMap<Site, Double> map;
		map = probhaplotype0.get(hh);
		assert map != null;
		map.put(ss, map.get(ss) + prob0);
		map = probhaplotype1.get(hh);
		map.put(ss, map.get(ss) + prob1);
	}
	

	@Override
	public double[] getProbabilities(Site ss, Diplotype dd) {
		double sum = 0.0;
		double[] result = new double[4];
		sum += result[0] = probdiplotype00.get(dd).get(ss);
		sum += result[1] = probdiplotype01.get(dd).get(ss);
		sum += result[2] = probdiplotype10.get(dd).get(ss);
		sum += result[3] = probdiplotype11.get(dd).get(ss);
		result[0] /= sum;
		result[1] /= sum;
		result[2] /= sum;
		result[3] /= sum;
		return result;
	}
	
	@Override
	public byte[] getPrediction(Site ss, Diplotype dd) {
		double[] probs = getProbabilities(ss, dd);
		if (probs[0] >= probs[1] && probs[0] >= probs[2] && probs[0] >= probs[3]) {
			return new byte[]{ 0, 0 };
		} else if (probs[1] >= probs[0] && probs[1] >= probs[2] && probs[1] >= probs[3]) {
			return new byte[]{ 0, 1 };
		} else if (probs[2] >= probs[0] && probs[2] >= probs[1] && probs[2] >= probs[3]) {
			return new byte[]{ 1, 0 };
		} else if (probs[3] >= probs[0] && probs[3] >= probs[1] && probs[3] >= probs[2]) {
			return new byte[]{ 1, 1 };
		} else {
			assert false;
			return null;
		}
	}

	@Override
	public double[] getProbabilities(Site ss, Genotype gg) {
		double sum = 0.0;
		double[] result = new double[3];
		sum += result[0] = probgenotype00.get(gg).get(ss);
		sum += result[2] = probgenotype11.get(gg).get(ss);
		sum += result[1] = probgenotype01.get(gg).get(ss);
		result[0] /= sum;
		result[1] /= sum;
		result[2] /= sum;
		return result;
	}

	@Override
	public void setPredictionMethod(int method) {
		this.predictionMethod = method;
	}

	@Override
	public int getPredictionMethod() {
		return this.predictionMethod;
	}

	@Override
	public byte[] getPrediction(Site ss, Genotype gg) {
		double[] probs = getProbabilities(ss, gg);
		if (predictionMethod == 0) {
			// old predictionMethod
			if (probs[0] >= .33) {
				return new byte[]{ 0, 0 };
			}
			if (probs[1] >= .33) {
				return new byte[]{ 0, 1 };
			} else {
				return new byte[]{ 1, 1 };
			}
		} else if (predictionMethod == 1 || predictionMethod == 2) {
			// a priori chance of heterozygous is 50%, while homozygous 0 or 1
			// both 25%.
			// default predictionMethod is 0, divide by two to even out.
			assert predictionMethod == 1 || predictionMethod == 2;
			probs[1] = probs[1] / predictionMethod;
			if (probs[1] >= probs[0] && probs[1] >= probs[2]) {
				return new byte[]{ 0, 1 };
			}
			if (probs[0] >= probs[1] && probs[0] >= probs[2]) {
				return new byte[] { 0, 0 };
			} else {
				return new byte[] { 1, 1 };
			}
		} else if (predictionMethod == 3) {
			// use mean frequency
			double mean = 2.0 * probs[2] + probs[1];
			if (mean > 1.5) {
				return new byte[] { 1, 1 };
			}
			if (mean < 0.5) {
				return new byte[] { 0, 0 };
			} else {
				return new byte[] { 0, 1 };
			}
		} else {
			System.err.println("Unknown prediction method");
			System.exit(1);
			return null;
		}
	}
	
	@Override
	public double[] getPhasedProbabilities(Site ss, Genotype gg) {
		// get diplotype answer
		Diplotype dd = allData.getDiplotype(gg.getName());
		double sum = 0.0;
		double[] result = new double[4];
		sum += result[0] = probdiplotype00.get(dd).get(ss);
		sum += result[1] = probdiplotype01.get(dd).get(ss);
		sum += result[2] = probdiplotype10.get(dd).get(ss);
		sum += result[3] = probdiplotype11.get(dd).get(ss);
		result[0] /= sum;
		result[1] /= sum;
		result[2] /= sum;
		result[3] /= sum;
		return result;
	}

	@Override
	public double[] getImputeProbabilities(Site ss, Haplotype hh) {
		double sum = 0.0;
		double[] result = new double[2];
		sum += result[0] = probhaplotype0.get(hh).get(ss);
		sum += result[1] = probhaplotype1.get(hh).get(ss);
		result[0] /= sum;
		result[1] /= sum;
		return result;
	}
	
	@Override
	public double getImputeVariance(Site ss, Haplotype hh) {
		double[] result = getImputeProbabilities(ss,hh);
		return result[0]*result[1];
	}

	@Override
	public byte getPrediction(Site ss, Haplotype hh) {
		double[] probs = getImputeProbabilities(ss, hh);
		if (probs[1] > .5) {
			return 1; // Ties go to zero.
		} else {
			return 0;
		}
	}
	
	// TODO: fix for dirichlet likelihood
	@Override
	public byte[] getPredictedHaplotype(Site ss, Genotype gg) {
		double[] probs = getPhasedProbabilities(ss, gg);
		int max = 0;
		for (int i = 1; i < probs.length; i++) if(probs[i]>probs[max]) max=i;
		assert(max>=0&&max<=3);
		
		if (max==0) return new byte[] {0, 0};
		else if (max==1) return new byte[] {0, 1};
		else if (max==2) return new byte[] {1, 0};
		else return new byte[] {1, 1};
		
	}

	@Override
	public ArrayDataSet<byte[]> getAllData() {
		return allData;
	}

	@Override
	public ArrayDataSet<byte[]> getTrainingSet() {
		//ArrayDataSet<byte[]>trainset = allData.clone();
		ArrayDataSet<byte[]> trainset = this.getAllData();
		assert trainset != null;
		for (Site ss : trainset.getSites()) {
			for (Genotype geno : trainset.getGenotypes()) {
				trainset.setObserved(ss, geno, !isPredictable(ss, geno)
						&& allData.isObserved(ss, geno));
			}
			for (Diplotype diplo : trainset.getDiplotypes()) {
				trainset.setObserved(ss, diplo, !isPredictable(ss, diplo)
						&& allData.isObserved(ss, diplo));
			}
			for (Haplotype haplo : trainset.getHaplotypes()) {
				trainset.setObserved(ss, haplo, !isPredictable(ss, haplo)
						&& allData.isObserved(ss, haplo));
			}
		}
		return trainset;
	}

	@Override
	public ArrayDataSet<byte[]>getTestSet() {
		boolean hastest = false;
		//ArrayDataSet<byte[]>testset = allData.clone();
		ArrayDataSet<byte[]> testset = this.getAllData();
		assert testset != null;
		for (Site ss : testset.getSites()) {
			for (Genotype geno : testset.getGenotypes()) {
				boolean istest = isPredictable(ss, geno)
						&& allData.isObserved(ss, geno);
				hastest |= istest;
				testset.setObserved(ss, geno, istest);
			}
			for (Haplotype haplo : testset.getHaplotypes()) {
				boolean istest = isPredictable(ss, haplo)
						&& allData.isObserved(ss, haplo);
				hastest |= istest;
				testset.setObserved(ss, haplo, istest);
			}
		}
		if (hastest) {
			return testset;
		} else {
			return null;
		}
	}

	@Override
	public ArrayDataSet<byte[]>getPredictedSet() {
		ArrayDataSet<byte[]> predicted = getTrainingSet().clone();
		for (Genotype gg : predicted.getGenotypes()) {
			for (Site ss : predicted.getSites()) {
				if (!isPredictable(ss, gg)) {
					predicted.setObserved(ss, gg, false);
				} else {
					predicted.setObserved(ss, gg, true);
					predicted.set(ss, gg, getPrediction(ss, gg));
				}
			}
		}
		for (Haplotype hh : predicted.getHaplotypes()) {
			for (Site ss : predicted.getSites()) {
				if (!isPredictable(ss, hh)) {
					predicted.setObserved(ss, hh, false);
				} else {
					predicted.setObserved(ss, hh, true);
					predicted.set(ss, hh, getPrediction(ss, hh));
				}
			}
		}
		return predicted;
	}

	@Override
	public ArrayDataSet<byte[]> getPhasedSet() {
		return this.getAllData();
		/*ArrayDataSet<byte[]> predicted = getTrainingSet().clone();
		for (Genotype gg : predicted.getGenotypes()) {
			for (Site ss : predicted.getSites()) {
				if(!isHeterozygous(ss, gg))
					predicted.setObserved(ss, gg, false);
				else {
					predicted.setObserved(ss, gg, true);
					//predicted.set(ss, gg, getPredictedHaplotype(ss, gg));
				}
			}
		}
		return predicted;*/
	}

	@Override
	public double getPredictionAccuracy() {
		ArrayDataSet<byte[]> predictions = getPredictedSet();
		return getPredictionAccuracy(predictions);
	}

	@Override
	public double[] getAllelicR2(int[] numSamples) {
		throw new Error("No test set defined; cannot compute allelic R2.");
	}

	@Override
	public void getConcordance(int[] levels, int[] correct, int[] counts) {
		throw new Error("No test set defined; cannot compute concordances.");
	}
	
	// TODO: fix for dirichlet likelihood
	@Override
	public double getMeanAllele(Site ss) {
		double numerator = 0.0;
		double denominator = 0.0;
		
		for (Genotype gg : allData.getGenotypes()) {
			if (allData.isObserved(ss, gg)) {
				numerator += allData.get(ss, gg)[0];
				numerator += allData.get(ss, gg)[1];
				denominator += 2.0;
			}
		}
		
		for (Diplotype dd : allData.getDiplotypes()) {
			if (allData.isObserved(ss, dd)) {
				numerator += allData.get(ss, dd)[0];
				numerator += allData.get(ss, dd)[1];
				denominator += 2.0;
			}
		}
		
		for (Haplotype hh : allData.getHaplotypes()) {
			if (allData.isObserved(ss, hh)) {
				numerator += allData.getAllele(ss, hh);
				denominator += 1.0;
			}
		}
		
		return numerator/denominator;
	}
}
