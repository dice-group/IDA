package org.dice.ida.action.def;

import java.io.File;
import java.util.Map;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.AttributeSummary;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.DataSummary;
import org.dice.ida.util.FileUtil;
import org.dice.ida.util.MetaFileReader;
import org.dice.ida.util.TextUtil;
import org.dice.ida.util.ValidatorUtil;
import org.dice.ida.visualizer.BubbleChartVisualizer;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

import static java.util.stream.Collectors.toList;

/**
 * Class to perform action on bar-graph intent detection.
 *
 * @author Maqbool
 */

public class BubbleChartAction implements Action {
	@Override
	public void performAction(Map<String, Object> paramMap, ChatMessageResponse chatMessageResponse) {
		if (ValidatorUtil.preActionValidation(chatMessageResponse)) {
			try {
				Map<String, Object> payload = chatMessageResponse.getPayload();
				String datasetName = payload.get("activeDS").toString();
				String tableName = payload.get("activeTable").toString();
				String filterString = paramMap.containsKey(IDAConst.PARAM_FILTER_STRING) ? paramMap.get(IDAConst.PARAM_FILTER_STRING).toString() : "";
				BubbleChartVisualizer bubbleChart;

				if (ValidatorUtil.isStringEmpty(filterString)) {
					double confidence = Double.parseDouble(paramMap.get(IDAConst.PARAM_INTENT_DETECTION_CONFIDENCE).toString());

					// If confidence is zero then it means provided filter was incorrect
					if (confidence == 0.0) {
						paramMap.replace(IDAConst.PARAM_TEXT_MSG, IDAConst.INVALID_FILTER);
					}
					SimpleTextAction.setSimpleTextResponse(paramMap, chatMessageResponse);
				} else {
					CSVLoader loader = new CSVLoader();
					//Loads the File
					String path = new FileUtil().fetchSysFilePath("datasets/" + datasetName + "/" + tableName);
					loader.setSource(new File(path));
					Instances data = loader.getDataSet();

					if (ValidatorUtil.isFilterRangeValid(filterString, data)) {

						if (paramMap.containsKey(IDAConst.BC_ONE) && !ValidatorUtil.isStringEmpty(paramMap.get(IDAConst.BC_COL_NAME).toString())) {
							String col_name = paramMap.get(IDAConst.BC_COL_NAME).toString();
							boolean isColExists = false;
							for (int i = 0; i < data.numAttributes(); i++) {
								if (TextUtil.matchString(data.attribute(i).name().trim(), col_name.trim())) {
									isColExists = true;
								}
							}

							if (isColExists) {
								bubbleChart = new BubbleChartVisualizer(new String[]{col_name}, datasetName, tableName, filterString, data);
								payload.put("bubbleChartData", bubbleChart.createBubbleChart());
								chatMessageResponse.setPayload(payload);
								chatMessageResponse.setMessage(IDAConst.BC_LOADED);
								chatMessageResponse.setUiAction(IDAConst.UIA_BUBBLECHART);
							} else {
								chatMessageResponse.setMessage(IDAConst.BC_INVALID_COL);
								chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
							}
						}
						else if (paramMap.containsKey(IDAConst.BC_TWO) && !ValidatorUtil.isStringEmpty(paramMap.get(IDAConst.BC_FIRST_COL).toString()) && !ValidatorUtil.isStringEmpty(paramMap.get(IDAConst.BC_SECOND_COL).toString())) {
							String first_col = paramMap.get(IDAConst.BC_FIRST_COL).toString();
							boolean isFirstColExists = false;

							String second_col = paramMap.get(IDAConst.BC_SECOND_COL).toString();
							boolean isSecondColExists = false;

							// Checking if both column exists on our dataset
							for (int i = 0; i < data.numAttributes(); i++) {
								if (TextUtil.matchString(data.attribute(i).name().trim(), first_col.trim())) {
									isFirstColExists = true;
								}
								if (TextUtil.matchString(data.attribute(i).name().trim(), second_col.trim())) {
									isSecondColExists = true;
								}
							}

							if (!isFirstColExists) {
								chatMessageResponse.setMessage(IDAConst.BC_INVALID_FIRST_COL);
								chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
							} else if (!isSecondColExists) {
								chatMessageResponse.setMessage(IDAConst.BC_INVALID_SECOND_COL);
								chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
							} else if (isFirstColExists && isSecondColExists) {
								// Both Column exists
								DataSummary DS = new MetaFileReader().createDataSummary(datasetName, tableName);
								AttributeSummary secondColSummary = DS.getAttributeSummaryList().stream().filter(x -> TextUtil.matchString(x.getName(), second_col)).collect(toList()).get(0);

								// Now checking if second column is numerical or not
								if (secondColSummary.getType().equalsIgnoreCase("Num")) {
									bubbleChart = new BubbleChartVisualizer(new String[]{first_col, second_col}, datasetName, tableName, filterString, data);
									payload.put("bubbleChartData", bubbleChart.createBubbleChart());
									chatMessageResponse.setPayload(payload);
									chatMessageResponse.setMessage(IDAConst.BC_LOADED);
									chatMessageResponse.setUiAction(IDAConst.UIA_BUBBLECHART);
								} else {
									chatMessageResponse.setMessage(IDAConst.BC_NOT_NUM_SECOND_COL);
									chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
								}
							}
						} else {
							SimpleTextAction.setSimpleTextResponse(paramMap, chatMessageResponse);
						}
					} else {
						// Provided data filter range was incorrect
						chatMessageResponse.setMessage(IDAConst.INVALID_RANGE);
						chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
					}
				}
			} catch (Exception e) {
				chatMessageResponse.setMessage(IDAConst.BOT_SOMETHING_WRONG);
				chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
				System.out.println(e);
			}
		}
	}
}
