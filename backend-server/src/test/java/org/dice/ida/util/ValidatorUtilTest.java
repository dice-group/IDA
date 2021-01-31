package org.dice.ida.util;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.exception.IDAException;
import org.dice.ida.model.ChatMessageResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
public class ValidatorUtilTest {

	@Test
	void testPreActionValidation() {
		Map<String, Object> payLoad = new HashMap<>();
		ChatMessageResponse chatMessageResponse = new ChatMessageResponse();
		chatMessageResponse.setPayload(payLoad);
		ValidatorUtil.preActionValidation(chatMessageResponse);
		assertEquals(IDAConst.BOT_SOMETHING_WRONG, chatMessageResponse.getMessage());
		payLoad.put("activeDS", "");
		payLoad.put("activeTable", "");
		assertFalse(ValidatorUtil.preActionValidation(chatMessageResponse));
		assertEquals(IDAConst.BOT_LOAD_DS_BEFORE, chatMessageResponse.getMessage());
		payLoad.put("activeDS", "testDatSet");
		assertFalse(ValidatorUtil.preActionValidation(chatMessageResponse));
		assertEquals(IDAConst.BOT_SELECT_TABLE, chatMessageResponse.getMessage());
		payLoad.put("activeTable", "testTable");
		assertTrue(ValidatorUtil.preActionValidation(chatMessageResponse));
	}

	@Test
	void testIsStringEmpty() {
		assertTrue(ValidatorUtil.isStringEmpty(""));
		assertFalse(ValidatorUtil.isStringEmpty("test"));
	}

	@Test
	void testAreParamsValid() throws IDAException, IOException {
		List<String> columnList = new ArrayList<>();
		IDAException exception = assertThrows(IDAException.class, () -> ValidatorUtil.areParametersValid("", "", null, false));
		assertEquals(IDAConst.BOT_LOAD_DS_BEFORE, exception.getMessage());
		exception = assertThrows(IDAException.class, () -> ValidatorUtil.areParametersValid("testDS", "", columnList, false));
		assertEquals(IDAConst.DS_DOES_NOT_EXIST_MSG, exception.getMessage());
		exception = assertThrows(IDAException.class, () -> ValidatorUtil.areParametersValid("covid19", "", columnList, false));
		assertEquals(IDAConst.BOT_SELECT_TABLE, exception.getMessage());
		exception = assertThrows(IDAException.class, () -> ValidatorUtil.areParametersValid("covid19", "testTable", columnList, false));
		assertEquals(IDAConst.TABLE_DOES_NOT_EXIST_MSG, exception.getMessage());
		exception = assertThrows(IDAException.class, () -> ValidatorUtil.areParametersValid("covid19", "testTable", columnList, false));
		assertEquals(IDAConst.TABLE_DOES_NOT_EXIST_MSG, exception.getMessage());
		columnList.add("cluster");
		List<Map<String, String>> response = ValidatorUtil.areParametersValid("covid19", "Case_Time_Series.csv", columnList, true);
		assertEquals(2, response.size());
		assertNotNull(response.get(0));
		assertNotNull(response.get(1));
	}
}
