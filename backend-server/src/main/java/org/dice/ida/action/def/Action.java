package org.dice.ida.action.def;

import java.util.Map;

import org.dice.ida.model.ChatMessageResponse;

public interface Action {
	public void performAction(Map<String, String> paramMap, ChatMessageResponse resp);
}
