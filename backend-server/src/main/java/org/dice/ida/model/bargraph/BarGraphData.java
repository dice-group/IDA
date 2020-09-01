package org.dice.ida.model.bargraph;

import java.util.List;

public class BarGraphData {
	private String label;
	private List<BarGraphItem> items;
	
	public BarGraphData(String label, List<BarGraphItem> items) {
		super();
		this.label = label;
		this.items = items;
	}
	//Optional attributes
	private String dsName;
	private String tableName;
	// TODO: Add filtering information
	
	public BarGraphData(String label, List<BarGraphItem> items, String dsName, String tableName) {
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
	public List<BarGraphItem> getItems() {
		return items;
	}
	public void setItems(List<BarGraphItem> items) {
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
