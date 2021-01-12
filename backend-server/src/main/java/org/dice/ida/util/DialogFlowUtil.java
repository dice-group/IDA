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
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

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
	public void deleteContext(String contextString) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
		String contextName = "projects/" + projectId + "/agent/sessions/" + idaChatBot.fetchDfSessionId() + "/contexts/" + contextString;
		ContextsClient contextsClient = ContextsClient.create(IDAChatbotUtil.getContextsSettings());
		contextsClient.deleteContext(contextName);
	}

	/**
	 * Method to add a context to list of active contexts
	 *
	 * @param contextString - context name
	 */
	public void setContext(String contextString) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
		ContextsClient contextsClient = ContextsClient.create(IDAChatbotUtil.getContextsSettings());
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
}

	/**
	 * Method to add a context to list of active contexts with a lifespan count
	 *
	 * @param contextString - context name
	 * @param lifeSpan      - life span count of the context
	 */
	public void setContext(String contextString, int lifeSpan) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
		ContextsClient contextsClient = ContextsClient.create(IDAChatbotUtil.getContextsSettings());
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
				.setLifespanCount(lifeSpan) // Number of query requests before the context expires.
				.build();

		// Performs the create context request
		contextsClient.createContext(session, context);

	}

	/**
	 * Method to reset all the active contexts at the end of a conversation
	 */
	public void resetContext() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
		ContextsClient contextsClient = ContextsClient.create(IDAChatbotUtil.getContextsSettings());
			// Set the session name using the sessionId (UUID) and projectId (my-project-id)
		SessionName session = SessionName.of(projectId, idaChatBot.fetchDfSessionId());

		// Performs the list contexts request
		for (Context context : contextsClient.listContexts(session).iterateAll()) {
			contextsClient.deleteContext(context.getName());
		}
	}

	/**
	 * Method to get the list of active context names
	 *
	 * @return - List of context names
	 */
	public List<String> getActiveContextList() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
		List<String> contextList = new ArrayList<>();
		ContextsClient contextsClient = ContextsClient.create(IDAChatbotUtil.getContextsSettings());
		// Set the session name using the sessionId (UUID) and projectId (my-project-id)
		SessionName session = SessionName.of(projectId, idaChatBot.fetchDfSessionId());
		String contextName;

		// Performs the list contexts request
		for (Context context : contextsClient.listContexts(session).iterateAll()) {
			contextName = context.getName();
			contextName = contextName.substring(contextName.lastIndexOf("/") + 1);
			contextList.add(contextName);
		}
		return contextList;
	}
}
