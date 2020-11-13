package org.dice.ida.action.def;

import com.google.cloud.dialogflow.v2beta1.Context;
import com.google.cloud.dialogflow.v2beta1.ContextName;
import com.google.cloud.dialogflow.v2beta1.ContextsClient;
import com.google.cloud.dialogflow.v2beta1.SessionName;
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
		resp.setMessage(textMsg);
		try {
			String source = paramMap.getOrDefault("source", "").toString();
			String destination = paramMap.getOrDefault("destination", "").toString();
			if(!source.isEmpty() && !source.isBlank() && !destination.isBlank() && !destination.isEmpty() && source.equals(destination)) {
				String contextName = "projects/" + projectId + "/agent/sessions/" + idaChatBot.fetchDfSessionId() + "/contexts/" + "get_date";
				deleteContext(contextName);
				resp.setMessage("Both source and destination cannot be same, please give a different city name.");
//				setContext();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		resp.setUiAction(IDAConst.UAC_NRMLMSG);
	}

	public void deleteContext(String contextName) {
		try (ContextsClient contextsClient = ContextsClient.create(IDAChatbotUtil.getContextsSettings())) {
			contextsClient.deleteContext(contextName);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setContext() {
		try (ContextsClient contextsClient = ContextsClient.create(IDAChatbotUtil.getContextsSettings())) {
			// Set the session name using the sessionId (UUID) and projectID (my-project-id)
			SessionName session = SessionName.of(projectId, idaChatBot.fetchDfSessionId());

			// Create the context name with the projectId, sessionId, and contextId
			ContextName contextName = ContextName.newBuilder()
					.setProject(projectId)
					.setSession(idaChatBot.fetchDfSessionId())
					.setContext("get_date")
					.build();

			// Create the context with the context name and lifespan count
			Context context = Context.newBuilder()
					.setName(contextName.toString()) // The unique identifier of the context
					.setLifespanCount(1) // Number of query requests before the context expires.
					.build();

			// Performs the create context request
			Context response = contextsClient.createContext(session, context);
			System.out.format("Context created: %s\n", response);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
