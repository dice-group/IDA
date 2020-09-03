package org.dice.ida.visualizer;

import java.util.ArrayList;
import java.util.List;

import org.dice.ida.model.AttributeSummary;
import org.dice.ida.model.DataSummary;
import org.dice.ida.model.bargraph.BarGraphData;
import org.dice.ida.model.bargraph.BarGraphItem;
import org.dice.ida.util.MetaFileReader;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.Instance;
/**
 * Class to provide required attributes for bar graph visualization and apply data filters.
 *
 * @author Sourabh
 */

public class BarGraphVisualizer {
	String xAxisLabel;
	String yAxisLabel;
	String label = "Bar Graph";
	String dataSetName;
	String tableName;
	Attribute xaxis;
	Attribute yaxis;
	List<BarGraphItem> barChartItems;
	Instances data;
	DataSummary DS;
	public BarGraphVisualizer(String xAxis, String yAxis, String dsName,
			String tableName, Instances data)
	{
		this.xAxisLabel = xAxis;
		this.yAxisLabel = yAxis;
		this.dataSetName = dsName;
		this.tableName = tableName;
		this.data = data;
		try {
			DS = new MetaFileReader().createDataSummary(dataSetName, tableName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public BarGraphData createBarGraph()
	{
		barChartItems = new ArrayList<BarGraphItem>();
		loadBarGraphItem();

		return new BarGraphData(label, barChartItems , xAxisLabel, yAxisLabel,dataSetName,
				tableName);
	}

	private void loadBarGraphItem()
	{
		xaxis = data.attribute(xAxisLabel);
		yaxis = data.attribute(yAxisLabel);
		if (xaxis.isNominal())
		{
			loadNominal();
		}
		if (xaxis.isNumeric())
		{
			loadNumericData();
		}
		if (xaxis.isDate())
		{

		}
	}
	public void loadNominal()
	{
		List<AttributeSummary> attributeSummary =DS.getAttributeSummaryList();

	}
	public void loadNumericData()
	{
		for(Instance instance: data) {
			barChartItems.add(new BarGraphItem(instance.toString(xaxis), instance.toString(yaxis)));
		}
	}
}
