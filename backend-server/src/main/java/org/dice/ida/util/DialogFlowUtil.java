package org.dice.ida.util;

import com.google.cloud.dialogflow.v2beta1.Context;
import com.google.cloud.dialogflow.v2beta1.ContextName;
import com.google.cloud.dialogflow.v2beta1.ContextsClient;
import com.google.cloud.dialogflow.v2beta1.SessionName;
import org.dice.ida.chatbot.IDAChatBot;
import org.dice.ida.chatbot.IDAChatbotUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * An util class to manage dialogflow contexts
 *
 * @author Nandeesh Patel, Sourabh Poddar
 */
@Component
public class DialogFlowUtil {

	@Value("${dialogflow.project.id}")
	private String projectId;

	@Autowired
	private IDAChatBot idaChatBot;

	/**
	 * Method to remove a context from list of active contexts
	 *
	 * @param contextString - context name
	 */
	public void deleteContext(String contextString) {
		String contextName = "projects/" + projectId + "/agent/sessions/" + idaChatBot.fetchDfSessionId() + "/contexts/" + contextString;
		try (ContextsClient contextsClient = ContextsClient.create(IDAChatbotUtil.getContextsSettings())) {
			contextsClient.deleteContext(contextName);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Method to add a context to list of active contexts
	 *
	 * @param contextString - context name
	 */
	public void setContext(String contextString) {
		try (ContextsClient contextsClient = ContextsClient.create(IDAChatbotUtil.getContextsSettings())) {
			// Set the session name using the sessionId (UUID) and projectID (my-project-id)
			SessionName session = SessionName.of(projectId, idaChatBot.fetchDfSessionId());

			// Create the context name with the projectId, sessionId, and contextId
			ContextName contextName = ContextName.newBuilder()
					.setProject(projectId)
					.setSession(idaChatBot.fetchDfSessionId())
					.setContext(contextString)
					.build();

			// Create the context with the context name and lifespan count
			Context context = Context.newBuilder()
					.setName(contextName.toString()) // The unique identifier of the context
					.setLifespanCount(1) // Number of query requests before the context expires.
					.build();

			// Performs the create context request
			contextsClient.createContext(session, context);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
