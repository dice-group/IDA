package org.dice.ida.util;

import org.dice.ida.controller.MessageController;
import org.dice.ida.model.EntityUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class DialogflowAdminUtilTest {

	@Autowired
	private MessageController messageController;

	@Test
	void addValuesToEntityTest() throws Exception {
		EntityUpdateRequest entityUpdateRequest = new EntityUpdateRequest();
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
		entityUpdateRequest.setEntityId("column_name");
		entityUpdateRequest.setEntityList(entities);
		String actualResponse = messageController.addColumnEntities(entityUpdateRequest);
		String expectedResponse = "Following columns already exist: [Detected State, Date]\n All other columns were added Successfully";
		assertEquals(expectedResponse, actualResponse);
	}

	@Test
	void invalidEntityIdTest() throws Exception {
		EntityUpdateRequest entityUpdateRequest = new EntityUpdateRequest();
		Map<String, List<String>> entities = new HashMap<>(){{
			put("Test Column", new ArrayList<>(){{
				add("Test Column");
				add("testcolumn");
			}});
		}};
		entityUpdateRequest.setEntityId("test_entity");
		entityUpdateRequest.setEntityList(entities);
		String actualResponse = messageController.addColumnEntities(entityUpdateRequest);
		String expectedResponse = "Given entity id does not exist";
		assertEquals(expectedResponse, actualResponse);
	}

	@Test
	void doesDatasetExistTest() throws Exception {
		boolean doesDatasetExist = messageController.checkDatasetExist("covid19");
		assertTrue(doesDatasetExist);
		doesDatasetExist = messageController.checkDatasetExist("election");
		assertFalse(doesDatasetExist);
	}
}
