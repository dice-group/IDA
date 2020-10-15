package org.dice.ida.util;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to expose util methods for Data based operation in IDA
 *
 * @author Sourabh
 *
 */
@Component
@Scope("singleton")

public class DataUtil {
	public List<Map<String, String>> getData(String datasetName, String tableName, List<String> columns) throws IOException {
		List<Attribute> attrcolumn = new ArrayList<>();
		List<Map<String, String>> extractedData = new ArrayList<>();
		CSVLoader loader = new CSVLoader();
		String path = new FileUtil().fetchSysFilePath("datasets/" + datasetName + "/" + tableName);
		loader.setSource(new File(path));
		Instances data = loader.getDataSet();
		for (String column : columns)
			attrcolumn.add(data.attribute(column));

		for (int i = 0; i < data.numInstances(); i++) {
			Map<String, String> temp = new HashMap<>();
			for (Attribute column : attrcolumn) {
				//TODO : update it once PR62 gets merged
				if (data.instance(i).toString(column) == null)
					temp.put(column.name(), "Not Defined");
				else
					temp.put(column.name(), data.instance(i).toString(column));
			}
			extractedData.add(temp);
		}
		return extractedData;

	}
}
