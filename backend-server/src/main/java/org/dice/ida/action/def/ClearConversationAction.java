package org.dice.ida.action.def;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.dice.ida.util.DialogFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

@Component
public class ClearConversationAction implements Action {
	@Autowired
	private DialogFlowUtil dialogFlowUtil;

	@Override
	public void performAction(Map<String, Object> paramMap, ChatMessageResponse resp, ChatUserMessage message) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
		dialogFlowUtil.resetContext();
		resp.setMessage(paramMap.get(IDAConst.PARAM_TEXT_MSG).toString());
		resp.setUiAction(IDAConst.UAC_NRMLMSG);
	}
}
