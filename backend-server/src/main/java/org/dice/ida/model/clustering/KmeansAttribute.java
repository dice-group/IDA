package org.dice.ida.model.clustering;

import weka.clusterers.SimpleKMeans;
import weka.core.SelectedTag;

public class KmeansAttribute {

	private int NumberOfCluster;
	private String IntitializeMethod;
	private int MaxIterations;
	private Boolean ReplaceMissingValues;
	private int NumOfExecutionSlots;
	private int RandomNumberSeed;
	private SimpleKMeans simpleKMeans;

	public KmeansAttribute(SimpleKMeans simpleKMeans)
	{
		this.simpleKMeans = simpleKMeans;
		this.NumberOfCluster = simpleKMeans.getNumClusters();
		this.IntitializeMethod = simpleKMeans.getInitializationMethod().getSelectedTag().getReadable();
		this.MaxIterations = simpleKMeans.getMaxIterations();
		this.ReplaceMissingValues = simpleKMeans.getDontReplaceMissingValues();
		this.NumOfExecutionSlots = simpleKMeans.getNumExecutionSlots();
		this.RandomNumberSeed = simpleKMeans.getSeed();
	}


	public int getNumberOfCluster() {
		return NumberOfCluster;
	}

	public void setNumberOfCluster(int numberOfCluster) {
		NumberOfCluster = numberOfCluster;
	}

	public String getIntitializeMethod() {
		return IntitializeMethod;
	}

	public void setIntitializeMethod(int intitializeMethod) {
		new SelectedTag(intitializeMethod, simpleKMeans.getInitializationMethod().getTags()).getSelectedTag().getReadable();


	}

	public int getMaxIterations() {
		return MaxIterations;
	}

	public void setMaxIterations(int maxIterations) {
		MaxIterations = maxIterations;
	}

	public Boolean getReplaceMissingValues() {
		return ReplaceMissingValues;
	}

	public void setReplaceMissingValues(Boolean replaceMissingValues) {
		ReplaceMissingValues = replaceMissingValues;
	}

	public int getNumOfExecutionSlots() {
		return NumOfExecutionSlots;
	}

	public void setNumOfExecutionSlots(int numOfExecutionSlots) {
		NumOfExecutionSlots = numOfExecutionSlots;
	}

	public int getRandomNumberSeed() {
		return RandomNumberSeed;
	}

	public void setRandomNumberSeed(int randomNumberSeed) {
		RandomNumberSeed = randomNumberSeed;
	}



}
