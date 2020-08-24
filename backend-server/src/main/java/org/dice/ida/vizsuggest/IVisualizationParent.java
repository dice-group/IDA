package org.dice.ida.vizsuggest;

import org.dice.ida.model.DataSummary;
import org.dice.ida.model.VisualizationSuggestion;

/**
 * Interface that needs to be implemented by all visualizations to suggest its parameters based on the given dataset
 *
 * @author Nandeesh & Sourabh
 */
public interface IVisualizationParent {
	VisualizationSuggestion getParams(DataSummary dataSummary);
}
