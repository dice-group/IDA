package org.dice.ida.visualizer;

import org.apache.commons.lang3.time.DateUtils;
import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.AttributeSummary;
import org.dice.ida.model.DataSummary;
import org.dice.ida.model.bargraph.BarGraphData;
import org.dice.ida.model.bargraph.BarGraphItem;
import org.dice.ida.util.MetaFileReader;
import org.dice.ida.util.TextUtil;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.*;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * Class to provide required attributes for bar graph visualization and apply data filters.
 *
 * @author Sourabh, Maqbool
 */

public class BarGraphVisualizer {
	private String yAxisLabel;
	private String xAxisLabel;
	private String dataSetName;
	private String tableName;
	private String label = "Bar Graph";
	private Attribute xaxis;
	private Attribute yaxis;
	private List<BarGraphItem> items;
	private Instances data;
	private DataSummary DS;

	public BarGraphVisualizer(String xAxis, String yAxis, String dsName,
							  String tableName, String filterText, Instances data) {
		this.xAxisLabel = xAxis;
		this.yAxisLabel = yAxis;
		this.tableName = tableName;
		xaxis = data.attribute(xAxisLabel);
		yaxis = data.attribute(yAxisLabel);

		// prepare filter then apply them to data
		prepareFilter(data, filterText);

		this.dataSetName = dsName;
		try {
			DS = new MetaFileReader().createDataSummary(dataSetName, tableName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public BarGraphData createBarGraph() {
		items = new ArrayList<>();
		loadBarGraphItem();

		return new BarGraphData(label, items, xAxisLabel, yAxisLabel, dataSetName,
				tableName);
	}

	private void loadBarGraphItem() {
		//check for filter and load bar graph items

		//Loads the File
		AttributeSummary xaxisSummary = DS.getAttributeSummaryList().stream().filter(x -> TextUtil.matchString(x.getName(), xAxisLabel)).collect(toList()).get(0);

		if (xaxisSummary.getType().equalsIgnoreCase("Nom")) {
			loadNominal();
		}
		if (xaxisSummary.getType().equalsIgnoreCase("Num")) {
			loadNumericData();
		}
		if (xaxisSummary.getType().equalsIgnoreCase("Date")) {
			loadDateData();
		}
	}

	public void loadDateData() {
		HashMap<String, Double> temp_date = new HashMap<>();
		HashMap<String, Double> temp_month = new HashMap<>();
		HashMap<String, Double> temp_year = new HashMap<>();
		Calendar cal = Calendar.getInstance();
		String[] parsePatterns = {"dd MMMM"};
		for (int i = 0; i < data.numInstances(); i++) {
			try {
				Date d = DateUtils.parseDateStrictly(data.instance(i).stringValue(xaxis).trim(), parsePatterns);
				cal.setTime(d);
				if (temp_date.containsKey(data.instance(i).stringValue(xaxis))) {
					double yvalue = temp_date.get(data.instance(i).stringValue(xaxis));
					temp_date.put(data.instance(i).stringValue(xaxis), yvalue + data.instance(i).value(yaxis));
				} else {
					temp_date.put(data.instance(i).stringValue(xaxis), data.instance(i).value(yaxis));
				}

				if (temp_month.containsKey(getMonth(cal.get(Calendar.MONTH)))) {
					double yvalue = temp_month.get(getMonth(cal.get(Calendar.MONTH)));
					temp_month.put(getMonth(cal.get(Calendar.MONTH)), yvalue + data.instance(i).value(yaxis));
				} else {
					temp_month.put(getMonth(cal.get(Calendar.MONTH)), data.instance(i).value(yaxis));
				}
				if (temp_year.containsKey(String.valueOf(cal.get(Calendar.YEAR)))) {
					double yvalue = temp_year.get(String.valueOf(cal.get(Calendar.YEAR)));
					temp_year.put(String.valueOf(cal.get(Calendar.YEAR)), yvalue + data.instance(i).value(yaxis));
				} else {
					temp_year.put(String.valueOf(cal.get(Calendar.YEAR)), data.instance(i).value(yaxis));
				}
			} catch (ParseException e) {
				continue;
			}
		}
		sortAndLoad(temp_date, temp_month, temp_year);
	}

	public String getMonth(int month) {
		return new DateFormatSymbols().getMonths()[month];
	}

	public void sortAndLoad(HashMap<String, Double> date, HashMap<String, Double> month, HashMap<String, Double> year) {
		int d = Math.abs(date.size() - 10), m = Math.abs(month.size() - 10), y = Math.abs(year.size() - 10);
		if (m <= d) {
			if (m <= y)
				load(month);
			else
				load(year);
		} else {
			if (y <= d)
				load(year);
			else
				load(month);
		}
	}

	public void load(HashMap<String, Double> loadData) {
		for (String x : loadData.keySet()) {
			items.add(new BarGraphItem(x, loadData.get(x)));
		}

	}

	public void loadNominal() {
		HashMap<String, Double> temp = new HashMap<>();
		AttributeSummary yaxisSummary = DS.getAttributeSummaryList().stream().filter(x -> TextUtil.matchString(x.getName(), yAxisLabel)).collect(toList()).get(0);
		if (yaxisSummary.getType().equalsIgnoreCase("Num")) {
			for (int i = 0; i < data.numInstances(); i++) {
				if (temp.containsKey(data.instance(i).stringValue(xaxis))) {
					double yvalue = temp.get(data.instance(i).stringValue(xaxis));
					temp.put(data.instance(i).stringValue(xaxis), yvalue + data.instance(i).value(yaxis));
				} else {
					temp.put(data.instance(i).stringValue(xaxis), data.instance(i).value(yaxis));
				}
			}
		}
		if (yaxisSummary.getType().equalsIgnoreCase("Nom")) {
			for (int i = 0; i < data.numInstances(); i++) {
				if (temp.containsKey(data.instance(i).stringValue(xaxis))) {
					double yvalue = temp.get(data.instance(i).stringValue(xaxis));
					temp.put(data.instance(i).stringValue(xaxis), yvalue + 1);
				} else {
					temp.put(data.instance(i).stringValue(xaxis), 1.0);
				}
			}
		}

		for (String key : sortAndLimit(temp).keySet()) {
			items.add(new BarGraphItem(key, temp.get(key)));
		}
	}

	public void loadNumericData() {
		HashMap<String, Double> bins = new HashMap<>();
		for (Instance instance : data) {
			// Aggregating all y-axis values on its x-axis
			if (!bins.containsKey(instance.toString(xaxis))) {
				bins.put(instance.toString(xaxis), instance.value(yaxis));
			} else {
				// bin has this x-value already then
				bins.put(instance.toString(xaxis), (bins.get(instance.toString(xaxis)) + instance.value(yaxis)));
			}
		}

		for (String key : sortAndLimit(bins).keySet()) {
			items.add(new BarGraphItem(key, bins.get(key)));
		}
	}

	/**
	 * This method Uses filterText provided by User and filter
	 * out required rows from data.
	 * It prepares ranges to filter out rows and then send
	 * those ranges to applyFilter function.
	 *
	 * @param data
	 * @param filterText
	 */
	private void prepareFilter(Instances data, String filterText) {
		if (filterText.equals(IDAConst.BG_FILTER_ALL)) {
			// All data has been selected
			this.data = data;
		} else {
			String[] tokens = filterText.split(" "); // tokenized filter text
			String filterType = tokens[0]; // Dialogflow makes sure that these tokens are in correct order
			int rangeStart = 0;
			int rangeEnd = 0;

			// Extracting ranges
			if (TextUtil.matchString(filterType, IDAConst.BG_FILTER_FIRST)) {
				rangeEnd = Math.min(Integer.parseInt(tokens[1]), data.size());
			} else if (TextUtil.matchString(filterType, IDAConst.BG_FILTER_LAST)) {
				rangeStart = Math.max(data.size() - Integer.parseInt(tokens[1]), 0);
				rangeEnd = data.size();
			} else if (TextUtil.matchString(filterType, IDAConst.BG_FILTER_FROM)) {
				rangeStart = Integer.parseInt(tokens[1]) == 0 ? 0 : Integer.parseInt(tokens[1]) - 1;
				rangeEnd = Integer.parseInt(tokens[3]);
			}
			applyFilter(data, rangeStart, rangeEnd);
		}
	}

	/**
	 * Uses ranges produced by prepareFilter method and simply
	 * filter out data
	 *
	 * @param data
	 * @param rangeStart
	 * @param rangeEnd
	 */
	private void applyFilter(Instances data, int rangeStart, int rangeEnd) {
		this.data = new Instances(data, rangeEnd - rangeStart);
		for (int i = rangeStart; i < rangeEnd; i++) {
			this.data.add(data.instance(i));
		}
	}

	/**
	 * Take hashmap as input and using Java Stream return sorted records.
	 * key value pairs as map in ascending order
	 *
	 * @param hm
	 * @return Map
	 */
	private Map<String, Double> sortAndLimit(HashMap<String, Double> hm) {
		return hm
				.entrySet()
				.stream()
				.sorted(comparingByValue())
				.collect(
						toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
								LinkedHashMap::new));
	}
}
