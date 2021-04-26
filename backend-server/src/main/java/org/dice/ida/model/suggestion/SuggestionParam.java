package org.dice.ida.model.suggestion;

public class SuggestionParam {
	private String param;
	private String value;

	public SuggestionParam(String param, String value) {
		this.param = param;
		this.value = value;
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
}
