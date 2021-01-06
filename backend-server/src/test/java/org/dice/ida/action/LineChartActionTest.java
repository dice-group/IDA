package org.dice.ida.action;

import org.dice.ida.action.def.LineChartAction;
import org.dice.ida.constant.IDAConst;
import org.dice.ida.controller.MessageController;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.dice.ida.model.linechart.LineChartData;
import org.dice.ida.model.linechart.LineChartItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.*;

@SpringBootTest
public class LineChartActionTest {
	private ChatUserMessage chatUserMessage;
	private ChatMessageResponse chatMessageResponse;
	@Autowired
	private LineChartAction lineChartAction;
	@Autowired
	private MessageController messageController;

	@Test
	void testLineChartFlow() {
		chatUserMessage = new ChatUserMessage();
		chatMessageResponse = new ChatMessageResponse();
		chatMessageResponse.setPayload(new HashMap<>() {{
			put("activeDS", "covid19");
			put("activeTable", "Patient_Data_Before_20-04-2020.csv");
		}});
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("records-selection", "first 10");
		paramMap.put(IDAConst.LINE_CHART_PARAM_DATE_COL, "Date Announced");
		paramMap.put(IDAConst.LINE_CHART_PARAM_LABEL_COL, "Detected State");
		paramMap.put(IDAConst.LINE_CHART_PARAM_VALUE_COL, "Detected State");
		paramMap.put(IDAConst.PARAM_TEXT_MSG, "This is a test");
		lineChartAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		LineChartData lineChartData = (LineChartData) chatMessageResponse.getPayload().get("lineChartData");
		List<LineChartItem> lineChartItemList = new ArrayList<>();
		lineChartItemList.add(new LineChartItem("Delhi", Arrays.asList(0.0, 0.0, 0.0, 1.0, 0.0, 0.0)));
		lineChartItemList.add(new LineChartItem("Haryana", Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0, 4.0)));
		lineChartItemList.add(new LineChartItem("Telangana", Arrays.asList(0.0, 0.0, 0.0, 1.0, 0.0, 0.0)));
		lineChartItemList.add(new LineChartItem("Rajasthan", Arrays.asList(0.0, 0.0, 0.0, 0.0, 1.0, 0.0)));
		lineChartItemList.add(new LineChartItem("Kerala", Arrays.asList(1.0, 1.0, 1.0, 0.0, 0.0, 0.0)));
		assertNotNull(lineChartItemList);
		assertEquals(lineChartItemList, lineChartData.getLines());
	}

	@Test
	void testLineChartWrongFilterString() throws Exception {
		chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("Draw line chart");
		chatUserMessage.setActiveDS("covid19");
		chatUserMessage.setActiveTable("Patient_Data_Before_20-04-2020.csv");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("null");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		assertEquals(IDAConst.INVALID_FILTER, chatMessageResponse.getMessage());
	}

	@Test
	void testLineChartWrongColumnName() {
		chatUserMessage = new ChatUserMessage();
		chatMessageResponse = new ChatMessageResponse();
		chatMessageResponse.setPayload(new HashMap<>() {{
			put("activeDS", "covid19");
			put("activeTable", "Patient_Data_Before_20-04-2020.csv");
		}});
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(IDAConst.INTENT_NAME, IDAConst.VIZ_TYPE_BAR_CHART);
		paramMap.put("records-selection", "all");
		paramMap.put(IDAConst.LINE_CHART_PARAM_DATE_COL, "Tested As Of");
		paramMap.put(IDAConst.LINE_CHART_PARAM_LABEL_COL, "Detected State");
		paramMap.put(IDAConst.LINE_CHART_PARAM_VALUE_COL, "Detected State");
		paramMap.put(IDAConst.PARAM_TEXT_MSG, "This is a test");
		lineChartAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		assertEquals("Tested As Of: " + IDAConst.BC_INVALID_COL, chatMessageResponse.getMessage());
	}
}
