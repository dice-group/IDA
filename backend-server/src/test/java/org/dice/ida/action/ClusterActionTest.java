package org.dice.ida.action;


import org.dice.ida.controller.MessageController;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.dice.ida.util.SessionUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ClusterActionTest {

	private ChatUserMessage chatUserMessage;
	private ChatMessageResponse chatMessageResponse;
	@Autowired
	private MessageController messageController;
	@Autowired
	private SessionUtil sessionUtil;

	@Test
	void testClusterData() throws Exception {
		chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("clustering");
		chatUserMessage.setActiveDS("countries");
		chatUserMessage.setActiveTable("countries-of-the-world.csv");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("all");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("kmeans");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("no");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		List<Map<String, String>> clusteredData = (List<Map<String, String>>) chatMessageResponse.getPayload().get("clusteredData");
		Set<String> clusterSet = new HashSet<>();
		clusteredData.forEach(data -> clusterSet.add(data.get("Cluster")));
		assertNotNull(clusteredData);
		assertTrue(clusterSet.size() > 1);
		sessionUtil.resetSessionId();
	}

	@Test
	void testNumberofClusters() throws Exception {
		chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("clustering");
		chatUserMessage.setActiveDS("countries");
		chatUserMessage.setActiveTable("countries-of-the-world.csv");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("all");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("kmeans");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("yes");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("N = 7");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("no");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		List<Map<String, String>> clusteredData = (List<Map<String, String>>) chatMessageResponse.getPayload().get("clusteredData");
		Set<String> clusterSet = new HashSet<>();
		clusteredData.forEach(data -> clusterSet.add(data.get("Cluster")));
		assertNotNull(clusteredData);
		assertEquals(clusterSet.size(), 7);
		sessionUtil.resetSessionId();
	}

	@Test
	void testFarthestFirst() throws Exception {
		chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("clustering");
		chatUserMessage.setActiveDS("countries");
		chatUserMessage.setActiveTable("countries-of-the-world.csv");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("all");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("farthest first");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("no");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		List<Map<String, String>> clusteredData = (List<Map<String, String>>) chatMessageResponse.getPayload().get("clusteredData");
		Set<String> clusterSet = new HashSet<>();
		clusteredData.forEach(data -> clusterSet.add(data.get("Cluster")));
		assertNotNull(clusteredData);
		assertTrue(clusterSet.size() > 1);
		sessionUtil.resetSessionId();
	}

	@Test
	void testMultiParamChange() throws Exception {
		chatUserMessage = new ChatUserMessage();
		chatUserMessage.setMessage("clustering");
		chatUserMessage.setActiveDS("countries");
		chatUserMessage.setActiveTable("countries-of-the-world.csv");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("all");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("kmeans");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("yes");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("N = 7, I = 50");
		messageController.handleMessage(chatUserMessage).call();
		chatUserMessage.setMessage("no");
		chatMessageResponse = messageController.handleMessage(chatUserMessage).call();
		List<Map<String, String>> clusteredData = (List<Map<String, String>>) chatMessageResponse.getPayload().get("clusteredData");
		Set<String> clusterSet = new HashSet<>();
		clusteredData.forEach(data -> clusterSet.add(data.get("Cluster")));
		assertNotNull(clusteredData);
		assertEquals(clusterSet.size(), 7);
		sessionUtil.resetSessionId();
	}
}
