package org.dice.ida.action.def;

import java.util.Map;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.vizsuggest.VizSuggestOrchestrator;

public class BarGraphAction implements Action {

	@Override
	public void performAction(Map<String, Object> paramMap, ChatMessageResponse chatMessageResponse) {
		// TODO Auto-generated method stub
		
		try {
			
			
			
			Map<String, Object> payload = chatMessageResponse.getPayload();
			if (payload.get("activeDS") == null || payload.get("activeTable") == null) {
				chatMessageResponse.setMessage(IDAConst.BOT_SOMETHING_WRONG);
				chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
				return;
			}
			else
			{
				String datasetName = payload.get("activeDS").toString();
				String tableName = payload.get("activeTable").toString();
				if (datasetName.isEmpty() || tableName.isEmpty()) {
					if (datasetName.isEmpty()) {
						chatMessageResponse.setMessage(IDAConst.BOT_LOAD_DS_BEFORE);
					}else 
					if (tableName.isEmpty()) {
						chatMessageResponse.setMessage(IDAConst.BOT_SELECT_TABLE);
					} 
					chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
					
				} 
				else 
				{
					String xAxis = paramMap.get(IDAConst.PARAM_XAXIS_NAME).toString();
					String yAxis = paramMap.get(IDAConst.PARAM_YAXIS_NAME).toString();
					
					if(xAxis != null && !xAxis.isEmpty() && yAxis != null && !yAxis.isEmpty())
					{
						//Code to create a bar graph
					}
					else
					SimpleTextAction.setSimpleTextResponse(paramMap, chatMessageResponse);
				}
			}
			
			
				
			}
		 catch (Exception e) {
				chatMessageResponse.setMessage(IDAConst.BOT_SOMETHING_WRONG);
				chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
			}
			
	}

}
