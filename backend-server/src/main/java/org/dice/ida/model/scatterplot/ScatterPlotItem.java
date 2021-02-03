package org.dice.ida.model.scatterplot;

public class ScatterPlotItem {

	private String x;
	private Double y;

	public ScatterPlotItem(String x, Double y) {
		super();
		this.x = x;
		this.y = y;
	}
	public String getX() {
		return x;
	}
	public void setX(String x) {
		this.x = x;
	}
	public Double getY() {
		return y;
	}
	public void setY(Double y) {
		this.y = y;
	}
	@Override
	public String toString() {
		return "ScatterPlotItem [x=" + x + ", y=" + y + "]";
	}

	@Override
	public boolean equals(Object obj2) {
		if (!(obj2 instanceof ScatterPlotItem)) {
			return false;
		}
		return this.toString().equals(obj2.toString());
	}

}
