package org.dice.ida.action.def;

import java.util.Map;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;

public class LoadDataSetAction implements Action {

	@Override
	public void performAction(Map<String, Object> paramMap, ChatMessageResponse resp) {
		// Check if datasetName is provided
		String datasetName = paramMap.get(IDAConst.PARAM_DATASET_NAME).toString();
		if(datasetName != null && !datasetName.isEmpty()) {
			System.out.println("Do something here!");
			SimpleTextAction.setSimpleTextResponse(paramMap, resp);
		} else {
			// Forward the message from the chatbot to the user
			SimpleTextAction.setSimpleTextResponse(paramMap, resp);
		}

	}

}
