package DataSetReader;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import model.AttributeSummary;
import model.DatasetSummary;
import model.VisualizationSuggestion;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

public class DatasetReader {

    private String fileName;
    Instances data;

    public DatasetSummary createMetainfo() throws IOException {
        int index = fileName.indexOf(".");
        Calendar c = Calendar.getInstance();
        fileName = fileName.substring(0, index);
        BufferedWriter writer = new BufferedWriter(new FileWriter("./src/main/metainfo/" + fileName + c.get(Calendar.MONTH) + c.get(Calendar.YEAR)));
        writer.write(data.toSummaryString());
        writer.close();
        StringReader stringReader = new StringReader(data.toSummaryString());
        String[] summaryLines = data.toSummaryString().split("\n");
        DatasetSummary datasetSummary = new DatasetSummary();
        datasetSummary.setName(summaryLines[0].split(":")[1].trim());
        datasetSummary.setNumberOfInstances(Long.parseLong(summaryLines[1].split(":")[1].trim()));
        String[] attributeValues_1;
        String attributeValues_2;
        List<AttributeSummary> attributeSummaryList = new ArrayList<>();
        AttributeSummary attributeSummary;
        for (int i = 5; i < summaryLines.length; i++) {
            summaryLines[i] = summaryLines[i].replaceAll("[/%]", "");
            summaryLines[i] = summaryLines[i].replaceFirst("[\\s]{2,}", "\t");
            attributeValues_2 = summaryLines[i].trim().split("\t")[0];
            attributeValues_1 = summaryLines[i].trim().split("\t")[1].split("[\\s]+");
            attributeSummary = new AttributeSummary();
            attributeSummary.setName(attributeValues_2.substring(attributeValues_2.indexOf(" ")));
            attributeSummary.setType(attributeValues_1[0]);
            attributeSummary.setNominalTypeProbability(Double.parseDouble(attributeValues_1[1]));
            attributeSummary.setIntegerTypeProbability(Double.parseDouble(attributeValues_1[2]));
            attributeSummary.setRealNumTypeProbability(Double.parseDouble(attributeValues_1[3]));
            attributeSummary.setMissingValuesCount(Long.parseLong(attributeValues_1[4]));
            attributeSummary.setMissingValuesProbability(Double.parseDouble(attributeValues_1[5]));
            attributeSummary.setUniqueValuesCount(Long.parseLong(attributeValues_1[6]));
            attributeSummary.setUniqueValuesProbability(Double.parseDouble(attributeValues_1[7]));
            attributeSummary.setDiscreteValuesCount(Long.parseLong(attributeValues_1[8]));
            attributeSummaryList.add(attributeSummary);
        }
        datasetSummary.setAttributeSummaryList(attributeSummaryList);
        suggestViz(datasetSummary);
        return datasetSummary;
    }

    public void FileReader(String fileName) throws IOException {
        this.fileName = fileName;
        CSVLoader loader = new CSVLoader();
        //Loads the File

        loader.setSource(new File("./src/main/resources/" + fileName));
        //Create an instance of the dataset 
        data = loader.getDataSet();
        //Set class index of dataset
        data.setClassIndex(data.numAttributes() - 1);
    }

    public VisualizationSuggestion suggestViz(DatasetSummary datasetSummary) {
        List<AttributeSummary> xAxis = datasetSummary.getAttributeSummaryList()
                .stream()
                .filter(x -> "Nom".equals(x.getType()) && x.getDiscreteValuesCount() <= 50).collect(Collectors.toList());
//                .map(AttributeSummary::getName).collect(Collectors.toList());
        Map<AttributeSummary, List<String>> paramMap = new HashMap<>();
        for (AttributeSummary attrributeSummary :
                xAxis) {
            if (attrributeSummary.getUniqueValuesProbability() == 100.00) {
                paramMap.put(attrributeSummary, datasetSummary.getAttributeSummaryList()
                        .stream()
                        .filter(x -> "Num".equals(x.getType()) && x.getUniqueValuesProbability() > 90.00)
                        .map(AttributeSummary::getName).collect(Collectors.toList()));
            } else if ((datasetSummary.getNumberOfInstances() / attrributeSummary.getDiscreteValuesCount()) > 30) {
                paramMap.put(attrributeSummary, new ArrayList<>() {{
                    add("count of " + attrributeSummary.getName());
                }});
            }
        }
        for (AttributeSummary x :
                paramMap.keySet()) {
            System.out.println(x.getName() + "  => " + paramMap.get(x));
        }
        return null;
    }

}
