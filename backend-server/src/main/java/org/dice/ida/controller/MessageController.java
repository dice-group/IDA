package org.dice.ida.controller;

import org.dice.ida.chatbot.StaticChatbot;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {
	
	@Autowired
	StaticChatbot chatbot;

	@RequestMapping("/checkservice")
	public String greeting() {
		return "Rest service is online!";
	}
	
	@RequestMapping(value= "/chatmessage", method = RequestMethod.POST)
	public ChatMessageResponse handleMessage(@RequestBody ChatUserMessage message) throws Exception {
		System.out.println(message.getMessage());
		return chatbot.processMessage(message);
	}
}
