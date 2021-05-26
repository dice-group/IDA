package org.dice.ida.action;

import org.dice.ida.controller.MessageController;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.dice.ida.model.scatterplot.ScatterPlotData;
import org.dice.ida.model.scatterplot.ScatterPlotItem;
import org.dice.ida.util.SessionUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ScatterPlotActionTest {
	@Autowired
	private MessageController messageController;
	@Autowired
	private SessionUtil sessionUtil;

	@Test
	void testScatterPlotFlow() throws Exception {
		ChatUserMessage chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("draw scatter plot");
		chatUserMessage.setActiveDS("test_dataset");
		chatUserMessage.setActiveTable("countries-of-the-world.csv");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("first 5");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("density");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("gdp");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("region");
		ChatMessageResponse chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		ScatterPlotData scatterPlotData = (ScatterPlotData) chatMessageResponse.getPayload().get("scatterPlotData");
		List<ScatterPlotItem> scatterPlotItemList = new ArrayList<>() {{
			add(new ScatterPlotItem(48.0, 700.0, "ASIA (EX. NEAR EAST)         "));
			add(new ScatterPlotItem(124.6, 4500.0, "EASTERN EUROPE                     "));
			add(new ScatterPlotItem(13.8, 6000.0, "NORTHERN AFRICA                    "));
			add(new ScatterPlotItem(290.4, 8000.0, "OCEANIA                            "));
			add(new ScatterPlotItem(152.1, 19000.0, "WESTERN EUROPE                     "));
		}};
		assertNotNull(scatterPlotData);
		assertEquals(scatterPlotData.getItems(), scatterPlotItemList);
		sessionUtil.resetSessionId();
	}
}
