package org.dice.ida.action.def;

import com.google.cloud.dialogflow.v2beta1.QueryResult;
import com.google.protobuf.Value;
import org.dice.ida.chatbot.IDAChatBot;
import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.dice.ida.util.RDFUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Class to handle the user help messages while drawing a visualizations
 *
 * @author Nandeesh Patel
 */
@Component
public class UserHelpAction implements Action {

	@Autowired
	private RDFUtil rdfUtil;

	@Autowired
	private IDAChatBot idaChatBot;

	@Override
	public void performAction(Map<String, Object> paramMap, ChatMessageResponse resp, ChatUserMessage message) throws Exception {
		Map<String, String> userHelpMessageMap = rdfUtil.getUserHelpMessages();
		String topic = paramMap.getOrDefault(IDAConst.HELP_TOPIC_PARAM, "").toString();
		if (topic.isEmpty() || !userHelpMessageMap.containsKey(topic)) {
			QueryResult queryResult = idaChatBot.getIntent(message.getChatbotMessage());
			for (Map.Entry<String, Value> entry : queryResult.getParameters().getFieldsMap().entrySet()) {
				if (IDAConst.HELP_TOPIC_PARAM.equals(entry.getKey())) {
					topic = entry.getValue().getStringValue();
					break;
				}
			}
		}
		String textMsg;
		if (topic.isEmpty() || !userHelpMessageMap.containsKey(topic)) {
			textMsg = IDAConst.UNKNOWN_HELP_TOPIC_MSG;
		} else {
			textMsg = userHelpMessageMap.get(topic);
		}
		resp.setMessage(textMsg);
		resp.setUiAction(IDAConst.UAC_NRMLMSG);
	}
}
