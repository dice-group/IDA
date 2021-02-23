package org.dice.ida.action.def;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.protobuf.Value;
import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.dice.ida.model.scatterplotmatrix.ScatterPlotMatrixData;
import org.dice.ida.util.DataUtil;
import org.dice.ida.util.DialogFlowUtil;
import org.dice.ida.util.RDFUtil;
import org.dice.ida.util.ValidatorUtil;
import org.dice.ida.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to handle the scatter plot matrix visualization
 *
 * @author Sourabh Poddar
 */
@Component
public class ScatterPlotMatrixAction implements Action {

	@Autowired
	private DataUtil dataUtil;
	@Autowired
	private DialogFlowUtil dialogFlowUtil;

	private Map<String, Object> payload;
	private String datasetName;
	private String tableName;
	private String selectAll;
	private String refColumn;
	private Map<String, Map<String, Map<String, String>>> instanceMap;
	private Map<String, String> columnMap;
	private List<Map<String, String>> tableData;

	/**
	 * @param paramMap            - parameters from dialogflow
	 * @param chatMessageResponse - API response object
	 */
	@Override
	public void performAction(Map<String, Object> paramMap, ChatMessageResponse chatMessageResponse, ChatUserMessage message) throws Exception {
		int UI_Action = IDAConst.UAC_NRMLMSG;
		StringBuilder textMsg = new StringBuilder(paramMap.get(IDAConst.PARAM_TEXT_MSG).toString());
		ArrayList<String> columnListParameter = new ArrayList<>();
		ArrayList<String> columnList;
		List<Map<String, String>> columnDetail;

		if (ValidatorUtil.preActionValidation(chatMessageResponse)) {
			String vizType = paramMap.get(IDAConst.INTENT_NAME).toString();
			payload = chatMessageResponse.getPayload();
			datasetName = payload.get("activeDS").toString();
			tableName = payload.get("activeTable").toString();
			selectAll = (String) paramMap.get("All_select");
			instanceMap = new RDFUtil().getInstances(vizType);
			List<String> attributeList = new ArrayList<>();
			Map<String, String> attributeTypeMap = new HashMap<>();
			instanceMap.keySet().forEach(instance -> attributeList.addAll(instanceMap.get(instance).keySet()));
			for (String attribute : attributeList) {
				instanceMap.keySet().forEach(instance -> attributeTypeMap.put(attribute, instanceMap.get(instance).get(attribute).get("type")));
			}
			Value paramVal = (Value) paramMap.get(attributeList.get(0));
			boolean onTemporaryData = message.isTemporaryData();
			String filterString = paramMap.get(IDAConst.PARAM_FILTER_STRING).toString();

			if (ValidatorUtil.isStringEmpty(filterString)) {
				double confidence = Double.parseDouble(paramMap.get(IDAConst.PARAM_INTENT_DETECTION_CONFIDENCE).toString());
				if (confidence == 0.0) {
					paramMap.replace(IDAConst.PARAM_TEXT_MSG, IDAConst.INVALID_FILTER);
					chatMessageResponse.setMessage(paramMap.get(IDAConst.PARAM_TEXT_MSG).toString());
					chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
				}
				chatMessageResponse.setMessage(paramMap.get(IDAConst.PARAM_TEXT_MSG).toString());
				chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
			} else {
				if (!(selectAll == null && paramVal == null)) {
					paramVal.getListValue().getValuesList().forEach(str -> columnListParameter.add(str.getStringValue()));
					if (!selectAll.isEmpty()) {
						columnList = getColumnList(datasetName, tableName);

					} else {
						columnList = columnListParameter;

					}
					columnDetail = ValidatorUtil.areParametersValid(datasetName, tableName, columnList, onTemporaryData);
					columnMap = columnDetail.get(0);
					columnList = filterColumns(columnList, attributeTypeMap.get("Column_List"));
					if (columnList.size() < 2) {
						textMsg = new StringBuilder("Please provide more than one Numeric columns");
						dialogFlowUtil.deleteContext("get_ref");
					} else {
						refColumn = (String) paramMap.get(attributeList.get(1));
						if (refColumn != null) {
							if (columnMap.containsKey(refColumn)) {
								columnList.add(refColumn);
								if (onTemporaryData) {
									tableData = message.getActiveTableData();
									tableData = dataUtil.filterData(tableData, filterString, columnList, columnMap);
								} else
									tableData = dataUtil.getData(datasetName, tableName, columnList, filterString, columnMap);
								createScatterPlotMatrixData(tableData, columnList, refColumn);
								textMsg = new StringBuilder("Scatter plot Matrix Loaded");
								UI_Action = IDAConst.UIA_SCATTERPLOT_MATRIX;
								dialogFlowUtil.resetContext();
							} else {
								textMsg = new StringBuilder("Column <b>" + refColumn + "</b> doesn't exist in the table " + tableName);
							}

						}
					}
				}
				chatMessageResponse.setUiAction(UI_Action);
				chatMessageResponse.setMessage(textMsg.toString());
			}
		} else {
			dialogFlowUtil.resetContext();
		}
	}

	private void createScatterPlotMatrixData(List<Map<String, String>> tableData, ArrayList<String> columnList, String ref_column) {
		ScatterPlotMatrixData scatterPlotMatrixData = new ScatterPlotMatrixData();
		scatterPlotMatrixData.setReferenceColumn(ref_column);
		ArrayList<String> numericColumns;
		numericColumns = (ArrayList<String>) columnList.clone();
		numericColumns.remove(ref_column);
		scatterPlotMatrixData.setColumns(numericColumns);
		scatterPlotMatrixData.setItems(tableData);
		payload.put("scatterPlotMatrixData", scatterPlotMatrixData);
	}

	private ArrayList<String> filterColumns(ArrayList<String> columnList, String type) {

		ArrayList<String> columnListTemp;
		columnListTemp = (ArrayList<String>) columnList.clone();
		for (String column : columnList) {
			if (!columnMap.get(column).equalsIgnoreCase(type))
				columnListTemp.remove(column);
		}
		return columnListTemp;
	}

	private ArrayList<String> getColumnList(String datasetName, String tableName) {

		ObjectNode metaData = null;
		ArrayList<String> columns = new ArrayList<>();
		try {
			metaData = new FileUtil().getDatasetMetaData(datasetName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		JsonNode fileDetails = metaData.get(IDAConst.FILE_DETAILS_ATTR);
		for (int i = 0; i < fileDetails.size(); i++) {
			if (tableName.equals(fileDetails.get(i).get(IDAConst.FILE_NAME_ATTR).asText())) {
				JsonNode columnDetails = fileDetails.get(i).get(IDAConst.COLUMN_DETAILS_ATTR);
				for (int j = 0; j < columnDetails.size(); j++)
					columns.add(columnDetails.get(j).get(IDAConst.COLUMN_NAME_ATTR).asText());
			}
		}
		return columns;
	}
}

