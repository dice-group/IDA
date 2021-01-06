package org.dice.ida.action;

import org.dice.ida.action.def.DefaultAction;
import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DefaultActionTest {
	@Autowired
	private DefaultAction defaultAction;

	@Test
	void testDefaultAction() {
		ChatUserMessage chatUserMessage = new ChatUserMessage();
		ChatMessageResponse chatMessageResponse = new ChatMessageResponse();
		defaultAction.performAction(null, chatMessageResponse, chatUserMessage);
		Assertions.assertEquals(IDAConst.BOT_SOMETHING_WRONG, chatMessageResponse.getMessage());
	}
}
