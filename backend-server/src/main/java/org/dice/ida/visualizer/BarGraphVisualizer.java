package org.dice.ida.visualizer;

import java.util.ArrayList;
import java.util.List;

import org.dice.ida.model.bargraph.BarGraphData;
import org.dice.ida.model.bargraph.BarGraphItem;

public class BarGraphVisualizer {
	
	public BarGraphData createBarGraph(String xAxis, String yAxis, String dsName,
			String tableName)
	{
		String xAxisLabel=null;
		String yAxisLabel=null;
		String label = null;
		List<BarGraphItem> items = new ArrayList<BarGraphItem>();
		loadBarGraphItem();  
		
		return new BarGraphData(label, items , xAxisLabel, yAxisLabel,dsName,
				tableName);
	}
	private void loadBarGraphItem()
	{
		//check for filter and load bar graph items 
	}
}
