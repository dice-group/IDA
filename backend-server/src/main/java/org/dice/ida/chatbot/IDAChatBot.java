package org.dice.ida.chatbot;

import org.dice.ida.action.process.ActionExecutor;
import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.TextInput;

import java.util.Map;

/**
 * Class to process messages using Dialogflow library
 * @author Nandeesh Patel
 */
@Component
public class IDAChatBot {
    @Value("${dialogflow.project.id}")
    private String projectId;

    @Value("${dialogflow.session.id}")
    private String sessionId;

    private ChatMessageResponse messageResponse;

    public IDAChatBot(ChatMessageResponse messageResponse) {
        this.messageResponse = messageResponse;
    }

    /**
     * Method to process the user chat message and return a valid response
     * @param userMessage . Chat message from the user
     * @return Response to the user chat message
     */
    public ChatMessageResponse processMessage(ChatUserMessage userMessage) {
        String msgText = userMessage.getMessage();
        messageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
        Map<String, Object> dataMap = messageResponse.getPayload();
        dataMap.put("activeDS", userMessage.getActiveDS());
        dataMap.put("activeTable", userMessage.getActiveTable());
        try{
            // Instantiate the dialogflow client using the credential json file
            SessionsClient sessionsClient = SessionsClient.create();

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
            ActionExecutor actionExecutor = new ActionExecutor(queryResult);
            actionExecutor.processAction(messageResponse);
        }catch (Exception ex){
            messageResponse.setMessage(IDAConst.BOT_UNAVAILABLE);
            messageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
        }
        return messageResponse;
    }
}
