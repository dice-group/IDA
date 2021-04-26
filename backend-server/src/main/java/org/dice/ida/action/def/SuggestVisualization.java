package org.dice.ida.action.def;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.dice.ida.model.suggestion.SuggestionData;
import org.dice.ida.model.suggestion.SuggestionParam;
import org.dice.ida.model.suggestion.VisualizationInfo;
import org.dice.ida.util.RDFUtil;
import org.dice.ida.util.SuggestionUtil;
import org.dice.ida.util.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Component
public class SuggestVisualization implements Action {
	@Autowired
	private SuggestionUtil suggestionUtil;

	@Autowired
	private RDFUtil rdfUtil;

	@Override
	public void performAction(Map<String, Object> paramMap, ChatMessageResponse chatMessageResponse, ChatUserMessage message) throws Exception {
		Map<String, Object> payload = chatMessageResponse.getPayload();
		if (ValidatorUtil.preActionValidation(chatMessageResponse)) {
			String datasetName = payload.get("activeDS").toString();
			String tableName = payload.get("activeTable").toString();
			Map<String, Map<String, Double>> statProps = suggestionUtil.getStatProps(datasetName, tableName);
			Map<String, Map<String, List<String>>> suggestionParam = rdfUtil.getSuggestionParamters();

			Map<String, VisualizationInfo> vizInfoMap = rdfUtil.getVisualizationInfo();
			List<SuggestionData> suggestionResponse = new ArrayList<>();
			for (String viz : suggestionParam.keySet()) {
				Map<String, List<String>> paramList = suggestionParam.get(viz);
				List<SuggestionParam> suggestionParamList = new ArrayList<>();
				Map<String, String> vizParams = new HashMap<>();
				for (String param : paramList.keySet()) {
					Map<String, Double> attributeList = statProps.get(paramList.get(param).get(0));
					String attributeProperty = paramList.get(param).get(1);
					String key;
					if (attributeList.isEmpty()) {
						suggestionParamList = new ArrayList<>();
						break;
					}
					if (attributeProperty.equals("min"))
						key = Collections.min(attributeList.entrySet(), Map.Entry.comparingByValue()).getKey();
					else
						key = Collections.max(attributeList.entrySet(), Map.Entry.comparingByValue()).getKey();
					if (key.contains("|")) {
						key = key.replace("|", " or ");
					}
					vizParams.put(param, key);
					param = IDAConst.PARAM_NAME_MAP.getOrDefault(param, param);
					suggestionParamList.add(new SuggestionParam(param, key));
				}
				if(!suggestionParamList.isEmpty()){
					SuggestionData suggestionData = new SuggestionData();
					suggestionData.setVizName(viz);
					suggestionData.setVisualizationInfo(vizInfoMap.get(viz));
					suggestionData.setSuggestionParamList(suggestionParamList);
					suggestionData.setVisualizationParams(vizParams);
					suggestionResponse.add(suggestionData);
				}
			}
			payload.put("suggestionData", suggestionResponse);
			chatMessageResponse.setUiAction(IDAConst.UIA_LOAD_SUGGESTION);
			chatMessageResponse.setMessage(IDAConst.SUGGESTION_LOADED);
		}
	}
}
