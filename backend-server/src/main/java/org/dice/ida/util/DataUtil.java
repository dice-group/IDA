package org.dice.ida.util;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to expose util methods for Data based operation in IDA
 *
 * @author Sourabh, Nandeesh
 */
@Component
@Scope("singleton")
public class DataUtil {
	/**
	 * Method to read a CSV file and return its contents (only required columns) as a list of maps
	 *
	 * @param datasetName - name of the dataset
	 * @param tableName - name of the table ( file name)
	 * @param columns - list of columns whose data is required
	 * @return list of maps where each map represents a row of the table
	 * @throws IOException - Exception when the dataset or table does not exist
	 */
	public List<Map<String, String>> getData(String datasetName, String tableName, List<String> columns) throws IOException {
		List<Map<String, String>> extractedData = new ArrayList<>();
		String path = new FileUtil().fetchSysFilePath("datasets/" + datasetName + "/" + tableName);
		List<Map<String, String>> fileData = new FileUtil().convertToMap(new File(path));
		for (Map<String, String> row : fileData) {
			Map<String, String> dataRow = new HashMap<>();
			for (String column : columns) {
				dataRow.put(column, DbUtils.manageNullValues(row.get(column)));
			}
			extractedData.add(dataRow);
		}
		return extractedData;

	}
}
