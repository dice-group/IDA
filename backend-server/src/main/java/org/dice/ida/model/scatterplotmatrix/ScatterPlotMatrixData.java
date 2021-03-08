package org.dice.ida.model.scatterplotmatrix;

import java.util.List;
import java.util.Map;

/**
 * Model class for Scatter Plot Matrix Data
 *
 * @author Sourabh Poddar
 */
public class ScatterPlotMatrixData {


	private List<String> columns;
	private String referenceColumn;
	private List<Map<String, String>> items;
	private String labelColumn;

	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	public String getReferenceColumn() {
		return referenceColumn;
	}

	public void setReferenceColumn(String referenceColumn) {
		this.referenceColumn = referenceColumn;
	}

	public List<Map<String, String>> getItems() {
		return items;
	}

	public void setItems(List<Map<String, String>> items) {
		this.items = items;
	}

	public String getLabelColumn() {
		return labelColumn;
	}

	public void setLabelColumn(String labelColumn) {
		this.labelColumn = labelColumn;
	}


	@Override
	public boolean equals(Object obj2) {
		if (!(obj2 instanceof ScatterPlotMatrixData)) {
			return false;
		}
		ScatterPlotMatrixData object2 = (ScatterPlotMatrixData) obj2;
		return !(!this.referenceColumn.equals(object2.getReferenceColumn()) || !this.columns.containsAll(object2.getColumns()) || !this.getItems().containsAll(object2.getItems()));
	}
}
