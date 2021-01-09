package org.dice.ida.action.def;

import java.util.Map;

import org.dice.ida.exception.IDAException;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.springframework.stereotype.Component;

@Component
public class CauseExceptionAction implements Action {

	@Override
	public void performAction(Map<String, Object> paramMap, ChatMessageResponse resp, ChatUserMessage message) throws IDAException{
		throw new IDAException("An exception has occurred.");
	}
}
