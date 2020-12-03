package org.dice.ida.action.def;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.dice.ida.vizsuggest.VizSuggestOrchestrator;
import org.springframework.stereotype.Component;

import java.util.Map;
@Component
public class SuggestVisualization implements Action {
	@Override
	public void performAction(Map<String, Object> paramMap, ChatMessageResponse chatMessageResponse, ChatUserMessage message) {
		try {
			Map<String, Object> payload = chatMessageResponse.getPayload();
			if (payload.get("activeDS") == null || payload.get("activeTable") == null) {
				chatMessageResponse.setMessage(IDAConst.BOT_SOMETHING_WRONG);
				chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
				return;
			}
			String datasetName = payload.get("activeDS").toString();
			String tableName = payload.get("activeTable").toString();
			if (datasetName.isEmpty()) {
				chatMessageResponse.setMessage(IDAConst.BOT_LOAD_DS_BEFORE);
			} else if (tableName.isEmpty()) {
				chatMessageResponse.setMessage(IDAConst.BOT_SELECT_TABLE);
			} else {
				VizSuggestOrchestrator vizSuggestOrchestrator = new VizSuggestOrchestrator(tableName, datasetName);
				chatMessageResponse.setMessage(vizSuggestOrchestrator.getSuggestion());
			}
			chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
		} catch (Exception e) {
			chatMessageResponse.setMessage(IDAConst.BOT_SOMETHING_WRONG);
			chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
		}
	}
}
