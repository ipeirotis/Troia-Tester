package main.com.troiaTester;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import troiaClient.Category;
import troiaClient.Label;

/**
 *
 * Object of this class represents summary of the ArtificialWorker object.
 *
 * @author piotr.gnys@10clouds.com
 */
public class ArtificialWorkerStats {

	double qualityExpected;
	double qualityOptimized;

	private ArtificialWorker worker;
	private Collection<Label> labels;
	private Collection<Category> categories;
	private CostMatrix cost;

	private Map<String, Double> workerPriors = null;
	private Map<String, Double> categoryPriors = null;

	public ArtificialWorkerStats(ArtificialWorker worker, Collection<Label> labels,
			Collection<Category> categories) {
		this(worker, labels, categories, new CostMatrix() {

			@Override
			public double getMisclassificationCost(String from, String to) {
				return from.equals(to) ? 0.0 : 1.0;
			}

		});
	}

	public ArtificialWorkerStats(ArtificialWorker worker, Collection<Label> labels,
			Collection<Category> categories, CostMatrix cost) {
		this.worker = worker;
		this.labels = labels;
		this.categories = categories;
		this.cost = cost;
		compute();
	}

	public ArtificialWorkerStats(double qualityOptimized, double qualityExpected) {
		this.qualityOptimized = qualityOptimized;
		this.qualityExpected = qualityOptimized;
	}

	public ArtificialWorker getWorker() {
		return worker;
	}

	public double getAccuracy() {
		return 0.0;
	}

	public double getQualityExpected() {
		return qualityExpected;
	}

	public double getQualityOptimized() {
		return qualityOptimized;
	}

	private void computeWorkerPriors() {
		workerPriors = new HashMap<String, Double>();
		for (Label label : labels) {
			if (label.getWorkerName().equals(worker.getName())) {
				String categoryName = label.getCategoryName();
				Double prior = workerPriors.get(categoryName);
				if (prior == null) {
					prior = 0.0;
				}
				workerPriors.put(categoryName, prior + 1);
			}
		}
		for (String name : workerPriors.keySet()) {
			workerPriors.put(name, workerPriors.get(name) / labels.size());
		}
	}

	private void computeCategoryPriors() {
		categoryPriors = new HashMap<String, Double>();
		for (Category category : categories) {
			categoryPriors.put(category.getName(), category.getPrior());
		}
	}

	private Map<String, Double> getSoftProbabilities(String fromName) {
		Map<String, Double> soft = new HashMap<String, Double>();
		ConfusionMatrix matrix = worker.getConfusionMatrix();
		for (Category to : categories) {
			String toName = to.getName();
			double workerPrior = workerPriors.get(toName);
			double prior = to.getPrior();
			double error = matrix.getMisclassificationProbability(fromName, toName);
			soft.put(toName, prior * error / workerPrior);
		}
		return soft;
	}

	private double getMinCost(Map<String, Double> probabilities) {

		double result  = 0.0;

		Double minCost = Double.MAX_VALUE;

		for (String name1 : probabilities.keySet()) {
			double currentCost = 0.0;
			for (String name2 : probabilities.keySet()) {
				currentCost += probabilities.get(name2) *
					cost.getMisclassificationCost(name2, name1);
			}
			if (currentCost < minCost) {
				result = currentCost;
			}

		}

		return result;
	}

	private double getExpectedCost(Map<String, Double> probabilities) {

		return 0;
	}

	private double getMinSpammerCost() {
		Map<String, Double> prior = new HashMap<String, Double>();
		return getExpectedCost(categoryPriors);
	}

	private void compute() {
		computeWorkerPriors();
		computeCategoryPriors();
		qualityExpected = 0.0;
		qualityOptimized = 0.0;
		for (Category from : categories) {
			String fromName = from.getName();
			Map<String, Double> soft = getSoftProbabilities(fromName);
			qualityExpected += getExpectedCost(soft);
			qualityOptimized += getMinCost(soft);
		}
		qualityExpected = 1 - qualityExpected / getMinSpammerCost();
		qualityOptimized = 1 - qualityOptimized / getMinSpammerCost();
		if (Double.isNaN(qualityExpected) || Double.isInfinite(qualityExpected))
			qualityExpected = -1;
		if (Double.isNaN(qualityOptimized) || Double.isInfinite(qualityOptimized))
			qualityOptimized = -1;
	}


}
