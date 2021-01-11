package org.dice.ida.action;


import org.dice.ida.action.def.ClusterAction;
import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ClusterActionTest {

	@Autowired
	private ClusterAction clusterAction;
	private ChatUserMessage chatUserMessage;
	private ChatMessageResponse chatMessageResponse;

	@Test
	void testClusterData() {
		chatUserMessage = new ChatUserMessage();
		chatMessageResponse = new ChatMessageResponse();
		chatMessageResponse.setPayload(new HashMap<>() {{
			put("activeDS", "countries");
			put("activeTable", "countries-of-the-world.csv");
		}});
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(IDAConst.FULL_INTENT_NAME, "clustering - getColumnList");
		com.google.protobuf.Value merged;
		merged = com.google.protobuf.Value.newBuilder().setListValue(com.google.protobuf.ListValue.newBuilder().addValues(com.google.protobuf.Value.newBuilder().setStringValue("All"))).build();
		paramMap.put("column_List", merged);
		paramMap.put(IDAConst.PARAM_TEXT_MSG, "This is a test");
		clusterAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		paramMap.put(IDAConst.FULL_INTENT_NAME, "clustering - Kmeans");
		clusterAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		paramMap.put(IDAConst.FULL_INTENT_NAME, "clustering - Kmeans - no");
		clusterAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		List<Map<String, String>> clusteredData = (List<Map<String, String>>) chatMessageResponse.getPayload().get("clusteredData");
		Set<String> clusterSet = new HashSet<>();
		clusteredData.forEach(data -> clusterSet.add(data.get("Cluster")));
		assertNotNull(clusteredData);
		assertTrue(clusterSet.size() > 1);
	}

	@Test
	void testNumberofClusters() {
		chatUserMessage = new ChatUserMessage();
		chatMessageResponse = new ChatMessageResponse();
		chatMessageResponse.setPayload(new HashMap<>() {{
			put("activeDS", "countries");
			put("activeTable", "countries-of-the-world.csv");
		}});
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(IDAConst.FULL_INTENT_NAME, "clustering - getColumnList");
		com.google.protobuf.Value merged;
		merged = com.google.protobuf.Value.newBuilder().setListValue(com.google.protobuf.ListValue.newBuilder().addValues(com.google.protobuf.Value.newBuilder().setStringValue("All"))).build();
		paramMap.put("column_List", merged);
		paramMap.put(IDAConst.PARAM_TEXT_MSG, "This is a test");
		clusterAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		paramMap.put(IDAConst.FULL_INTENT_NAME, "clustering - Kmeans");
		clusterAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		paramMap.put(IDAConst.FULL_INTENT_NAME, "clustering - Kmeans - yes - getNumCluster");
		paramMap.put(IDAConst.NUMBER_OF_CLUSTER, "number_value: 7.0");
		clusterAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		paramMap.put(IDAConst.FULL_INTENT_NAME, "clustering - Kmeans - no");
		clusterAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		List<Map<String, String>> clusteredData = (List<Map<String, String>>) chatMessageResponse.getPayload().get("clusteredData");
		Set<String> clusterSet = new HashSet<>();
		clusteredData.forEach(data -> clusterSet.add(data.get("Cluster")));
		assertNotNull(clusteredData);
		assertEquals(clusterSet.size(), 7);
	}

	@Test
	void testFarthestFirst() {
		chatUserMessage = new ChatUserMessage();
		chatMessageResponse = new ChatMessageResponse();
		chatMessageResponse.setPayload(new HashMap<>() {{
			put("activeDS", "countries");
			put("activeTable", "countries-of-the-world.csv");
		}});
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(IDAConst.FULL_INTENT_NAME, "clustering - getColumnList");
		com.google.protobuf.Value merged;
		merged = com.google.protobuf.Value.newBuilder().setListValue(com.google.protobuf.ListValue.newBuilder().addValues(com.google.protobuf.Value.newBuilder().setStringValue("All"))).build();
		paramMap.put("column_List", merged);
		paramMap.put(IDAConst.PARAM_TEXT_MSG, "This is a test");
		clusterAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		paramMap.put(IDAConst.FULL_INTENT_NAME, "clustering - FarthestFirst");
		clusterAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		paramMap.put(IDAConst.FULL_INTENT_NAME, "clustering - FarthestFirst - no");
		clusterAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		List<Map<String, String>> clusteredData = (List<Map<String, String>>) chatMessageResponse.getPayload().get("clusteredData");
		Set<String> clusterSet = new HashSet<>();
		clusteredData.forEach(data -> clusterSet.add(data.get("Cluster")));
		assertNotNull(clusteredData);
		assertTrue(clusterSet.size() > 1);
	}

	@Test
	void testMultiParamChange() {
		chatUserMessage = new ChatUserMessage();
		chatMessageResponse = new ChatMessageResponse();
		chatMessageResponse.setPayload(new HashMap<>() {{
			put("activeDS", "countries");
			put("activeTable", "countries-of-the-world.csv");
		}});
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(IDAConst.FULL_INTENT_NAME, "clustering - getColumnList");
		com.google.protobuf.Value merged;
		merged = com.google.protobuf.Value.newBuilder().setListValue(com.google.protobuf.ListValue.newBuilder().addValues(com.google.protobuf.Value.newBuilder().setStringValue("All"))).build();
		paramMap.put("column_List", merged);
		paramMap.put(IDAConst.PARAM_TEXT_MSG, "This is a test");
		clusterAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		paramMap.put(IDAConst.FULL_INTENT_NAME, "clustering - Kmeans");
		clusterAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		paramMap.put(IDAConst.FULL_INTENT_NAME, "clustering - Kmeans - yes - getMultiParam");
		paramMap.put(IDAConst.NUMBER_OF_CLUSTER, "number_value: 7.0");
		paramMap.put(IDAConst.MAX_ITERATION, "number_value: 50.0");
		paramMap.put(IDAConst.INIT_METHOD, "");
		paramMap.put(IDAConst.IS_REPLACE_MISSING_VALUE, "");
		paramMap.put(IDAConst.NUM_OF_SLOT, "");
		paramMap.put(IDAConst.RANDOM_SEED, "");
		clusterAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		paramMap.put(IDAConst.FULL_INTENT_NAME, "clustering - Kmeans - no");
		clusterAction.performAction(paramMap, chatMessageResponse, chatUserMessage);
		List<Map<String, String>> clusteredData = (List<Map<String, String>>) chatMessageResponse.getPayload().get("clusteredData");
		Set<String> clusterSet = new HashSet<>();
		clusteredData.forEach(data -> clusterSet.add(data.get("Cluster")));
		assertNotNull(clusteredData);
		assertEquals(clusterSet.size(), 7);
	}
}
