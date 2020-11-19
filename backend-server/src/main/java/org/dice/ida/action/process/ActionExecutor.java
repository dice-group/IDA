package org.dice.ida.action.process;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.dice.ida.action.def.Action;
import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.Intent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.cloud.dialogflow.v2beta1.QueryResult;
import com.google.protobuf.Value;

@Lazy
@Component
@Scope("prototype")
public class ActionExecutor {
	@Autowired
	private ActionMappingHelper mappingHelper;
	private Action action;
	private Map<String, Object> paramMap;
	private QueryResult queryResult;

	public ActionExecutor(QueryResult queryResult) {
		this.queryResult = queryResult;
	}

	@PostConstruct
	public void initialize() {
		// Initiate the instance for the action
		Intent intent = Intent.getForKey(queryResult.getIntent().getDisplayName());
		this.paramMap = createParamMap(queryResult);
		paramMap.put(IDAConst.INTENT_NAME, intent.getKey());
		paramMap.put(IDAConst.FULL_INTENT_NAME,queryResult.getIntent().getDisplayName());
		this.action = mappingHelper.fetchActionInstance(intent.getKey());
	}

	private Map<String, Object> createParamMap(QueryResult queryResult) {

		Map<String, Object> paramMap = new HashMap<>();
		String messageResponseText = queryResult.getFulfillmentText();

		paramMap.put(IDAConst.PARAM_TEXT_MSG, messageResponseText);
		paramMap.put(IDAConst.PARAM_ALL_REQUIRED_PARAMS_PRESENT, queryResult.getAllRequiredParamsPresent());
		paramMap.put(IDAConst.PARAM_INTENT_DETECTION_CONFIDENCE, queryResult.getIntentDetectionConfidence());

		if (queryResult.getParameters().getFieldsCount() > 0) {
			for (Map.Entry<String, Value> entry : queryResult.getParameters().getFieldsMap().entrySet()) {
				if (Value.KindCase.STRING_VALUE.equals(entry.getValue().getKindCase())) {
					paramMap.put(entry.getKey(), entry.getValue().getStringValue());
				} else {
					paramMap.put(entry.getKey(), entry.getValue());
				}
			}
		}

		return paramMap;
	}

	public void processAction(ChatMessageResponse resp) {
		action.performAction(paramMap, resp);
	}

}
