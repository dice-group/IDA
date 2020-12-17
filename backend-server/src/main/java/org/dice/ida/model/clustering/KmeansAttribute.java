package org.dice.ida.model.clustering;

import weka.clusterers.SimpleKMeans;
import weka.core.SelectedTag;

/**
 * Model class for K-mean clustering algorithm
 *
 * @author Sourabh Poddar
 */
public class KmeansAttribute {

	private int NumberOfCluster;
	private SelectedTag InitializeMethod;
	private int MaxIterations;
	private Boolean ReplaceMissingValues;
	private int NumOfExecutionSlots;
	private int RandomNumberSeed;
	private SimpleKMeans simpleKMeans;

	/**
	 * Constructor for KmeansAttribute class
	 *
	 * @param simpleKMeans - SimpleKMeans class Object
	 * @param numcluster - Number of clusters
	 */
	public KmeansAttribute(SimpleKMeans simpleKMeans, int numcluster)
	{
		this.simpleKMeans = simpleKMeans;
		this.NumberOfCluster = numcluster;
		this.InitializeMethod = simpleKMeans.getInitializationMethod();
		this.MaxIterations = simpleKMeans.getMaxIterations();
		this.ReplaceMissingValues = simpleKMeans.getDontReplaceMissingValues();
		this.NumOfExecutionSlots = simpleKMeans.getNumExecutionSlots();
		this.RandomNumberSeed = simpleKMeans.getSeed();
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
	 * Get the initialization method
	 *
	 * @return initialization method
	 */
	public SelectedTag getInitializeMethod() {
		return InitializeMethod;
	}

	/**
	 * Set the initialization method
	 *
	 * @param initializeMethod Initialization Method
	 */
	public void setInitializeMethod(int initializeMethod) {
		InitializeMethod = new SelectedTag(initializeMethod, simpleKMeans.getInitializationMethod().getTags());
	}

	/**
	 * Get the max number of iteration
	 *
	 * @return max number of iteration
	 */
	public int getMaxIterations() {
		return MaxIterations;
	}

	/**
	 * Set the max number of iterations
	 *
	 * @param maxIterations max number of iterations
	 */
	public void setMaxIterations(int maxIterations) {
		MaxIterations = maxIterations;
	}

	/**
	 * Get the replace missing value
	 *
	 * @return replace missing values
	 */
	public Boolean getReplaceMissingValues() {
		return ReplaceMissingValues;
	}

	/**
	 * Set the replace missing value
	 *
	 * @param replaceMissingValues replace missing value
	 */
	public void setReplaceMissingValues(Boolean replaceMissingValues) {
		ReplaceMissingValues = replaceMissingValues;
	}

	/**
	 * Get the number of execution slots
	 *
	 * @return Number of execution slots
	 */
	public int getNumOfExecutionSlots() {
		return NumOfExecutionSlots;
	}

	/**
	 * Set the number of execution slots
	 *
	 * @param numOfExecutionSlots number of execution slots
	 */
	public void setNumOfExecutionSlots(int numOfExecutionSlots) {
		NumOfExecutionSlots = numOfExecutionSlots;
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
