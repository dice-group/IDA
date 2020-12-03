package org.dice.ida.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.dice.ida.constant.IDAConst;
import org.dice.ida.exception.IDAException;
import org.dice.ida.model.ChatMessageResponse;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import weka.core.Instances;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * This Class contains methods to validate different parts required
 * for IDA operations
 *
 * @author Maqbool
 */
@Component
@Scope("singleton")
public class ValidatorUtil {
	private static final Map<String, String> dsPathMap = new HashMap<String, String>();;

	public static boolean isStringEmpty(String str) {
		return str == null || str.isEmpty();
	}

	public ValidatorUtil() throws IOException {
		// Read dsmap file
		Properties prop = new Properties();
		InputStream input = new FileInputStream(getClass().getClassLoader().getResource(IDAConst.DSMAP_PROP_FILEPATH).getFile());
		prop.load(input);
		String keyStr;
		for (Object key : prop.keySet()) {
			keyStr = key.toString();
			dsPathMap.put(keyStr, prop.getProperty(keyStr));
		}

	}

	/**
	 * We are only interested to validate range filter e.g from 100 to 200
	 * And this function does it!
	 *
	 * @param filterText
	 * @return
	 */
	public static boolean isFilterRangeValid(String filterText, Instances data) {
		String[] tokens = filterText.split(" "); // tokenized filter text
		String filterType = tokens[0]; // Dialogflow makes sure that these tokens are in correct order
		boolean result = true;
		int rangeStart;
		int rangeEnd;

		if (filterType.equalsIgnoreCase(IDAConst.BG_FILTER_FROM)) {
			rangeStart = Integer.parseInt(tokens[1]);
			rangeEnd = Integer.parseInt(tokens[3]);
			result = !(rangeStart >= rangeEnd || rangeStart > data.size() - 1 || rangeEnd > data.size());
		}
		return result;
	}

	/**
	 * This function checks if dataset was loaded and also user has
	 * selected the table
	 * It returns boolean result which reflects validation & in case
	 * of validation failure it sends message back to UI
	 *
	 * @param chatMessageResponse
	 * @return
	 */
	public static boolean preActionValidation(ChatMessageResponse chatMessageResponse) {

		boolean results = false;
		Map<String, Object> payload = chatMessageResponse.getPayload();

		if (payload.get("activeDS") == null || payload.get("activeTable") == null) {
			chatMessageResponse.setMessage(IDAConst.BOT_SOMETHING_WRONG);
		} else {
			String datasetName = payload.get("activeDS").toString();
			String tableName = payload.get("activeTable").toString();
			if (datasetName.isEmpty()) {
				chatMessageResponse.setMessage(IDAConst.BOT_LOAD_DS_BEFORE);
			} else if (tableName.isEmpty()) {
				chatMessageResponse.setMessage(IDAConst.BOT_SELECT_TABLE);
			} else {
				results = true;
			}
		}
		chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
		return results;
	}

	/**
	 * This function validates all the parameters required for any visualization. Following checks are done,
	 * Validation of dataset name for its existence
	 * Validation of table name for its existence
	 * Validation of column names for their existence in the given table
	 * @param dsName - Name of the dataset to be validated
	 * @param tableName - Name of the table to be validated
	 * @param columnList - List of column names to be validated (provided as parameters to the visualization)
	 * @throws IDAException - Throws exception with a message based on the failed scenario
	 */
	public static List<Map<String, String>> areParametersValid(String dsName, String tableName, List<String> columnList, boolean fromTemporaryData) throws IDAException {
		if (isStringEmpty(dsName)) {
			throw new IDAException(IDAConst.BOT_LOAD_DS_BEFORE);
		}
		if(dsPathMap.get(dsName) == null) {
			throw new IDAException(IDAConst.DS_DOES_NOT_EXIST_MSG);
		}
		if (isStringEmpty(tableName)) {
			throw new IDAException(IDAConst.BOT_SELECT_TABLE);
		}
		try {
			Map<String, String> columnTypeMap = new HashMap<>();
			Map<String, String> columnUniquenessMap = new HashMap<>();
			boolean tableExists = false;
			ObjectNode metaData = new FileUtil().getDatasetMetaData(dsName);
			JsonNode fileDetails = metaData.get(IDAConst.FILE_DETAILS_ATTR);
			for (int i = 0; i < fileDetails.size(); i++) {
				if (tableName.equals(fileDetails.get(i).get(IDAConst.FILE_NAME_ATTR).asText())) {
					tableExists = true;
					if(columnList != null && !columnList.isEmpty()) {
						JsonNode columnDetails = fileDetails.get(i).get(IDAConst.COLUMN_DETAILS_ATTR);
						List<String> columns = new ArrayList<>();
						updateColumnDetailMaps(columnDetails, columnTypeMap, columnUniquenessMap, columns);
						if(fromTemporaryData) {
							columns.add("cluster");
							columnTypeMap.put("Cluster", "numeric");
							columnUniquenessMap.put("Cluster", "false");
						}
						for (String column : columnList) {
							if (!columns.contains(column.toLowerCase())) {
								throw new IDAException(column + ": " + IDAConst.BC_INVALID_COL);
							}
						}
					}
				}
			}
			if(!tableExists) {
				throw new IDAException(IDAConst.TABLE_DOES_NOT_EXIST_MSG);
			}
			return new ArrayList<>(){{
				add(columnTypeMap);
				add(columnUniquenessMap);
			}};
		} catch (IOException ex) {
			throw new IDAException(IDAConst.TABLE_DOES_NOT_EXIST_MSG);
		}
	}

	private static void updateColumnDetailMaps(JsonNode columnDetails, Map<String, String> columnTypeMap, Map<String, String> columnUniquenessMap, List<String> columns) {
		String columnName;
		for (int j = 0; j < columnDetails.size(); j++) {
			columnName = columnDetails.get(j).get(IDAConst.COLUMN_NAME_ATTR).asText();
			columns.add(columnName.toLowerCase());
			columnTypeMap.put(columnName, columnDetails.get(j).get(IDAConst.COLUMN_TYPE_ATTR).asText());
			columnUniquenessMap.put(columnName, columnDetails.get(j).get(IDAConst.COLUMN_UNIQUE_ATTR).asText());
		}
	}
}
