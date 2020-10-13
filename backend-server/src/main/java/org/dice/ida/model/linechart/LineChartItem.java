package org.dice.ida.model.linechart;

import java.util.List;

public class LineChartItem {
	private String label;
	private List<Double> values;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<Double> getValues() {
		return values;
	}

	public void setValues(List<Double> values) {
		this.values = values;
	}
}
