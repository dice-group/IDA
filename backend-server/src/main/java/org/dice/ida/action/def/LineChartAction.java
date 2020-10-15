package org.dice.ida.action.def;

import org.apache.commons.lang3.time.DateUtils;
import org.dice.ida.constant.IDAConst;
import org.dice.ida.exception.IDAException;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.linechart.LineChartData;
import org.dice.ida.model.linechart.LineChartItem;
import org.dice.ida.util.DataUtil;
import org.dice.ida.util.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.HashMap;

@Component
public class LineChartAction implements Action {

	@Autowired
	private DataUtil dataUtil;

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
			validateParamTypes(dateColumn, labelColumn, valueColumn, columnMap);
			List<Map<String, String>> data = dataUtil.getData(datasetName, tableName, new ArrayList<>() {
				{
					add(dateColumn);
					add(labelColumn);
					add(valueColumn);
				}
			});
			payload.put("lineChartData", binDataByDate(data, dateColumn, labelColumn, valueColumn));
			chatMessageResponse.setUiAction(IDAConst.UIA_LINECHART);
			chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
			chatMessageResponse.setMessage(paramMap.get(IDAConst.PARAM_TEXT_MSG).toString());
		} catch (IDAException ex) {
			chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
			chatMessageResponse.setMessage(ex.getMessage());
		} catch (Exception ex) {
			ex.printStackTrace();
			chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
			chatMessageResponse.setMessage(IDAConst.BOT_SOMETHING_WRONG);
		}
	}
	private void validateParamTypes(String dateColumn, String labelColumn, String valueColumn, Map<String, String> paramMap) throws IDAException {
		if (!IDAConst.COLUMN_TYPE_DATE.equals(paramMap.get(dateColumn))) {
			throw new IDAException(dateColumn + IDAConst.INVALID_DATE_COLUMN_MSG);
		} else if (!IDAConst.COLUMN_TYPE_NUMERIC.equals(paramMap.get(valueColumn)) && !labelColumn.equals(valueColumn)) {
			throw new IDAException(valueColumn + IDAConst.INVALID_NUMERIC_COLUMN_MSG);
		}
	}
	private LineChartData binDataByDate(List<Map<String, String>> data, String dateColumn, String valueColumn, String labelColumn) throws ParseException {
		Calendar cal = Calendar.getInstance();
		String[] parsePatterns = {"dd/MM/yyyy"};
		List<String> day = new ArrayList<>(), month = new ArrayList<>(), year = new ArrayList<>();
		for (Map<String, String> object : data) {
			String currentDate = object.get(dateColumn).trim();
			Date d = DateUtils.parseDateStrictly(currentDate, parsePatterns);
			cal.setTime(d);

			if (!day.contains(currentDate)) {
				day.add(currentDate);
			}
			if (!month.contains(getMonth(cal.get(Calendar.MONTH))+cal.get(Calendar.YEAR))) {
				month.add(getMonth(cal.get(Calendar.MONTH))+cal.get(Calendar.YEAR));
			}
			if (!year.contains(String.valueOf(cal.get(Calendar.YEAR)))) {
				year.add(String.valueOf(cal.get(Calendar.YEAR)));
			}
		}
		int d = Math.abs(day.size() - 10);
		int m = Math.abs(month.size() - 10);
		int y = Math.abs(year.size() - 10);
		if (d <= m && d <= y) {
			return loadData(data, dateColumn, valueColumn, labelColumn, day, "day");
		} else if (m <= y && m <= d) {
			return loadData(data, dateColumn, valueColumn, labelColumn, month, "month");
		} else {
			return loadData(data, dateColumn, valueColumn, labelColumn, year, "year");
		}
	}
	private String extractDateKey(String type, String dateString) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		String[] parsePatterns = {"dd/MM/yyyy"};
		Date date = DateUtils.parseDateStrictly(dateString, parsePatterns);
		calendar.setTime(date);
		switch(type) {
			case "month":
				return getMonth(calendar.get(Calendar.MONTH))+calendar.get(Calendar.YEAR);
			case "year":
				return String.valueOf(calendar.get(Calendar.YEAR));
			default:
				return "";
		}
	}
	private LineChartData loadData(List<Map<String, String>> data, String dateColumn, String valueColumn, String labelColumn, List<String> xaxislabels, String binType) throws ParseException{
		HashMap<String, HashMap<String, Double>> dataMap = new HashMap<>();
		for(Map<String,String> row: data)
		{
			String date = row.get(dateColumn);
			String label = row.get(labelColumn);
			String value = row.get(valueColumn);
			HashMap<String,Double> labelData = new HashMap<>();
			if(dataMap.containsKey(label))
			{
				labelData=dataMap.get(label);
			}
			String dateKey = extractDateKey(binType, date);
			if(labelData.containsKey(dateKey))
				labelData.put(dateKey,labelData.get(dateKey)+Integer.parseInt(value));
			else
				labelData.put(dateKey, (double) Integer.parseInt(value));

			dataMap.put(label,labelData);
		}
		for(String label : dataMap.keySet())
		{
			HashMap<String,Double> labelData = dataMap.get(label);
			for(String xaxislabel:xaxislabels)
			{
				if(!labelData.containsKey(xaxislabel))
					labelData.put(xaxislabel,0.0);
			}
			dataMap.put(label,labelData);
		}
		return createLineChartData(dataMap,xaxislabels,dateColumn,valueColumn);
	}
	private LineChartData createLineChartData(Map<String,HashMap<String,Double>> data,List<String> xaxisLabel, String dateColumn, String valueColumn )
	{
		LineChartData lineChartData= new LineChartData();
		lineChartData.setxAxisLabel(dateColumn);
		lineChartData.setyAxisLabel(valueColumn);
		lineChartData.setChartDesc("Line Chart");
		lineChartData.setxAxisLabels(xaxisLabel);
		List<LineChartItem> lines = new ArrayList<>();
		for (String label : data.keySet())
		{
			HashMap<String,Double> labelData = data.get(label);
			LineChartItem lineChartItem = new LineChartItem();
			lineChartItem.setLabel(label);
			List<Double> values= new ArrayList<>();
			for(String key: labelData.keySet())
			{
				values.add(labelData.get(key));
			}
			lineChartItem.setValues(values);
			lines.add(lineChartItem);
		}
		lineChartData.setLines(lines);

		return lineChartData;
	}
	private String getMonth(int month) {
		return new DateFormatSymbols().getMonths()[month];
	}
}
