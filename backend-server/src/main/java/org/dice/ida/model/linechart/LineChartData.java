package org.dice.ida.model.linechart;

import java.util.List;

public class LineChartData {
	private String chartDesc;
	private String xAxisLabel;
	private String yAxisLabel;
	private List<String> xAxisLabels;
	private List<LineChartItem> lines;

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

	public List<LineChartItem> getLines() {
		return lines;
	}

	public void setLines(List<LineChartItem> lines) {
		this.lines = lines;
	}
}
