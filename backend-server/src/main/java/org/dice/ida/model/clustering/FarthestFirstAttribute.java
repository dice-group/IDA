package org.dice.ida.model.clustering;

import weka.clusterers.FarthestFirst;

public class FarthestFirstAttribute {

	private int NumberOfCluster;
	private int RandomNumberSeed;

	public FarthestFirstAttribute(FarthestFirst farthestFirst, int numcluster) {
		this.NumberOfCluster = numcluster;
		this.RandomNumberSeed = farthestFirst.getSeed();
	}

	public int getNumberOfCluster() {
		return NumberOfCluster;
	}

	public void setNumberOfCluster(int numberOfCluster) {
		NumberOfCluster = numberOfCluster;
	}

	public int getRandomNumberSeed() {
		return RandomNumberSeed;
	}

	public void setRandomNumberSeed(int randomNumberSeed) {
		RandomNumberSeed = randomNumberSeed;
	}
}
