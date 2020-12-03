package org.dice.ida.action.def;

import java.util.Map;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.dice.ida.model.Intent;
import org.dice.ida.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component
public class SimpleTextAction implements Action {
	@Autowired
	private SessionUtil sessionUtil;

	@Override
	public void performAction(Map<String, Object> paramMap, ChatMessageResponse resp, ChatUserMessage message) {
		Map<String, Object> sessionMap = sessionUtil.getSessionMap();

		int unknownIntentCount = 0; // counter for how many times IDA could not understand User

		if (paramMap.get(IDAConst.PARAM_INTENT) == Intent.UNKNOWN) {
			if (! sessionMap.containsKey(IDAConst.UNK_INTENT_COUNT)) {
				sessionMap.put(IDAConst.UNK_INTENT_COUNT, unknownIntentCount);
			} else {
				unknownIntentCount = ((int) sessionMap.get(IDAConst.UNK_INTENT_COUNT)) + 1;
				sessionMap.put(IDAConst.UNK_INTENT_COUNT, unknownIntentCount);
			}
		}

		if (unknownIntentCount > 0 && unknownIntentCount % 2 == 0) {
			// If IDA could not understand user twice it will show help message automatically!
			paramMap.put(IDAConst.PARAM_TEXT_MSG, IDAConst.BOT_HELP);
		}

		setSimpleTextResponse(paramMap, resp);
	}

	public static void setSimpleTextResponse(Map<String, Object> paramMap, ChatMessageResponse resp) {
		// Set text message for a normal text response
		String textMsg = paramMap.get(IDAConst.PARAM_TEXT_MSG).toString();
		resp.setMessage(textMsg);
		resp.setUiAction(IDAConst.UAC_NRMLMSG);
	}

}
