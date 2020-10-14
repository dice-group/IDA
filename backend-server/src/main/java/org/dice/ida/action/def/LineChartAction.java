package org.dice.ida.action.def;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.exception.IDAException;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.util.ValidatorUtil;

import java.util.ArrayList;
import java.util.Map;

public class LineChartAction implements Action {
	@Override
	public void performAction(Map<String, Object> paramMap, ChatMessageResponse chatMessageResponse) {
		Map<String, Object> payload = chatMessageResponse.getPayload();
		String datasetName = payload.get("activeDS").toString();
		String tableName = payload.get("activeTable").toString();
		String dateColumn = paramMap.get(IDAConst.LINE_CHART_PARAM_DATE_COL).toString().toLowerCase();
		String labelColumn = paramMap.get(IDAConst.LINE_CHART_PARAM_LABEL_COL).toString().toLowerCase();
		String valueColumn = paramMap.get(IDAConst.LINE_CHART_PARAM_VALUE_COL).toString().toLowerCase();
		try {
			if (ValidatorUtil.isStringEmpty(dateColumn) || ValidatorUtil.isStringEmpty(labelColumn) || ValidatorUtil.isStringEmpty(valueColumn)) {
				SimpleTextAction.setSimpleTextResponse(paramMap, chatMessageResponse);
				return;
			}
			Map<String, String> columnMap = ValidatorUtil.areParametersValid(datasetName, tableName, new ArrayList<>() {
				{
					add(dateColumn);
					add(labelColumn);
					add(valueColumn);
				}
			});
			// TODO: Implement line chart logic here
			validateParamTypes(dateColumn, labelColumn, valueColumn, columnMap);
//			chatMessageResponse.setUiAction(IDAConst.UIA_LINECHART);
			chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
			chatMessageResponse.setMessage(paramMap.get(IDAConst.PARAM_TEXT_MSG).toString());
		} catch (IDAException ex) {
			chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
			chatMessageResponse.setMessage(ex.getMessage());
		}
	}

	public void validateParamTypes(String dateColumn, String labelColumn, String valueColumn, Map<String, String> paramMap) throws IDAException {
		if (!IDAConst.COLUMN_TYPE_DATE.equals(paramMap.get(dateColumn))) {
			throw new IDAException(dateColumn + IDAConst.INVALID_DATE_COLUMN_MSG);
		} else if (!IDAConst.COLUMN_TYPE_NUMERIC.equals(paramMap.get(valueColumn)) && !labelColumn.equals(valueColumn)) {
			throw new IDAException(valueColumn + IDAConst.INVALID_NUMERIC_COLUMN_MSG);
		}
	}
}
