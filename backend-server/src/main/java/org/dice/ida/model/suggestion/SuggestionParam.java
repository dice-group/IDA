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

	@Override
	public String toString() {
		return "Suggestion Param [param=" + param + ", value=" + value + ", attribute=" + attributeName + "]";
	}

	@Override
	public boolean equals(Object obj2) {
		if (!(obj2 instanceof SuggestionParam)) {
			return false;
		}
		return this.toString().equals(obj2.toString());
	}
}
