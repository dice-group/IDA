package org.dice.ida.action;

import org.dice.ida.controller.MessageController;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.dice.ida.util.RDFUtil;
import org.dice.ida.util.SessionUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ListVisualizationsActionTest {
	@Autowired
	private MessageController messageController;
	@Autowired
	private SessionUtil sessionUtil;
	@Autowired
	private RDFUtil rdfUtil;

	@Test
	void testListVisualizations() throws Exception {
		ChatUserMessage chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("list all visualizations");
		ChatMessageResponse chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		String expectedMsg = "I can draw " + String.join(", ", rdfUtil.getVisualizationList());
		assertEquals(expectedMsg, chatMessageResponse.getMessage());
		sessionUtil.resetSessionId();
	}
}
