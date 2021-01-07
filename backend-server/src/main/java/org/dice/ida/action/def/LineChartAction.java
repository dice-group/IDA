package org.dice.ida.action.def;

import org.apache.commons.lang3.time.DateUtils;
import org.dice.ida.constant.IDAConst;
import org.dice.ida.exception.IDAException;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.dice.ida.model.linechart.LineChartData;
import org.dice.ida.model.linechart.LineChartItem;
import org.dice.ida.util.DataUtil;
import org.dice.ida.util.ValidatorUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeMap;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Class to handle the line chart implementation
 *
 * @author Nandeesh Patel, Sourabh Poddar
 */
@Component
public class LineChartAction implements Action {

	private final DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
	private List<Map<String, String>> tableData;
	private String dateColumn;
	private String labelColumn;
	private String valueColumn;
	private Map<String, Integer> monthIndexMap;
	private Comparator<String> dateComparator;
	private Comparator<String> monthComparator;
	private Comparator<String> yearComparator;
	private List<String> xAxisLabels;
	private String binType;
	private final Map<String, Map<String, Double>> chartData = new HashMap<>();

	/**
	 * @param paramMap            - parameters from dialogflow
	 * @param chatMessageResponse - API response object
	 */
	@Override
	public void performAction(Map<String, Object> paramMap, ChatMessageResponse chatMessageResponse, ChatUserMessage message) throws IDAException, IOException {
		if (ValidatorUtil.preActionValidation(chatMessageResponse)) {
			Map<String, Object> payload = chatMessageResponse.getPayload();
			String datasetName = payload.get("activeDS").toString();
			String tableName = payload.get("activeTable").toString();
			boolean onTemporaryData = message.isTemporaryData();
			dateColumn = paramMap.get(IDAConst.LINE_CHART_PARAM_DATE_COL).toString();
			labelColumn = paramMap.get(IDAConst.LINE_CHART_PARAM_LABEL_COL).toString();
			valueColumn = paramMap.get(IDAConst.LINE_CHART_PARAM_VALUE_COL).toString();
			String filterString = paramMap.get(IDAConst.PARAM_FILTER_STRING).toString();

			if (ValidatorUtil.isStringEmpty(filterString)) {
				double confidence = Double.parseDouble(paramMap.get(IDAConst.PARAM_INTENT_DETECTION_CONFIDENCE).toString());
				if (confidence == 0.0) {
					paramMap.replace(IDAConst.PARAM_TEXT_MSG, IDAConst.INVALID_FILTER);
					chatMessageResponse.setMessage(paramMap.get(IDAConst.PARAM_TEXT_MSG).toString());
					chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
				}
				chatMessageResponse.setMessage(paramMap.get(IDAConst.PARAM_TEXT_MSG).toString());
				chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
			} else {
					if (ValidatorUtil.isStringEmpty(dateColumn) || ValidatorUtil.isStringEmpty(labelColumn) || ValidatorUtil.isStringEmpty(valueColumn)) {
						SimpleTextAction.setSimpleTextResponse(paramMap, chatMessageResponse);
						return;
					}
					List<String> columnNameList = new ArrayList<>();
					columnNameList.add(dateColumn);
					columnNameList.add(labelColumn);
					columnNameList.add(valueColumn);
					Map<String, String> columnMap = ValidatorUtil.areParametersValid(datasetName, tableName, columnNameList, onTemporaryData).get(0);
					validateParamTypes(columnMap);
					if (onTemporaryData) {
						tableData = message.getActiveTableData();
					} else {
						tableData = new DataUtil().getData(datasetName, tableName, columnNameList, filterString,columnMap);    // extract data from file
					}
					setBinTypeAndLabels();    // Decide the label intervals for X-Axis
					createChartData();    // Create data for the chart based on intervals
					payload.put(IDAConst.LINE_CHART_PROPERTY_NAME, createLineChartData());
					chatMessageResponse.setUiAction(IDAConst.UIA_LINECHART);
					chatMessageResponse.setMessage(paramMap.get(IDAConst.PARAM_TEXT_MSG).toString());
			}
		}
	}

	/**
	 * Method to validate the datatype of the columns provided for the line chart
	 *
	 * @param paramMap - map containing the name and datatype of all the columns in selected table
	 * @throws IDAException - An exception when the datatype of the given columns are not suitable for line chart.
	 */
	private void validateParamTypes(Map<String, String> paramMap) throws IDAException {
		if (!IDAConst.COLUMN_TYPE_DATE.equals(paramMap.get(dateColumn))) {
			throw new IDAException(dateColumn + IDAConst.INVALID_DATE_COLUMN_MSG);
		} else if (!IDAConst.COLUMN_TYPE_NUMERIC.equals(paramMap.get(valueColumn)) && !labelColumn.equals(valueColumn)) {
			throw new IDAException(valueColumn + IDAConst.INVALID_NUMERIC_COLUMN_MSG);
		}
	}

	/**
	 * Method to create the X-Axis labels and intervals based on the size of data
	 * Eg: Days when data is small, Months when data is spread across many months, Years when data is spread across years
	 */
	private void setBinTypeAndLabels() {
		Calendar calendar = Calendar.getInstance();
		List<String> dayLabels = new ArrayList<>();
		List<String> monthLabels = new ArrayList<>();
		List<String> yearLabels = new ArrayList<>();
		createMonthIndexMap();
		createComparators();

		for (Map<String, String> object : tableData) {
			String currentDate = object.get(dateColumn).trim();
			try {
				calendar.setTime(DateUtils.parseDateStrictly(currentDate, IDAConst.DATE_PATTERNS));
			} catch (ParseException ex) {
				ex.printStackTrace();
				continue; // Ignore the row and continue with the next
			}

			if (!dayLabels.contains(currentDate)) {
				dayLabels.add(currentDate);
			}
			if (!monthLabels.contains(getMonth(calendar.get(Calendar.MONTH)) + ", " + calendar.get(Calendar.YEAR))) {
				monthLabels.add(getMonth(calendar.get(Calendar.MONTH)) + ", " + calendar.get(Calendar.YEAR));
			}
			if (!yearLabels.contains(String.valueOf(calendar.get(Calendar.YEAR)))) {
				yearLabels.add(String.valueOf(calendar.get(Calendar.YEAR)));
			}
		}
		dayLabels.sort(dateComparator);
		monthLabels.sort(monthComparator);
		yearLabels.sort(yearComparator);
		/*int d = Math.abs(dayLabels.size() - 10);
		int m = Math.abs(monthLabels.size() - 10);
		int y = Math.abs(yearLabels.size() - 10);
		if (d <= m && d <= y) {
			xAxisLabels = dayLabels;
			binType = IDAConst.LABEL_TYPE_DATE;
		} else if (m <= y) {
			xAxisLabels = monthLabels;
			binType = IDAConst.LABEL_TYPE_MONTH;
		} else {
			xAxisLabels = yearLabels;
			binType = IDAConst.LABEL_TYPE_YEAR;
		}*/
		// TODO: since D3 handles the binning automatically remove the binning logic here. Can be done while integrating with RDF model
		xAxisLabels = dayLabels;
		binType = IDAConst.LABEL_TYPE_DATE;
	}

	/**
	 * Method to create the data for each lines based on the bin type.
	 */
	private void createChartData() {
		Map<String, Double> labelData;
		String date;
		String label;
		double value;
		String dateKey;
		for (Map<String, String> row : tableData) {
			date = row.get(dateColumn).trim();
			label = row.get(labelColumn);
			value = 0.0;
			try {
				value = labelColumn.equals(valueColumn) ? 1.0 : Double.parseDouble(row.get(valueColumn));
			} catch (NumberFormatException ex) {
				System.out.println(ex.getMessage());
			}
			if (binType.equals(IDAConst.LABEL_TYPE_MONTH)) {
				labelData = new TreeMap<>(monthComparator);
			} else if (binType.equals(IDAConst.LABEL_TYPE_YEAR)) {
				labelData = new TreeMap<>(yearComparator);
			} else {
				labelData = new TreeMap<>(dateComparator);
			}
			if (chartData.containsKey(label)) {
				labelData = chartData.get(label);
			} else {
				for (String l : xAxisLabels) {
					labelData.put(l, 0.0);
				}
			}
			try {
				dateKey = extractDateKey(binType, date);
			} catch (ParseException ex) {
				ex.printStackTrace();
				continue; // Ignore the row and continue with the next
			}
			Double oldValue = labelData.get(dateKey);
			if (oldValue != null)
				labelData.put(dateKey, oldValue + value);
			else
				labelData.put(dateKey, value);
			chartData.put(label, labelData);
		}
	}

	/**
	 * Method to create a LineChartData object with all the values that would be sent in the response
	 *
	 * @return - An object of LineChartData class with all the values
	 */
	private LineChartData createLineChartData() {
		LineChartData lineChartData = new LineChartData();
		lineChartData.setxAxisLabel(dateColumn);
		String yAxisLabel = valueColumn;
		if (labelColumn.equals(valueColumn)) {
			yAxisLabel = IDAConst.COUNT_OF_PREFIX + valueColumn;
		}
		lineChartData.setyAxisLabel(yAxisLabel);
		lineChartData.setChartDesc(IDAConst.LINE_CHART_DESC_PREFIX + yAxisLabel + " across " + dateColumn);
		List<Date> dateLabels = xAxisLabels.stream().map(l -> {
			try {
				return DateUtils.parseDate(l, IDAConst.DATE_PATTERNS);
			} catch (ParseException e) {
				System.out.println("Date parse exception:" + l);
			}
			return new Date();
		}).collect(Collectors.toList());
		lineChartData.setxAxisLabels(dateLabels);
		List<LineChartItem> lines = new ArrayList<>();
		for (String label : chartData.keySet()) {
			Map<String, Double> labelData = chartData.get(label);
			LineChartItem lineChartItem = new LineChartItem();
			lineChartItem.setLabel(label);
			List<Double> values = new ArrayList<>();
			for (String key : labelData.keySet()) {
				values.add(labelData.get(key));
			}
			lineChartItem.setLineValues(values);
			lines.add(lineChartItem);
		}
		lineChartData.setLines(lines);
		return lineChartData;
	}

	/**
	 * Method to convert the month index to its string equivalent
	 *
	 * @param month - index of the month as integer
	 * @return - name of the month based on the integer
	 */
	private String getMonth(int month) {
		return dateFormatSymbols.getMonths()[month];
	}

	/**
	 * Method to create the label for the graph based on the bin type for a given date string
	 *
	 * @param type       - type of the binning (date, month, or year)
	 * @param dateString - date in string format
	 * @return - Label to be used on the graph
	 * @throws ParseException - Exception when a date is not in correct format
	 */
	private String extractDateKey(String type, String dateString) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		Date date = DateUtils.parseDateStrictly(dateString.trim(), IDAConst.DATE_PATTERNS);
		calendar.setTime(date);
		switch (type) {
			case IDAConst.LABEL_TYPE_MONTH:
				return getMonth(calendar.get(Calendar.MONTH)) + ", " + calendar.get(Calendar.YEAR);
			case IDAConst.LABEL_TYPE_YEAR:
				return String.valueOf(calendar.get(Calendar.YEAR));
			default:
				return dateString;
		}
	}

	/**
	 * Method to create a map of months and their index which is used for sorting the month labels
	 */
	private void createMonthIndexMap() {
		monthIndexMap = new HashMap<>();
		for (int i = 0; i < dateFormatSymbols.getMonths().length; i++) {
			monthIndexMap.put(dateFormatSymbols.getMonths()[i], i);
		}
	}

	/**
	 * Method to create comparators for different label types (date, month, and year) which is used for sorting the labels
	 */
	private void createComparators() {
		dateComparator = (String date1, String date2) -> {
			try {
				return DateUtils.parseDateStrictly(date1, IDAConst.DATE_PATTERNS).compareTo(DateUtils.parseDateStrictly(date2, IDAConst.DATE_PATTERNS));
			} catch (ParseException e) {
				return -1;
			}
		};
		monthComparator = Comparator.comparing((String month) -> monthIndexMap.get(month.split(",")[0]));
		yearComparator = Comparator.comparing(Integer::parseInt);
	}

}
