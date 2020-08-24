package org.dice.ida.model;

import java.util.List;

public class DataSummary {
	private String name;
	private long numberOfInstances;
	private List<AttributeSummary> attributeSummaryList;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getNumberOfInstances() {
		return numberOfInstances;
	}

	public void setNumberOfInstances(long numberOfInstances) {
		this.numberOfInstances = numberOfInstances;
	}

	public List<AttributeSummary> getAttributeSummaryList() {
		return attributeSummaryList;
	}

	public void setAttributeSummaryList(List<AttributeSummary> attributeSummaryList) {
		this.attributeSummaryList = attributeSummaryList;
	}
}
