package org.dice.ida.chatbot;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component
public class StaticChatbot {
	@Autowired
	ChatMessageResponse messageResponse;
	
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
