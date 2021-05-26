package org.dice.ida.action;

import org.dice.ida.controller.MessageController;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.dice.ida.model.suggestion.SuggestionData;
import org.dice.ida.model.suggestion.SuggestionParam;
import org.dice.ida.util.SessionUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class SuggestVisualizationActionTest {
	@Autowired
	private MessageController messageController;
	@Autowired
	private SessionUtil sessionUtil;

	@Test
	void suggestionListTest() throws Exception {
		ChatUserMessage chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("suggest me visualizations");
		chatUserMessage.setActiveDS("test_dataset");
		chatUserMessage.setActiveTable("Patient_Data_Before_20-04-2020.csv");
		ChatMessageResponse chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		List<SuggestionData> actualResponse = (List<SuggestionData>) chatMessageResponse.getPayload().get("suggestionData");
		assertNotNull(actualResponse);
		Map<String, List<SuggestionParam>> actualSuggestionList = new HashMap<>();
		Map<String, Map<String, Set<String>>> actualSuggestedTypeList = new HashMap<>();
		for(SuggestionData suggestionData: actualResponse) {
			actualSuggestionList.put(suggestionData.getVizName(), suggestionData.getSuggestionParamList());
			actualSuggestedTypeList.put(suggestionData.getVizName(), suggestionData.getVisualizationParamTypeList());
		}
		Map<String, List<SuggestionParam>> expectedSuggestionList = new HashMap<>(){{
			put("Line Chart", new ArrayList<>(){{
				add(new SuggestionParam("X-Axis (Temporal data)", "Date Announced", "Temporal_Column"));
				add(new SuggestionParam("Line Labels", "Detected District", "Line_Label"));
				add(new SuggestionParam("Line Values", "Patient Number", "Line_Value"));
			}});
			put("Bubble Chart", new ArrayList<>(){{
				add(new SuggestionParam("Size of the bubbles", "Patient Number", "Bubble_Size"));
				add(new SuggestionParam("Label of the bubbles", "Detected District", "Bubble_Label"));
			}});
			put("Bar Chart", new ArrayList<>(){{
				add(new SuggestionParam("X-Axis", "Detected District", "X-Axis"));
				add(new SuggestionParam("Y-Axis", "Patient Number", "Y-Axis"));
			}});
			put("Scatter plot", new ArrayList<>(){{
				add(new SuggestionParam("Reference Values", "Detected District", "Reference_Values"));
				add(new SuggestionParam("X-Axis", "Age Bracket", "X-Axis"));
				add(new SuggestionParam("Y-Axis", "Patient Number", "Y-Axis"));
			}});
		}};
		Map<String, Map<String, Set<String>>> expectedSuggestedTypeList = new HashMap<>(){{
			put("Line Chart", new HashMap<>(){{
				put("Line_Label", new HashSet<>(){{
					add("Non Unique");
				}});
				put("Line_Value", new HashSet<>(){{
					add("Count Of");
					add("Average");
					add("Sum Of");
				}});
				put("Temporal_Column", new HashSet<>(){{
					add("Date");
				}});
			}});
			put("Bubble Chart", new HashMap<>(){{
				put("Bubble_Label", new HashSet<>(){{
					add("Non Unique");
				}});
				put("Bubble_Size", new HashSet<>(){{
					add("Count Of");
					add("Average");
					add("Sum Of");
				}});
			}});
			put("Bar Chart", new HashMap<>(){{
				put("X-Axis", new HashSet<>(){{
					add("Non Unique");
				}});
				put("Y-Axis", new HashSet<>(){{
					add("Count Of");
					add("Average");
					add("Sum Of");
				}});
			}});
			put("Scatter plot", new HashMap<>(){{
				put("Reference_Values", new HashSet<>(){{
					add("Not Required");
				}});
				put("X-Axis", new HashSet<>(){{
					add("Numeric");
				}});
				put("Y-Axis", new HashSet<>(){{
					add("Numeric");
				}});
			}});
		}};
		assertEquals(expectedSuggestionList, actualSuggestionList);
		assertEquals(expectedSuggestedTypeList, actualSuggestedTypeList);
		sessionUtil.resetSessionId();
	}
}
