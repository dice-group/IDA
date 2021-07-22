package org.dice.ida.action;

import org.dice.ida.controller.MessageController;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.dice.ida.model.scatterplotmatrix.ScatterPlotMatrixData;
import org.dice.ida.util.SessionUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ScatterPlotMatrixActionTest {
	@Autowired
	private MessageController messageController;
	@Autowired
	private SessionUtil sessionUtil;
	private ChatMessageResponse chatMessageResponse;

	@Test
	void testScatterPlotMatrixFlow() throws Exception {
		ChatUserMessage chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("draw scatter plot matrix");
		chatUserMessage.setActiveDS("test_dataset");
		chatUserMessage.setActiveTable("countries-of-the-world.csv");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("country, density, gdp, literacy");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("region");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("no");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		ScatterPlotMatrixData actualData = (ScatterPlotMatrixData) chatMessageResponse.getPayload().get("scatterPlotMatrixData");
		ScatterPlotMatrixData expectedData = new ScatterPlotMatrixData();
		expectedData.setColumns(new ArrayList<>() {{
			add("pop. density (per sq. mi.)");
			add("gdp ($ per capita)");
			add("literacy (%)");
		}});
		expectedData.setLabelColumn("region");
		expectedData.setItems(new ArrayList<>() {{
			add(new HashMap<>() {{
				put("literacy (%)", "36.0");
				put("gdp ($ per capita)", "700");
				put("region", "ASIA (EX. NEAR EAST)         ");
				put("pop. density (per sq. mi.)", "48.0");
			}});
			add(new HashMap<>() {{
				put("gdp ($ per capita)", "4500");
				put("literacy (%)", "86.5");
				put("pop. density (per sq. mi.)", "124.6");
				put("region", "EASTERN EUROPE                     ");
			}});
			add(new HashMap<>() {{
				put("gdp ($ per capita)", "6000");
				put("literacy (%)", "70.0");
				put("pop. density (per sq. mi.)", "13.8");
				put("region", "NORTHERN AFRICA                    ");
			}});
			add(new HashMap<>() {{
				put("gdp ($ per capita)", "8000");
				put("literacy (%)", "97.0");
				put("pop. density (per sq. mi.)", "290.4");
				put("region", "OCEANIA                            ");
			}});
			add(new HashMap<>() {{
				put("gdp ($ per capita)", "19000");
				put("literacy (%)", "100.0");
				put("pop. density (per sq. mi.)", "152.1");
				put("region", "WESTERN EUROPE                     ");
			}});
			add(new HashMap<>() {{
				put("gdp ($ per capita)", "1900");
				put("literacy (%)", "42.0");
				put("pop. density (per sq. mi.)", "9.7");
				put("region", "SUB-SAHARAN AFRICA                 ");
			}});
			add(new HashMap<>() {{
				put("gdp ($ per capita)", "8600");
				put("literacy (%)", "95.0");
				put("pop. density (per sq. mi.)", "132.1");
				put("region", "LATIN AMER. & CARIB    ");
			}});
			add(new HashMap<>() {{
				put("gdp ($ per capita)", "11000");
				put("literacy (%)", "89.0");
				put("pop. density (per sq. mi.)", "156.0");
				put("region", "LATIN AMER. & CARIB    ");
			}});
			add(new HashMap<>() {{
				put("gdp ($ per capita)", "11200");
				put("literacy (%)", "97.1");
				put("pop. density (per sq. mi.)", "14.4");
				put("region", "LATIN AMER. & CARIB    ");
			}});
			add(new HashMap<>() {{
				put("gdp ($ per capita)", "3500");
				put("literacy (%)", "98.6");
				put("pop. density (per sq. mi.)", "99.9");
				put("region", "C.W. OF IND. STATES ");
			}});
		}});
		assertNotNull(actualData);
		assertEquals(expectedData.getLabelColumn(), actualData.getLabelColumn());
		assertEquals(expectedData.getColumns(), actualData.getColumns());
		assertTrue(actualData.getItems().containsAll(expectedData.getItems()));
		sessionUtil.resetSessionId();
	}

	@Test
	void testLabelledScatterPlotMatrix() throws Exception {
		ChatUserMessage chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("draw scatter plot matrix");
		chatUserMessage.setActiveDS("test_dataset");
		chatUserMessage.setActiveTable("countries-of-the-world.csv");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("country, density, gdp, literacy");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("country");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("yes");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("region");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		ScatterPlotMatrixData actualData = (ScatterPlotMatrixData) chatMessageResponse.getPayload().get("scatterPlotMatrixData");
		ScatterPlotMatrixData expectedData = new ScatterPlotMatrixData();
		expectedData.setColumns(new ArrayList<>() {{
			add("pop. density (per sq. mi.)");
			add("gdp ($ per capita)");
			add("literacy (%)");
		}});
		expectedData.setReferenceColumn("region");
		expectedData.setLabelColumn("country");
		expectedData.setItems(new ArrayList<>() {{
			add(new HashMap<>() {{
				put("literacy (%)", "36.0");
				put("gdp ($ per capita)", "700");
				put("region", "ASIA (EX. NEAR EAST)         ");
				put("pop. density (per sq. mi.)", "48.0");
				put("country", "Afghanistan ");
			}});
			add(new HashMap<>() {{
				put("gdp ($ per capita)", "4500");
				put("literacy (%)", "86.5");
				put("pop. density (per sq. mi.)", "124.6");
				put("region", "EASTERN EUROPE                     ");
				put("country", "Albania ");
			}});
			add(new HashMap<>() {{
				put("gdp ($ per capita)", "6000");
				put("literacy (%)", "70.0");
				put("pop. density (per sq. mi.)", "13.8");
				put("region", "NORTHERN AFRICA                    ");
				put("country", "Algeria ");
			}});
			add(new HashMap<>() {{
				put("gdp ($ per capita)", "8000");
				put("literacy (%)", "97.0");
				put("pop. density (per sq. mi.)", "290.4");
				put("region", "OCEANIA                            ");
				put("country", "American Samoa ");
			}});
			add(new HashMap<>() {{
				put("gdp ($ per capita)", "19000");
				put("literacy (%)", "100.0");
				put("pop. density (per sq. mi.)", "152.1");
				put("region", "WESTERN EUROPE                     ");
				put("country", "Andorra ");
			}});
		}});
		assertNotNull(actualData);
		assertEquals(expectedData.getLabelColumn(), actualData.getLabelColumn());
		assertEquals(expectedData.getReferenceColumn(), actualData.getReferenceColumn());
		assertEquals(expectedData.getColumns(), actualData.getColumns());
		assertTrue(actualData.getItems().containsAll(expectedData.getItems()));
		sessionUtil.resetSessionId();
	}

	@Test
	void testScatterPlotMatrixAllColumns() throws Exception {
		ChatUserMessage chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("draw scatter plot matrix");
		chatUserMessage.setActiveDS("test_dataset");
		chatUserMessage.setActiveTable("Patient_Data_Before_20-04-2020.csv");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("all");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("detected state");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("no");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		ScatterPlotMatrixData actualData = (ScatterPlotMatrixData) chatMessageResponse.getPayload().get("scatterPlotMatrixData");
		ScatterPlotMatrixData expectedData = new ScatterPlotMatrixData();
		expectedData.setColumns(new ArrayList<>() {{
			add("patient number");
			add("age bracket");
		}});
		expectedData.setLabelColumn("detected state");
		expectedData.setItems(new ArrayList<>() {{
			add(new HashMap<>() {{
				put("age bracket", "20");
				put("detected state", "Kerala");
				put("patient number", "1");
			}});
			add(new HashMap<>() {{
				put("age bracket", "UNKNOWN");
				put("detected state", "Kerala");
				put("patient number", "2");
			}});
			add(new HashMap<>() {{
				put("age bracket", "UNKNOWN");
				put("detected state", "Kerala");
				put("patient number", "3");
			}});
			add(new HashMap<>() {{
				put("age bracket", "45");
				put("detected state", "Delhi");
				put("patient number", "4");
			}});
			add(new HashMap<>() {{
				put("age bracket", "24");
				put("detected state", "Telangana");
				put("patient number", "5");
			}});
		}});
		assertNotNull(actualData);
		assertEquals(expectedData.getLabelColumn(), actualData.getLabelColumn());
		assertEquals(expectedData.getColumns(), actualData.getColumns());
		assertTrue(actualData.getItems().containsAll(expectedData.getItems()));
		sessionUtil.resetSessionId();
	}

	@Test
	void testScatterPlotMatrixNonNumericColumn() throws Exception {
		ChatUserMessage chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("draw scatter plot matrix");
		chatUserMessage.setActiveDS("test_dataset");
		chatUserMessage.setActiveTable("countries-of-the-world.csv");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("region, density");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		assertEquals("Please provide more than one Numeric columns", chatMessageResponse.getMessage());
		sessionUtil.resetSessionId();
	}

	@Test
	void testClusterScatterPlotMatrix() throws Exception {
		ChatUserMessage chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("cluster");
		chatUserMessage.setActiveDS("test_dataset");
		chatUserMessage.setActiveTable("countries-of-the-world.csv");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("population, population density");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("kmeans");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("no");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setActiveTableData((List<Map<String, String>>) chatMessageResponse.getPayload().get("clusteredData"));
		chatUserMessage.setTemporaryData(true);
		chatUserMessage.setMessage("draw scatter plot matrix");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("density, gdp, literacy");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("cluster");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("no");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		ScatterPlotMatrixData actualData = (ScatterPlotMatrixData) chatMessageResponse.getPayload().get("scatterPlotMatrixData");
		ScatterPlotMatrixData expectedData = new ScatterPlotMatrixData();
		expectedData.setColumns(new ArrayList<>() {{
			add("pop. density (per sq. mi.)");
			add("gdp ($ per capita)");
			add("literacy (%)");
		}});
		expectedData.setLabelColumn("cluster");
		expectedData.setItems(new ArrayList<>() {{
			add(new HashMap<>() {{
				put("literacy (%)", "36.0");
				put("gdp ($ per capita)", "700");
				put("cluster", "3");
				put("pop. density (per sq. mi.)", "48.0");
			}});
			add(new HashMap<>() {{
				put("gdp ($ per capita)", "4500");
				put("literacy (%)", "86.5");
				put("pop. density (per sq. mi.)", "124.6");
				put("cluster", "0");
			}});
			add(new HashMap<>() {{
				put("gdp ($ per capita)", "6000");
				put("literacy (%)", "70.0");
				put("pop. density (per sq. mi.)", "13.8");
				put("cluster", "3");
			}});
			add(new HashMap<>() {{
				put("gdp ($ per capita)", "8000");
				put("literacy (%)", "97.0");
				put("pop. density (per sq. mi.)", "290.4");
				put("cluster", "5");
			}});
			add(new HashMap<>() {{
				put("gdp ($ per capita)", "19000");
				put("literacy (%)", "100.0");
				put("pop. density (per sq. mi.)", "152.1");
				put("cluster", "0");
			}});

		}});
		assertNotNull(actualData);
		assertEquals(expectedData.getLabelColumn(), actualData.getLabelColumn());
		assertEquals(expectedData.getColumns(), actualData.getColumns());
		assertTrue(actualData.getItems().containsAll(expectedData.getItems()));
		sessionUtil.resetSessionId();
	}
}
