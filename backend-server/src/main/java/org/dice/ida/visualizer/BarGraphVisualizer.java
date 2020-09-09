package org.dice.ida.visualizer;

import java.util.*;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.*;

import org.dice.ida.model.AttributeSummary;
import org.dice.ida.model.DataSummary;
import org.dice.ida.model.bargraph.BarGraphData;
import org.dice.ida.model.bargraph.BarGraphItem;
import org.dice.ida.util.MetaFileReader;
import org.dice.ida.constant.IDAConst;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

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
		items = new ArrayList<BarGraphItem>();
		loadBarGraphItem();

		return new BarGraphData(label, items, xAxisLabel, yAxisLabel, dataSetName,
				tableName);
	}

	private void loadBarGraphItem() {
		//check for filter and load bar graph items

		//Loads the File
		xaxis = data.attribute(xAxisLabel);
		yaxis = data.attribute(yAxisLabel);
		if (xaxis.isNominal()) {
			loadNominal();
		}
		if (xaxis.isNumeric()) {
			loadNumericData();
		}
//		if (xaxis.isDate())
//		{
//			// Date is non trivial case
//		}
	}

	public void loadNominal() {
		HashMap<String, Double> temp = new HashMap<String, Double>();
		AttributeSummary yaxisSummary = DS.getAttributeSummaryList().stream().filter(x -> x.getName().equalsIgnoreCase(yAxisLabel)).collect(toList()).get(0);
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
			int rangeStart = 0, rangeEnd = 0;

			// Extracting ranges
			if (filterType.equalsIgnoreCase(IDAConst.BG_FILTER_FIRST)) {
				rangeStart = 0;
				rangeEnd = Integer.parseInt(tokens[1]);
			} else if (filterType.equalsIgnoreCase(IDAConst.BG_FILTER_LAST)) {
				rangeStart = data.size() - Integer.parseInt(tokens[1]);
				rangeEnd = data.size();
			} else if (filterType.equalsIgnoreCase(IDAConst.BG_FILTER_FROM)) {
				rangeStart =  Integer.parseInt(tokens[1]) - 1;
				rangeEnd =  Integer.parseInt(tokens[3]);
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
			System.out.println(data.instance(i));
			this.data.add(data.instance(i));
		}
	}

	/**
	 * Take hashmap as input and using Java Stream return sorted first 20 records
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
