package org.dice.ida.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class ChatUserMessage {

	private String senderId;
	private String senderName;
	private String message;
	private String activeDS;
	private String activeTable;
	private Date timestamp;
	private List<Map<String, String>> activeTableData;
	private boolean temporaryData;

	public List<Map<String, String>> getActiveTableData() {
		return activeTableData;
	}

	public void setActiveTableData(List<Map<String, String>> activeTableData) {
		this.activeTableData = activeTableData;
	}

	public boolean isTemporaryData() {
		return temporaryData;
	}

	public void setTemporaryData(boolean temporaryData) {
		this.temporaryData = temporaryData;
	}

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
