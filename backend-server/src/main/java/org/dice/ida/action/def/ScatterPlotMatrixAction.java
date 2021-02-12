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
import org.dice.ida.util.FileUtil;
import org.dice.ida.util.ValidatorUtil;
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
	private StringBuilder textMsg;
	private Object column_list;
	private String select_All;
	private String ref_column;
	private Map<String, String> columnMap;
	private List<Map<String, String>> tableData;
	private int UI_Action = IDAConst.UAC_NRMLMSG;

	/**
	 * @param paramMap            - parameters from dialogflow
	 * @param chatMessageResponse - API response object
	 */
	@Override
	public void performAction(Map<String, Object> paramMap, ChatMessageResponse chatMessageResponse, ChatUserMessage message) throws Exception {
		textMsg = new StringBuilder(paramMap.get(IDAConst.PARAM_TEXT_MSG).toString());
		ArrayList<String> columnListParameter = new ArrayList<>();
		ArrayList<String> columnList = new ArrayList<>();
		List<Map<String, String>> columnDetail = null;

		if (ValidatorUtil.preActionValidation(chatMessageResponse)) {
			payload = chatMessageResponse.getPayload();
			datasetName = payload.get("activeDS").toString();
			tableName = payload.get("activeTable").toString();
			select_All = (String) paramMap.get("All_select");
			Value paramVal = (Value) paramMap.get("column_List");


			if (!(select_All == null && paramVal == null)) {
				paramVal.getListValue().getValuesList().forEach(str -> columnListParameter.add(str.getStringValue()));
				if (!select_All.isEmpty()) {
					columnList = getColumnList(datasetName, tableName);

				} else {
					columnList = columnListParameter;

				}
				columnDetail = ValidatorUtil.areParametersValid(datasetName, tableName, columnList, false);
				columnMap = columnDetail.get(0);
				columnList = filterNonNumericColumn(columnList);
				if(columnList.size()<2)
				{
					textMsg = new StringBuilder("Please provide more than one Numeric columns");
					dialogFlowUtil.deleteContext("get_ref");
				}
				else
				{
					ref_column = (String) paramMap.get("ref_column");
					if(ref_column!=null)
					{
						if(columnMap.containsKey(ref_column))
						{
							columnList.add(ref_column);
							tableData = dataUtil.getData(datasetName, tableName, columnList, "all", columnMap);
							createScatterPlotMatrixData(tableData, columnList, ref_column);
							textMsg = new StringBuilder("Scatter plot Matrix Loaded");
							UI_Action = IDAConst.UIA_SCATTERPLOT_MATRIX;
							dialogFlowUtil.resetContext();
						}
						else
						{
							textMsg = new StringBuilder("Column <b>"+ ref_column+ "</b> doesn't exist in the table "+tableName);

						}

					}
				}
			}
			chatMessageResponse.setUiAction(UI_Action);
			chatMessageResponse.setMessage(textMsg.toString());
		}
	}

	private void createScatterPlotMatrixData(List<Map<String, String>> tableData, ArrayList<String> columnList, String ref_column) {
		ScatterPlotMatrixData scatterPlotMatrixData = new ScatterPlotMatrixData();
		scatterPlotMatrixData.setReferenceColumn(ref_column);
		ArrayList<String> numericColumns = new ArrayList<>();
		numericColumns = (ArrayList<String>) columnList.clone();
		numericColumns.remove(ref_column);
		scatterPlotMatrixData.setColumns(numericColumns);
		Map<String, Double> data = new HashMap<>();
		scatterPlotMatrixData.setItems(tableData);
		payload.put("scatterPlotMatrixData",scatterPlotMatrixData);
	}

	private ArrayList<String> filterNonNumericColumn(ArrayList<String> columnList) {

		ArrayList<String> columnListTemp = new ArrayList<>();
		columnListTemp = (ArrayList<String>) columnList.clone();
		for (String column : columnList)
		{
			if(!columnMap.get(column).equals("numeric"))
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

