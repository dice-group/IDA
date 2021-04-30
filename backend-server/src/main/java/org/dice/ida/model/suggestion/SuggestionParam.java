package org.dice.ida.model.suggestion;

public class SuggestionParam {
	private String param;
	private String value;
	private String attributeName;

	public SuggestionParam(String param, String value, String attributeName) {
		this.param = param;
		this.value = value;
		this.attributeName = attributeName;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
}
