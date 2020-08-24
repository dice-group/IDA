package org.dice.ida.controller;

import org.dice.ida.chatbot.IDAChatBot;
import org.dice.ida.vizsuggest.VizSuggestOrchestrator;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest controller to handle the normal chat messages from the client
 *
 * @author Nikit
 */
@RestController
public class MessageController {

	@Autowired
	private IDAChatBot idaChatBot;

	/**
	 * Method to check the availability of the rest service
	 *
	 * @return String literal stating the availability
	 */
	@RequestMapping("/checkservice")
	public String greeting() {
		return "Rest service is online!";
	}

	/**
	 * Method to handle the chat messages from the client
	 *
	 * @param message - chat message from the user
	 * @return Response to the chat message
	 * @throws Exception
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/chatmessage", method = RequestMethod.POST)
	public ChatMessageResponse handleMessage(@RequestBody ChatUserMessage message) throws Exception {
		return idaChatBot.processMessage(message);
	}
}
