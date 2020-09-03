package org.dice.ida.model.bargraph;

public class BarGraphItem {

	private String x;
	private String y;

	public BarGraphItem(String x, String y) {
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
	public String getY() {
		return y;
	}
	public void setY(String y) {
		this.y = y;
	}
	@Override
	public String toString() {
		return "BarGraphItem [x=" + x + ", y=" + y + "]";
	}

}
