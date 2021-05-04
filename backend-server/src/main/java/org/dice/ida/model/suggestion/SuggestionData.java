package org.dice.ida.model.suggestion;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SuggestionData {
	private String vizName;
	private VisualizationInfo visualizationInfo;
	private List<SuggestionParam> suggestionParamList;
	private Map<String, String> visualizationParams;
	private Map<String, Set<String>> visualizationParamTypeList;

	public Map<String, Set<String>> getVisualizationParamTypeList() {
		return visualizationParamTypeList;
	}

	public void setVisualizationParamTypeList(Map<String, Set<String>> visualizationParamTypeList) {
		this.visualizationParamTypeList = visualizationParamTypeList;
	}

	public String getVizName() {
		return vizName;
	}

	public void setVizName(String vizName) {
		this.vizName = vizName;
	}

	public VisualizationInfo getVisualizationInfo() {
		return visualizationInfo;
	}

	public void setVisualizationInfo(VisualizationInfo visualizationInfo) {
		this.visualizationInfo = visualizationInfo;
	}

	public List<SuggestionParam> getSuggestionParamList() {
		return suggestionParamList;
	}

	public void setSuggestionParamList(List<SuggestionParam> suggestionParamList) {
		this.suggestionParamList = suggestionParamList;
	}

	public Map<String, String> getVisualizationParams() {
		return visualizationParams;
	}

	public void setVisualizationParams(Map<String, String> visualizationParams) {
		this.visualizationParams = visualizationParams;
	}
}
