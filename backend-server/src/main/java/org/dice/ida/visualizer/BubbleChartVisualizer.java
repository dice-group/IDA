package org.dice.ida.visualizer;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.DataSummary;
import org.dice.ida.model.bubblechart.BubbleChartData;
import org.dice.ida.model.bubblechart.BubbleChartItem;
import org.dice.ida.util.FilterUtil;
import org.dice.ida.util.MetaFileReader;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import java.util.*;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

/**
 * Class to provide required attributes for bar graph visualization and apply data filters.
 *
 * @author Sourabh, Maqbool
 */

public class BubbleChartVisualizer {
	private String dataSetName;
	private String tableName;
	private String label = "Bubble Chart";
	private String[] columns;
	private List<BubbleChartItem> items;
	private Instances data;

	public BubbleChartVisualizer(String[] columns, String dsName,
							  String tableName, String filterText, Instances data) {
		this.tableName = tableName;
		this.columns = columns;

		// prepare filter then apply them to data
		this.data = FilterUtil.filterData(data, filterText);

		this.dataSetName = dsName;
	}

	public BubbleChartData createBubbleChart() {
		items = new ArrayList<>();
		loadBubbleGraphItem();

		return new BubbleChartData(label, items, dataSetName,
				tableName);
	}

	private void loadBubbleGraphItem() {
		if (this.columns.length == 1) {
			HashMap<String, Double> bins = new HashMap<>();
			Attribute col = data.attribute(this.columns[0]);

			// **** for one column
			for (Instance instance : data) {
				// Aggregating all y-axis values on its x-axis
				if (!bins.containsKey(instance.toString(col))) {
					bins.put(instance.toString(col), 1.0);
				} else {
					// bin has this x-value already then
					bins.put(instance.toString(col), (bins.get(instance.toString(col)) + 1));
				}
			}

			for (String key : sortAndLimit(bins).keySet()) {
				items.add(new BubbleChartItem(key, "description", bins.get(key)));
			}
		} else if (this.columns.length == 2) {
			HashMap<String, Double> bins = new HashMap<>();
			Attribute ref_col = data.attribute(this.columns[0]);
			Attribute val_col = data.attribute(this.columns[1]);
			for (Instance instance : data) {
				// Aggregating all y-axis values on its x-axis
				if (!bins.containsKey(instance.toString(ref_col))) {
					bins.put(instance.toString(ref_col), instance.value(val_col));
				} else {
					// bin has this x-value already then
					bins.put(instance.toString(ref_col), (bins.get(instance.toString(ref_col)) + instance.value(val_col)));
				}
			}
			for (String key : sortAndLimit(bins).keySet()) {
				items.add(new BubbleChartItem(key, "description", bins.get(key)));
			}
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
