package org.dice.ida.util;

import org.dice.ida.constant.IDAConst;
import weka.core.Instances;

public class FilterUtil {

	/**
	 * This method Uses filterText provided by User and filter
	 * out required rows from the data.
	 *
	 * @param data
	 * @param filterText
	 */
	public static Instances filterData(Instances data, String filterText) {
		Instances filteredData;
		if (filterText.equals(IDAConst.BG_FILTER_ALL)) {
			// All data has been selected
			filteredData = data;
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
			// Filtering data using extracted ranges
			filteredData = new Instances(data, rangeEnd - rangeStart);
			for (int i = rangeStart; i < rangeEnd; i++) {
				filteredData.add(data.instance(i));
			}
		}
		return filteredData;
	}
}
