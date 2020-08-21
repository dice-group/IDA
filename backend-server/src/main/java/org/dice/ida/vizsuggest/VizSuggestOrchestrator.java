package org.dice.ida.vizsuggest;

import org.dice.ida.model.AttributeSummary;
import org.dice.ida.model.DataSummary;
import org.dice.ida.model.VisualizationSuggestion;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Class to orchestrate the visualization suggestion based on the dataset
 *
 * @author Nandeesh & Sourabh
 */
public class VizSuggestOrchestrator {
    private final String datasetName;

    public VizSuggestOrchestrator(String datasetName) {
        this.datasetName = datasetName;
    }

    /**
     * Function accessible from outside to get the visualization suggestion for a dataset
     *
     * @return Message to the user containing suggested visualization details
     * @throws Exception
     */
    public String getSuggestion() throws Exception {
        VizSuggestionFactory vizSuggestionFactory = new VizSuggestionFactory();
        DataSummary dataSummary = createDataSummary(this.datasetName);
        VisualizationSuggestion visualizationSuggestion = vizSuggestionFactory.suggestVisualization(dataSummary).getParams(dataSummary);
        StringBuilder responseMsg = new StringBuilder(visualizationSuggestion.getName() + " suits better for this dataset with following parameters\n");
        List<Map<String, String>> paramsMapLst = visualizationSuggestion.getParamMap();
        for (Map<String, String> paramsMap :
                paramsMapLst) {
            for (String paramName : paramsMap.keySet()) {
                responseMsg.append(paramName).append(": ");
                responseMsg.append(paramsMap.get(paramName)).append("\n");
            }
        }
        return responseMsg.toString();
    }

    /**
     * Function to read summary of a dataset from file and create summary model
     *
     * @param datasetName data summary object of the given dataset
     * @return Data summary model of the dataset
     * @throws Exception
     */
    private DataSummary createDataSummary(String datasetName) throws Exception {
        String metaData = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("metadata/" + datasetName)).getFile())));
        String[] summaryLines = metaData.split("\n");
        DataSummary dataSummary = new DataSummary();
        dataSummary.setName(summaryLines[0].split("\t")[1].trim());
        dataSummary.setNumberOfInstances(Long.parseLong(summaryLines[1].split("\t")[1].trim()));
        String[] attributeValues;
        List<AttributeSummary> attributeSummaryList = new ArrayList<>();
        AttributeSummary attributeSummary;
        for (int i = 4; i < summaryLines.length; i++) {
            summaryLines[i] = summaryLines[i].replaceAll("[/%]", "");
            attributeValues = summaryLines[i].trim().split("\t");
            attributeSummary = new AttributeSummary();
            attributeSummary.setName(attributeValues[0]);
            attributeSummary.setType(attributeValues[2]);
            attributeSummary.setNominalTypeProbability(Double.parseDouble(attributeValues[3]));
            attributeSummary.setIntegerTypeProbability(Double.parseDouble(attributeValues[4]));
            attributeSummary.setRealNumTypeProbability(Double.parseDouble(attributeValues[5]));
            attributeSummary.setMissingValuesCount(Long.parseLong(attributeValues[6]));
            attributeSummary.setMissingValuesProbability(Double.parseDouble(attributeValues[7]));
            attributeSummary.setUniqueValuesCount(Long.parseLong(attributeValues[8]));
            attributeSummary.setUniqueValuesProbability(Double.parseDouble(attributeValues[9]));
            attributeSummary.setDiscreteValuesCount(Long.parseLong(attributeValues[10]));
            attributeSummaryList.add(attributeSummary);
        }
        dataSummary.setAttributeSummaryList(attributeSummaryList);
        return dataSummary;
    }
}
