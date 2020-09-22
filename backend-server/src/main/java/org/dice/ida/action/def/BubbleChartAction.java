package org.dice.ida.action.def;

import java.io.File;
import java.util.Map;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.util.FileUtil;
import org.dice.ida.util.TextUtil;
import org.dice.ida.util.ValidatorUtil;
import org.dice.ida.visualizer.BubbleChartVisualizer;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

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
				BubbleChartVisualizer bubbleChart;

				System.out.println(paramMap);

				if (paramMap.containsKey("one") && !ValidatorUtil.isStringEmpty(paramMap.get("col_name").toString())) {

					String col_name = paramMap.get("col_name").toString();
					CSVLoader loader = new CSVLoader();
					//Loads the File
					String path = new FileUtil().fetchSysFilePath("datasets/" + datasetName + "/" + tableName);
					loader.setSource(new File(path));
					Instances data = loader.getDataSet();

					bubbleChart = new BubbleChartVisualizer(new String[]{col_name}, datasetName, tableName, "first 20", data);
					payload.put("bubbleChartData", bubbleChart.createBubbleChart());
					chatMessageResponse.setPayload(payload);
					chatMessageResponse.setMessage("Bubble chart laoded");
					chatMessageResponse.setUiAction(IDAConst.UIA_BUBBLECHART);
//					for (int i = 0; i < data.numAttributes(); i++) {
//						validation
//						if ( TextUtil.matchString( data.attribute(i).name().trim(), col_name.trim() )  ) {
//
//							bubbleChart = new BubbleChartVisualizer(new String[]{col_name}, datasetName, tableName, "first 20", data);
//							payload.put("bubbleChartData", bubbleChart.createBubbleChart());
//							chatMessageResponse.setPayload(payload);
//							chatMessageResponse.setMessage("Bubble chart laoded");
//							chatMessageResponse.setUiAction(IDAConst.UIA_BUBBLECHART);
//						} else {
//							chatMessageResponse.setMessage("Incorrect column name! please try again later");
//							chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
//						}
//					}
				} else if (paramMap.containsKey("two") && !ValidatorUtil.isStringEmpty(paramMap.get("first_col").toString()) && !ValidatorUtil.isStringEmpty(paramMap.get("second_col").toString())) {
					System.out.println("well");
					String first_col = paramMap.get("first_col").toString();
					String second_col = paramMap.get("second_col").toString();
					CSVLoader loader = new CSVLoader();
					//Loads the File
					String path = new FileUtil().fetchSysFilePath("datasets/" + datasetName + "/" + tableName);
					loader.setSource(new File(path));
					Instances data = loader.getDataSet();
					bubbleChart = new BubbleChartVisualizer(new String[]{first_col, second_col}, datasetName, tableName, "first 20", data);
					payload.put("bubbleChartData", bubbleChart.createBubbleChart());
					chatMessageResponse.setPayload(payload);
					chatMessageResponse.setMessage("Bubble chart laoded");
					chatMessageResponse.setUiAction(IDAConst.UIA_BUBBLECHART);
				} else {
					SimpleTextAction.setSimpleTextResponse(paramMap, chatMessageResponse);
				}
			} catch (Exception e) {

			}
		}
	}
}
