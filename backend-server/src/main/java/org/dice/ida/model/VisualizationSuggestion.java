package org.dice.ida.model;

import java.util.List;
import java.util.Map;

public class VisualizationSuggestion {
    private String name;
    private Map<String, List<String>> paramMap;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, List<String>> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, List<String>> paramMap) {
        this.paramMap = paramMap;
    }
}
