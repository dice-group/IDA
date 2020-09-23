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
	private final String dataSetName;
	private final String tableName;
	private final String[] columns;
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
		String label = "Bubble Chart";
		return new BubbleChartData(label, items, dataSetName,
				tableName);
	}

	private void loadBubbleGraphItem() {
		if (this.columns.length == 1) {
			Attribute col = data.attribute(this.columns[0]);
			populateItems(new Attribute[]{col});
		} else if (this.columns.length == 2) {
			Attribute ref_col = data.attribute(this.columns[0]);
			Attribute val_col = data.attribute(this.columns[1]);
			populateItems(new Attribute[]{ref_col, val_col});
		}
	}

	private void populateItems (Attribute[] cols) {
		HashMap<String, Double> bins = new HashMap<>();

		for (Instance instance : data) {
			if (!bins.containsKey(instance.toString(cols[0]))) {
				if (cols.length == 1) {
					bins.put(instance.toString(cols[0]), 1.0);
				} else {
					bins.put(instance.toString(cols[0]), instance.value(cols[1]));
				}
			} else {
				if (cols.length == 1) {
					bins.put(instance.toString(cols[0]), (bins.get(instance.toString(cols[0])) + 1));
				} else {
					bins.put(instance.toString(cols[0]), (bins.get(instance.toString(cols[0])) + instance.value(cols[1])));
				}
			}
		}

		for (String key : sortAndLimit(bins).keySet()) {
			items.add(new BubbleChartItem(key, key, bins.get(key)));
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
