package org.dice.ida.action.def;

import java.util.Map;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.springframework.stereotype.Component;
@Component
public class UploadDatasetAction implements Action {

	@Override
	public void performAction(Map<String, Object> paramMap, ChatMessageResponse resp, ChatUserMessage message) {
		String textMsg = paramMap.get(IDAConst.PARAM_TEXT_MSG).toString();
		resp.setMessage(textMsg);
		resp.setUiAction(IDAConst.UAC_UPLDDTMSG);
	}
}
