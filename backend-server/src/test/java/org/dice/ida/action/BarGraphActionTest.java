package org.dice.ida.action;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.controller.MessageController;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.dice.ida.model.bargraph.BarGraphData;
import org.dice.ida.model.bargraph.BarGraphItem;
import org.dice.ida.model.groupedbargraph.GroupedBarGraphData;
import org.dice.ida.util.SessionUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
public class BarGraphActionTest {
	@Autowired
	private MessageController messageController;
	private ChatUserMessage chatUserMessage;
	private ChatMessageResponse chatMessageResponse;
	@Autowired
	private SessionUtil sessionUtil;

	@Test
	void testBarGraphFlow() throws Exception {
		chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("draw bar graph");
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
		BarGraphData barGraphData = (BarGraphData) chatMessageResponse.getPayload().get("barGraphData");
		List<BarGraphItem> barGraphItemList = new ArrayList<>();
		barGraphItemList.add(new BarGraphItem("Delhi", 1.0));
		barGraphItemList.add(new BarGraphItem("Kerala", 3.0));
		barGraphItemList.add(new BarGraphItem("Telangana", 1.0));
		assertNotNull(barGraphData);
		assertEquals(barGraphData.getItems(), barGraphItemList);
		sessionUtil.resetSessionId();
	}

	@Test
	void testBarGraphSteps() throws Exception{
		chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("draw bar graph");
		chatUserMessage.setActiveDS("covid19");
		chatUserMessage.setActiveTable("Patient_Data_Before_20-04-2020.csv");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("first 5");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("age");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("unique");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		assertEquals("Age Bracket cannot be used as Unique. Please provide correct type.", chatMessageResponse.getMessage());
		chatUserMessage.setMessage("bins");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		assertEquals("What should be the size of each bin?<br/>Eg: 10, 25, 15, twenty, twelve", chatMessageResponse.getMessage());
		chatUserMessage.setMessage("10");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		assertEquals("Which column values should be mapped to Y-Axis?", chatMessageResponse.getMessage());
		sessionUtil.resetSessionId();
	}

	@Test
	void testBarGraphNonSuitableColumn() throws Exception {
		chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("draw bar graph");
		chatUserMessage.setActiveDS("covid19");
		chatUserMessage.setActiveTable("Case_Time_Series.csv");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("first 5");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("Date");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("unique");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("Date");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		assertEquals("Date cannot be used as Y-Axis. Please give a different column?", chatMessageResponse.getMessage());
		sessionUtil.resetSessionId();
	}

	@Test
	void testBarGraphNumBins() throws Exception {
		chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("draw bar graph");
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
		sessionUtil.resetSessionId();
	}

	@Test
	void testBarGraphDateBins() throws Exception {
		chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("draw bar graph");
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
		BarGraphData barGraphData = (BarGraphData) chatMessageResponse.getPayload().get("barGraphData");
		List<BarGraphItem> barGraphItemList = new ArrayList<>();
		barGraphItemList.add(new BarGraphItem("January-2020", 1.0));
		barGraphItemList.add(new BarGraphItem("February-2020", 2.0));
		barGraphItemList.add(new BarGraphItem("March-2020", 1632.0));
		barGraphItemList.add(new BarGraphItem("April-2020", 15729.0));
		assertNotNull(barGraphData);
		assertEquals(barGraphData.getItems(), barGraphItemList);
		sessionUtil.resetSessionId();
	}

	@Test
	void testBarGraphDateWeekBins() throws Exception {
		chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("draw bar graph");
		chatUserMessage.setActiveDS("covid19");
		chatUserMessage.setActiveTable("ICMR_Tests_Datewise.csv");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("first 50");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("Tested as of");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("bins");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("2 weeks");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("Total positive cases");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("sum of");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("no");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		BarGraphData barGraphData = (BarGraphData) chatMessageResponse.getPayload().get("barGraphData");
		List<BarGraphItem> barGraphItemList = new ArrayList<>();
		barGraphItemList.add(new BarGraphItem("19-04-20 to 02-05-20", 62914.0));
		barGraphItemList.add(new BarGraphItem("22-03-20 to 04-04-20", 16094.0));
		barGraphItemList.add(new BarGraphItem("05-04-20 to 18-04-20", 107431.581));
		barGraphItemList.add(new BarGraphItem("08-03-20 to 21-03-20", 1606.0));
		barGraphItemList.add(new BarGraphItem("UNKNOWN", 0.0));
		assertNotNull(barGraphData);
		assertEquals(barGraphData.getItems(), barGraphItemList);
		sessionUtil.resetSessionId();
	}

	@Test
	void testBarGraphDateDayBins() throws Exception {
		chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("draw bar graph");
		chatUserMessage.setActiveDS("covid19");
		chatUserMessage.setActiveTable("ICMR_Tests_Datewise.csv");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("first 50");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("Tested as of");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("bins");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("14 days");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("Total positive cases");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("sum of");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("no");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		BarGraphData barGraphData = (BarGraphData) chatMessageResponse.getPayload().get("barGraphData");
		List<BarGraphItem> barGraphItemList = new ArrayList<>();
		barGraphItemList.add(new BarGraphItem("19-04-20 to 02-05-20", 62914.0));
		barGraphItemList.add(new BarGraphItem("22-03-20 to 04-04-20", 16094.0));
		barGraphItemList.add(new BarGraphItem("05-04-20 to 18-04-20", 107431.581));
		barGraphItemList.add(new BarGraphItem("08-03-20 to 21-03-20", 1606.0));
		barGraphItemList.add(new BarGraphItem("UNKNOWN", 0.0));
		assertNotNull(barGraphData);
		assertEquals(barGraphData.getItems(), barGraphItemList);
		sessionUtil.resetSessionId();
	}

	@Test
	void testBarGraphDateYearBins() throws Exception {
		chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("draw bar graph");
		chatUserMessage.setActiveDS("covid19");
		chatUserMessage.setActiveTable("ICMR_Tests_Datewise.csv");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("all");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("Tested as of");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("bins");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("1 year");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("Total positive cases");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("average");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("no");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		BarGraphData barGraphData = (BarGraphData) chatMessageResponse.getPayload().get("barGraphData");
		List<BarGraphItem> barGraphItemList = new ArrayList<>();
		barGraphItemList.add(new BarGraphItem("2020", 1205.420391025641));
		barGraphItemList.add(new BarGraphItem("UNKNOWN", 0.0));
		assertNotNull(barGraphData);
		assertEquals(barGraphData.getItems(), barGraphItemList);
		sessionUtil.resetSessionId();
	}

	@Test
	void testBarGraphFlowUniqueLabels() throws Exception {
		chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("draw bar graph");
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
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		BarGraphData barGraphData = (BarGraphData) chatMessageResponse.getPayload().get("barGraphData");
		List<BarGraphItem> barGraphItemList = new ArrayList<>();
		barGraphItemList.add(new BarGraphItem("02 February ", 1.0));
		barGraphItemList.add(new BarGraphItem("31 January ", 0.0));
		barGraphItemList.add(new BarGraphItem("01 February ", 0.0));
		barGraphItemList.add(new BarGraphItem("03 February ", 1.0));
		barGraphItemList.add(new BarGraphItem("30 January ", 1.0));
		assertNotNull(barGraphData);
		assertEquals(barGraphData.getItems(), barGraphItemList);
		sessionUtil.resetSessionId();
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
		sessionUtil.resetSessionId();
	}

	@Test
	void testBarGraphWrongColumnName() throws Exception {
		chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("draw bar graph");
		chatUserMessage.setActiveDS("covid19");
		chatUserMessage.setActiveTable("Patient_Data_Before_20-04-2020.csv");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("all");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("Population");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("unique");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("Daily confirmed");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		assertEquals("Population: " + IDAConst.BC_INVALID_COL, chatMessageResponse.getMessage());
		sessionUtil.resetSessionId();
	}

	@Test
	void testGroupedBarGraph() throws Exception {
		chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("draw bar graph");
		chatUserMessage.setActiveDS("covid19");
		chatUserMessage.setActiveTable("Patient_Data_Before_20-04-2020.csv");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("first 10");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("current status");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("current status");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("yes");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("Detected State");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		GroupedBarGraphData groupedBarGraphData = (GroupedBarGraphData) chatMessageResponse.getPayload().get("barGraphData");
		Map<String, List<BarGraphItem>> groupedBarChartItems = new HashMap<>();
		groupedBarChartItems.put("Delhi", new ArrayList<>(){{
			add(new BarGraphItem("Recovered", 1.0));
		}});
		groupedBarChartItems.put("Haryana", new ArrayList<>(){{
			add(new BarGraphItem("Recovered", 4.0));
		}});
		groupedBarChartItems.put("Kerala", new ArrayList<>(){{
			add(new BarGraphItem("Recovered", 3.0));
		}});
		groupedBarChartItems.put("Rajasthan", new ArrayList<>(){{
			add(new BarGraphItem("Recovered", 1.0));
		}});
		groupedBarChartItems.put("Telangana", new ArrayList<>(){{
			add(new BarGraphItem("Recovered", 1.0));
		}});
		assertNotNull(groupedBarGraphData);
		assertEquals(groupedBarGraphData.getGroupedBarChartData().keySet(), groupedBarChartItems.keySet());
		for(String key: groupedBarChartItems.keySet()){
			assertTrue(groupedBarChartItems.get(key).containsAll(groupedBarGraphData.getGroupedBarChartData().get(key)));
		}
		sessionUtil.resetSessionId();
	}

	@Test
	void testGroupedBarGraphNumLabels() throws Exception {
		chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("draw bar graph");
		chatUserMessage.setActiveDS("covid19");
		chatUserMessage.setActiveTable("Patient_Data_Before_20-04-2020.csv");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("first 5");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("age");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("bin");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("20");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("age");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("count of");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("yes");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("Detected State");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		GroupedBarGraphData groupedBarGraphData = (GroupedBarGraphData) chatMessageResponse.getPayload().get("barGraphData");
		Map<String, List<BarGraphItem>> groupedBarChartItems = new HashMap<>();
		groupedBarChartItems.put("Delhi", new ArrayList<>(){{
			add(new BarGraphItem("40.0 - 59.0", 1.0));
			add(new BarGraphItem("20.0 - 39.0", 0.0));
			add(new BarGraphItem("0.0 - 19.0", 0.0));
			add(new BarGraphItem("UNKNOWN", 0.0));
		}});
		groupedBarChartItems.put("Kerala", new ArrayList<>(){{
			add(new BarGraphItem("40.0 - 59.0", 0.0));
			add(new BarGraphItem("20.0 - 39.0", 1.0));
			add(new BarGraphItem("0.0 - 19.0", 0.0));
			add(new BarGraphItem("UNKNOWN", 2.0));
		}});
		groupedBarChartItems.put("Telangana", new ArrayList<>(){{
			add(new BarGraphItem("40.0 - 59.0", 0.0));
			add(new BarGraphItem("20.0 - 39.0", 1.0));
			add(new BarGraphItem("0.0 - 19.0", 0.0));
			add(new BarGraphItem("UNKNOWN", 0.0));
		}});
		assertNotNull(groupedBarGraphData);
		assertEquals(groupedBarGraphData.getGroupedBarChartData().keySet(), groupedBarChartItems.keySet());
		for(String key: groupedBarChartItems.keySet()){
			assertTrue(groupedBarChartItems.get(key).containsAll(groupedBarGraphData.getGroupedBarChartData().get(key)));
		}
		sessionUtil.resetSessionId();
	}

	@Test
	void testGroupedBarGraphDateLabels() throws Exception {
		chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("draw bar graph");
		chatUserMessage.setActiveDS("covid19");
		chatUserMessage.setActiveTable("Patient_Data_Before_20-04-2020.csv");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("first 30");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("date announced");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("bin");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("2 weeks");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("date announced");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("yes");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("Detected State");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		GroupedBarGraphData groupedBarGraphData = (GroupedBarGraphData) chatMessageResponse.getPayload().get("barGraphData");
		Map<String, List<BarGraphItem>> groupedBarChartItems = new HashMap<>();
		groupedBarChartItems.put("Delhi", new ArrayList<>(){{
			add(new BarGraphItem("23-02-20 to 07-03-20", 2.0));
			add(new BarGraphItem("26-01-20 to 08-02-20", 0.0));
			add(new BarGraphItem("09-02-20 to 22-02-20", 0.0));
		}});
		groupedBarChartItems.put("Haryana", new ArrayList<>(){{
			add(new BarGraphItem("23-02-20 to 07-03-20", 14.0));
			add(new BarGraphItem("26-01-20 to 08-02-20", 0.0));
			add(new BarGraphItem("09-02-20 to 22-02-20", 0.0));
		}});
		groupedBarChartItems.put("Kerala", new ArrayList<>(){{
			add(new BarGraphItem("23-02-20 to 07-03-20", 0.0));
			add(new BarGraphItem("26-01-20 to 08-02-20", 3.0));
			add(new BarGraphItem("09-02-20 to 22-02-20", 0.0));
		}});
		groupedBarChartItems.put("Rajasthan", new ArrayList<>(){{
			add(new BarGraphItem("23-02-20 to 07-03-20", 2.0));
			add(new BarGraphItem("26-01-20 to 08-02-20", 0.0));
			add(new BarGraphItem("09-02-20 to 22-02-20", 0.0));
		}});
		groupedBarChartItems.put("Telangana", new ArrayList<>(){{
			add(new BarGraphItem("23-02-20 to 07-03-20", 1.0));
			add(new BarGraphItem("26-01-20 to 08-02-20", 0.0));
			add(new BarGraphItem("09-02-20 to 22-02-20", 0.0));
		}});
		groupedBarChartItems.put("Uttar Pradesh", new ArrayList<>(){{
			add(new BarGraphItem("23-02-20 to 07-03-20", 8.0));
			add(new BarGraphItem("26-01-20 to 08-02-20", 0.0));
			add(new BarGraphItem("09-02-20 to 22-02-20", 0.0));
		}});
		assertNotNull(groupedBarGraphData);
		assertEquals(groupedBarGraphData.getGroupedBarChartData().keySet(), groupedBarChartItems.keySet());
		for(String key: groupedBarChartItems.keySet()){
			assertTrue(groupedBarChartItems.get(key).containsAll(groupedBarGraphData.getGroupedBarChartData().get(key)));
		}
		sessionUtil.resetSessionId();
	}
}
