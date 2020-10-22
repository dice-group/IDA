package org.dice.ida.model.linechart;

import java.util.List;

public class LineChartItem {
	private String label;
	private List<Double> lineValues;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<Double> getLineValues() {
		return lineValues;
	}

	public void setLineValues(List<Double> lineValues) {
		this.lineValues = lineValues;
	}
}
