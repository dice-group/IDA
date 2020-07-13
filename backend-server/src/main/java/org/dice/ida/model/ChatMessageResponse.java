package org.dice.ida.model;

import java.util.Date;

public class ChatMessageResponse {
	private String content;
	private Date timestamp;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}
