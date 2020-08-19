package org.dice.ida.action.process;

import java.util.Map;

import org.dice.ida.action.def.Action;
import org.dice.ida.action.def.DefaultAction;
import org.dice.ida.action.def.LoadDataAction;
import org.dice.ida.action.def.SimpleTextAction;
import org.dice.ida.model.Intent;
public class ActionMapper {
	
	public static Action fetchActionInstance(String intentText, Map<String, String> paramData) {
		Action action = null;
		// return the instance for requested action
		Intent intent = Intent.getForKey(intentText);
		switch(intent) {
			case GREETING:
				action = new SimpleTextAction();
				break;
			case HELP:
				// TODO: do something
				break;
			case UPLOAD_DATASET:
				// TODO: do something
				break;
			case LOAD_DATASET:
				action = new LoadDataAction();
				break;
			case UNKNOWN:
				// do something
				action = new SimpleTextAction();
				break;
			default:
				action = new DefaultAction();
		}
		return action;
	}
	
	

}
