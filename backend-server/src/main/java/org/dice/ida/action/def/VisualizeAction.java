package org.dice.ida.action.def;

import org.apache.http.ParseException;
import org.apache.http.client.utils.DateUtils;
import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.LableComparator;
import org.dice.ida.model.bargraph.BarGraphData;
import org.dice.ida.model.bargraph.BarGraphItem;
import org.dice.ida.model.bubblechart.BubbleChartData;
import org.dice.ida.model.bubblechart.BubbleChartItem;
import org.dice.ida.util.DataUtil;
import org.dice.ida.util.DialogFlowUtil;
import org.dice.ida.util.RDFUtil;
import org.dice.ida.util.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.protobuf.Value;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.List;
import java.util.Comparator;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.Date;
import java.util.Objects;
import java.util.Calendar;

/**
 * Class to handle the any visualization
 *
 * @author Nandeesh Patel, Sourabh Poddar
 */
@Component
public class VisualizeAction implements Action {

	@Autowired
	private DialogFlowUtil dialogFlowUtil;

	@Autowired
	private DataUtil dataUtil;

	private List<Map<String, String>> tableData;
	private Map<Integer, String> attributeList;
	private Map<String, Map<String, Map<String, String>>> instanceMap;
	private Map<String, String> columnMap;
	private Map<String, String> columnUniquenessMap;
	Map<String, String> parameterMap;
	Map<String, String> parameterTypeMap;
	Map<String, Object> payload;
	Map<String, Double> graphItems;
	Comparator<String> comparator = LableComparator.getForKey(IDAConst.COMPARATOR_TYPE_UNKNOWN);

	/**
	 * @param paramMap            - parameters from dialogflow
	 * @param chatMessageResponse - API response object
	 */
	@Override
	public void performAction(Map<String, Object> paramMap, ChatMessageResponse chatMessageResponse) {
		try {
			StringBuilder textMsg = new StringBuilder(paramMap.get(IDAConst.PARAM_TEXT_MSG).toString());
			if (ValidatorUtil.preActionValidation(chatMessageResponse)) {
				String vizType = paramMap.get(IDAConst.INTENT_NAME).toString();
				payload = chatMessageResponse.getPayload();
				instanceMap = new RDFUtil().getInstances(vizType);
				String attributeType;
				String attributeName;
				String paramType;
				String datasetName = payload.get("activeDS").toString();
				String tableName = payload.get("activeTable").toString();
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
					attributeList = new RDFUtil().getAttributeList(paramMap.get(IDAConst.INTENT_NAME).toString());
					List<String> columnNameList = getColumnNames(attributeList, paramMap);
					List<Map<String, String>> columnDetail = ValidatorUtil.areParametersValid(datasetName, tableName, columnNameList);
					Set<String> options = new HashSet<>();
					columnMap = columnDetail.get(0);
					columnUniquenessMap = columnDetail.get(1);
					for (int i = 1; i <= attributeList.size(); i++) {
						attributeName = attributeList.get(i);
						attributeType = paramMap.getOrDefault(attributeName + IDAConst.ATTRIBUTE_TYPE_SUFFIX, "").toString();
						if (paramMap.getOrDefault(attributeName, "").toString().isEmpty()) {
							if (i > 1) {
								dialogFlowUtil.deleteContext("get_" + attributeList.get(i - 1) + IDAConst.ATTRIBUTE_TYPE_SUFFIX);
							}
							dialogFlowUtil.setContext("get_" + attributeList.get(i));
							textMsg = new StringBuilder("Which column should be mapped to " + attributeList.get(i) + " ?");
							break;
						}
						paramType = attributeType.isEmpty() ?
								columnMap.get(paramMap.get(attributeName).toString()) :
								attributeType;
						options = getFilteredInstances(attributeName, paramType.toLowerCase(), paramMap.get(attributeName).toString(), !attributeType.isEmpty());
						if (options.size() == 0 && attributeType.isEmpty()) {
							dialogFlowUtil.deleteContext("get_" + attributeList.get(i) + IDAConst.ATTRIBUTE_TYPE_SUFFIX);
							dialogFlowUtil.setContext("get_" + attributeList.get(i));
							textMsg = new StringBuilder("It cannot be used as " + attributeList.get(i) + ". Please give a different column?");
							break;
						} else if (options.size() == 0 || (options.size() > 1 && attributeType.isEmpty())) {
							dialogFlowUtil.deleteContext("get_" + attributeList.get(i + 1));
							dialogFlowUtil.setContext("get_" + attributeList.get(i) + IDAConst.ATTRIBUTE_TYPE_SUFFIX);
							textMsg = new StringBuilder("It can be used as ");
							textMsg.append(String.join(" or ", options));
							textMsg.append("\n Which option do you need?");
							break;
						}
						if (i == 1 && !attributeType.isEmpty() && IDAConst.INSTANCE_PARAM_TYPE_BINS.equals(attributeType.toLowerCase())) {
							Value paramVal = (Value) paramMap.get(IDAConst.PARAMETER_TYPE_BIN_SIZE);
							if ("date".equals(columnMap.get(paramMap.get(attributeName).toString()))) {
								if (paramVal == null || !paramVal.hasStructValue()) {
									dialogFlowUtil.deleteContext("get_" + attributeList.get(i + 1));
									dialogFlowUtil.setContext(IDAConst.CONTEXT_GET_BIN_DURATION);
									textMsg = new StringBuilder("What should be the duration of each bin?");
									break;
								}
							} else {
								if (paramVal == null) {
									dialogFlowUtil.deleteContext("get_" + attributeList.get(i + 1));
									dialogFlowUtil.setContext(IDAConst.CONTEXT_GET_BIN_SIZE);
									textMsg = new StringBuilder("What should be the size of each bin?");
									break;
								}
							}
						}
					}
					if (options.size() == 1 && columnNameList.size() == attributeList.size()) {
						tableData = dataUtil.getData(datasetName, tableName, columnNameList, filterString);
						getParameters(paramMap);
						switch (vizType) {
							case IDAConst.VIZ_TYPE_BAR_CHART:
								createGraphData(IDAConst.X_AXIS_PARAM, IDAConst.Y_AXIS_PARAM, paramMap);
								createBarGraphResponse();
								chatMessageResponse.setMessage(IDAConst.BAR_GRAPH_LOADED);
								chatMessageResponse.setUiAction(IDAConst.UIA_BARGRAPH);
								break;
							case IDAConst.VIZ_TYPE_BUBBLE_CHART:
								createGraphData(IDAConst.BUBBLE_LABEL_PARAM, IDAConst.BUBBLE_SIZE_PARAM, paramMap);
								createBubbleChartResponse(datasetName, tableName);
								chatMessageResponse.setMessage(IDAConst.BC_LOADED);
								chatMessageResponse.setUiAction(IDAConst.UIA_BUBBLECHART);
						}
					} else {
						chatMessageResponse.setMessage(textMsg.toString());
						chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to filter the possible instances for the visualization based on the parameter and its type
	 *
	 * @param attribute - parameter label
	 * @param attributeType - parameter type ( bin, numeric, date etc.,)
	 * @param columnName - name of the column to be mapped to the parameter
	 * @param isTypeFromUser - Was the type selected by user or fetched from metadata
	 * @return - list of options for the user to choose from
	 */
	private Set<String> getFilteredInstances(String attribute, String attributeType, String columnName, boolean isTypeFromUser) {
		Map<String, Map<String, Map<String, String>>> filteredInstances = new HashMap<>();
		String instanceParamType;
		String instanceParamTransType;
		Set<String> options = new HashSet<>();
		for (String instance : instanceMap.keySet()) {
			for (String param : instanceMap.get(instance).keySet()) {
				instanceParamType = instanceMap.get(instance).get(param).get(IDAConst.INSTANCE_PARAM_TYPE_KEY).toLowerCase();
				instanceParamTransType = instanceMap.get(instance).get(param).get(IDAConst.INSTANCE_PARAM_TRANS_TYPE_KEY).toLowerCase();
				if (attribute.equals(param) &&
						(
								attributeType.equals(instanceParamType) ||
										attributeType.equals(instanceParamTransType) ||
										(!isTypeFromUser && IDAConst.INSTANCE_PARAM_TYPE_NOT_REQUIRED.equals(instanceParamType)) ||
										(IDAConst.PARAM_TYPE_TREE.get(attributeType) != null && IDAConst.PARAM_TYPE_TREE.get(attributeType).contains(instanceParamType))
						)) {
					if ((IDAConst.INSTANCE_PARAM_TYPE_UNIQUE.equals(instanceParamType) && !Boolean.parseBoolean(columnUniquenessMap.get(columnName))) ||
							(IDAConst.INSTANCE_PARAM_TYPE_NON_UNIQUE.equals(instanceParamType) && Boolean.parseBoolean(columnUniquenessMap.get(columnName)))) {
						break;
					}
					filteredInstances.put(instance, instanceMap.get(instance));
				}
			}
		}
		instanceMap = filteredInstances;
		for (String instance : instanceMap.keySet()) {
			for (String attr : instanceMap.get(instance).keySet()) {
				if (attr.equals(attribute)) {
					options.add(instanceMap.get(instance).get(attribute).get(IDAConst.INSTANCE_PARAM_TRANS_TYPE_KEY));
				}
			}
		}
		return options;
	}

	/**
	 * Method to get the list of column names required for rendering the visualization from the values received from Dialogflow
	 *
	 * @param attributeList - list of attributes from the RDF model
	 * @param paramMap - map of parameter and its value from dialogflow
	 * @return - list of column names
	 */
	private List<String> getColumnNames(Map<Integer, String> attributeList, Map<String, Object> paramMap) {
		List<String> columnList = new ArrayList<>();
		for (String param : attributeList.values()) {
			if (!paramMap.getOrDefault(param, "").toString().isEmpty()) {
				columnList.add(paramMap.get(param).toString());
			}
		}
		return columnList;
	}

	/**
	 * Method to initialize the columns for all parameters and values for the parameter types
	 *
	 * @param paramMap - map of parameter and its value from dialogflow
	 */
	private void getParameters(Map<String, Object> paramMap) {
		parameterMap = new HashMap<>();
		parameterTypeMap = new HashMap<>();
		String attributeName;
		Map<String, Map<String, String>> instance = new HashMap<>();
		for (String instanceKey : instanceMap.keySet()) {
			instance = instanceMap.get(instanceKey);
		}
		for (int i = 1; i <= attributeList.size(); i++) {
			attributeName = attributeList.get(i);
			parameterMap.put(attributeName, paramMap.get(attributeName).toString());
			for (String attr : instance.keySet()) {
				if (attributeName.equals(attr)) {
					parameterTypeMap.put(attributeName + IDAConst.ATTRIBUTE_TYPE_SUFFIX, instance.get(attr).get(IDAConst.INSTANCE_PARAM_TRANS_TYPE_KEY).toLowerCase());
				}
			}
		}
	}

	/**
	 * Method to create data for the visualization based on parameters (for now, this works only for bar graph and bubble chart)
	 *
	 * @param param1 - first param for the visualization
	 * @param param2 - second param for the visualization
	 * @param paramMap - map of parameter and its value from dialogflow
	 */
	private void createGraphData(String param1, String param2, Map<String, Object> paramMap) {
		String xAxisColumn = parameterMap.get(param1);
		String yAxisColumn = parameterMap.get(param2);
		String xAxisColumnType = parameterTypeMap.get(param1 + IDAConst.ATTRIBUTE_TYPE_SUFFIX);
		String yAxisColumnType = parameterTypeMap.get(param2 + IDAConst.ATTRIBUTE_TYPE_SUFFIX);
		graphItems = new HashMap<>();
		String xValue;
		double yValue;
		Map<String, Integer> labelCounts = new HashMap<>();
		if (IDAConst.INSTANCE_PARAM_TYPE_UNIQUE.equals(xAxisColumnType)) {
			for (Map<String, String> entry : tableData) {
				xValue = entry.get(xAxisColumn);
				try {
					yValue = Double.parseDouble(entry.get(yAxisColumn));
				} catch (Exception ex) {
					yValue = 0.0;
				}
				graphItems.put(xValue, yValue);
			}
		} else if (IDAConst.INSTANCE_PARAM_TYPE_NON_UNIQUE.equals(xAxisColumnType)) {
			for (Map<String, String> entry : tableData) {
				xValue = entry.get(xAxisColumn);
				updateGraphItemList(xValue, entry.get(yAxisColumn), yAxisColumnType, labelCounts);
			}
			if (IDAConst.TRANSFORMATION_TYPE_AVG.equals(yAxisColumnType)) {
				graphItems.replaceAll((l, v) -> graphItems.get(l) / labelCounts.get(l));
			}
		} else {
			Value paramVal = (Value) paramMap.get(IDAConst.PARAMETER_TYPE_BIN_SIZE);
			int binSize;
			String binType;
			if (IDAConst.COLUMN_TYPE_NUMERIC.equals(columnMap.get(xAxisColumn))) {
				List<Double> values = tableData.stream().map(e -> {
					try {
						return Double.parseDouble(e.get(xAxisColumn));
					} catch (NumberFormatException ex) {
						return 0.0;
					}
				}).sorted().collect(Collectors.toList());
				double min = values.get(0);
				double max = values.get(values.size() - 1);
				double binVal;
				double intervalBegin;
				binSize = (int) Math.abs(paramVal.getNumberValue());
				for (double i = min; i < max; i += binSize) {
					graphItems.put(i + " - " + (i + binSize - 1), 0.0);
					labelCounts.put(i + " - " + (i + binSize - 1), 1);
				}
				for (Map<String, String> entry : tableData) {
					try {
						binVal = Double.parseDouble(entry.get(xAxisColumn));
						intervalBegin = binVal - (binVal % binSize);
						xValue = intervalBegin + " - " + (intervalBegin + binSize - 1);
					} catch (NumberFormatException ex) {
						xValue = entry.get(xAxisColumn);
					}
					updateGraphItemList(xValue, entry.get(yAxisColumn), yAxisColumnType, labelCounts);
				}
				if (IDAConst.TRANSFORMATION_TYPE_AVG.equals(yAxisColumnType)) {
					graphItems.replaceAll((l, v) -> graphItems.get(l) / labelCounts.get(l));
				}
			} else if (IDAConst.COLUMN_TYPE_DATE.equals(columnMap.get(xAxisColumn))) {
				binSize = (int) Math.abs(paramVal.getStructValue().getFieldsMap().get(IDAConst.PARAMETER_TYPE_DURATION_SIZE).getNumberValue());
				binType = paramVal.getStructValue().getFieldsMap().get(IDAConst.PARAMETER_TYPE_DURATION_UNIT).getStringValue();
				List<Date> values = tableData.stream().map(e -> e.get(xAxisColumn))
						.sorted(LableComparator.getForKey(IDAConst.COMPARATOR_TYPE_DATE_BIN))
						.map(e -> {
							try {
								return DateUtils.parseDate(e, IDAConst.DATE_PATTERNS);
							} catch (ParseException ex) {
								return null;
							}
						})
						.filter(Objects::nonNull)
						.collect(Collectors.toList());
				Calendar calendar = Calendar.getInstance();
				Date min = values.get(0);
				String label;
				LocalDate binVal;
				LocalDate intervalBegin;
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern(IDAConst.LABEL_PATTERN_DATE);
				calendar.setTime(min);
				if (IDAConst.DURATION_TYPE_MONTH.equals(binType)) {
					calendar.set(Calendar.DAY_OF_MONTH, 1);
					formatter = DateTimeFormatter.ofPattern(IDAConst.LABEL_PATTERN_MONTH);
				} else if (IDAConst.DURATION_TYPE_YEAR.equals(binType)) {
					calendar.set(Calendar.DAY_OF_YEAR, 1);
					formatter = DateTimeFormatter.ofPattern(IDAConst.LABEL_PATTERN_YEAR);
				} else {
					calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
				}
				min = calendar.getTime();
				LocalDate localMin = min.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				LocalDate max = LocalDate.now();
				try {
					max = values.get(values.size() - 1).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				} catch (Exception e) {
					e.printStackTrace();
				}
				LocalDate startDate = localMin;
				while (startDate.isBefore(max)) {
					switch (binType) {
						case IDAConst.DURATION_TYPE_WEEK:
							label = startDate.format(formatter) + " - " + startDate.plusWeeks(binSize).minusDays(1).format(formatter);
							startDate = startDate.plusWeeks(binSize);
							break;
						case IDAConst.DURATION_TYPE_MONTH:
							label = binSize == 1 ? startDate.format(formatter) : startDate.format(formatter) + " - " + startDate.plusMonths(binSize).minusDays(1).format(formatter);
							startDate = startDate.plusMonths(binSize);
							break;
						case IDAConst.DURATION_TYPE_YEAR:
							label = binSize == 1 ? startDate.format(formatter) : startDate.format(formatter) + " - " + startDate.plusYears(binSize).minusDays(1).format(formatter);
							startDate = startDate.plusYears(binSize);
							break;
						default:
							label = startDate.format(formatter) + " - " + startDate.plusDays(binSize - 1).format(formatter);
							startDate = startDate.plusDays(binSize);
					}
					graphItems.put(label, 0.0);
					labelCounts.put(label, 1);
				}
				long diff;
				for (Map<String, String> entry : tableData) {
					try {
						calendar.setTime(DateUtils.parseDate(entry.get(xAxisColumn), IDAConst.DATE_PATTERNS));
						switch (binType) {
							case IDAConst.DURATION_TYPE_WEEK:
								calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
								binVal = calendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
								diff = ChronoUnit.WEEKS.between(localMin, binVal);
								intervalBegin = localMin.plusWeeks((diff / binSize) * binSize);
								xValue = intervalBegin.format(formatter) + " - " + intervalBegin.plusWeeks(binSize).minusDays(1).format(formatter);
								break;
							case IDAConst.DURATION_TYPE_MONTH:
								calendar.set(Calendar.DAY_OF_MONTH, 1);
								binVal = calendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
								diff = ChronoUnit.MONTHS.between(localMin, binVal);
								intervalBegin = localMin.plusMonths((diff / binSize) * binSize);
								xValue = binSize == 1 ? intervalBegin.format(formatter) : intervalBegin.format(formatter) + " - " + intervalBegin.plusMonths(binSize).minusDays(1).format(formatter);
								break;
							case IDAConst.DURATION_TYPE_YEAR:
								calendar.set(Calendar.DAY_OF_YEAR, 1);
								binVal = calendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
								diff = ChronoUnit.YEARS.between(localMin, binVal);
								intervalBegin = localMin.plusYears((diff / binSize) * binSize);
								xValue = binSize == 1 ? intervalBegin.format(formatter) : intervalBegin.format(formatter) + " - " + intervalBegin.plusYears(binSize).minusDays(1).format(formatter);
								break;
							default:
								binVal = calendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
								diff = ChronoUnit.DAYS.between(localMin, binVal);
								intervalBegin = localMin.plusDays((diff / binSize) * binSize);
								xValue = intervalBegin.format(formatter) + " - " + intervalBegin.plusDays(binSize - 1).format(formatter);
						}
					} catch (ParseException | NullPointerException ex) {
						xValue = entry.get(xAxisColumn);
					}
					updateGraphItemList(xValue, entry.get(yAxisColumn), yAxisColumnType, labelCounts);
				}
				if (IDAConst.TRANSFORMATION_TYPE_AVG.equals(yAxisColumnType)) {
					graphItems.replaceAll((l, v) -> graphItems.get(l) / labelCounts.get(l));
				}
			}
		}
		if (IDAConst.COLUMN_TYPE_NUMERIC.equals(columnMap.get(xAxisColumn))) {
			comparator = IDAConst.INSTANCE_PARAM_TYPE_BINS.equals(xAxisColumnType) ? LableComparator.getForKey(IDAConst.COMPARATOR_TYPE_DOUBLE_BIN) : LableComparator.getForKey(IDAConst.COMPARATOR_TYPE_DOUBLE);
		} else if (IDAConst.COLUMN_TYPE_DATE.equals(columnMap.get(xAxisColumn))) {
			comparator = IDAConst.INSTANCE_PARAM_TYPE_BINS.equals(xAxisColumnType) ? LableComparator.getForKey(IDAConst.COMPARATOR_TYPE_DATE_BIN) : LableComparator.getForKey(IDAConst.COMPARATOR_TYPE_DATE);
		}
	}

	/**
	 * Method to add a new entry to list of graph items
	 *
	 * @param xValue - label value of an entry
	 * @param yValueString - second parameter value in string
	 * @param yAxisColumnType - second parameter type
	 * @param labelCounts - count of each label (useful for calculating the average)
	 */
	private void updateGraphItemList(String xValue, String yValueString, String yAxisColumnType, Map<String, Integer> labelCounts) {
		Double yValue;
		try {
			yValue = Double.parseDouble(yValueString);
		} catch (Exception ex) {
			yValue = 0.0;
		}
		if (graphItems.get(xValue) == null) {
			if (IDAConst.TRANSFORMATION_TYPE_COUNT.equals(yAxisColumnType)) {
				graphItems.put(xValue, 1.0);
			} else {
				graphItems.put(xValue, yValue);
			}
			labelCounts.put(xValue, 1);
		} else {
			if (IDAConst.TRANSFORMATION_TYPE_COUNT.equals(yAxisColumnType)) {
				graphItems.put(xValue, graphItems.get(xValue) + 1.0);
			} else {
				graphItems.put(xValue, graphItems.get(xValue) + yValue);
			}
			labelCounts.put(xValue, labelCounts.get(xValue) + 1);
		}
	}

	/**
	 * Method to create a response object based on graph items for bar graph
	 */
	private void createBarGraphResponse() {
		String xAxisColumn = parameterMap.get(IDAConst.X_AXIS_PARAM);
		String yAxisColumn = parameterMap.get(IDAConst.Y_AXIS_PARAM);
		String xAxisColumnType = parameterTypeMap.get(IDAConst.X_AXIS_PARAM + IDAConst.ATTRIBUTE_TYPE_SUFFIX);
		String yAxisColumnType = parameterTypeMap.get(IDAConst.Y_AXIS_PARAM + IDAConst.ATTRIBUTE_TYPE_SUFFIX);
		String yAxisLabel = IDAConst.INSTANCE_PARAM_TYPE_UNIQUE.equals(xAxisColumnType) ? yAxisColumn : yAxisColumnType + " " + yAxisColumn;
		String graphLabel = "Bar graph for " + xAxisColumn + " and " + yAxisLabel;
		List<BarGraphItem> barGraphItemList = new ArrayList<>();
		for (String label : graphItems.keySet().stream().sorted(comparator).collect(Collectors.toList())) {
			barGraphItemList.add(new BarGraphItem(label, graphItems.get(label)));
		}
		payload.put("barGraphData", new BarGraphData(graphLabel, barGraphItemList, xAxisColumn, yAxisLabel));
	}

	/**
	 * Method to create a response object based on graph items for bubble graph
	 *
	 * @param dsName - name of the dataset
	 * @param tableName - name of the table
	 */
	private void createBubbleChartResponse(String dsName, String tableName) {
		String labelColumn = parameterMap.get(IDAConst.BUBBLE_LABEL_PARAM);
		String sizeColumn = parameterMap.get(IDAConst.BUBBLE_SIZE_PARAM);
		String labelColumnType = parameterTypeMap.get(IDAConst.BUBBLE_LABEL_PARAM + IDAConst.ATTRIBUTE_TYPE_SUFFIX);
		String sizeColumnType = parameterTypeMap.get(IDAConst.BUBBLE_SIZE_PARAM + IDAConst.ATTRIBUTE_TYPE_SUFFIX);
		String sizeLabel = IDAConst.INSTANCE_PARAM_TYPE_UNIQUE.equals(labelColumnType) ? sizeColumn : sizeColumnType + " " + sizeColumn;
		String graphLabel = "Bubble chart for " + labelColumn + " and " + sizeLabel;
		List<BubbleChartItem> bubbleChartItemList = new ArrayList<>();
		for (String label : graphItems.keySet().stream().sorted(comparator).collect(Collectors.toList())) {
			bubbleChartItemList.add(new BubbleChartItem(label, label, graphItems.get(label)));
		}
		payload.put("bubbleChartData", new BubbleChartData(graphLabel, bubbleChartItemList, dsName, tableName));
	}

}
