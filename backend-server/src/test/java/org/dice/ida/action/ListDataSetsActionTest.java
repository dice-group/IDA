package org.dice.ida.action;

import org.dice.ida.action.def.ListDataSetsAction;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ListDataSetsActionTest {
	@Autowired
	private ListDataSetsAction listDataSetsAction;

	@Test
	void testListDataSets() {
		ChatUserMessage chatUserMessage = new ChatUserMessage();
		ChatMessageResponse chatMessageResponse = new ChatMessageResponse();
		listDataSetsAction.performAction(null, chatMessageResponse, chatUserMessage);
		assertTrue(chatMessageResponse.getMessage().startsWith("I have total "));
	}
}
