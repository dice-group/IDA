package org.dice.ida.config;

import org.dice.ida.chatbot.IDAChatBot;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.google.cloud.dialogflow.v2.SessionsClient;

@Component
public class IdaContextListener {
	
	@EventListener
	public void handleContextCloseEvent(ContextClosedEvent ctxCloseEvt) {
	    // close the DialogFlow's SessionsClient
		SessionsClient client = IDAChatBot.SESSIONS_CLIENT;
		if(client!=null && !client.isShutdown()) {
			client.close();
		}
	}

}
