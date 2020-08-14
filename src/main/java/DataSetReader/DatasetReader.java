package DataSetReader;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Enumeration;
import model.AttributeSummary;
import model.DatasetSummary;
import model.VisualizationSuggestion;
import weka.core.Attribute;
import weka.core.AttributeStats;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

public class DatasetReader {

    private String fileName;
    Instances data;
    DatasetSummary datasetSummary;
    public void createMetainfo() throws IOException {
        int index = fileName.indexOf(".");
        fileName = fileName.substring(0, index);
        List<Attribute> attributes = new ArrayList<Attribute>(); 
        BufferedWriter writer = new BufferedWriter(new FileWriter("./src/main/metainfo/" + fileName));
        for(int i=0;i<data.numAttributes();i++)
           	attributes.add(data.attribute(i));
        StringBuilder str = new StringBuilder();
        str.append("Number of records\t"+data.numInstances()+"\n");
        str.append("Number of Attributes\t"+ data.numAttributes()+"\n");
        str.append("Name\t\tType\tNom\tInt\tReal\tMissing\tUnique\tDist"+"\n");
        int k=0;
        for (Attribute s:attributes)
        	{
        		AttributeStats as =  data.attributeStats(k);
        		String stat =  as.toString().split("\\n")[1];
        		String fields[] = stat.split("\\s+");
        		if(fields[6].length()<=1)
        		{
        			if(fields[9].length()<=1)
        				str.append(s.name()+"\t\t"+fields[1]+"\t"+fields[2]+"\t"+fields[3]+"\t"+fields[4]+"\t"+fields[5]+"/"+fields[7]+"\t"+fields[8]+"/"+fields[10]+"\t"+fields[11]+"\n");
        			if(fields[9].length()>1)
        				str.append(s.name()+"\t\t"+fields[1]+"\t"+fields[2]+"\t"+fields[3]+"\t"+fields[4]+"\t"+fields[5]+"/"+fields[7]+"\t"+fields[8]+fields[9]+"\t"+fields[10]+"\n");
        		}
        		if(fields[6].length()>1)
        		{
        			if (fields[8].length()<=1)
        				str.append(s.name()+"\t\t"+fields[1]+"\t"+fields[2]+"\t"+fields[3]+"\t"+fields[4]+"\t"+fields[5]+fields[6]+"\t"+fields[7]+"/"+fields[9]+"\t"+fields[10]+"\n");
        			if (fields[8].length()>1)
        				str.append(s.name()+"\t\t"+fields[1]+"\t"+fields[2]+"\t"+fields[3]+"\t"+fields[4]+"\t"+fields[5]+fields[6]+"\t"+fields[7]+fields[8]+"\t"+fields[9]+"\n");
        		}
        		k++;
        	}
        writer.write(str.toString());
        writer.close();
        createDataSetSummary(fileName);
    }
    public void createDataSetSummary(String metaFileName)
    {
        StringReader stringReader = new StringReader(data.toSummaryString());
        String[] summaryLines = data.toSummaryString().split("\n");
        datasetSummary = new DatasetSummary();
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
       
    }

    public void FileReader(String fileName) throws IOException {
        this.fileName = fileName;
        CSVLoader loader = new CSVLoader();
        //Loads the File
        
        loader.setSource(new File("./src/main/resources/" + fileName));
        //Create an instance of the dataset 
        data = loader.getDataSet();
        //Set class index of dataset
        
        boolean exists = new File("./src/main/metainfo/" + fileName.split("\\.")[0]).exists();
        if(!exists)
        	createMetainfo(); 
       
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
                paramMap.put(attrributeSummary, new ArrayList<String>() {{
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
