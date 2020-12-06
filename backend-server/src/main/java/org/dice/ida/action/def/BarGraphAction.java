package org.dice.ida.action.def;

import java.io.File;
import java.util.Map;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.dice.ida.model.bargraph.BarGraphData;
import org.dice.ida.util.FileUtil;
import org.dice.ida.util.ValidatorUtil;
import org.dice.ida.util.TextUtil;
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
	public void performAction(Map<String, Object> paramMap, ChatMessageResponse chatMessageResponse, ChatUserMessage message) {

		if (ValidatorUtil.preActionValidation(chatMessageResponse)) {
			try {
				Map<String, Object> payload = chatMessageResponse.getPayload();
				String datasetName = payload.get("activeDS").toString();
				String tableName = payload.get("activeTable").toString();
				String xAxis = paramMap.get(IDAConst.PARAM_XAXIS_NAME).toString();
				String yAxis = paramMap.get(IDAConst.PARAM_YAXIS_NAME).toString();
				String filterString = paramMap.get(IDAConst.PARAM_FILTER_STRING).toString();
				BarGraphData barGraph;

				if (!ValidatorUtil.isStringEmpty(filterString) && !ValidatorUtil.isStringEmpty(xAxis) && !ValidatorUtil.isStringEmpty(yAxis)) {
					boolean xaxist = false;
					boolean yaxist = false;
					CSVLoader loader = new CSVLoader();
					//Loads the File
					String path = new FileUtil().fetchSysFilePath("datasets/" + datasetName + "/" + tableName);
					loader.setSource(new File(path));
					Instances data = loader.getDataSet();
					for (int i = 0; i < data.numAttributes(); i++) {
						if (TextUtil.matchString(data.attribute(i).name().trim(),xAxis.trim())) {
							xAxis = data.attribute(i).name();
							xaxist = true;
						}
						if (TextUtil.matchString(data.attribute(i).name().trim(),yAxis.trim())) {
							yAxis = data.attribute(i).name();
							yaxist = true;
						}
					}
					if (xaxist && yaxist && ValidatorUtil.isFilterRangeValid(filterString, data)) {
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
						} else if (!ValidatorUtil.isFilterRangeValid(filterString, data)) {
							chatMessageResponse.setMessage(IDAConst.INVALID_RANGE);
						}
						chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
					}
				} else {
					double confidence = Double.parseDouble(paramMap.get(IDAConst.PARAM_INTENT_DETECTION_CONFIDENCE).toString());

					// Here for incorrect "filterstring" parameter value, confidence will be 0 or 1 otherwise
					// because we have a regex for this parameter (over Dialogflow) so it will either match
					// or wont match
					if (ValidatorUtil.isStringEmpty(filterString) && confidence == 0.0) {
						paramMap.replace(IDAConst.PARAM_TEXT_MSG, IDAConst.INVALID_FILTER);
					}
					SimpleTextAction.setSimpleTextResponse(paramMap, chatMessageResponse);
				}
			} catch (Exception e) {
				chatMessageResponse.setMessage(IDAConst.BOT_SOMETHING_WRONG);
				chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
			}
		}
	}
}
