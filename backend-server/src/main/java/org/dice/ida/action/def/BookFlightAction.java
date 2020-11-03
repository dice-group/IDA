package org.dice.ida.action.def;

import com.google.cloud.dialogflow.v2beta1.ContextsClient;
import org.dice.ida.chatbot.IDAChatBot;
import org.dice.ida.chatbot.IDAChatbotUtil;
import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BookFlightAction implements Action {
	@org.springframework.beans.factory.annotation.Value("${dialogflow.project.id}")
	private String projectId;

	@Autowired
	private IDAChatBot idaChatBot;

	@Override
	public void performAction(Map<String, Object> paramMap, ChatMessageResponse resp) {
		String textMsg = paramMap.get(IDAConst.PARAM_TEXT_MSG).toString();
		try {
			String contextName = "projects/" + projectId + "/agent/sessions/" + idaChatBot.fetchDfSessionId() + "/contexts/" + "get_destination";
			deleteContext(contextName);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		resp.setMessage(textMsg);
		resp.setUiAction(IDAConst.UAC_NRMLMSG);

	}

	public void deleteContext(String contextName) {
		try (ContextsClient contextsClient = ContextsClient.create(IDAChatbotUtil.getContextsSettings())) {
			contextsClient.deleteContext(contextName);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
