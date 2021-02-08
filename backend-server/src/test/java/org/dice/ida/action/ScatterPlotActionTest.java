package org.dice.ida.action;

import org.dice.ida.controller.MessageController;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.dice.ida.model.bubblechart.BubbleChartData;
import org.dice.ida.model.bubblechart.BubbleChartItem;
import org.dice.ida.model.scatterplot.ScatterPlotData;
import org.dice.ida.model.scatterplot.ScatterPlotItem;
import org.dice.ida.util.SessionUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ScatterPlotActionTest {
	private ChatUserMessage chatUserMessage;
	private ChatMessageResponse chatMessageResponse;
	@Autowired
	private MessageController messageController;
	@Autowired
	private SessionUtil sessionUtil;

	@Test
	void testScatterPlotFlow() throws Exception {
		chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("draw scatter plot");
		chatUserMessage.setActiveDS("covid19");
		chatUserMessage.setActiveTable("Case_Time_Series.csv");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("first 5");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("daily confirm cases");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("bins");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("10");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("total confirm cases");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("average");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		ScatterPlotData scatterPlotData = (ScatterPlotData) chatMessageResponse.getPayload().get("scatterPlotData");
		List<ScatterPlotItem> scatterPlotItemList = new ArrayList<>();
		scatterPlotItemList.add(new ScatterPlotItem("0.0 - 9.0", 1.3333333333333333));
		assertNotNull(scatterPlotData);
		assertEquals(scatterPlotData.getItems(), scatterPlotItemList);
		sessionUtil.resetSessionId();
	}
}
