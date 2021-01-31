package org.dice.ida.model.groupedBarGraph;

import org.dice.ida.model.bargraph.BarGraphItem;

import java.util.List;
import java.util.Map;

public class GroupedBarGraphData {
	private String chartDesc;
	private String xAxisLabel;
	private String yAxisLabel;
	private List<String> xAxisLabels;
	private Map<String, List<BarGraphItem>> groupedBarChartData;

	public GroupedBarGraphData(String chartDesc, String xAxisLabel, String yAxisLabel, List<String> xAxisLabels, Map<String, List<BarGraphItem>> groupedBarChartData) {
		this.chartDesc = chartDesc;
		this.xAxisLabel = xAxisLabel;
		this.yAxisLabel = yAxisLabel;
		this.xAxisLabels = xAxisLabels;
		this.groupedBarChartData = groupedBarChartData;
	}

	public String getChartDesc() {
		return chartDesc;
	}

	public void setChartDesc(String chartDesc) {
		this.chartDesc = chartDesc;
	}

	public String getxAxisLabel() {
		return xAxisLabel;
	}

	public void setxAxisLabel(String xAxisLabel) {
		this.xAxisLabel = xAxisLabel;
	}

	public String getyAxisLabel() {
		return yAxisLabel;
	}

	public void setyAxisLabel(String yAxisLabel) {
		this.yAxisLabel = yAxisLabel;
	}

	public List<String> getxAxisLabels() {
		return xAxisLabels;
	}

	public void setxAxisLabels(List<String> xAxisLabels) {
		this.xAxisLabels = xAxisLabels;
	}

	public Map<String, List<BarGraphItem>> getGroupedBarChartData() {
		return groupedBarChartData;
	}

	public void setGroupedBarChartData(Map<String, List<BarGraphItem>> groupedBarChartData) {
		this.groupedBarChartData = groupedBarChartData;
	}
}
