package org.dice.ida.action.process;

import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.Value;
import org.dice.ida.action.def.Action;
import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.Intent;

import com.google.cloud.dialogflow.v2.QueryResult;
public class ActionExecutor {

	private Action action;
	private Map<String, Object> paramMap;

	public ActionExecutor(QueryResult queryResult) {
		// Initiate the instance for the action
		Intent intent = Intent.getForKey(queryResult.getIntent().getDisplayName());
		this.paramMap = createParamMap(queryResult);
		this.action = ActionMappingHelper.fetchActionInstance(intent.getKey());
	}

	private Map<String, Object> createParamMap(QueryResult queryResult){

		Map<String, Object> paramMap = new HashMap<>();
		String messageResponseText = queryResult.getFulfillmentText();

		paramMap.put(IDAConst.PARAM_TEXT_MSG, messageResponseText);
		paramMap.put(IDAConst.PARAM_ALL_REQUIRED_PARAMS_PRESENT, queryResult.getAllRequiredParamsPresent());

		if (queryResult.getParameters().getFieldsCount() > 0) {
			for (Map.Entry<String, Value> entry : queryResult.getParameters().getFieldsMap().entrySet()) {
				paramMap.put(entry.getKey(), entry.getValue().getStringValue());
			}
		}

		return paramMap;
	}

	public void processAction(ChatMessageResponse resp) {
		action.performAction(paramMap, resp);
	}

}
