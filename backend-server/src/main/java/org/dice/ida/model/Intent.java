package org.dice.ida.model;

import org.dice.ida.constant.IDAConst;

public enum Intent {
    GREETING("greeting", IDAConst.UAC_NRMLMSG),
    HELP("help", IDAConst.UAC_NRMLMSG),
    UPLOAD_DATASET("upload-dataset", IDAConst.UAC_UPLDDTMSG),
    LOAD_DATASET("load-dataset", IDAConst.UIA_LOADDS),
    LIST_DATASET("list-dataset", IDAConst.UAC_NRMLMSG),
	LIST_VISUALIZATION("list-visualization", IDAConst.UAC_NRMLMSG),
    SUGGEST_VISUALIZATION("suggest-visualization", IDAConst.UAC_NRMLMSG),
	LINE_CHART("line-chart", IDAConst.UIA_LINECHART),
	BARCHART("bar_chart",IDAConst.UIA_BARGRAPH),
	BUBBLECHART("bubble_chart", IDAConst.UIA_BUBBLECHART),
	CLUSTERING("clustering",IDAConst.UAC_NRMLMSG),
	NEXT_STEPS("next-steps", IDAConst.UAC_NRMLMSG),
	CLEAR("clear_context", IDAConst.UAC_NRMLMSG),
    UNKNOWN("unknown", IDAConst.UAC_NRMLMSG);

    private final String key;
    private final int action;

    Intent(String key, int action) {
        this.key = key;
        this.action = action;
    }

    public static Intent getForKey(String key) {
		// Follow up intent management
		String intentKey = key.contains(" - ")  ? key.split(" - ")[0] : key;

        for (Intent intent : Intent.values()) {
            if (intent.key.equalsIgnoreCase(intentKey))
                return intent;
        }
        return UNKNOWN;
    }

    public String getKey() {
        return key;
    }

    public int getAction() {
        return action;
    }
}
