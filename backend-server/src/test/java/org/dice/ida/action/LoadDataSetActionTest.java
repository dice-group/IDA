package org.dice.ida.action;

import org.dice.ida.action.def.LoadDataSetAction;
import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
		Assertions.assertNull(chatMessageResponse.getPayload().get("dsData"));
	}

	@Test
	void testEmptyDSName() {
		chatUserMessage = new ChatUserMessage();
		chatMessageResponse = new ChatMessageResponse();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(IDAConst.PARAM_DATASET_NAME, "");
		paramMap.put(IDAConst.PARAM_TEXT_MSG, "This is a test");
		loadDataSetAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		Assertions.assertEquals("This is a test", chatMessageResponse.getMessage());
	}

	@Test
	void testLoadDSPos() {
		chatUserMessage = new ChatUserMessage();
		chatMessageResponse = new ChatMessageResponse();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(IDAConst.PARAM_DATASET_NAME, "covid19");
		paramMap.put(IDAConst.PARAM_TEXT_MSG, "This is a test");
		loadDataSetAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		Assertions.assertNotNull(chatMessageResponse.getPayload().get("dsData"));
	}

}
