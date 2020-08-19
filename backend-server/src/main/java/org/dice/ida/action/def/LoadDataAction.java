package org.dice.ida.action.def;

import java.util.Map;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;

public class LoadDataAction implements Action {

	@Override
	public void performAction(Map<String, String> paramMap, ChatMessageResponse resp) {
		// Check if datasetName is provided
		String datasetName = paramMap.get(IDAConst.PARAM_DATASET_NAME).trim();
		if(datasetName!=null && !datasetName.isEmpty()) {
			// TODO: check for the dataset with available name and load it/ if dataset is not available then add the appropriate message
		} else {
			// Forward the message from the chatbot to the user
			SimpleTextAction.setSimpleTextResponse(paramMap, resp);
		}

	}

}
