package org.dice.ida.util;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SuggestionUtil {

	public static Map<String, Double> getCovariance(String datasetName, String tableName, Map<String, String> columnMap) throws IOException {
		List<Map<String, String>> extractedData = new ArrayList<>();
		String path = new FileUtil().fetchSysFilePath("datasets/" + datasetName + "/" + tableName);
		List<Map<String, String>> fileData = new FileUtil().convertToMap(new File(path));
		List<String> columnList = new ArrayList<String>();
		columnList = fileData.get(0).keySet().stream().collect(Collectors.toList());
		for (int  i = 0; i < fileData.size(); i++) {
			Map<String, String> dataRow = new HashMap<>();
			for (String column : columnList) {
				String columnValue = fileData.get(i).get(column);
				if(columnMap.get(column).equalsIgnoreCase("Numeric"))
					if(DbUtils.manageNullValues(columnValue).equals("UNKNOWN"))
					{
						dataRow.put(column, "0.0");
					}
					else
					dataRow.put(column, DbUtils.manageNullValues(columnValue.replaceAll(",",".")));

			}
			extractedData.add(dataRow);
		}
		columnList = extractedData.get(0).keySet().stream().collect(Collectors.toList());
		Map<String, Double> covarianceMatrix= new HashMap<String, Double>();
		for (String column1 : columnList)
		{
			for (String column2 : columnList)
			{
				if(!column1.equalsIgnoreCase(column2))
				{
					Double covariance = getCovariancepair(extractedData, column1,  column2);
					covarianceMatrix.put(column1+","+column2,covariance);
				}
			}
		}

		return covarianceMatrix;

	}

	private static Double getCovariancepair(List<Map<String, String>> fileData, String column1, String column2) {

		double[][] matrix = new double[fileData.size()][2];

		for (int i =0; i<fileData.size();i++)
		{
			matrix[i][0] = Double.parseDouble(fileData.get(i).get(column1));
			matrix[i][1] = Double.parseDouble(fileData.get(i).get(column2));

		}
		RealMatrix mx = MatrixUtils.createRealMatrix(matrix);
		RealMatrix cov = new Covariance(mx).getCovarianceMatrix();


		return cov.getColumn(0)[1];

	}
}
