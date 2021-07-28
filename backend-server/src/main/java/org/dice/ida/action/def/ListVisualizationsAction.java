package org.dice.ida.action.def;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.dice.ida.util.RDFUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ListVisualizationsAction implements Action {

	@Autowired
	private RDFUtil rdfUtil;

	@Override
	public void performAction(Map<String, Object> paramMap, ChatMessageResponse resp, ChatUserMessage userMessage) {
		StringBuilder message = new StringBuilder("I can draw ");
		message.append(String.join(", ", rdfUtil.getVisualizationList()));
		resp.setMessage(message.toString());
		resp.setUiAction(IDAConst.UAC_NRMLMSG);
	}
}
