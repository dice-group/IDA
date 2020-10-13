package org.dice.ida.action.def;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;

import java.util.Map;

public class LineChartAction implements Action {
	@Override
	public void performAction(Map<String, Object> paramMap, ChatMessageResponse resp) {
		resp.setUiAction(IDAConst.UIA_LINECHART);
		resp.setMessage("Line chart added to the view");
		resp.setPayload(null);
	}
}
