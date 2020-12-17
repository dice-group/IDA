package org.dice.ida.action.def;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.dice.ida.util.FileUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

@Component
public class ListDataSetsAction implements Action {
	@Override
	public void performAction(Map<String, Object> paramMap, ChatMessageResponse resp, ChatUserMessage userMessage) throws IOException {
		
		FileUtil fileUtil = new FileUtil();
		ArrayList<String> datasets = fileUtil.getListOfDatasets();
		int datasetsSize = datasets.size();

		StringBuilder message = new StringBuilder();

		if (datasetsSize == 1) {
			message.append("I have just <b>\"");
			message.append(datasets.get(0));
			message.append("\"</b> dataset available at the moment.");
		} else if (datasetsSize == 0) {
			message.append("I don't have any available dataset at the moment.");
		} else {
			message.append("I have total ");
			message.append(datasetsSize);
			message.append(" available datasets: <br> ");
			for (int i = 0; i < datasetsSize; i++)
			{
				if (i == (datasetsSize-1)) {
					message.append(" and <br>");
				} else if (i != 0) {
					message.append(",<br> ");
				}
				message.append("<b>");
				message.append(datasets.get(i));
				message.append("</b>");
			}
			
		}
		resp.setMessage(message.toString());
		resp.setUiAction(IDAConst.UAC_NRMLMSG);
		
	}
}
