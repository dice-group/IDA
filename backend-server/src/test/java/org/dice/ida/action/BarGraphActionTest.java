package org.dice.ida.action;

import com.google.protobuf.Struct;
import org.dice.ida.action.def.VisualizeAction;
import org.dice.ida.constant.IDAConst;
import org.dice.ida.controller.MessageController;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.dice.ida.model.bargraph.BarGraphData;
import org.dice.ida.model.bargraph.BarGraphItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.google.protobuf.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
public class BarGraphActionTest {
	@Autowired
	private MessageController messageController;
	private ChatUserMessage chatUserMessage;
	private ChatMessageResponse chatMessageResponse;
	@Autowired
	private VisualizeAction visualizeAction;

	@Test
	void testBarGraphFlow() {
		chatUserMessage = new ChatUserMessage();
		chatMessageResponse = new ChatMessageResponse();
		chatMessageResponse.setPayload(new HashMap<>() {{
			put("activeDS", "covid19");
			put("activeTable", "Patient_Data_Before_20-04-2020.csv");
		}});
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(IDAConst.INTENT_NAME, IDAConst.VIZ_TYPE_BAR_CHART);
		paramMap.put("records-selection", "first 5");
		paramMap.put("X-Axis", "Detected State");
		paramMap.put("Y-Axis", "Detected State");
		paramMap.put(IDAConst.PARAM_TEXT_MSG, "This is a test");
		visualizeAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		BarGraphData barGraphData = (BarGraphData) chatMessageResponse.getPayload().get("barGraphData");
		List<BarGraphItem> barGraphItemList = new ArrayList<>();
		barGraphItemList.add(new BarGraphItem("Delhi", 1.0));
		barGraphItemList.add(new BarGraphItem("Kerala", 3.0));
		barGraphItemList.add(new BarGraphItem("Telangana", 1.0));
		assertNotNull(barGraphData);
		assertEquals(barGraphData.getItems(), barGraphItemList);
	}

	@Test
	void testBarGraphSteps() {
		chatUserMessage = new ChatUserMessage();
		chatMessageResponse = new ChatMessageResponse();
		chatMessageResponse.setPayload(new HashMap<>() {{
			put("activeDS", "covid19");
			put("activeTable", "Patient_Data_Before_20-04-2020.csv");
		}});
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(IDAConst.INTENT_NAME, IDAConst.VIZ_TYPE_BAR_CHART);
		paramMap.put("records-selection", "first 5");
		paramMap.put("X-Axis", "Age Bracket");
		paramMap.put(IDAConst.PARAM_TEXT_MSG, "This is a test");
		visualizeAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		assertEquals("Age Bracket can be used as:<br><ul><li><b>Bins</b> - Group of N values</li><li><b>Non Unique</b> - As it is</li></ul><br/>\n Which option do you need (Bins / Non Unique)?", chatMessageResponse.getMessage());
		paramMap.put("X-Axis_type", "unique");
		visualizeAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		assertEquals("Age Bracket cannot be used as unique. Please provide correct type.", chatMessageResponse.getMessage());
		paramMap.put("X-Axis_type", "bins");
		visualizeAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		assertEquals("What should be the size of each bin?<br/>Eg: 10, 25, 15, twenty, twelve", chatMessageResponse.getMessage());
		paramMap.put("bin_size", Value.newBuilder().setNumberValue(10).build());
		visualizeAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		assertEquals("Which column values should be mapped to Y-Axis?", chatMessageResponse.getMessage());
	}

	@Test
	void testBarGraphNonSuitableColumn() {
		chatUserMessage = new ChatUserMessage();
		chatMessageResponse = new ChatMessageResponse();
		chatMessageResponse.setPayload(new HashMap<>() {{
			put("activeDS", "covid19");
			put("activeTable", "Case_Time_Series.csv");
		}});
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(IDAConst.INTENT_NAME, IDAConst.VIZ_TYPE_BAR_CHART);
		paramMap.put("records-selection", "first 5");
		paramMap.put("X-Axis", "Date");
		paramMap.put("X-Axis_type", "unique");
		paramMap.put("Y-Axis", "Date");
		paramMap.put(IDAConst.PARAM_TEXT_MSG, "This is a test");
		visualizeAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		assertEquals("Date cannot be used as Y-Axis. Please give a different column?", chatMessageResponse.getMessage());
	}

	@Test
	void testBarGraphNumBins() {
		chatUserMessage = new ChatUserMessage();
		chatMessageResponse = new ChatMessageResponse();
		chatMessageResponse.setPayload(new HashMap<>() {{
			put("activeDS", "covid19");
			put("activeTable", "Patient_Data_Before_20-04-2020.csv");
		}});
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(IDAConst.INTENT_NAME, IDAConst.VIZ_TYPE_BAR_CHART);
		paramMap.put("records-selection", "first 10");
		paramMap.put("X-Axis", "Age Bracket");
		paramMap.put("X-Axis_type", "bins");
		paramMap.put("bin_size", Value.newBuilder().setNumberValue(10).build());
		paramMap.put("Y-Axis", "Age Bracket");
		paramMap.put("Y-Axis_type", "count of");
		paramMap.put(IDAConst.PARAM_TEXT_MSG, "This is a test");
		visualizeAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		BarGraphData barGraphData = (BarGraphData) chatMessageResponse.getPayload().get("barGraphData");
		List<BarGraphItem> barGraphItemList = new ArrayList<>();
		barGraphItemList.add(new BarGraphItem("0.0 - 9.0", 0.0));
		barGraphItemList.add(new BarGraphItem("10.0 - 19.0", 0.0));
		barGraphItemList.add(new BarGraphItem("20.0 - 29.0", 2.0));
		barGraphItemList.add(new BarGraphItem("30.0 - 39.0", 0.0));
		barGraphItemList.add(new BarGraphItem("40.0 - 49.0", 1.0));
		barGraphItemList.add(new BarGraphItem("50.0 - 59.0", 4.0));
		barGraphItemList.add(new BarGraphItem("60.0 - 69.0", 1.0));
		barGraphItemList.add(new BarGraphItem("UNKNOWN", 2.0));
		assertNotNull(barGraphData);
		assertEquals(barGraphData.getItems(), barGraphItemList);
	}

	@Test
	void testBarGraphDateBins() {
		chatUserMessage = new ChatUserMessage();
		chatMessageResponse = new ChatMessageResponse();
		chatMessageResponse.setPayload(new HashMap<>() {{
			put("activeDS", "covid19");
			put("activeTable", "Patient_Data_Before_20-04-2020.csv");
		}});
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(IDAConst.INTENT_NAME, IDAConst.VIZ_TYPE_BAR_CHART);
		paramMap.put("records-selection", "all");
		paramMap.put("X-Axis", "Date Announced");
		paramMap.put("X-Axis_type", "bins");
		paramMap.put(IDAConst.PARAM_TEXT_MSG, "This is a test");
		visualizeAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		assertEquals("What should be the duration of each bin?<br/>Eg: 1 week, 2 weeks, 3 months", chatMessageResponse.getMessage());
		Struct struct = Struct.newBuilder().putAllFields(new HashMap<>() {{
			put("unit", Value.newBuilder().setStringValue("mo").build());
			put("amount", Value.newBuilder().setNumberValue(1).build());
		}}).build();
		paramMap.put("bin_size", Value.newBuilder().setStructValue(struct).build());
		paramMap.put("Y-Axis", "Date Announced");
		visualizeAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		BarGraphData barGraphData = (BarGraphData) chatMessageResponse.getPayload().get("barGraphData");
		List<BarGraphItem> barGraphItemList = new ArrayList<>();
		barGraphItemList.add(new BarGraphItem("January-2020", 1.0));
		barGraphItemList.add(new BarGraphItem("February-2020", 2.0));
		barGraphItemList.add(new BarGraphItem("March-2020", 1632.0));
		barGraphItemList.add(new BarGraphItem("April-2020", 15729.0));
		assertNotNull(barGraphData);
		assertEquals(barGraphData.getItems(), barGraphItemList);
	}

	@Test
	void testBarGraphDateWeekBins() {
		chatUserMessage = new ChatUserMessage();
		chatMessageResponse = new ChatMessageResponse();
		chatMessageResponse.setPayload(new HashMap<>() {{
			put("activeDS", "covid19");
			put("activeTable", "ICMR_Tests_Datewise.csv");
		}});
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(IDAConst.INTENT_NAME, IDAConst.VIZ_TYPE_BAR_CHART);
		paramMap.put("records-selection", "first 50");
		paramMap.put("X-Axis", "Tested As Of");
		paramMap.put("X-Axis_type", "bins");
		Struct struct = Struct.newBuilder().putAllFields(new HashMap<>() {{
			put("unit", Value.newBuilder().setStringValue("wk").build());
			put("amount", Value.newBuilder().setNumberValue(2).build());
		}}).build();
		paramMap.put("bin_size", Value.newBuilder().setStructValue(struct).build());
		paramMap.put("Y-Axis", "Total Positive Cases");
		paramMap.put("Y-Axis_type", "sum of");
		paramMap.put(IDAConst.PARAM_TEXT_MSG, "This is a test");
		visualizeAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		BarGraphData barGraphData = (BarGraphData) chatMessageResponse.getPayload().get("barGraphData");
		List<BarGraphItem> barGraphItemList = new ArrayList<>();
		barGraphItemList.add(new BarGraphItem("19-04-20 to 02-05-20", 62914.0));
		barGraphItemList.add(new BarGraphItem("22-03-20 to 04-04-20", 16094.0));
		barGraphItemList.add(new BarGraphItem("05-04-20 to 18-04-20", 107431.581));
		barGraphItemList.add(new BarGraphItem("08-03-20 to 21-03-20", 1606.0));
		barGraphItemList.add(new BarGraphItem("UNKNOWN", 0.0));
		assertNotNull(barGraphData);
		assertEquals(barGraphData.getItems(), barGraphItemList);
	}

	@Test
	void testBarGraphDateDayBins() {
		chatUserMessage = new ChatUserMessage();
		chatMessageResponse = new ChatMessageResponse();
		chatMessageResponse.setPayload(new HashMap<>() {{
			put("activeDS", "covid19");
			put("activeTable", "ICMR_Tests_Datewise.csv");
		}});
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(IDAConst.INTENT_NAME, IDAConst.VIZ_TYPE_BAR_CHART);
		paramMap.put("records-selection", "first 50");
		paramMap.put("X-Axis", "Tested As Of");
		paramMap.put("X-Axis_type", "bins");
		Struct struct = Struct.newBuilder().putAllFields(new HashMap<>() {{
			put("unit", Value.newBuilder().setStringValue("day").build());
			put("amount", Value.newBuilder().setNumberValue(14).build());
		}}).build();
		paramMap.put("bin_size", Value.newBuilder().setStructValue(struct).build());
		paramMap.put("Y-Axis", "Total Positive Cases");
		paramMap.put("Y-Axis_type", "sum of");
		paramMap.put(IDAConst.PARAM_TEXT_MSG, "This is a test");
		visualizeAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		BarGraphData barGraphData = (BarGraphData) chatMessageResponse.getPayload().get("barGraphData");
		List<BarGraphItem> barGraphItemList = new ArrayList<>();
		barGraphItemList.add(new BarGraphItem("19-04-20 to 02-05-20", 62914.0));
		barGraphItemList.add(new BarGraphItem("22-03-20 to 04-04-20", 16094.0));
		barGraphItemList.add(new BarGraphItem("05-04-20 to 18-04-20", 107431.581));
		barGraphItemList.add(new BarGraphItem("08-03-20 to 21-03-20", 1606.0));
		barGraphItemList.add(new BarGraphItem("UNKNOWN", 0.0));
		assertNotNull(barGraphData);
		assertEquals(barGraphData.getItems(), barGraphItemList);
	}

	@Test
	void testBarGraphDateYearBins() {
		chatUserMessage = new ChatUserMessage();
		chatMessageResponse = new ChatMessageResponse();
		chatMessageResponse.setPayload(new HashMap<>() {{
			put("activeDS", "covid19");
			put("activeTable", "ICMR_Tests_Datewise.csv");
		}});
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(IDAConst.INTENT_NAME, IDAConst.VIZ_TYPE_BAR_CHART);
		paramMap.put("records-selection", "all");
		paramMap.put("X-Axis", "Tested As Of");
		paramMap.put("X-Axis_type", "bins");
		Struct struct = Struct.newBuilder().putAllFields(new HashMap<>() {{
			put("unit", Value.newBuilder().setStringValue("yr").build());
			put("amount", Value.newBuilder().setNumberValue(1).build());
		}}).build();
		paramMap.put("bin_size", Value.newBuilder().setStructValue(struct).build());
		paramMap.put("Y-Axis", "Total Positive Cases");
		paramMap.put("Y-Axis_type", "average");
		paramMap.put(IDAConst.PARAM_TEXT_MSG, "This is a test");
		visualizeAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		BarGraphData barGraphData = (BarGraphData) chatMessageResponse.getPayload().get("barGraphData");
		List<BarGraphItem> barGraphItemList = new ArrayList<>();
		barGraphItemList.add(new BarGraphItem("2020", 1205.420391025641));
		barGraphItemList.add(new BarGraphItem("UNKNOWN", 0.0));
		assertNotNull(barGraphData);
		assertEquals(barGraphData.getItems(), barGraphItemList);
	}

	@Test
	void testBarGraphFlowUniqueLabels() {
		chatUserMessage = new ChatUserMessage();
		chatMessageResponse = new ChatMessageResponse();
		chatMessageResponse.setPayload(new HashMap<>() {{
			put("activeDS", "covid19");
			put("activeTable", "Case_Time_Series.csv");
		}});
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(IDAConst.INTENT_NAME, IDAConst.VIZ_TYPE_BAR_CHART);
		paramMap.put("records-selection", "first 5");
		paramMap.put("X-Axis", "Date");
		paramMap.put("X-Axis_type", "unique");
		paramMap.put("Y-Axis", "Daily Confirmed");
		paramMap.put(IDAConst.PARAM_TEXT_MSG, "This is a test");
		visualizeAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		BarGraphData barGraphData = (BarGraphData) chatMessageResponse.getPayload().get("barGraphData");
		List<BarGraphItem> barGraphItemList = new ArrayList<>();
		barGraphItemList.add(new BarGraphItem("02 February ", 1.0));
		barGraphItemList.add(new BarGraphItem("31 January ", 0.0));
		barGraphItemList.add(new BarGraphItem("01 February ", 0.0));
		barGraphItemList.add(new BarGraphItem("03 February ", 1.0));
		barGraphItemList.add(new BarGraphItem("30 January ", 1.0));
		assertNotNull(barGraphData);
		assertEquals(barGraphData.getItems(), barGraphItemList);
	}

	@Test
	void testBarGraphWrongFilterString() throws Exception {
		chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("Draw bar graph");
		chatUserMessage.setActiveDS("covid19");
		chatUserMessage.setActiveTable("Patient_Data_Before_20-04-2020.csv");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("null");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		assertEquals(IDAConst.INVALID_FILTER, chatMessageResponse.getMessage());
	}

	@Test
	void testBarGraphWrongColumnName() {
		chatUserMessage = new ChatUserMessage();
		chatMessageResponse = new ChatMessageResponse();
		chatMessageResponse.setPayload(new HashMap<>() {{
			put("activeDS", "covid19");
			put("activeTable", "Patient_Data_Before_20-04-2020.csv");
		}});
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(IDAConst.INTENT_NAME, IDAConst.VIZ_TYPE_BAR_CHART);
		paramMap.put("records-selection", "all");
		paramMap.put("X-Axis", "Population");
		paramMap.put(IDAConst.PARAM_TEXT_MSG, "This is a test");
		visualizeAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		assertEquals("Population: " + IDAConst.BC_INVALID_COL, chatMessageResponse.getMessage());
	}
}
