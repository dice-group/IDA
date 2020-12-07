package org.dice.ida.action.def;

import java.io.IOException;
import java.util.Map;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatUserMessage;
import org.dice.ida.util.FileUtil;
import org.springframework.stereotype.Component;
import org.dice.ida.model.ChatMessageResponse;
@Component
public class LoadDataSetAction implements Action {


	@Override
	public void performAction(Map<String, Object> paramMap, ChatMessageResponse resp, ChatUserMessage message) {
		try {
			FileUtil fileUtil = new FileUtil();
			// Check if datasetName is provided
			String datasetName = paramMap.get(IDAConst.PARAM_DATASET_NAME).toString();
			if (datasetName != null && !datasetName.isEmpty()) {
				if (fileUtil.datasetExists(datasetName)) {
					Map<String, Object> dataMap = resp.getPayload();
					dataMap.put("label", datasetName);
					dataMap.put("dsName", datasetName);
					dataMap.put("activeTable", ""); // its required for visualization suggestions
					dataMap.put("dsMd", fileUtil.getDatasetMetaData(datasetName));
					dataMap.put("dsData", fileUtil.getDatasetContent(datasetName));
					resp.setPayload(dataMap);
					resp.setUiAction(IDAConst.UIA_LOADDS);
				} else {
					paramMap.put("'"+IDAConst.PARAM_TEXT_MSG+"'", datasetName + IDAConst.DS_DOES_NOT_EXIST_MSG);
					resp.setUiAction(IDAConst.UAC_NRMLMSG);
				}
				setLoadDatasetResponse(paramMap, resp);
			} else {
				// Forward the message from the chatbot to the user
				SimpleTextAction.setSimpleTextResponse(paramMap, resp);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setLoadDatasetResponse(Map<String, Object> paramMap, ChatMessageResponse resp) {
		String textMsg = paramMap.get(IDAConst.PARAM_TEXT_MSG).toString();
		resp.setMessage(textMsg);
	}

}
