package org.dice.ida.action.def;

import java.io.IOException;
import java.util.Map;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.util.FileUtil;
import org.dice.ida.model.ChatMessageResponse;

public class LoadDataSetAction implements Action {


	@Override
	public void performAction(Map<String, Object> paramMap, ChatMessageResponse resp) {
		try {
			FileUtil fileUtil = new FileUtil();
			// Check if datasetName is provided
			String datasetName = paramMap.get(IDAConst.PARAM_DATASET_NAME).toString();
			if(datasetName != null && !datasetName.isEmpty()) {
				if (fileUtil.datasetExists(datasetName)) {
					Map<String, Object> dataMap = resp.getPayload();
					dataMap.put("label", datasetName);
					dataMap.put("dsName", datasetName);
					dataMap.put("dsMd", fileUtil.getDatasetMetaData(datasetName));
					resp.setPayload(dataMap);
					resp.setUiAction(IDAConst.UIA_LOADDS);
				}
				SimpleTextAction.setSimpleTextResponse(paramMap, resp);
			} else {
				// Forward the message from the chatbot to the user
				SimpleTextAction.setSimpleTextResponse(paramMap, resp);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
