package org.dice.ida.model.bubblechart;

import java.util.List;

public class BubbleChartData {
	private String label;
	private List<BubbleChartItem> items;

	private String dsName;
	private String tableName;

	// Optional
	public BubbleChartData(String label, List<BubbleChartItem> items, String dsName,
						String tableName) {
		super();
		this.label = label;
		this.items = items;
		this.dsName = dsName;
		this.tableName = tableName;
	}

	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public List<BubbleChartItem> getItems() {
		return items;
	}
	public void setItems(List<BubbleChartItem> items) {
		this.items = items;
	}
	public String getDsName() {
		return dsName;
	}
	public void setDsName(String dsName) {
		this.dsName = dsName;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
}
