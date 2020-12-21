package org.dice.ida.controller;

import org.dice.ida.chatbot.IDAChatBot;
import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.dice.ida.util.DialogFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.concurrent.Callable;
import org.slf4j.Logger;

/**
 * Rest controller to handle the normal chat messages from the client
 *
 * @author Nikit
 */
@RestController
public class MessageController {

	@Autowired
	private IDAChatBot idaChatBot;
	private String activeDS;
	private String activeTable;

	@Autowired
	private ChatMessageResponse response;
	@Autowired
	private DialogFlowUtil dialogFlowUtil;

	@Autowired
	@Qualifier("chat-logger")
	private Logger chatLog;

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
	@CrossOrigin(origins = "*", allowCredentials = "true")
	@RequestMapping(value = "/chatmessage", method = RequestMethod.POST)
	public Callable<ChatMessageResponse> handleMessage(@RequestBody ChatUserMessage message) throws Exception {
		activeDS = message.getActiveDS();
		activeTable = message.getActiveTable();
		return () -> {
			chatLog.info("session id:\t" + idaChatBot.fetchDfSessionId() + "\t user message:\t" + message.getMessage());
			ChatMessageResponse response = idaChatBot.processMessage(message);
			chatLog.info("session id:\t" + idaChatBot.fetchDfSessionId() + "\t Response:\t" + response.getMessage());
			return response;
		};
	}

	@ExceptionHandler(AsyncRequestTimeoutException.class)
	public ChatMessageResponse handleAsyncRequestTimeoutException() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
		response.setUiAction(IDAConst.UAC_NRMLMSG);
		Map<String, Object> dataMap = response.getPayload();
		dataMap.put("activeDS", activeDS);
		dataMap.put("activeTable", activeTable);
		response.setMessage(IDAConst.TIMEOUT_MSG);
		dialogFlowUtil.resetContext();
		return response;
	}

}
