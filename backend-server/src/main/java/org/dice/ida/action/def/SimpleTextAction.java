package org.dice.ida.action.def;

import java.util.Map;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;

public class SimpleTextAction implements Action {

	@Override
	public void performAction(Map<String, String> paramMap, ChatMessageResponse resp) {
		setSimpleTextResponse(paramMap, resp);
	}

	public static void setSimpleTextResponse(Map<String, String> paramMap, ChatMessageResponse resp) {
		// Set text message for a normal text response
		String textMsg = paramMap.get(IDAConst.PARAM_TEXT_MSG);
		resp.setMessage(textMsg);
		resp.setUiAction(IDAConst.UAC_NRMLMSG);
	}

}
