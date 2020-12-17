package org.dice.ida.visualizer;

import org.apache.commons.lang3.time.DateUtils;
import org.dice.ida.model.AttributeSummary;
import org.dice.ida.model.DataSummary;
import org.dice.ida.model.bargraph.BarGraphData;
import org.dice.ida.model.bargraph.BarGraphItem;
import org.dice.ida.util.DbUtils;
import org.dice.ida.util.FilterUtil;
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
							  String tableName, String filterText, Instances data) throws Exception {
		this.xAxisLabel = xAxis;
		this.yAxisLabel = yAxis;
		this.tableName = tableName;
		xaxis = data.attribute(xAxisLabel);
		yaxis = data.attribute(yAxisLabel);

		// filtering the data w.r.t to filterText
		this.data = FilterUtil.filterData(data, filterText);

		this.dataSetName = dsName;
		
		
		DS = new MetaFileReader().createDataSummary(dataSetName, tableName);


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

				if (temp_month.containsKey(getMonth(cal.get(Calendar.MONTH))+(cal.get(Calendar.YEAR)))) {
					double yvalue = temp_month.get(getMonth(cal.get(Calendar.MONTH)));
					temp_month.put(getMonth(cal.get(Calendar.MONTH))+(cal.get(Calendar.YEAR)), yvalue + data.instance(i).value(yaxis));
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
		int d = Math.abs(date.size() - 10);
		int m = Math.abs(month.size() - 10);
		int y = Math.abs(year.size() - 10);
		if (d <= m && d <= y) {
			load(date);
		} else if (m <= y && m <= d) {
			load(month);
		} else {
			load(year);
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
					temp.put(DbUtils.manageNullValues(data.instance(i).stringValue(xaxis)), data.instance(i).value(yaxis));
				}
			}
		}
		if (yaxisSummary.getType().equalsIgnoreCase("Nom")) {
			for (int i = 0; i < data.numInstances(); i++) {
				if (temp.containsKey(data.instance(i).stringValue(xaxis))) {
					double yvalue = temp.get(data.instance(i).stringValue(xaxis));
					temp.put(data.instance(i).stringValue(xaxis), yvalue + 1);
				} else {
					temp.put(DbUtils.manageNullValues(data.instance(i).stringValue(xaxis)), 1.0);
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
			if (bins.containsKey(instance.toString(xaxis))) {
				// bin has this x-value already then
				bins.put(instance.toString(xaxis), (bins.get(instance.toString(xaxis)) + instance.value(yaxis)));
			} else {
				bins.put(DbUtils.manageNullValues(instance.toString(xaxis)), instance.value(yaxis));
			}
		}

		for (String key : sortAndLimit(bins).keySet()) {
			items.add(new BarGraphItem(key, bins.get(key)));
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
