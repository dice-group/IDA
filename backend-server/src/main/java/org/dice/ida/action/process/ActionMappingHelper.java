package org.dice.ida.action.process;

import org.dice.ida.action.def.Action;
import org.dice.ida.action.def.SimpleTextAction;
import org.dice.ida.action.def.LoadDataSetAction;
import org.dice.ida.action.def.DefaultAction;
import org.dice.ida.action.def.SuggestVisualization;
import org.dice.ida.model.Intent;

public class ActionMappingHelper {

    public static Action fetchActionInstance(String intentText) {
        Action action;
        // return the instance for requested action
        Intent intent = Intent.getForKey(intentText);
        switch (intent) {
            case GREETING:
            case UNKNOWN:
            case HELP:
                action = new SimpleTextAction();
                break;
//			case UPLOAD_DATASET:
//				 TODO: do something
//				break;
            case LOAD_DATASET:
                action = new LoadDataSetAction();
                break;
            case SUGGEST_VISUALIZATION:
                action = new SuggestVisualization();
                break;
            default:
                action = new DefaultAction();
                break;
        }
        return action;
    }


}
