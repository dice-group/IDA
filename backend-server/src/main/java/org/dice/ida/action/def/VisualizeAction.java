package org.dice.ida.action.def;

import org.apache.http.ParseException;
import org.apache.http.client.utils.DateUtils;
import org.dice.ida.constant.IDAConst;
import org.dice.ida.exception.IDAException;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.ChatUserMessage;
import org.dice.ida.model.LableComparator;
import org.dice.ida.model.bargraph.BarGraphData;
import org.dice.ida.model.bargraph.BarGraphItem;
import org.dice.ida.model.bubblechart.BubbleChartData;
import org.dice.ida.model.bubblechart.BubbleChartItem;
import org.dice.ida.model.groupedBarGraph.GroupedBarGraphData;
import org.dice.ida.model.groupedBubbleChart.GroupedBubbleChartData;
import org.dice.ida.util.DataUtil;
import org.dice.ida.util.DialogFlowUtil;
import org.dice.ida.util.RDFUtil;
import org.dice.ida.util.ValidatorUtil;
import org.dice.ida.util.TextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.protobuf.Value;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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
import java.util.TreeMap;

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
	private Map<String, String> parameterMap;
	private Map<String, String> parameterTypeMap;
	private Map<String, Object> payload;
	private Map<String, Double> graphItems;
	private Map<String, Map<String, Double>> groupedGraphItems;
	private Comparator<String> comparator;
	private StringBuilder textMsg;
	private boolean groupingNeeded;

	/**
	 * @param paramMap            - parameters from dialogflow
	 * @param chatMessageResponse - API response object
	 */
	@Override
	public void performAction(Map<String, Object> paramMap, ChatMessageResponse chatMessageResponse, ChatUserMessage message) throws IOException, IDAException, InvalidKeySpecException, NoSuchAlgorithmException {
		textMsg = new StringBuilder(paramMap.get(IDAConst.PARAM_TEXT_MSG).toString());
		if (ValidatorUtil.preActionValidation(chatMessageResponse)) {
			String vizType = paramMap.get(IDAConst.INTENT_NAME).toString();
			payload = chatMessageResponse.getPayload();
			instanceMap = new RDFUtil().getInstances(vizType);
			String datasetName = payload.get("activeDS").toString();
			String tableName = payload.get("activeTable").toString();
			boolean onTemporaryData = message.isTemporaryData();
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
				List<Map<String, String>> columnDetail = ValidatorUtil.areParametersValid(datasetName, tableName, columnNameList, onTemporaryData);
				columnMap = columnDetail.get(0);
				columnUniquenessMap = columnDetail.get(1);
				Set<String> options = processParameters(paramMap);
				if (options.size() == 1 && columnNameList.size() == attributeList.size()) {
					getParameters(paramMap);
					groupingNeeded = false;
					if (!IDAConst.INSTANCE_PARAM_TYPE_UNIQUE.equals(parameterTypeMap.get(IDAConst.X_AXIS_PARAM + IDAConst.ATTRIBUTE_TYPE_SUFFIX)) &&
							!handleGroupingLogic(chatMessageResponse, paramMap)) {
						chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
						return;
					}
					if (onTemporaryData) {
						tableData = message.getActiveTableData();
					} else {
						if (groupingNeeded) {
							columnNameList.add(paramMap.get("group_column").toString());
						}
						tableData = dataUtil.getData(datasetName, tableName, columnNameList, filterString, columnMap);
					}
					comparator = LableComparator.getForKey(IDAConst.COMPARATOR_TYPE_UNKNOWN);
					switch (vizType) {
						case IDAConst.VIZ_TYPE_BAR_CHART:
							createGraphData(IDAConst.X_AXIS_PARAM, IDAConst.Y_AXIS_PARAM, paramMap);
							if (groupingNeeded) {
								createGroupedBarGraphResponse();
								chatMessageResponse.setUiAction(IDAConst.UIA_GROUPED_BARGRAPH);
							} else {
								createBarGraphResponse();
								chatMessageResponse.setUiAction(IDAConst.UIA_BARGRAPH);
							}
							chatMessageResponse.setMessage(IDAConst.BAR_GRAPH_LOADED);

							break;
						case IDAConst.VIZ_TYPE_BUBBLE_CHART:
							createGraphData(IDAConst.BUBBLE_LABEL_PARAM, IDAConst.BUBBLE_SIZE_PARAM, paramMap);
							if (groupingNeeded) {
								createGroupedBubbleChartResponse();
								chatMessageResponse.setUiAction(IDAConst.UIA_GROUPED_BUBBLECHART);
							} else {
								createBubbleChartResponse(datasetName, tableName);
								chatMessageResponse.setUiAction(IDAConst.UIA_BUBBLECHART);
							}
							chatMessageResponse.setMessage(IDAConst.BC_LOADED);
							break;
						default:
							chatMessageResponse.setMessage(IDAConst.BOT_SOMETHING_WRONG);
							chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
							break;
					}
					dialogFlowUtil.resetContext();
				} else {
					chatMessageResponse.setMessage(textMsg.toString());
					chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
				}
			}
		}
	}

	/**
	 * Method to process the user input for the visualization parameters and get all available options
	 *
	 * @param paramMap - param map from Dialogflow
	 * @return - set of options possible for rendering the visualization based on user inputs
	 */
	private Set<String> processParameters(Map<String, Object> paramMap) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
		String attributeType;
		String attributeName;
		String paramType;
		Set<String> options = new HashSet<>();
		for (int i = 1; i <= attributeList.size(); i++) {
			attributeName = attributeList.get(i);
			attributeType = paramMap.getOrDefault(attributeName + IDAConst.ATTRIBUTE_TYPE_SUFFIX, "").toString();
			if (paramMap.getOrDefault(attributeName, "").toString().isEmpty()) {
				if (i > 1) {
					dialogFlowUtil.deleteContext("get_" + attributeList.get(i - 1) + IDAConst.ATTRIBUTE_TYPE_SUFFIX);
				}
				dialogFlowUtil.setContext("get_" + attributeList.get(i));
				textMsg = new StringBuilder("Which column values should be mapped to " + IDAConst.PARAM_NAME_MAP.get(attributeList.get(i)) + "?");
				break;
			}
			paramType = attributeType.isEmpty() ?
					columnMap.get(paramMap.get(attributeName).toString()) :
					attributeType;
			options = getFilteredInstances(attributeName, paramType.toLowerCase(), paramMap.get(attributeName).toString(), !attributeType.isEmpty());
			if (createResponseForUser(options, i, attributeName, attributeType, paramMap, paramType)) {
				break;
			}
		}
		return options;
	}

	/**
	 * Validate the value for attribute and its type. Return true if response is ready
	 *
	 * @param options       - options available after filtering
	 * @param i             - priority of the attribute
	 * @param attributeName - name of the attribute
	 * @param attributeType - type of the attribute
	 * @param paramMap      - parameter map from dialogflow
	 * @return - true if response is ready and false otherwise
	 */
	private boolean createResponseForUser(Set<String> options, int i, String attributeName, String attributeType, Map<String, Object> paramMap, String paramType) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
		String columnName = paramMap.get(attributeName).toString();
		if (options.size() == 0 && attributeType.isEmpty()) {
			dialogFlowUtil.deleteContext("get_" + attributeList.get(i) + IDAConst.ATTRIBUTE_TYPE_SUFFIX);
			dialogFlowUtil.setContext("get_" + attributeList.get(i));
			textMsg = new StringBuilder(columnName + " cannot be used as " + IDAConst.PARAM_NAME_MAP.get(attributeList.get(i)) + ". Please give a different column?");
			return true;
		} else if (options.size() == 0) {
			dialogFlowUtil.deleteContext("get_" + attributeList.get(i + 1));
			dialogFlowUtil.setContext("get_" + attributeList.get(i) + IDAConst.ATTRIBUTE_TYPE_SUFFIX);
			textMsg = new StringBuilder(columnName + " cannot be used as " + attributeType + ". Please provide correct type.");
			return true;
		} else if (options.size() > 1 && attributeType.isEmpty()) {
			dialogFlowUtil.deleteContext("get_" + attributeList.get(i + 1));
			dialogFlowUtil.setContext("get_" + attributeList.get(i) + IDAConst.ATTRIBUTE_TYPE_SUFFIX);
			textMsg = new StringBuilder(columnName + " can be used as:<br>");
			textMsg.append("<ul>");
			for (String t : options) {
				textMsg.append("<li><b>").append(t).append("</b> - ");
				if (IDAConst.INSTANCE_PARAM_TYPE_BINS.equalsIgnoreCase(t)) {
					textMsg.append(IDAConst.PARAM_TYPE_EG_MAP.get(paramType));
				} else if (IDAConst.TRANSFORMATION_TYPES.contains(t)) {
					textMsg.append(IDAConst.TRANSFORMATION_EG_MAP.get(t));
				} else {
					textMsg.append(IDAConst.PARAM_TYPE_NON_BIN);
				}
				textMsg.append("</li>");
			}
			textMsg.append("</ul><br/>");
			textMsg.append("\n Which option do you need (");
			textMsg.append(String.join(" / ", options));
			textMsg.append(")?");
			return true;
		}
		if (i == 1 && !attributeType.isEmpty() && IDAConst.INSTANCE_PARAM_TYPE_BINS.equals(attributeType.toLowerCase())) {
			Value paramVal = (Value) paramMap.get(IDAConst.PARAMETER_TYPE_BIN_SIZE);
			if ("date".equals(columnMap.get(paramMap.get(attributeName).toString()))) {
				if (paramVal == null || !paramVal.hasStructValue()) {
					dialogFlowUtil.deleteContext("get_" + attributeList.get(i + 1));
					dialogFlowUtil.setContext(IDAConst.CONTEXT_GET_BIN_DURATION);
					textMsg = new StringBuilder("What should be the duration of each bin?<br/>Eg: 1 week, 2 weeks, 3 months");
					return true;
				}
			} else {
				if (paramVal == null) {
					dialogFlowUtil.deleteContext("get_" + attributeList.get(i + 1));
					dialogFlowUtil.setContext(IDAConst.CONTEXT_GET_BIN_SIZE);
					textMsg = new StringBuilder("What should be the size of each bin?<br/>Eg: 10, 25, 15, twenty, twelve");
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Method to filter the possible instances for the visualization based on the parameter and its type
	 *
	 * @param attribute      - parameter label
	 * @param attributeType  - parameter type ( bin, numeric, date etc.,)
	 * @param columnName     - name of the column to be mapped to the parameter
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
	 * @param paramMap      - map of parameter and its value from dialogflow
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
		if (groupingNeeded) {
			parameterMap.put("group_column", paramMap.getOrDefault("group_column", "").toString());
		}
	}

	/**
	 * Method to create data for the visualization based on parameters (for now, this works only for bar graph and bubble chart)
	 *
	 * @param param1   - first param for the visualization
	 * @param param2   - second param for the visualization
	 * @param paramMap - map of parameter and its value from dialogflow
	 */
	private void createGraphData(String param1, String param2, Map<String, Object> paramMap) {
		String xAxisColumn = parameterMap.get(param1);
		String yAxisColumn = parameterMap.get(param2);
		String xAxisColumnType = parameterTypeMap.get(param1 + IDAConst.ATTRIBUTE_TYPE_SUFFIX);
		String yAxisColumnType = parameterTypeMap.get(param2 + IDAConst.ATTRIBUTE_TYPE_SUFFIX);
		graphItems = new HashMap<>();
		groupedGraphItems = new HashMap<>();
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
			if (groupingNeeded) {
				String groupColumn = paramMap.get("group_column").toString();
				List<String> groups = tableData.stream().map(e -> e.get(groupColumn)).distinct().collect(Collectors.toList());
				List<String> labels = tableData.stream().map(e -> e.get(xAxisColumn)).distinct().collect(Collectors.toList());
				Map<String, Double> groupEntries = new HashMap<>();
				Map<String, Map<String, Integer>> groupedLabelCounts = new HashMap<>();
				for (String label : labels) {
					labelCounts.put(label, 0);
					groupEntries.put(label, 0.0);
				}
				for (String group : groups) {
					groupedGraphItems.put(group, new HashMap<>() {{
						putAll(groupEntries);
					}});
					groupedLabelCounts.put(group, new HashMap<>() {{
						putAll(labelCounts);
					}});
				}
				for (Map<String, String> entry : tableData) {
					xValue = entry.get(xAxisColumn);
					updateGraphItemList(xValue, entry.get(yAxisColumn), yAxisColumnType, groupedLabelCounts.get(entry.get(groupColumn)), groupedGraphItems.get(entry.get(groupColumn)));
				}
				if (IDAConst.TRANSFORMATION_TYPE_AVG.equals(yAxisColumnType)) {
					for (String group : groupedGraphItems.keySet()) {
						Map<String, Double> entries = groupedGraphItems.get(group);
						Map<String, Integer> lbls = groupedLabelCounts.get(group);
						entries.replaceAll((l, v) -> entries.get(l) / lbls.get(l));
						groupedGraphItems.put(group, entries);
					}
					graphItems.replaceAll((l, v) -> graphItems.get(l) / labelCounts.get(l));
				}
			}
			for (Map<String, String> entry : tableData) {
				xValue = entry.get(xAxisColumn);
				updateGraphItemList(xValue, entry.get(yAxisColumn), yAxisColumnType, labelCounts, graphItems);
			}
			if (IDAConst.TRANSFORMATION_TYPE_AVG.equals(yAxisColumnType)) {
				graphItems.replaceAll((l, v) -> graphItems.get(l) / labelCounts.get(l));
			}
		} else {
			Value paramVal = (Value) paramMap.get(IDAConst.PARAMETER_TYPE_BIN_SIZE);
			int binSize;
			String binType;
			if (IDAConst.COLUMN_TYPE_NUMERIC.equals(columnMap.get(xAxisColumn))) {
				if (groupingNeeded) {
					processGroupedBinsForNumericLabels((int) Math.abs(paramVal.getNumberValue()), xAxisColumn, yAxisColumn, yAxisColumnType, paramMap.get("group_column").toString());
				}
				processBinsForNumericLabels((int) Math.abs(paramVal.getNumberValue()), xAxisColumn, yAxisColumn, yAxisColumnType, labelCounts);
			} else if (IDAConst.COLUMN_TYPE_DATE.equals(columnMap.get(xAxisColumn))) {
				binSize = (int) Math.abs(paramVal.getStructValue().getFieldsMap().get(IDAConst.PARAMETER_TYPE_DURATION_SIZE).getNumberValue());
				binType = paramVal.getStructValue().getFieldsMap().get(IDAConst.PARAMETER_TYPE_DURATION_UNIT).getStringValue();
				if (groupingNeeded) {
					processGroupedBinsForDateLabels(binSize, binType, xAxisColumn, yAxisColumn, yAxisColumnType, paramMap.get("group_column").toString());
				}
				processBinsForDateLabels(binSize, binType, xAxisColumn, yAxisColumn, yAxisColumnType, labelCounts);
			}
		}
		if (IDAConst.COLUMN_TYPE_NUMERIC.equals(columnMap.get(xAxisColumn))) {
			comparator = IDAConst.INSTANCE_PARAM_TYPE_BINS.equals(xAxisColumnType) ? LableComparator.getForKey(IDAConst.COMPARATOR_TYPE_DOUBLE_BIN) : LableComparator.getForKey(IDAConst.COMPARATOR_TYPE_DOUBLE);
		} else if (IDAConst.COLUMN_TYPE_DATE.equals(columnMap.get(xAxisColumn))) {
			comparator = IDAConst.INSTANCE_PARAM_TYPE_BINS.equals(xAxisColumnType) ? LableComparator.getForKey(IDAConst.COMPARATOR_TYPE_DATE_BIN) : LableComparator.getForKey(IDAConst.COMPARATOR_TYPE_DATE);
		}
	}

	/**
	 * Method to process the bins for numeric labels
	 *
	 * @param binSize         - size of the bins
	 * @param xAxisColumn     - column for primary parameter
	 * @param yAxisColumn     - column for secondary parameter
	 * @param yAxisColumnType - type of secondary parameter
	 * @param labelCounts     - map of labels to its counts
	 */
	private void processBinsForNumericLabels(int binSize, String xAxisColumn, String yAxisColumn, String yAxisColumnType, Map<String, Integer> labelCounts) {
		String xValue;
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
		for (double i = min; i < max; i += binSize) {
			graphItems.put(i + " - " + (i + binSize - 1), 0.0);
			labelCounts.put(i + " - " + (i + binSize - 1), 1);
		}
		for (Map<String, String> entry : tableData) {
			String valueString = entry.get(xAxisColumn);
			if (TextUtil.isDoubleString(valueString)) {
				binVal = Double.parseDouble(valueString);
				intervalBegin = binVal - (binVal % binSize);
				xValue = intervalBegin + " - " + (intervalBegin + binSize - 1);
				updateGraphItemList(xValue, entry.get(yAxisColumn), yAxisColumnType, labelCounts, graphItems);
			} else if (valueString.equalsIgnoreCase(IDAConst.NULL_VALUE_IDENTIFIER)) {
				updateGraphItemList(valueString, entry.get(yAxisColumn), yAxisColumnType, labelCounts, graphItems);
			}
		}
		if (IDAConst.TRANSFORMATION_TYPE_AVG.equals(yAxisColumnType)) {
			graphItems.replaceAll((l, v) -> graphItems.get(l) / labelCounts.get(l));
		}
	}

	/**
	 * Method to process the bins for numeric labels along with grouping
	 *
	 * @param binSize 			- size of the bins
	 * @param xAxisColumn 		- column for primary parameter
	 * @param yAxisColumn 		- column for secondary parameter
	 * @param yAxisColumnType 	- type of secondary parameter
	 * @param groupColumn 		- column for grouping the labels
	 */
	private void processGroupedBinsForNumericLabels(int binSize, String xAxisColumn, String yAxisColumn, String yAxisColumnType, String groupColumn) {
		String xValue;
		List<Double> values = tableData.stream().map(e -> {
			try {
				return Double.parseDouble(e.get(xAxisColumn));
			} catch (NumberFormatException ex) {
				return 0.0;
			}
		}).sorted().collect(Collectors.toList());
		List<String> groups = tableData.stream().map(e -> e.get(groupColumn)).distinct().collect(Collectors.toList());
		double min = values.get(0);
		double max = values.get(values.size() - 1);
		double binVal;
		double intervalBegin;
		Map<String, Double> groupEntries = new HashMap<>();
		Map<String, Map<String, Integer>> groupedLabelCounts = new HashMap<>();
		Map<String, Integer> labelCounts = new HashMap<>();
		for (double i = min; i < max; i += binSize) {
			groupEntries.put(i + " - " + (i + binSize - 1), 0.0);
			labelCounts.put(i + " - " + (i + binSize - 1), 1);
		}
		if ((int) tableData.stream().filter(e -> IDAConst.NULL_VALUE_IDENTIFIER.equalsIgnoreCase(e.get(xAxisColumn))).count() > 0) {
			groupEntries.put(IDAConst.NULL_VALUE_IDENTIFIER, 0.0);
			labelCounts.put(IDAConst.NULL_VALUE_IDENTIFIER, 1);
		}
		for (String group : groups) {
			groupedGraphItems.put(group, new HashMap<>() {{
				putAll(groupEntries);
			}});
			groupedLabelCounts.put(group, new HashMap<>() {{
				putAll(labelCounts);
			}});
		}
		for (Map<String, String> entry : tableData) {
			String valueString = entry.get(xAxisColumn);
			if (TextUtil.isDoubleString(valueString)) {
				binVal = Double.parseDouble(valueString);
				intervalBegin = binVal - (binVal % binSize);
				xValue = intervalBegin + " - " + (intervalBegin + binSize - 1);
				updateGraphItemList(xValue, entry.get(yAxisColumn), yAxisColumnType, groupedLabelCounts.get(entry.get(groupColumn)), groupedGraphItems.get(entry.get(groupColumn)));
			} else if (valueString.equalsIgnoreCase(IDAConst.NULL_VALUE_IDENTIFIER)) {
				updateGraphItemList(valueString, entry.get(yAxisColumn), yAxisColumnType, groupedLabelCounts.get(entry.get(groupColumn)), groupedGraphItems.get(entry.get(groupColumn)));
			}
		}
		if (IDAConst.TRANSFORMATION_TYPE_AVG.equals(yAxisColumnType)) {
			for (String group : groupedGraphItems.keySet()) {
				Map<String, Double> entries = groupedGraphItems.get(group);
				Map<String, Integer> labels = groupedLabelCounts.get(group);
				entries.replaceAll((l, v) -> entries.get(l) / labels.get(l));
				groupedGraphItems.put(group, entries);
			}
		}
	}

	/**
	 * Method to process the bins for date labels
	 *
	 * @param binSize         - size of the bins
	 * @param binType         - type of duration (days, weeks, months or years)
	 * @param xAxisColumn     - column for labels
	 * @param yAxisColumn     - column for values
	 * @param yAxisColumnType - type of value column
	 * @param labelCounts     - map of labels to its counts
	 */
	private void processBinsForDateLabels(int binSize, String binType, String xAxisColumn, String yAxisColumn, String yAxisColumnType, Map<String, Integer> labelCounts) {
		String xValue;
		Calendar calendar = Calendar.getInstance();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(IDAConst.LABEL_PATTERN_DATE);
		if (IDAConst.DURATION_TYPE_MONTH.equals(binType)) {
			formatter = DateTimeFormatter.ofPattern(IDAConst.LABEL_PATTERN_MONTH);
		} else if (IDAConst.DURATION_TYPE_YEAR.equals(binType)) {
			formatter = DateTimeFormatter.ofPattern(IDAConst.LABEL_PATTERN_YEAR);
		}

		labelCounts.putAll(initializeGraphItemsForDateBins(binSize, binType, xAxisColumn, calendar));
		Date min = calendar.getTime();
		LocalDate localMin = min.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		for (Map<String, String> entry : tableData) {
			String valueString = entry.get(xAxisColumn);
			try {
				calendar.setTime(DateUtils.parseDate(valueString, IDAConst.DATE_PATTERNS));
				xValue = getBinLabelFromDate(binType, calendar, binSize, localMin, formatter);
				updateGraphItemList(xValue, entry.get(yAxisColumn), yAxisColumnType, labelCounts, graphItems);
			} catch (ParseException | NullPointerException ex) {
				if (valueString.equalsIgnoreCase(IDAConst.NULL_VALUE_IDENTIFIER))
					updateGraphItemList(valueString, entry.get(yAxisColumn), yAxisColumnType, labelCounts, graphItems);
			}
		}
		if (IDAConst.TRANSFORMATION_TYPE_AVG.equals(yAxisColumnType)) {
			graphItems.replaceAll((l, v) -> graphItems.get(l) / labelCounts.get(l));
		}
	}

	/**
	 * Method to process the bins for date labels along with grouping
	 *
	 * @param binSize 			- size of the bins
	 * @param binType 			- type of duration (days, weeks, months or years)
	 * @param xAxisColumn 		- column for labels
	 * @param yAxisColumn 		- column for values
	 * @param yAxisColumnType 	- type of value column
	 * @param groupColumn 		- column for grouping the labels
	 */
	private void processGroupedBinsForDateLabels(int binSize, String binType, String xAxisColumn, String yAxisColumn, String yAxisColumnType, String groupColumn) {
		String xValue;
		Calendar calendar = Calendar.getInstance();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(IDAConst.LABEL_PATTERN_DATE);
		if (IDAConst.DURATION_TYPE_MONTH.equals(binType)) {
			formatter = DateTimeFormatter.ofPattern(IDAConst.LABEL_PATTERN_MONTH);
		} else if (IDAConst.DURATION_TYPE_YEAR.equals(binType)) {
			formatter = DateTimeFormatter.ofPattern(IDAConst.LABEL_PATTERN_YEAR);
		}

		List<String> groups = tableData.stream().map(e -> e.get(groupColumn)).distinct().collect(Collectors.toList());
		Map<String, Map<String, Integer>> groupedLabelCounts = new HashMap<>();
		Map<String, Integer> labelCounts = new HashMap<>(initializeGraphItemsForDateBins(binSize, binType, xAxisColumn, calendar));
		Map<String, Double> groupEntries = new HashMap<>() {{
			putAll(graphItems);
		}};
		for (String group : groups) {
			groupedGraphItems.put(group, new HashMap<>() {{
				putAll(groupEntries);
			}});
			groupedLabelCounts.put(group, new HashMap<>() {{
				putAll(labelCounts);
			}});
		}
		Date min = calendar.getTime();
		LocalDate localMin = min.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		for (Map<String, String> entry : tableData) {
			String valueString = entry.get(xAxisColumn);
			try {
				calendar.setTime(DateUtils.parseDate(valueString, IDAConst.DATE_PATTERNS));
				xValue = getBinLabelFromDate(binType, calendar, binSize, localMin, formatter);
				updateGraphItemList(xValue, entry.get(yAxisColumn), yAxisColumnType, groupedLabelCounts.get(entry.get(groupColumn)), groupedGraphItems.get(entry.get(groupColumn)));
			} catch (ParseException | NullPointerException ex) {
				if (valueString.equalsIgnoreCase(IDAConst.NULL_VALUE_IDENTIFIER))
					updateGraphItemList(valueString, entry.get(yAxisColumn), yAxisColumnType, groupedLabelCounts.get(entry.get(groupColumn)), groupedGraphItems.get(entry.get(groupColumn)));
			}
		}
		if (IDAConst.TRANSFORMATION_TYPE_AVG.equals(yAxisColumnType)) {
			for (String group : groupedGraphItems.keySet()) {
				Map<String, Double> entries = groupedGraphItems.get(group);
				Map<String, Integer> labels = groupedLabelCounts.get(group);
				entries.replaceAll((l, v) -> entries.get(l) / labels.get(l));
				groupedGraphItems.put(group, entries);
			}
		}
	}

	/**
	 * Method to initialize the graph items for all bins
	 *
	 * @param binSize     - size of each bin
	 * @param binType     - type of bin (Eg: days, weeks, months)
	 * @param xAxisColumn - column mapped to labels
	 * @param calendar    - calender instance passed as reference for future use from the callee
	 * @return - map of bin labels and their counts
	 */
	private Map<String, Integer> initializeGraphItemsForDateBins(int binSize, String binType, String xAxisColumn, Calendar calendar) {
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
		Date min = values.get(0);
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
		LocalDate max = values.get(values.size() - 1).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();


		Map<String, Integer> labelCounts = new HashMap<>();
		String label;
		LocalDate nextDate = localMin;
		while (nextDate.isBefore(max)) {
			switch (binType) {
				case IDAConst.DURATION_TYPE_WEEK:
					label = nextDate.format(formatter) + " to " + nextDate.plusWeeks(binSize).minusDays(1).format(formatter);
					nextDate = nextDate.plusWeeks(binSize);
					break;
				case IDAConst.DURATION_TYPE_MONTH:
					label = binSize == 1 ? nextDate.format(formatter) : nextDate.format(formatter) + " to " + nextDate.plusMonths(binSize).minusDays(1).format(formatter);
					nextDate = nextDate.plusMonths(binSize);
					break;
				case IDAConst.DURATION_TYPE_YEAR:
					label = binSize == 1 ? nextDate.format(formatter) : nextDate.format(formatter) + " to " + nextDate.plusYears(binSize).minusDays(1).format(formatter);
					nextDate = nextDate.plusYears(binSize);
					break;
				default:
					label = nextDate.format(formatter) + " to " + nextDate.plusDays(binSize - 1).format(formatter);
					nextDate = nextDate.plusDays(binSize);
					break;
			}
			graphItems.put(label, 0.0);
			labelCounts.put(label, 1);
		}
		return labelCounts;
	}

	/**
	 * Method to get bin label based on the date string, bin type and bin size
	 *
	 * @param binType   - type of the bin
	 * @param calendar  - calendar set from date string
	 * @param binSize   - size of the bin
	 * @param localMin  - min date from the table data
	 * @param formatter - date string formatter
	 * @return - bin label for the given date
	 */
	private String getBinLabelFromDate(String binType, Calendar calendar, int binSize, LocalDate localMin, DateTimeFormatter formatter) {
		String xValue;
		LocalDate binVal;
		LocalDate intervalBegin;
		long diff;
		switch (binType) {
			case IDAConst.DURATION_TYPE_WEEK:
				calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
				binVal = calendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				diff = ChronoUnit.WEEKS.between(localMin, binVal);
				intervalBegin = localMin.plusWeeks((diff / binSize) * binSize);
				xValue = intervalBegin.format(formatter) + " to " + intervalBegin.plusWeeks(binSize).minusDays(1).format(formatter);
				break;
			case IDAConst.DURATION_TYPE_MONTH:
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				binVal = calendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				diff = ChronoUnit.MONTHS.between(localMin, binVal);
				intervalBegin = localMin.plusMonths((diff / binSize) * binSize);
				xValue = binSize == 1 ? intervalBegin.format(formatter) : intervalBegin.format(formatter) + " to " + intervalBegin.plusMonths(binSize).minusDays(1).format(formatter);
				break;
			case IDAConst.DURATION_TYPE_YEAR:
				calendar.set(Calendar.DAY_OF_YEAR, 1);
				binVal = calendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				diff = ChronoUnit.YEARS.between(localMin, binVal);
				intervalBegin = localMin.plusYears((diff / binSize) * binSize);
				xValue = binSize == 1 ? intervalBegin.format(formatter) : intervalBegin.format(formatter) + " to " + intervalBegin.plusYears(binSize).minusDays(1).format(formatter);
				break;
			default:
				binVal = calendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				diff = ChronoUnit.DAYS.between(localMin, binVal);
				intervalBegin = localMin.plusDays((diff / binSize) * binSize);
				xValue = intervalBegin.format(formatter) + " to " + intervalBegin.plusDays(binSize - 1).format(formatter);
				break;
		}
		return xValue;
	}

	/**
	 * Method to add a new entry to list of graph items
	 *
	 * @param xValue          - label value of an entry
	 * @param yValueString    - second parameter value in string
	 * @param yAxisColumnType - second parameter type
	 * @param labelCounts     - count of each label (useful for calculating the average)
	 */
	private void updateGraphItemList(String xValue, String yValueString, String yAxisColumnType, Map<String, Integer> labelCounts, Map<String, Double> graphItems) {
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
	 * Method to create a response object based on graph items for grouped bar graph
	 */
	private void createGroupedBarGraphResponse() {
		String xAxisColumn = parameterMap.get("group_column");
		String yAxisColumn = parameterMap.get(IDAConst.Y_AXIS_PARAM);
		String xAxisColumnType = parameterTypeMap.get(IDAConst.X_AXIS_PARAM + IDAConst.ATTRIBUTE_TYPE_SUFFIX);
		String yAxisColumnType = parameterTypeMap.get(IDAConst.Y_AXIS_PARAM + IDAConst.ATTRIBUTE_TYPE_SUFFIX);
		String yAxisLabel = IDAConst.INSTANCE_PARAM_TYPE_UNIQUE.equals(xAxisColumnType) ? yAxisColumn : yAxisColumnType + " " + yAxisColumn;
		String graphLabel = "Bar graph for " + xAxisColumn + " and " + yAxisLabel;
		List<BarGraphItem> barGraphItemList;
		Map<String, List<BarGraphItem>> groupedBarChartData = new TreeMap<>();
		for (String groupLabel : groupedGraphItems.keySet().stream().sorted(comparator).collect(Collectors.toList())) {
			barGraphItemList = new ArrayList<>();
			for (String label : groupedGraphItems.get(groupLabel).keySet()) {
				barGraphItemList.add(new BarGraphItem(label, groupedGraphItems.get(groupLabel).get(label)));
			}
			groupedBarChartData.put(groupLabel, barGraphItemList);
		}
		List<String> xAxisLabels = groupedGraphItems.get(groupedGraphItems.keySet().iterator().next()).keySet().stream().sorted(comparator).collect(Collectors.toList());
		payload.put("barGraphData", new GroupedBarGraphData(graphLabel, xAxisColumn, yAxisLabel, xAxisLabels, groupedBarChartData));
	}

	/**
	 * Method to create a response object based on graph items for bubble graph
	 *
	 * @param dsName    - name of the dataset
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

	/**
	 * Method to create a response object based on graph items for grouped bubble chart
	 */
	private void createGroupedBubbleChartResponse() {
		String labelColumn = parameterMap.get("group_name");
		String sizeColumn = parameterMap.get(IDAConst.BUBBLE_SIZE_PARAM);
		String labelColumnType = parameterTypeMap.get(IDAConst.BUBBLE_LABEL_PARAM + IDAConst.ATTRIBUTE_TYPE_SUFFIX);
		String sizeColumnType = parameterTypeMap.get(IDAConst.BUBBLE_SIZE_PARAM + IDAConst.ATTRIBUTE_TYPE_SUFFIX);
		String sizeLabel = IDAConst.INSTANCE_PARAM_TYPE_UNIQUE.equals(labelColumnType) ? sizeColumn : sizeColumnType + " " + sizeColumn;
		String graphLabel = "Bubble chart for " + labelColumn + " and " + sizeLabel;
		List<BubbleChartItem> bubbleChartItemList;
		Map<String, List<BubbleChartItem>> groupedBubbleChartData = new TreeMap<>();
		for (String groupLabel : groupedGraphItems.keySet().stream().sorted(comparator).collect(Collectors.toList())) {
			bubbleChartItemList = new ArrayList<>();
			for (String label : groupedGraphItems.get(groupLabel).keySet()) {
				bubbleChartItemList.add(new BubbleChartItem(label, label, groupedGraphItems.get(groupLabel).get(label)));
			}
			groupedBubbleChartData.put(groupLabel, bubbleChartItemList);
		}
		List<String> bubbleLabels = groupedGraphItems.get(groupedGraphItems.keySet().iterator().next()).keySet().stream().sorted(comparator).collect(Collectors.toList());
		payload.put("bubbleChartData", new GroupedBubbleChartData(graphLabel, bubbleLabels, groupedBubbleChartData));
	}

	/**
	 * Method to handle the chatbot flow for grouping the visualizations
	 *
	 * @param chatMessageResponse 			- instance of the chatbot response
	 * @param paramMap 						- parameter map from dialogflow
	 * @return 								- true if grouping flow is complete and false otherwise
	 * @throws NoSuchAlgorithmException 	- dialogflow auth encryption algorithm is invalid
	 * @throws IOException 					- dialogflow credentials file does not exist
	 * @throws InvalidKeySpecException 		- dialogflow auth key invalid
	 */
	private boolean handleGroupingLogic(ChatMessageResponse chatMessageResponse, Map<String, Object> paramMap) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
		String isGroupNeeded = paramMap.getOrDefault("isGrouped", "").toString();
		if (isGroupNeeded.isEmpty()) {
			dialogFlowUtil.setContext("get_group_needed");
			chatMessageResponse.setMessage("Do you want to group the data?");
			return false;
		} else if ("false".equals(isGroupNeeded)) {
			groupingNeeded = false;
			return true;
		} else {
			groupingNeeded = true;
			String groupColumn = paramMap.getOrDefault("group_column", "").toString();
			if (groupColumn.isEmpty()) {
				dialogFlowUtil.setContext("get_group_column");
				chatMessageResponse.setMessage("Which column should be used to group the data?");
				return false;
			} else if ("true".equals(columnUniquenessMap.get(groupColumn))) {
				dialogFlowUtil.setContext("get_group_column");
				chatMessageResponse.setMessage(groupColumn + " cannot be used to group the data. The column values must be non unique. Please provide a different column");
				return false;
			}
			return true;
		}
	}

}
