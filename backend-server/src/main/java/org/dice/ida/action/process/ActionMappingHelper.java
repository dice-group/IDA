package org.dice.ida.action.process;

import org.dice.ida.action.def.*;
import org.dice.ida.model.Intent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ActionMappingHelper {

	@Autowired
	private ApplicationContext appContext;

	public Action fetchActionInstance(String intentText) {
		Action action;
		// return the instance for requested action
		Intent intent = Intent.getForKey(intentText);
		switch (intent) {
			case GREETING:
			case UNKNOWN:
			case HELP:
				action = appContext.getBean(SimpleTextAction.class);
				break;
//			case UPLOAD_DATASET:
//				 TODO: do something
//				break;
			case LOAD_DATASET:
				action = appContext.getBean(LoadDataSetAction.class);
				break;
			case SUGGEST_VISUALIZATION:
				action = appContext.getBean(SuggestVisualization.class);
				break;
			case BAR_GRAPH:
				action = new BarGraphAction();
				break;
			case BAR_GRAPH_TEST:
				action = appContext.getBean(BarGraphTestAction.class);
				break;
			case VISUALIZE:
				action = appContext.getBean(VisualizeAction.class);
				break;
			case BUBBLE_CHART:
				action = new BubbleChartAction();
				break;
			case LINE_CHART:
				action = new LineChartAction();
				break;
			case BOOK_FLIGHT:
				action = appContext.getBean(BookFlightAction.class);
				break;
			case BUBBLECHART:
				action = appContext.getBean(VisualizeAction.class);
				break;
			default:
				action = appContext.getBean(DefaultAction.class);
				break;
		}
		return action;
	}


}
