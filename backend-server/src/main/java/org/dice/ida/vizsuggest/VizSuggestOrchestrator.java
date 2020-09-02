package org.dice.ida.vizsuggest;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.AttributeSummary;
import org.dice.ida.model.DataSummary;
import org.dice.ida.model.VisualizationSuggestion;
import org.dice.ida.util.MetaFileReader;

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
	private final String tableName;
	private final String datasetName;

	public VizSuggestOrchestrator(String tableName, String datasetName) {
		this.tableName = tableName;
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
		DataSummary dataSummary = new MetaFileReader().createDataSummary(datasetName, tableName);
		VisualizationSuggestion visualizationSuggestion = vizSuggestionFactory.suggestVisualization(dataSummary).getParams(dataSummary);
		List<Map<String, String>> paramsMapLst = visualizationSuggestion.getParamMap();
		if (paramsMapLst.size() <= 0) {
			return IDAConst.NO_VISUALIZATION_MSG;
		}
		StringBuilder responseMsg = new StringBuilder(visualizationSuggestion.getName() + " suits better for this dataset with following parameters\n");
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
	 * @param tableName data summary object of the given dataset
	 * @return Data summary model of the dataset
	 * @throws Exception
	 */
	

	/**
	 * TODO Use below functions with upload dataset to create metadata as soon as user uploads new dataset
	 */
    /*public void createMetainfo(String fileName, Instances data) throws IOException {
        int index = fileName.lastIndexOf(".");
        fileName = fileName.substring(0, index);
        List<Attribute> attributes = new ArrayList<Attribute>();
        BufferedWriter writer = new BufferedWriter(new FileWriter(getClass().getClassLoader().getResource("metadata/" + datasetName + "/" + tableName) + fileName));
        for(int i=0;i<data.numAttributes();i++)
            attributes.add(data.attribute(i));
        StringBuilder str = new StringBuilder();
        str.append("Number of Dataset\t"+data.relationName()+"\n");
        str.append("Number of records\t"+data.numInstances()+"\n");
        str.append("Number of Attributes\t"+ data.numAttributes()+"\n");
        str.append("Name\t\tType\tNom\tInt\tReal\tMissingCount\tMissingPercentage\tUniqueCount\tUniquePercentage\tDist"+"\n");
        int k=0;
        for (Attribute s:attributes)
        {
            AttributeStats as =  data.attributeStats(k);
            String stat =  as.toString().split("\\n")[1];
            String fields[] = stat.split("\\s+");
            if(fields[6].length()<=1)
            {
                if(fields[9].length()<=1)
                    str.append(s.name()+"\t\t"+fields[1]+"\t"+fields[2]+"\t"+fields[3]+"\t"+fields[4]+"\t"+fields[5]+"\t"+fields[7]+"\t"+fields[8]+"\t"+fields[10]+"\t"+fields[11]+"\n");
                if(fields[9].length()>1)
                    str.append(s.name()+"\t\t"+fields[1]+"\t"+fields[2]+"\t"+fields[3]+"\t"+fields[4]+"\t"+fields[5]+"\t"+fields[7]+"\t"+fields[8]+"\t"+fields[9].substring(1)+"\t"+fields[10]+"\n");
            }
            if(fields[6].length()>1)
            {
                if (fields[8].length()<=1)
                    str.append(s.name()+"\t\t"+fields[1]+"\t"+fields[2]+"\t"+fields[3]+"\t"+fields[4]+"\t"+fields[5]+"\t"+fields[6].substring(1)+"\t"+fields[7]+"\t"+fields[9]+"\t"+fields[10]+"\n");
                if (fields[8].length()>1)
                    str.append(s.name()+"\t\t"+fields[1]+"\t"+fields[2]+"\t"+fields[3]+"\t"+fields[4]+"\t"+fields[5]+"\t"+fields[6].substring(1)+"\t"+fields[7]+"\t"+fields[8].substring(1)+"\t"+fields[9]+"\n");
            }
            k++;
        }
        writer.write(str.toString());
        writer.close();
    }

    public void FileReader() throws Exception {
        File dir = new File(getClass().getClassLoader().getResource("datasets/" + datasetName).getFile());
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                if (!child.getName().matches(IDAConst.DSMD_FILE_PATTERN)) {
                    String fileName = child.getName();
                    CSVLoader loader = new CSVLoader();
                    loader.setSource(child);
                    Instances data = loader.getDataSet();
                    createMetainfo(fileName, data);
                }
            }
        }
    }*/
}
