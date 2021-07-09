package org.dice.ida.action.process;

import org.dice.ida.action.def.Action;
import org.dice.ida.action.def.ListDataSetsAction;
import org.dice.ida.action.def.LoadDataSetAction;
import org.dice.ida.action.def.UploadDatasetAction;
import org.dice.ida.action.def.SimpleTextAction;
import org.dice.ida.action.def.SuggestVisualization;
import org.dice.ida.action.def.ListVisualizationsAction;
import org.dice.ida.action.def.VisualizeAction;
import org.dice.ida.action.def.ClusterAction;
import org.dice.ida.action.def.ClearConversationAction;
import org.dice.ida.action.def.CauseExceptionAction;
import org.dice.ida.action.def.DefaultAction;
import org.dice.ida.action.def.UserHelpAction;
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
			case NEXT_STEPS:
			case HELP:
				action = appContext.getBean(SimpleTextAction.class);
				break;
			case UPLOAD_DATASET:
				 action = appContext.getBean(UploadDatasetAction.class);
				break;
			case LOAD_DATASET:
				action = appContext.getBean(LoadDataSetAction.class);
				break;
			case LIST_DATASET:
				action = appContext.getBean(ListDataSetsAction.class);
				break;
			case LIST_VISUALIZATION:
				action = appContext.getBean(ListVisualizationsAction.class);
				break;
			case SUGGEST_VISUALIZATION:
				action = appContext.getBean(SuggestVisualization.class);
				break;
			case LINE_CHART:
			case BARCHART:
			case BUBBLECHART:
			case SCATTERPLOT:
			case SCATTERPLOTMATRIX:
				action = appContext.getBean(VisualizeAction.class);
				break;
			case CLUSTERING:
				action = appContext.getBean(ClusterAction.class);
				break;
			case CLEAR:
				action = appContext.getBean(ClearConversationAction.class);
				break;
			case CAUSE_EXCEPTION:
				action = appContext.getBean(CauseExceptionAction.class);
				break;
			case USER_HELP:
				action = appContext.getBean(UserHelpAction.class);
				break;
			default:
				action = appContext.getBean(DefaultAction.class);
				break;
		}
		return action;
	}


}
