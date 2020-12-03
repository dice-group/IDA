package org.dice.ida.action.def;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ListVisualizationsAction implements Action {
	@Override
	public void performAction(Map<String, Object> paramMap, ChatMessageResponse resp, ChatUserMessage userMessage) {
		String[] vs_list = IDAConst.VISUALIZATIONS_LIST;

		int list_size = vs_list.length;

		StringBuilder message = new StringBuilder();

		if (list_size == 1) {
			message.append("I can only draw \"");
			message.append(vs_list[0]);
		} else {
			message.append("I can draw ");
			message.append(list_size);
			message.append(" visualization. ");
			for (int i = 0; i < list_size; i++)
			{
				if (i == (list_size-1)) {
					message.append(" and ");
				} else if (i != 0) {
					message.append(", ");
				}
				message.append(vs_list[i]);
			}
		}
		resp.setMessage(message.toString());



//		resp.setMessage(IDAConst.VISUALIZATIONS_LIST);
		resp.setUiAction(IDAConst.UAC_NRMLMSG);
	}
}
