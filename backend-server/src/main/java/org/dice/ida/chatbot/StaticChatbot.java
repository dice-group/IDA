package org.dice.ida.chatbot;

import org.dice.ida.constant.IDAConst;

import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/**
 * Dummy class mimicking the functionality of a chatbot 
 * @author Nikit
 *
 */
@Component
public class StaticChatbot {
	@Autowired
	private ChatMessageResponse messageResponse;
	/**
	 * Method to process the user chat message and return a valid response
	 * @param userMessage . Chat message from the user
	 * @return Response to the user chat message
	 */
	public ChatMessageResponse processMessage(ChatUserMessage userMessage) {
		String msgText = userMessage.getMessage();
		String respMsg = "I am sorry! I did not understand that, please try rephrasing that.";
		messageResponse.setMessage(respMsg);
		messageResponse.setUiAction(IDAConst.UAC_NrmlMsg);
		//process the user message
		//upload dataset
		if(msgText.matches(".*[uU]pload.*[dD]ataset.*")) {
			messageResponse.setMessage("");
			messageResponse.setUiAction(IDAConst.UAC_UpldDtMsg);
		}
		//respond if hello
		else if(msgText.matches(".*(([hH]i)|([hH]ello)).*")) {
			messageResponse.setMessage("Hi there! how can I help you?");
		}
		return messageResponse;
	}

}
