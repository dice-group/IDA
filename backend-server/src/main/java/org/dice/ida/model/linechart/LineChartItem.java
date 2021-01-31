package org.dice.ida.model.linechart;


import java.util.List;

public class LineChartItem {
	private String label;
	private List<Double> lineValues;

	public LineChartItem() {
		super();
	}

	public LineChartItem(String label, List<Double> lineValues) {
		super();
		this.label = label;
		this.lineValues = lineValues;
	}

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

	@Override
	public String toString() {
		return "LineChartItem [label=" + label + ", lineValues=" + lineValues + "]";
	}

	@Override
	public boolean equals(Object obj2) {
		if (!(obj2 instanceof LineChartItem)) {
			return false;
		}
		return this.toString().equals(obj2.toString());
	}
}
