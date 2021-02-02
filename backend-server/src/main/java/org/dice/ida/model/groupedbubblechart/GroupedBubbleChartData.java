package org.dice.ida.model.groupedbubblechart;

import org.dice.ida.model.bubblechart.BubbleChartItem;

import java.util.List;
import java.util.Map;

public class GroupedBubbleChartData {
	private String chartDesc;
	private List<String> bubbleLabels;
	private Map<String, List<BubbleChartItem>> groupedBubbleChartData;

	public GroupedBubbleChartData(String chartDesc, List<String> bubbleLabels, Map<String, List<BubbleChartItem>> groupedBubbleChartData) {
		this.chartDesc = chartDesc;
		this.bubbleLabels = bubbleLabels;
		this.groupedBubbleChartData = groupedBubbleChartData;
	}

	public String getChartDesc() {
		return chartDesc;
	}

	public void setChartDesc(String chartDesc) {
		this.chartDesc = chartDesc;
	}

	public List<String> getBubbleLabels() {
		return bubbleLabels;
	}

	public void setBubbleLabels(List<String> bubbleLabels) {
		this.bubbleLabels = bubbleLabels;
	}

	public Map<String, List<BubbleChartItem>> getGroupedBubbleChartData() {
		return groupedBubbleChartData;
	}

	public void setGroupedBubbleChartData(Map<String, List<BubbleChartItem>> groupedBubbleChartData) {
		this.groupedBubbleChartData = groupedBubbleChartData;
	}
}
