package org.dice.ida.action;

import org.dice.ida.action.def.ClearConversationAction;
import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

@SpringBootTest
public class ClearConversationActionTest {
	@Autowired
	private ClearConversationAction clearConversationAction;

	@Test
	void testClearConversation() {
		ChatUserMessage chatUserMessage = new ChatUserMessage();
		ChatMessageResponse chatMessageResponse = new ChatMessageResponse();
		clearConversationAction.performAction(new HashMap<>() {{
			put(IDAConst.PARAM_TEXT_MSG, "");
		}}, chatMessageResponse, chatUserMessage);
		Assertions.assertNull(chatMessageResponse.getActiveContexts());
	}
}
