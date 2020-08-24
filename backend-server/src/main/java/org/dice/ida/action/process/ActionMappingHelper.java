package org.dice.ida.action.process;

import org.dice.ida.action.def.*;
import org.dice.ida.model.Intent;

public class ActionMappingHelper {

    public static Action fetchActionInstance(String intentText) {
        Action action = null;
        // return the instance for requested action
        Intent intent = Intent.getForKey(intentText);
        switch (intent) {
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
