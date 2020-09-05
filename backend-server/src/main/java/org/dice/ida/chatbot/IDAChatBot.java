package org.dice.ida.chatbot;

import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.dice.ida.action.process.ActionExecutor;
import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.dice.ida.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.TextInput;

/**
 * Class to process messages using Dialogflow library
 *
 * @author Nandeesh Patel
 */
@Component
public class IDAChatBot {
	@Value("${dialogflow.project.id}")
	private String projectId;
	private String sessionId = null;

	@Autowired
	private ApplicationContext appContext;
	@Autowired
	private SessionUtil sessionUtil;
	@Autowired
	private ChatMessageResponse messageResponse;

	/**
	 * Method to process the user chat message and return a valid response
	 *
	 * @param userMessage . Chat message from the user
	 * @return Response to the user chat message
	 */
	public ChatMessageResponse processMessage(ChatUserMessage userMessage) {
		String msgText = userMessage.getMessage();
		messageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
		Map<String, Object> dataMap = messageResponse.getPayload();
		dataMap.put("activeDS", userMessage.getActiveDS());
		dataMap.put("activeTable", userMessage.getActiveTable());
		try {
			// Instantiate the dialogflow client using the credential json file
			SessionsClient sessionsClient = SessionsClient.create();

			if (sessionId == null) {
				sessionId = fetchDfSessionId();
			}
			// Set the session name using the sessionId and projectID
			SessionName session = SessionName.of(projectId, sessionId);

			// Set the text from user and language code for the query
			TextInput.Builder textInput =
					TextInput.newBuilder().setText(msgText).setLanguageCode(IDAConst.BOT_LANGUAGE);

			// Build the query with the TextInput
			QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

			// Detect the intent of the query
			DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);
			QueryResult queryResult = response.getQueryResult();
			// forwarding the flow to action executor

			AutowireCapableBeanFactory factory = appContext.getAutowireCapableBeanFactory();
			ActionExecutor actionExecutor = factory.getBean(ActionExecutor.class, queryResult);
			actionExecutor.processAction(messageResponse);
		} catch (Exception ex) {
			messageResponse.setMessage(IDAConst.BOT_UNAVAILABLE);
			messageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
		}
		return messageResponse;
	}


	public String fetchDfSessionId() {
		String sessionId = null;
		Map<String, Object> sessionMap = sessionUtil.getSessionMap();
		if(!sessionMap.containsKey(IDAConst.DF_SESSION_ID)) {
			// TODO: change the type of key needed as per the requirement
			// Create new session key
			sessionMap.put(IDAConst.DF_SESSION_ID,RandomStringUtils.randomAlphanumeric(IDAConst.DF_SID_LEN));
		}
		sessionId = (String) sessionMap.get(IDAConst.DF_SESSION_ID);
		return sessionId;
	}
}
