package org.dice.ida.action;

import org.dice.ida.action.def.ListVisualizationsAction;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ListVisualizationsActionTest {
	@Autowired
	private ListVisualizationsAction listVisualizationsAction;

	@Test
	void testListVisualizations() {
		ChatUserMessage chatUserMessage = new ChatUserMessage();
		ChatMessageResponse chatMessageResponse = new ChatMessageResponse();
		listVisualizationsAction.performAction(null, chatMessageResponse, chatUserMessage);
		assertTrue(chatMessageResponse.getMessage().startsWith("I can draw "));
	}
}
