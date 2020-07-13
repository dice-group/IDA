package org.dice.ida.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import org.dice.ida.model.ChatUserMessage;
import org.dice.ida.model.ChatMessageResponse;

@Controller
public class ChatController {
	
	@MessageMapping("/msg")
	@SendTo("/topic/ida")
	public ChatMessageResponse handleMessaging(ChatUserMessage message) throws Exception {
		Thread.sleep(1000); // simulate delay

		return new ChatMessageResponse(message.getMessage());
	}
}
