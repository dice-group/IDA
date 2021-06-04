package org.dice.ida.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class DialogflowAdminUtilTest {

	@Autowired
	private DialogFlowAdminUtil dialogFlowAdminUtil;

	@Test
	void addValuesToEntityTest() throws Exception {
		Map<String, List<String>> entities = new HashMap<>(){{
			put("Detected State", new ArrayList<>(){{
				add("Detected State");
				add("State");
			}});
			put("Date", new ArrayList<>(){{
				add("Date");
				add("date");
			}});
		}};
		String actualResponse = dialogFlowAdminUtil.addValuesToEntity("column_name", entities);
		String expectedResponse = "Following columns already exist: [Detected State, Date]\n All other columns were added Successfully";
		assertEquals(expectedResponse, actualResponse);
	}

	@Test
	void invalidEntityIdTest() throws Exception {
		Map<String, List<String>> entities = new HashMap<>(){{
			put("Test Column", new ArrayList<>(){{
				add("Test Column");
				add("testcolumn");
			}});
		}};
		String actualResponse = dialogFlowAdminUtil.addValuesToEntity("test_entity", entities);
		String expectedResponse = "Given entity id does not exist";
		assertEquals(expectedResponse, actualResponse);
	}
}
