package org.dice.ida.visualizer;

import java.util.*;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.*;

import org.dice.ida.model.AttributeSummary;
import org.dice.ida.model.DataSummary;
import org.dice.ida.model.bargraph.BarGraphData;
import org.dice.ida.model.bargraph.BarGraphItem;
import org.dice.ida.util.MetaFileReader;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Class to provide required attributes for bar graph visualization and apply data filters.
 *
 * @author Sourabh
 */

public class BarGraphVisualizer {
	private String yAxisLabel;
	private String xAxisLabel;
	private String dataSetName;
	private String tableName;
	private String label = "Bar Graph";
	private Attribute xaxis;
	private Attribute yaxis;
	private String filterText;
	private List<BarGraphItem> items;
	private Instances data;
	private DataSummary DS;

	public BarGraphVisualizer(String xAxis, String yAxis, String dsName,
							  String tableName, String filterText, Instances data) {
		this.xAxisLabel = xAxis;
		this.yAxisLabel = yAxis;
		this.filterText = filterText;
		this.tableName = tableName;

		applyFilter(data);

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
		System.out.println("well");
		HashMap<String, Double> bins = new HashMap<>();
		for (Instance instance : data) {
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

	private void applyFilter(Instances rows) {
		if (filterText.equals("all")) {
			this.data = rows;
		} else {
			String[] tokens = filterText.split(" ");
			String filterType = null;
			int filterQuantity;

			filterQuantity =  Integer.parseInt(tokens[1]);
			filterType = tokens[0];

			if (filterType.equalsIgnoreCase("first")) {

				this.data = new Instances(rows, filterQuantity);
				for (int i = 0; i < filterQuantity; i++) {
					this.data.add(rows.instance(i));
				}
			} else if (filterType.equalsIgnoreCase("last")) {

				this.data = new Instances(rows, filterQuantity);
				for (int i = rows.size(); i >  rows.size()-filterQuantity; i--) {
					this.data.add(rows.instance(i-1));
				}
			} else if (filterType.equalsIgnoreCase("from")) {
				int rangeStart =  Integer.parseInt(tokens[1]);
				int rangeEnd =  Integer.parseInt(tokens[3]);
				this.data = new Instances(rows, rangeEnd - rangeStart);
				for (int i = rangeStart; i < rangeEnd; i++) {
					this.data.add(rows.instance(i));
				}
			}
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
