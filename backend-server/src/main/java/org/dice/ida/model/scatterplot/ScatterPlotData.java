package org.dice.ida.model.scatterplot;

import java.util.List;

public class ScatterPlotData {
	private String label;
	private List<ScatterPlotItem> items;
	private String xAxisLabel;
	private String yAxisLabel;

	//Optional attributes
	private String dsName;
	private String tableName;
	// TODO: Add filtering information

	public ScatterPlotData(String label, List<ScatterPlotItem> items, String xAxisLabel, String yAxisLabel) {
		super();
		this.label = label;
		this.items = items;
		this.xAxisLabel = xAxisLabel;
		this.yAxisLabel = yAxisLabel;
	}
	// Optional
	public ScatterPlotData(String label, List<ScatterPlotItem> items, String xAxisLabel, String yAxisLabel, String dsName,
			String tableName) {
		super();
		this.label = label;
		this.items = items;
		this.xAxisLabel = xAxisLabel;
		this.yAxisLabel = yAxisLabel;
		this.dsName = dsName;
		this.tableName = tableName;
	}

	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public List<ScatterPlotItem> getItems() {
		return items;
	}
	public void setItems(List<ScatterPlotItem> items) {
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

}
