package org.dice.ida.action;

import org.dice.ida.controller.MessageController;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.dice.ida.util.SessionUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
public class ClearConversationActionTest {
	@Autowired
	private MessageController messageController;
	@Autowired
	private SessionUtil sessionUtil;

	@Test
	void testClearConversation() throws Exception {
		ChatUserMessage chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("load covid dataset");
		ChatMessageResponse chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		assertNotNull(chatMessageResponse.getActiveContexts());
		chatUserMessage.setMessage("clear");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		assertTrue(chatMessageResponse.getActiveContexts().isEmpty());
		sessionUtil.resetSessionId();
	}
}
