package org.dice.ida.action.def;

import java.util.Map;

import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;

public interface Action {
	void performAction(Map<String, Object> paramMap, ChatMessageResponse resp, ChatUserMessage message) throws Exception;
}
