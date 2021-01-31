package org.dice.ida.action;

import org.dice.ida.controller.MessageController;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.dice.ida.util.SessionUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
public class LoadDataSetActionTest {

	private ChatUserMessage chatUserMessage;
	private ChatMessageResponse chatMessageResponse;
	@Autowired
	private MessageController messageController;
	@Autowired
	private SessionUtil sessionUtil;

	@Test
	void testUnavailableSet() throws Exception {
		chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("load null dataset");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		assertEquals("'<b>null</b>' dataset does not exist. <br/><br/> You can ask to \"list all datasets\" to confirm if your dataset is present.", chatMessageResponse.getMessage());
		assertNull(chatMessageResponse.getPayload().get("dsData"));
		sessionUtil.resetSessionId();
	}

	@Test
	void testEmptyDSName() throws Exception {
		chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("load dataset");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		assertEquals("ok. tell me name of the dataset?", chatMessageResponse.getMessage());
		assertNull(chatMessageResponse.getPayload().get("dsData"));
		sessionUtil.resetSessionId();
	}

	@Test
	void testLoadDSPos() throws Exception {
		chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("load covid19 dataset");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		assertEquals("<b>'covid19'</b> dataset has been loaded", chatMessageResponse.getMessage());
		assertNotNull(chatMessageResponse.getPayload().get("dsData"));
		sessionUtil.resetSessionId();
	}

}
