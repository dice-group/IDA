package org.dice.ida.action;

import org.dice.ida.action.def.LoadDataSetAction;
import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class LoadDataSetActionTest {

	@Autowired
	private LoadDataSetAction loadDataSetAction;
	private ChatUserMessage chatUserMessage;
	private ChatMessageResponse chatMessageResponse;

	@Test
	void testUnavailableSet() {
		chatUserMessage = new ChatUserMessage();
		chatMessageResponse = new ChatMessageResponse();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(IDAConst.PARAM_DATASET_NAME, "null");
		paramMap.put(IDAConst.PARAM_TEXT_MSG, "This is a test");
		loadDataSetAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		assertNull(chatMessageResponse.getPayload().get("dsData"));
	}

	@Test
	void testEmptyDSName() {
		chatUserMessage = new ChatUserMessage();
		chatMessageResponse = new ChatMessageResponse();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(IDAConst.PARAM_DATASET_NAME, "");
		paramMap.put(IDAConst.PARAM_TEXT_MSG, "This is a test");
		loadDataSetAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		assertEquals("This is a test", chatMessageResponse.getMessage());
	}

	@Test
	void testLoadDSPos() {
		chatUserMessage = new ChatUserMessage();
		chatMessageResponse = new ChatMessageResponse();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(IDAConst.PARAM_DATASET_NAME, "covid19");
		paramMap.put(IDAConst.PARAM_TEXT_MSG, "This is a test");
		loadDataSetAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		assertNotNull(chatMessageResponse.getPayload().get("dsData"));
	}

}
