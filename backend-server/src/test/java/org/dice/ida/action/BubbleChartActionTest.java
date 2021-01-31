package org.dice.ida.action;

import org.dice.ida.controller.MessageController;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.dice.ida.model.bubblechart.BubbleChartData;
import org.dice.ida.model.bubblechart.BubbleChartItem;
import org.dice.ida.util.SessionUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class BubbleChartActionTest {
	private ChatUserMessage chatUserMessage;
	private ChatMessageResponse chatMessageResponse;
	@Autowired
	private MessageController messageController;
	@Autowired
	private SessionUtil sessionUtil;

	@Test
	void testBubbleChartFlow() throws Exception {
		chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("draw bubble chart");
		chatUserMessage.setActiveDS("covid19");
		chatUserMessage.setActiveTable("Patient_Data_Before_20-04-2020.csv");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("first 5");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("Detected State");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("Detected State");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("no");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		BubbleChartData bubbleChartData = (BubbleChartData) chatMessageResponse.getPayload().get("bubbleChartData");
		List<BubbleChartItem> bubbleChartItemList = new ArrayList<>();
		bubbleChartItemList.add(new BubbleChartItem("Delhi", "Delhi", 1.0));
		bubbleChartItemList.add(new BubbleChartItem("Kerala", "Kerala", 3.0));
		bubbleChartItemList.add(new BubbleChartItem("Telangana", "Telangana", 1.0));
		assertNotNull(bubbleChartData);
		assertEquals(bubbleChartData.getItems(), bubbleChartItemList);
		sessionUtil.resetSessionId();
	}

	@Test
	void testBubbleChartNumBins() throws Exception {
		chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("draw bubble chart");
		chatUserMessage.setActiveDS("covid19");
		chatUserMessage.setActiveTable("Patient_Data_Before_20-04-2020.csv");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("first 10");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("Age");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("bins");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("10");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("age");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("count of");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("no");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
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
		sessionUtil.resetSessionId();
	}

	@Test
	void testBubbleChartDateBins() throws Exception {
		chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("draw bubble chart");
		chatUserMessage.setActiveDS("covid19");
		chatUserMessage.setActiveTable("Patient_Data_Before_20-04-2020.csv");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("all");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("Date announced");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("bins");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		assertEquals("What should be the duration of each bin?<br/>Eg: 1 week, 2 weeks, 3 months", chatMessageResponse.getMessage());
		chatUserMessage.setMessage("1 month");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("Date Announced");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("no");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		BubbleChartData bubbleChartData = (BubbleChartData) chatMessageResponse.getPayload().get("bubbleChartData");
		List<BubbleChartItem> bubbleChartItemList = new ArrayList<>();
		bubbleChartItemList.add(new BubbleChartItem("January-2020", "January-2020", 1.0));
		bubbleChartItemList.add(new BubbleChartItem("February-2020", "February-2020", 2.0));
		bubbleChartItemList.add(new BubbleChartItem("March-2020", "March-2020", 1632.0));
		bubbleChartItemList.add(new BubbleChartItem("April-2020", "April-2020", 15729.0));
		assertNotNull(bubbleChartData);
		assertEquals(bubbleChartData.getItems(), bubbleChartItemList);
		sessionUtil.resetSessionId();
	}

	@Test
	void testBubbleChartFlowUniqueLabels() throws Exception {
		chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("draw bubble chart");
		chatUserMessage.setActiveDS("covid19");
		chatUserMessage.setActiveTable("Case_Time_Series.csv");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("first 5");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("Date");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("unique");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("Daily confirmed");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("no");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		BubbleChartData bubbleChartData = (BubbleChartData) chatMessageResponse.getPayload().get("bubbleChartData");
		List<BubbleChartItem> bubbleChartItemList = new ArrayList<>();
		bubbleChartItemList.add(new BubbleChartItem("02 February ", "02 February ", 1.0));
		bubbleChartItemList.add(new BubbleChartItem("31 January ", "31 January ", 0.0));
		bubbleChartItemList.add(new BubbleChartItem("01 February ", "01 February ", 0.0));
		bubbleChartItemList.add(new BubbleChartItem("03 February ", "03 February ", 1.0));
		bubbleChartItemList.add(new BubbleChartItem("30 January ", "30 January ", 1.0));
		assertNotNull(bubbleChartData);
		assertEquals(bubbleChartData.getItems(), bubbleChartItemList);
		sessionUtil.resetSessionId();
	}

}
