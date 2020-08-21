package org.dice.ida.action.process;

import org.dice.ida.action.def.Action;
import org.dice.ida.action.def.DefaultAction;
import org.dice.ida.action.def.LoadDataSetAction;
import org.dice.ida.action.def.SimpleTextAction;
import org.dice.ida.model.Intent;
public class ActionMappingHelper {

	public static Action fetchActionInstance(String intentText) {
		Action action = null;
		// return the instance for requested action
		Intent intent = Intent.getForKey(intentText);
		switch(intent) {
			case GREETING:
			case UNKNOWN:
				// do something
				action = new SimpleTextAction();
				break;
//			case HELP:
//				// TODO: do something
//				break;
//			case UPLOAD_DATASET:
//				// TODO: do something
//				break;
			case LOAD_DATASET:
				action = new LoadDataSetAction();
				break;
			default:
				action = new DefaultAction();
				break;
		}
		return action;
	}



}
