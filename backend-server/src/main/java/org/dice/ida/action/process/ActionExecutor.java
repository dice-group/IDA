package org.dice.ida.action.process;

import java.util.HashMap;
import java.util.Map;

import org.dice.ida.action.def.Action;
import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.Intent;

import com.google.cloud.dialogflow.v2.QueryResult;
public class ActionExecutor {
	
	Action action;
	Map<String, String> paramMap;
	
	public ActionExecutor(QueryResult queryResult) {
		// Initiate the instance for the action
		Intent intent = Intent.getForKey(queryResult.getIntent().getDisplayName());
		this.paramMap = createParamMap(queryResult);
		this.action = ActionMapper.fetchActionInstance(intent.getKey(), paramMap);
	}
	
	private Map<String, String> createParamMap(QueryResult queryResult){
		Map<String, String> paramMap = new HashMap<String, String>();
		String messageText = queryResult.getFulfillmentText();
		paramMap.put(IDAConst.PARAM_TEXT_MSG, messageText);
		// TODO: Extract more params
		return paramMap;
	}
	
	public void processAction(ChatMessageResponse resp) {
		action.performAction(paramMap, resp);
	}

}
