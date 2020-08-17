package org.dice.ida.vizsuggest;

import org.dice.ida.model.AttributeSummary;
import org.dice.ida.model.DataSummary;
import org.dice.ida.model.VisualizationSuggestion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A class to suggest bar graph parameters to the users for the given dataset
 * @author Nandeesh & Sourabh
 */
public class BarGraph implements IVisualizationParent{

    /**
     * Function to decide best suitable attributes for bar graph in the given dataset
     * @param dataSummary  data summary object of the dataset
     * @return Object of visualization suggestion class having the list of x-axis and corresponding y-axis parameters
     */
    @Override
    public VisualizationSuggestion getParams(DataSummary dataSummary) {
        VisualizationSuggestion visualizationSuggestion = new VisualizationSuggestion();
        List<AttributeSummary> xAxis = dataSummary.getAttributeSummaryList()
                .stream()
                .filter(x -> "Nom".equals(x.getType()) && x.getDiscreteValuesCount() <= 50).collect(Collectors.toList());
        Map<AttributeSummary, List<String>> paramMap = new HashMap<>();
        for (AttributeSummary attributeSummary :
                xAxis) {
            if (attributeSummary.getUniqueValuesProbability() == 100.00) {
                paramMap.put(attributeSummary, dataSummary.getAttributeSummaryList()
                        .stream()
                        .filter(x -> "Num".equals(x.getType()) && x.getUniqueValuesProbability() > 90.00)
                        .map(AttributeSummary::getName).collect(Collectors.toList()));
            } else if ((dataSummary.getNumberOfInstances() / attributeSummary.getDiscreteValuesCount()) > 30) {
                paramMap.put(attributeSummary, new ArrayList<>() {{
                    add("count of " + attributeSummary.getName());
                }});
            }
        }
        visualizationSuggestion.setName("Bar graph");
        Map<String, List<String>> paramsMap = new HashMap<>();
        for (AttributeSummary x :
                paramMap.keySet()) {
            paramsMap.put(x.getName(), paramMap.get(x));
        }
        visualizationSuggestion.setParamMap(paramsMap);
        return visualizationSuggestion;
    }
}
