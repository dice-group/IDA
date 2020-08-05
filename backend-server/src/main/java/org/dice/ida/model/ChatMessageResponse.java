package org.dice.ida.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.dice.ida.serializer.ChatMsgRespSerializer;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
/**
 * Class to model the message response to chat messages from a client
 * @author Nikit
 *
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
@JsonSerialize(using = ChatMsgRespSerializer.class)
public class ChatMessageResponse {
	private String message;
	private int uiAction;
	private Map<Integer, String> predefinedActions;
	private Map<String, Object> payload;
	private Date timestamp;

	public ChatMessageResponse() {
		//Setting timestamp by default at creation
		this.timestamp = new Date();
		this.predefinedActions = new HashMap<Integer, String>();
		this.payload = new HashMap<String, Object>();
	}

	public ChatMessageResponse(String message) {
		this.message = message;
		this.timestamp = new Date();
	}
	
	public void addPayloadItem(String key, Object value) {
		if(this.payload == null) {
			this.payload = new HashMap<String, Object>();
		}
		this.payload.put(key, value);
	}
	
	public void addPredefinedAction(Integer actionCode, String label) {
		if(this.predefinedActions == null) {
			this.predefinedActions = new HashMap<Integer, String>();
		}
		this.predefinedActions.put(actionCode, label);
	}
	//Setter and Getter

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public int getUiAction() {
		return uiAction;
	}

	public void setUiAction(int uiAction) {
		this.uiAction = uiAction;
	}

	public Map<Integer, String> getPredefinedActions() {
		return predefinedActions;
	}

	public void setPredefinedActions(Map<Integer, String> predefinedActions) {
		this.predefinedActions = predefinedActions;
	}

	public Map<String, Object> getPayload() {
		return payload;
	}

	public void setPayload(Map<String, Object> payload) {
		this.payload = payload;
	}
	
}
