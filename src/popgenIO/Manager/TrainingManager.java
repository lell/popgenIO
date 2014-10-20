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

public class TrainingManager extends AbstractManager implements Serializable {
	DataSet<Boolean> test;

	public TrainingManager(DataSet<Boolean> train) {
		super(train);
		wantUnobserved();
		/* TODO: Add phasing results into collector framework */
		//wantUnphased();
	}

	public void wantUnobserved() {
		DataSet<Boolean> trainset = this.getTrainingSet();
		for (Site ss : trainset.getSites()) {

			for (Genotype gg : trainset.getGenotypes()) {
				if (!trainset.isObserved(ss, gg)) {
					wantPrediction(ss, gg);
					assert isPredictable(ss, gg);
				}
			}

			for (Diplotype dd : trainset.getDiplotypes()) {
				if (!trainset.isObserved(ss, dd)) {
					wantPrediction(ss, dd);
					assert isPredictable(ss, dd);
				}
			}
		
			for (Haplotype hh : trainset.getHaplotypes()) {
				if (!trainset.isObserved(ss, hh)) {
					wantPrediction(ss, hh);
					assert isPredictable(ss, hh);
				}
			}
		}
	}
	
	public void wantUnphased() {
		DataSet<Boolean> trainset = this.getTrainingSet();
		for (Site ss : trainset.getSites()) {
			for (Genotype gg : trainset.getGenotypes()) {
				if (trainset.isHeterozygous(ss, gg)) {
					wantPhase(ss, gg);
					assert isPhaseable(ss, gg);
				}
			}
		}
	}

	public void attachTestSet(DataSet<Boolean> test) {
		this.test = test;
	}

	@Override
	public double getChanceAccuracy() {
		assert test != null;
		double numerator = 0.0;
		double denominator = 0.0;
		for (Site ss : test.getSites()) {
			boolean mean = this.getMeanAllele(ss) > 0.5;

			for (Genotype gg : test.getGenotypes()) {
				if (test.isObserved(ss, gg)) {
					if (test.get(ss, gg)[0] == mean && test.get(ss, gg)[1] == mean) {
						numerator += 1.0;
					}
					denominator += 1.0;
				}
			}

			for (Diplotype dd : test.getDiplotypes()) {
				if (test.isObserved(ss, dd)) {
					if (test.get(ss, dd)[0] == mean && test.get(ss, dd)[1] == mean) {
						numerator += 1.0;
					}
					denominator += 1.0;
				}
			}

			for (Haplotype hh : test.getHaplotypes()) {
				if (test.isObserved(ss, hh)) {
					if (test.get(ss, hh) == mean) {
						numerator += 1.0;
					}
					denominator += 1.0;
				}
			}
		}
		return numerator / denominator;
	}

	@Override
	public double getPredictionAccuracy(DataSet<Boolean> predictions) {
		assert test != null;
		double numerator = 0.0;
		double denominator = 0.0;
		for (Diplotype dd : test.getDiplotypes()) {
			double n0 = 0.0;
			double n1 = 0.0;

			// order doesn't matter for diplotypes, try both orders:
			for (Site ss : test.getSites()) {
				if (test.isObserved(ss, dd)) {
					if (predictions.get(ss, dd)[0] == test.get(ss, dd)[0]
							&& predictions.get(ss, dd)[1] == test.get(ss, dd)[1]) {

						n0 += 1.0;
					}
					denominator += 1.0;
				}				
			}

			for (Site ss : test.getSites()) {
				if (test.isObserved(ss, dd)) {
					if (predictions.get(ss, dd)[0] == test.get(ss, dd)[1]
							&& predictions.get(ss, dd)[1] == test.get(ss, dd)[0]) {

						n1 += 1.0;
					}
				}				
			}

			if (n0 > n1) {
				numerator += n0;
			} else {
				numerator += n1;
			}
		}

		for (Site ss : test.getSites()) {
			for (Genotype gg : test.getGenotypes()) {
				if (test.isObserved(ss, gg)) {
					if ((predictions.get(ss, gg)[0] == test.get(ss, gg)[0]
							&& predictions.get(ss, gg)[1] == test.get(ss, gg)[1]) ||
							(predictions.get(ss, gg)[0] == test.get(ss, gg)[1]
									&& predictions.get(ss, gg)[1] == test.get(ss, gg)[0])	
							) {
						
						numerator += 1.0;
					}
					denominator += 1.0;
				}
			}

			for (Haplotype hh : test.getHaplotypes()) {
				if (test.isObserved(ss, hh)) {
					if (predictions.get(ss, hh) == test.get(ss, hh)) {
						numerator += 1.0;
					}
					denominator += 1.0;
				}
			}

			for (Haplotype hh : test.getHaplotypes()) {
				if (test.isObserved(ss, hh)) {
					if (predictions.get(ss, hh) == test.get(ss, hh)) {
						numerator += 1.0;
					}
					denominator += 1.0;
				}
			}

		}
		return numerator / denominator;
	}
}
