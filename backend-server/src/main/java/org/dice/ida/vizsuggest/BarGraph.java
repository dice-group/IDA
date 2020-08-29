package org.dice.ida.vizsuggest;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.AttributeSummary;
import org.dice.ida.model.DataSummary;
import org.dice.ida.model.VisualizationSuggestion;

import java.util.*;
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
                .filter(x -> "Nom".equals(x.getType()) && x.getDiscreteValuesCount() <= IDAConst.X_PARAM_MAX_COUNT_OF_VALUES).collect(Collectors.toList());
        Map<AttributeSummary, List<String>> paramMap = new HashMap<>();
        for (AttributeSummary attributeSummary :
                xAxis) {
            if (attributeSummary.getUniqueValuesProbability() == IDAConst.X_PARAM_UNIQUENESS_PROBABILITY) {
                paramMap.put(attributeSummary, dataSummary.getAttributeSummaryList()
                        .stream()
                        .filter(x -> "Num".equals(x.getType()) && x.getUniqueValuesProbability() > IDAConst.Y_PARAM_UNIQUENESS_MIN_PROBABILITY)
                        .map(AttributeSummary::getName).collect(Collectors.toList()));
            } else if ((dataSummary.getNumberOfInstances() / attributeSummary.getDiscreteValuesCount()) > IDAConst.X_PARAM_MIN_DUPLICATE_RATIO) {
                paramMap.put(attributeSummary, new ArrayList<>() {{
                    add("count of " + attributeSummary.getName());
                }});
            }
        }
        visualizationSuggestion.setName("Bar graph");
        List<Map<String, String>> paramsLst = new ArrayList<>();
        Map<String, String> xAxisMap;
        Map<String, String> yAxisMap;
        for (AttributeSummary x :
                paramMap.keySet()) {
            xAxisMap = new HashMap<>();
            yAxisMap = new HashMap<>();
            xAxisMap.put("X-Axis", x.getName());
            yAxisMap.put("Y-Axis", String.join(", ", paramMap.get(x)));
            paramsLst.add(xAxisMap);
            paramsLst.add(yAxisMap);
        }
        visualizationSuggestion.setParamMap(paramsLst);
        return visualizationSuggestion;
    }
}
