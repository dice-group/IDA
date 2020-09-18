package org.dice.ida.util;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import weka.core.Instances;

import java.util.HashMap;
import java.util.Map;

/**
 * This Class contains methods to validate different parts required
 * for IDA operations
 *
 * @author Maqbool
 */
@Component
@Scope("singleton")
public class ValidatorUtil {
	public boolean isStringEmpty(String str) {
		return str == null || str.isEmpty();
	}

	/**
	 * We are only interested to validate range filter e.g from 100 to 200
	 * And this function does it!
	 *
	 * @param filterText
	 * @return
	 */
	public boolean isFilterRangeValid(String filterText, Instances data) {
		String[] tokens = filterText.split(" "); // tokenized filter text
		String filterType = tokens[0]; // Dialogflow makes sure that these tokens are in correct order
		boolean result = true;
		int rangeStart;
		int rangeEnd;

		if (filterType.equalsIgnoreCase(IDAConst.BG_FILTER_FROM)) {
			rangeStart = Integer.parseInt(tokens[1]);
			rangeEnd = Integer.parseInt(tokens[3]);
			result = !(rangeStart >= rangeEnd || rangeStart > data.size() - 1 || rangeEnd > data.size());
		}
		return result;
	}

	/**
	 * This function checks if dataset was loaded and also user has
	 * 	selected the table
	 *  It returns boolean result which reflects validation & in case
	 *  of validation failure it sends message back to UI
	 * @param chatMessageResponse
	 * @return
	 */
	public boolean preActionValidation(ChatMessageResponse chatMessageResponse) {

		boolean results = false;
		Map<String, Object> payload = chatMessageResponse.getPayload();

		if (payload.get("activeDS") == null || payload.get("activeTable") == null) {
			chatMessageResponse.setMessage(IDAConst.BOT_SOMETHING_WRONG);
		} else {
			String datasetName = payload.get("activeDS").toString();
			String tableName = payload.get("activeTable").toString();
			if (datasetName.isEmpty()) {
				chatMessageResponse.setMessage(IDAConst.BOT_LOAD_DS_BEFORE);
			} else if (tableName.isEmpty()) {
				chatMessageResponse.setMessage(IDAConst.BOT_SELECT_TABLE);
			} else {
				results = true;
			}
		}
		chatMessageResponse.setUiAction(IDAConst.UIA_BARGRAPH);
		return results;
	}

}
