package org.dice.ida.action.process;

import org.dice.ida.action.def.Action;
import org.dice.ida.action.def.BubbleChartAction;
import org.dice.ida.action.def.BarGraphAction;
import org.dice.ida.action.def.DefaultAction;
import org.dice.ida.action.def.LoadDataSetAction;
import org.dice.ida.action.def.SimpleTextAction;
import org.dice.ida.action.def.SuggestVisualization;
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
			case BUBBLE_CHART:
				action = new BubbleChartAction();
				break;
			default:
				action = appContext.getBean(DefaultAction.class);
				break;
		}
		return action;
	}


}
