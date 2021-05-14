package org.dice.ida.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.dice.ida.constant.IDAConst;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class containing util function for suggestion module
 *
 * @author Nandeesh Pate, Sourabh Poddar
 */
@Component
public class SuggestionUtil {

	/**
	 * Method to get statistical properties (Standard deviation, Covariance Matrix) for column values of a given table
	 *
	 * @param dsName    - name of the dataset
	 * @param tableName - name of the table
	 * @return - map containing standard deviation for label counts, numeric columns and covariance matrix
	 * @throws Exception - If dataset or table does not exist. In case of statistical properties file creation failure
	 */
	public Map<String, Map<String, Double>> getStatProps(String dsName, String tableName) throws Exception {
		FileUtil fileUtil = new FileUtil();
		Map<String, Map<String, Double>> stats = new HashMap<>();
		File fl = new File(getClass().getClassLoader().getResource("").getPath(), IDAConst.STATS_DIR + "/" + dsName + "/" + tableName + ".json");
		if (!fl.exists()) {
			ObjectNode metaData = fileUtil.getDatasetMetaData(dsName);
			JsonNode fileDetails = metaData.get(IDAConst.FILE_DETAILS_ATTR);
			for (int i = 0; i < fileDetails.size(); i++) {
				if (tableName.equals(fileDetails.get(i).get(IDAConst.FILE_NAME_ATTR).asText())) {
					JsonNode columnDetails = fileDetails.get(i).get(IDAConst.COLUMN_DETAILS_ATTR);
					Map<String, String> columnMap = new HashMap<>();
					for (int j = 0; j < columnDetails.size(); j++) {
						columnMap.put(columnDetails.get(j).get(IDAConst.COLUMN_NAME_ATTR).asText(), columnDetails.get(j).get(IDAConst.COLUMN_TYPE_ATTR).asText());
					}
					List<Map<String, String>> tableData = getColumnData(dsName, tableName, columnMap);
					stats.put(IDAConst.COLUMN_SD_NOMINAL, calculateCountSD(tableData, columnMap, new ArrayList<>() {{
						add(IDAConst.COLUMN_TYPE_NOMINAL);
					}}));
					stats.put(IDAConst.COLUMN_SD_ALL, calculateCountSD(tableData, columnMap, new ArrayList<>() {{
						add(IDAConst.KEY_ALL);
					}}));
					stats.put(IDAConst.COLUMN_SD_TEMPORAL, calculateCountSD(tableData, columnMap, new ArrayList<>() {{
						add(IDAConst.COLUMN_TYPE_DATE);
					}}));
					stats.put(IDAConst.COVARIANCE_MATRIX, getCovariance(tableData, columnMap));
					stats.put(IDAConst.COLUMN_SD_NUMERIC, calculateSD(tableData, columnMap));
					writeStatsToFile(dsName, tableName, stats);
					break;
				}
			}
		} else {
			ObjectMapper mapper = new ObjectMapper();
			stats = mapper.readValue(fl, new TypeReference<>() {
			});
		}
		return stats;
	}

	/**
	 * Method to get covariance matrix for numerical columns of a given table
	 *
	 * @param columnData - column values for the table
	 * @param columnMap  - column name and datatype map
	 * @return - covariance matrix as a map (key - columnM|columnN, value - covariance)
	 */
	private Map<String, Double> getCovariance(List<Map<String, String>> columnData, Map<String, String> columnMap) {
		List<String> columnList = columnMap.keySet().stream().filter(c -> columnMap.get(c).equals(IDAConst.COLUMN_TYPE_NUMERIC)).collect(Collectors.toList());
		Map<String, Double> covarianceMatrix = new HashMap<>();
		for (String column1 : columnList) {
			for (String column2 : columnList) {
				if (!column1.equalsIgnoreCase(column2)) {
					Double covariance = getCovariancePair(columnData, column1, column2);
					covarianceMatrix.put(column1 + "|" + column2, Math.abs(covariance));
				}
			}
		}
		return covarianceMatrix;
	}

	/**
	 * Method to calculate an entry of a covariance matrix
	 *
	 * @param fileData - table data
	 * @param column1  - first column
	 * @param column2  - second column
	 * @return covariance of column 1 and 2
	 */
	private Double getCovariancePair(List<Map<String, String>> fileData, String column1, String column2) {
		double[][] matrix = new double[fileData.size()][2];
		for (int i = 0; i < fileData.size(); i++) {
			try {
				matrix[i][0] = Double.parseDouble(fileData.get(i).get(column1));
				matrix[i][1] = Double.parseDouble(fileData.get(i).get(column2));
			} catch (Exception e) {
				matrix[i][0] = 0.0;
				matrix[i][1] = 0.0;
			}
		}
		RealMatrix mx = MatrixUtils.createRealMatrix(matrix);
		RealMatrix cov = new Covariance(mx).getCovarianceMatrix();
		return cov.getColumn(0)[1];
	}

	/**
	 * Method to get the table data from resource file
	 *
	 * @param datasetName - name of the dataset
	 * @param tableName   - name of the table
	 * @param columnMap   - column name and datatype map
	 * @return - table in form of list of rows (each row is a map)
	 * @throws Exception - if dataset or table does not exist
	 */
	private List<Map<String, String>> getColumnData(String datasetName, String tableName, Map<String, String> columnMap) throws Exception {
		List<Map<String, String>> extractedData = new ArrayList<>();
		String path = new FileUtil().fetchSysFilePath("datasets/" + datasetName + "/" + tableName);
		List<Map<String, String>> fileData = new FileUtil().convertToMap(new File(path));
		for (Map<String, String> rowData : fileData) {
			Map<String, String> dataRow = new HashMap<>();
			for (String column : columnMap.keySet()) {
				String columnValue = rowData.get(column);
				if (columnMap.get(column).equalsIgnoreCase("Numeric")) {
					if (DbUtils.manageNullValues(columnValue).equals("UNKNOWN")) {
						dataRow.put(column, "0.0");
					} else {
						dataRow.put(column, DbUtils.manageNullValues(columnValue.replaceAll(",", ".")));
					}
				} else {
					dataRow.put(column, columnValue);
				}
			}
			extractedData.add(dataRow);
		}
		return extractedData;
	}

	/**
	 * Method to calculate standard deviation for count of labels(non numeric columns) of a table
	 *
	 * @param columnData - table data
	 * @param colTypeMap - column name and datatype map
	 * @return standard deviation of label counts
	 */
	private Map<String, Double> calculateCountSD(List<Map<String, String>> columnData, Map<String, String> colTypeMap, List<String> colTypeList) {
		Map<String, Double> statsMap = new HashMap<>();
		Map<String, Map<String, Double>> countMap = new HashMap<>();
		Map<String, Double> colCountMap;
		String val;
		for (Map<String, String> row : columnData) {
			for (String col : colTypeMap.keySet()) {
				if (colTypeList.contains(colTypeMap.get(col)) || colTypeList.contains(IDAConst.KEY_ALL)) {
					colCountMap = countMap.getOrDefault(col, new HashMap<>());
					val = row.get(col);
					if (colCountMap.containsKey(val)) {
						colCountMap.put(val, colCountMap.get(val) + 1.0);
					} else {
						colCountMap.put(val, 1.0);
					}
					countMap.put(col, colCountMap);
				}
			}
		}
		for (String col : countMap.keySet()) {
			SummaryStatistics summaryStatistics = new SummaryStatistics();
			countMap.get(col).values().forEach(summaryStatistics::addValue);
			statsMap.put(col, summaryStatistics.getStandardDeviation());
		}
		return statsMap;
	}

	/**
	 * Method to calculate standard deviation of values of numeric columns of a table
	 *
	 * @param tableData  - table data
	 * @param colTypeMap - column name and datatype map
	 * @return - standard deviation of numeric column values
	 */
	private Map<String, Double> calculateSD(List<Map<String, String>> tableData, Map<String, String> colTypeMap) {
		List<String> columnList = colTypeMap.keySet().stream().filter(c -> colTypeMap.get(c).equals(IDAConst.COLUMN_TYPE_NUMERIC)).collect(Collectors.toList());
		Map<String, SummaryStatistics> statsMap = new HashMap<>();
		for (Map<String, String> rowData : tableData) {
			for (String col : columnList) {
				SummaryStatistics summaryStatistics = statsMap.getOrDefault(col, new SummaryStatistics());
				try {
					summaryStatistics.addValue(Double.parseDouble(rowData.get(col)));
				} catch (Exception e) {
					summaryStatistics.addValue(0.0);
				}
				statsMap.put(col, summaryStatistics);
			}
		}
		System.out.println("debugg");
		return statsMap.keySet().stream().collect(Collectors.toMap(k -> k, k -> statsMap.get(k).getStandardDeviation()));
	}

	/**
	 * Method to write the statistical properties of a table to a resource file for future use
	 *
	 * @param dsName    - name of the dataset
	 * @param tableName - name of the table
	 * @param statProps - map of statistical properties
	 * @throws IOException - file write permission issues
	 */
	private void writeStatsToFile(String dsName, String tableName, Map<String, Map<String, Double>> statProps) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		File fl = new File(getClass().getClassLoader().getResource("").getPath(), IDAConst.STATS_DIR + "/" + dsName + "/" + tableName + ".json");
		if (!fl.exists()) {
			File dir = new File(getClass().getClassLoader().getResource("").getPath(), IDAConst.STATS_DIR);
			if (!dir.exists()) {
				dir.mkdir();
			}
			dir = new File(getClass().getClassLoader().getResource(IDAConst.STATS_DIR).getPath(), dsName);
			if (!dir.exists()) {
				dir.mkdir();
			}
		}
		fl.delete();
		fl.createNewFile();
		mapper.writeValue(fl, statProps);
	}
}
