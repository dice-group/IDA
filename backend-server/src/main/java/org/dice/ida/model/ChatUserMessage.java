package org.dice.ida.model;

import java.util.Date;

public class ChatUserMessage {

	private String senderId;
	private String senderName;
	private String message;
	private String activeDS;
	private String activeTable;
	private Date timestamp;

	public ChatUserMessage() {
	}

	public ChatUserMessage(String message) {
		this.message = message;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getActiveDS() {
		return activeDS;
	}

	public void setActiveDS(String activeDS) {
		this.activeDS = activeDS;
	}

	public String getActiveTable() {
		return activeTable;
	}

	public void setActiveTable(String activeTable) {
		this.activeTable = activeTable;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}
