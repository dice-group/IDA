package org.dice.ida.vizsuggest;

import org.dice.ida.model.DataSummary;
import org.springframework.stereotype.Component;

/**
 * Class to suggest the best suitable visualization for the given dataset
 *
 * @author Nandeesh & Sourabh
 */
@Component
public class VizSuggestionFactory {
	/**
	 * Function to predict best suitable visualization for the given dataset and return an object which implements a function to predict the suitable parameters
	 *
	 * @param dataSummary data summary object of the given dataset
	 * @return Object of a class implementing IVisualizationParent interface
	 */
	public IVisualizationParent suggestVisualization(DataSummary dataSummary) {
		// TODO: model to predict best suitable visualization for the given dataset
		return new BarGraph();
	}
}
