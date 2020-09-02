package org.dice.ida.util;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.dice.ida.model.AttributeSummary;
import org.dice.ida.model.DataSummary;

public class MetaFileReader {
	public DataSummary createDataSummary(String datasetName, String tableName) throws Exception {
		int index = tableName.lastIndexOf(".");
		String fileName = tableName.substring(0, index);
		String metaData = new String(Files.readAllBytes(Paths.get("./src/main/resources/"+"metadata/" + datasetName + "/" + fileName)));
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
