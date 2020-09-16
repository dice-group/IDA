package org.dice.ida.action.def;

import java.io.File;
import java.util.Map;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.bargraph.BarGraphData;
import org.dice.ida.util.FileUtil;
import org.dice.ida.visualizer.BarGraphVisualizer;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

/**
 * Class to perform action on bar-graph intent detection.
 *
 * @author Sourabh
 */

public class BarGraphAction implements Action {

	@Override
	public void performAction(Map<String, Object> paramMap, ChatMessageResponse chatMessageResponse) {

		try {
			Map<String, Object> payload = chatMessageResponse.getPayload();
			if (payload.get("activeDS") == null || payload.get("activeTable") == null) {
				chatMessageResponse.setMessage(IDAConst.BOT_SOMETHING_WRONG);
				chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
				return;
			} else {
				String datasetName = payload.get("activeDS").toString();
				String tableName = payload.get("activeTable").toString();
				if (datasetName.isEmpty() || tableName.isEmpty()) {
					if (datasetName.isEmpty()) {
						chatMessageResponse.setMessage(IDAConst.BOT_LOAD_DS_BEFORE);
					} else if (tableName.isEmpty()) {
						chatMessageResponse.setMessage(IDAConst.BOT_SELECT_TABLE);
					}
					chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
				} else {
					String xAxis = paramMap.get(IDAConst.PARAM_XAXIS_NAME).toString();
					String yAxis = paramMap.get(IDAConst.PARAM_YAXIS_NAME).toString();
					String filterString = paramMap.get(IDAConst.PARAM_FILTER_STRING).toString();
					BarGraphData barGraph;

					if (!isStringEmpty(filterString) && !isStringEmpty(xAxis) && !isStringEmpty(yAxis)) {
						boolean xaxist = false;
						boolean yaxist = false;
						CSVLoader loader = new CSVLoader();
						//Loads the File
						String path = new FileUtil().fetchSysFilePath("datasets/" + datasetName + "/" + tableName);
						loader.setSource(new File(path));
						Instances data = loader.getDataSet();
						for (int i = 0; i < data.numAttributes(); i++) {
							if (data.attribute(i).name().trim().equalsIgnoreCase(xAxis.trim())) {
								xAxis = data.attribute(i).name();
								xaxist = true;
							}
							if (data.attribute(i).name().trim().equalsIgnoreCase(yAxis.trim())) {
								yAxis = data.attribute(i).name();
								yaxist = true;
							}
						}
						if (xaxist && yaxist && isFilterRangeValid(filterString, data)) {
							barGraph = new BarGraphVisualizer(xAxis, yAxis, datasetName, tableName, filterString, data).createBarGraph();
							payload.put("barGraphData", barGraph);
							chatMessageResponse.setPayload(payload);
							chatMessageResponse.setMessage(IDAConst.BAR_GRAPH_LOADED);
							chatMessageResponse.setUiAction(IDAConst.UIA_BARGRAPH);
						} else {
							if (!xaxist) {
								// If x-axis was invalid then
								chatMessageResponse.setMessage(IDAConst.INVALID_X_AXIS_NAME);
							} else if (!yaxist) {
								// If y-axis was invalid then
								chatMessageResponse.setMessage(IDAConst.INVALID_Y_AXIS_NAME);
							} else if (!isFilterRangeValid(filterString, data)) {
								chatMessageResponse.setMessage(IDAConst.INVALID_RANGE);
							}
							chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
						}
					} else {
						double confidence = Double.parseDouble(paramMap.get(IDAConst.PARAM_INTENT_DETECTION_CONFIDENCE).toString());

						// Here for incorrect "filterstring" parameter value, confidence will be 0 or 1 otherwise
						// because we have a regex for this parameter (over Dialogflow) so it will either match
						// or wont match
						if (isStringEmpty(filterString) && confidence == 0.0) {
							paramMap.replace(IDAConst.PARAM_TEXT_MSG, IDAConst.INVALID_FILTER);
						}
						SimpleTextAction.setSimpleTextResponse(paramMap, chatMessageResponse);
					}
				}
			}
		} catch (Exception e) {
			chatMessageResponse.setMessage(IDAConst.BOT_SOMETHING_WRONG);
			chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
		}

	}

	public boolean isStringEmpty(String str) {
		return str == null || str.isEmpty();
	}

	/**
	 * We are only interested to validate range filter e.g from 100 to 200
	 * And this function does it!
	 * @param filterText
	 * @return
	 */
	public boolean isFilterRangeValid(String filterText, Instances data) {
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


}