package org.dice.ida.model.bubblechart;

public class BubbleChartItem {
	private String label;
	private String description;
	private Double size;

	public BubbleChartItem(String label, String description, Double size) {
		super();
		this.label = label;
		this.description = description;
		this.size = size;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getSize() {
		return this.size;
	}

	public void setSize(Double size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return "BubbleChartItem [label=" + label + ", description=" + description + ", size=" + size + "]";
	}

}
