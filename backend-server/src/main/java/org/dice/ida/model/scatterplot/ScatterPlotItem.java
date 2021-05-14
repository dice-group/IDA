package org.dice.ida.model.scatterplot;

public class ScatterPlotItem {

	private Double x;
	private Double y;
	private String reference;
	private String labelColumn;


	public ScatterPlotItem(Double x, Double y, String reference, String label ) {
		super();
		this.x = x;
		this.y = y;
		this.reference = reference;
		this.labelColumn = label;
	}

	public String getLabelColumn() {
		return labelColumn;
	}

	public void setLabelColumn(String labelColumn) {
		this.labelColumn = labelColumn;
	}

	public Double getX() {
		return x;
	}

	public void setX(Double x) {
		this.x = x;
	}

	public Double getY() {
		return y;
	}

	public void setY(Double y) {
		this.y = y;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	@Override
	public String toString() {
		return "ScatterPlotItem [x=" + x + ", y=" + y + ", reference=]" + reference;
	}

	@Override
	public boolean equals(Object obj2) {
		if (!(obj2 instanceof ScatterPlotItem)) {
			return false;
		}
		return this.toString().equals(obj2.toString());
	}

}
