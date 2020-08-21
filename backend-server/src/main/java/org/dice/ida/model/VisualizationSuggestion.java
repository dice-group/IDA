package org.dice.ida.model;

import java.util.List;
import java.util.Map;

public class VisualizationSuggestion {
    private String name;
    private List<Map<String, String>> paramMap;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Map<String, String>> getParamMap() {
        return paramMap;
    }

    public void setParamMap(List<Map<String, String>> paramMap) {
        this.paramMap = paramMap;
    }
}
