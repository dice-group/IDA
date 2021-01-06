package org.dice.ida.action;

import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import org.dice.ida.action.def.VisualizeAction;
import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.dice.ida.model.bubblechart.BubbleChartData;
import org.dice.ida.model.bubblechart.BubbleChartItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class BubbleChartActionTest {
	@Autowired
	private VisualizeAction visualizeAction;
	private ChatUserMessage chatUserMessage;
	private ChatMessageResponse chatMessageResponse;

	@Test
	void testBubbleChartFlow() {
		chatUserMessage = new ChatUserMessage();
		chatUserMessage = new ChatUserMessage();
		chatMessageResponse = new ChatMessageResponse();
		chatMessageResponse.setPayload(new HashMap<>() {{
			put("activeDS", "covid19");
			put("activeTable", "Patient_Data_Before_20-04-2020.csv");
		}});
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(IDAConst.INTENT_NAME, IDAConst.VIZ_TYPE_BUBBLE_CHART);
		paramMap.put("records-selection", "first 5");
		paramMap.put("Bubble_Label", "Detected State");
		paramMap.put("Bubble_Size", "Detected State");
		paramMap.put(IDAConst.PARAM_TEXT_MSG, "This is a test");
		visualizeAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		BubbleChartData bubbleChartData = (BubbleChartData) chatMessageResponse.getPayload().get("bubbleChartData");
		List<BubbleChartItem> bubbleChartItemList = new ArrayList<>();
		bubbleChartItemList.add(new BubbleChartItem("Delhi", "Delhi", 1.0));
		bubbleChartItemList.add(new BubbleChartItem("Kerala", "Kerala", 3.0));
		bubbleChartItemList.add(new BubbleChartItem("Telangana", "Telangana", 1.0));
		assertNotNull(bubbleChartData);
		assertEquals(bubbleChartData.getItems(), bubbleChartItemList);
	}

	@Test
	void testBubbleChartNumBins() {
		chatUserMessage = new ChatUserMessage();
		chatMessageResponse = new ChatMessageResponse();
		chatMessageResponse.setPayload(new HashMap<>() {{
			put("activeDS", "covid19");
			put("activeTable", "Patient_Data_Before_20-04-2020.csv");
		}});
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(IDAConst.INTENT_NAME, IDAConst.VIZ_TYPE_BUBBLE_CHART);
		paramMap.put("records-selection", "first 10");
		paramMap.put("Bubble_Label", "Age Bracket");
		paramMap.put("Bubble_Label_type", "bins");
		paramMap.put("bin_size", Value.newBuilder().setNumberValue(10).build());
		paramMap.put("Bubble_Size", "Age Bracket");
		paramMap.put("Bubble_Size_type", "count of");
		paramMap.put(IDAConst.PARAM_TEXT_MSG, "This is a test");
		visualizeAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		BubbleChartData bubbleChartData = (BubbleChartData) chatMessageResponse.getPayload().get("bubbleChartData");
		List<BubbleChartItem> bubbleChartItemList = new ArrayList<>();
		bubbleChartItemList.add(new BubbleChartItem("0.0 - 9.0", "0.0 - 9.0", 0.0));
		bubbleChartItemList.add(new BubbleChartItem("10.0 - 19.0", "10.0 - 19.0", 0.0));
		bubbleChartItemList.add(new BubbleChartItem("20.0 - 29.0", "20.0 - 29.0", 2.0));
		bubbleChartItemList.add(new BubbleChartItem("30.0 - 39.0", "30.0 - 39.0", 0.0));
		bubbleChartItemList.add(new BubbleChartItem("40.0 - 49.0", "40.0 - 49.0", 1.0));
		bubbleChartItemList.add(new BubbleChartItem("50.0 - 59.0", "50.0 - 59.0", 4.0));
		bubbleChartItemList.add(new BubbleChartItem("60.0 - 69.0", "60.0 - 69.0", 1.0));
		bubbleChartItemList.add(new BubbleChartItem("UNKNOWN", "UNKNOWN", 2.0));
		assertNotNull(bubbleChartData);
		assertEquals(bubbleChartData.getItems(), bubbleChartItemList);
	}

	@Test
	void testBubbleChartDateBins() {
		chatUserMessage = new ChatUserMessage();
		chatMessageResponse = new ChatMessageResponse();
		chatMessageResponse.setPayload(new HashMap<>() {{
			put("activeDS", "covid19");
			put("activeTable", "Patient_Data_Before_20-04-2020.csv");
		}});
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(IDAConst.INTENT_NAME, IDAConst.VIZ_TYPE_BUBBLE_CHART);
		paramMap.put("records-selection", "all");
		paramMap.put("Bubble_Label", "Date Announced");
		paramMap.put("Bubble_Label_type", "bins");
		Struct struct = Struct.newBuilder().putAllFields(new HashMap<>() {{
			put("unit", Value.newBuilder().setStringValue("mo").build());
			put("amount", Value.newBuilder().setNumberValue(1).build());
		}}).build();
		paramMap.put("bin_size", Value.newBuilder().setStructValue(struct).build());
		paramMap.put("Bubble_Size", "Date Announced");
		paramMap.put(IDAConst.PARAM_TEXT_MSG, "This is a test");
		visualizeAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		BubbleChartData bubbleChartData = (BubbleChartData) chatMessageResponse.getPayload().get("bubbleChartData");
		List<BubbleChartItem> bubbleChartItemList = new ArrayList<>();
		bubbleChartItemList.add(new BubbleChartItem("January-2020", "January-2020", 1.0));
		bubbleChartItemList.add(new BubbleChartItem("February-2020", "February-2020", 2.0));
		bubbleChartItemList.add(new BubbleChartItem("March-2020", "March-2020", 1632.0));
		bubbleChartItemList.add(new BubbleChartItem("April-2020", "April-2020", 15729.0));
		assertNotNull(bubbleChartData);
		assertEquals(bubbleChartData.getItems(), bubbleChartItemList);
	}

	@Test
	void testBubbleChartFlowUniqueLabels() {
		chatUserMessage = new ChatUserMessage();
		chatMessageResponse = new ChatMessageResponse();
		chatMessageResponse.setPayload(new HashMap<>() {{
			put("activeDS", "covid19");
			put("activeTable", "Case_Time_Series.csv");
		}});
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(IDAConst.INTENT_NAME, IDAConst.VIZ_TYPE_BUBBLE_CHART);
		paramMap.put("records-selection", "first 5");
		paramMap.put("Bubble_Label", "Date");
		paramMap.put("Bubble_Label_type", "unique");
		paramMap.put("Bubble_Size", "Daily Confirmed");
		paramMap.put(IDAConst.PARAM_TEXT_MSG, "This is a test");
		visualizeAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		BubbleChartData bubbleChartData = (BubbleChartData) chatMessageResponse.getPayload().get("bubbleChartData");
		List<BubbleChartItem> bubbleChartItemList = new ArrayList<>();
		bubbleChartItemList.add(new BubbleChartItem("02 February ", "02 February ", 1.0));
		bubbleChartItemList.add(new BubbleChartItem("31 January ", "31 January ", 0.0));
		bubbleChartItemList.add(new BubbleChartItem("01 February ", "01 February ", 0.0));
		bubbleChartItemList.add(new BubbleChartItem("03 February ", "03 February ", 1.0));
		bubbleChartItemList.add(new BubbleChartItem("30 January ", "30 January ", 1.0));
		assertNotNull(bubbleChartData);
		assertEquals(bubbleChartData.getItems(), bubbleChartItemList);
	}

}
