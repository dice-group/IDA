package org.dice.ida.model.clustering;

import weka.clusterers.FarthestFirst;

/**
 * Model class for Farthest First clustering algorithm
 *
 * @author Sourabh Poddar
 */
public class FarthestFirstAttribute {

	private int NumberOfCluster;
	private int RandomNumberSeed;

	/**
	 * Constructor for FarthestFirstAttribute class
	 *
	 * @param farthestFirst - FarthestFirst class Object
	 * @param numcluster - Number of clusters
	 */
	public FarthestFirstAttribute(FarthestFirst farthestFirst, int numcluster) {
		this.NumberOfCluster = numcluster;
		this.RandomNumberSeed = farthestFirst.getSeed();
	}

	/**
	 * Get the number of clusters
	 *
	 * @return Number of clusters
	 */
	public int getNumberOfCluster() {
		return NumberOfCluster;
	}

	/**
	 * Set the number of clusters
	 *
	 * @param numberOfCluster Number of clusters
	 */
	public void setNumberOfCluster(int numberOfCluster) {
		NumberOfCluster = numberOfCluster;
	}

	/**
	 * Get the random number seed
	 *
	 * @return random number seed
	 */
	public int getRandomNumberSeed() {
		return RandomNumberSeed;
	}

	/**
	 * Set the random number seed
	 *
	 * @param randomNumberSeed random number seed
	 */
	public void setRandomNumberSeed(int randomNumberSeed) {
		RandomNumberSeed = randomNumberSeed;
	}
}
