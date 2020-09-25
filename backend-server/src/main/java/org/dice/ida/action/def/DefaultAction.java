package org.dice.ida.action.def;

import java.util.Map;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.springframework.stereotype.Component;
@Component
public class DefaultAction implements Action {

	@Override
	public void performAction(Map<String, Object> paramMap, ChatMessageResponse resp) {
		resp.setMessage(IDAConst.BOT_SOMETHING_WRONG);
		resp.setUiAction(IDAConst.UAC_NRMLMSG);
	}

}
