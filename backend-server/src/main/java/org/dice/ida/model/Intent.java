package org.dice.ida.model;

import org.dice.ida.constant.IDAConst;

public enum Intent {
    GREETING("greeting", IDAConst.UAC_NrmlMsg),
    HELP("help", IDAConst.UAC_NrmlMsg),
    UPLOAD_DATASET("upload-dataset", IDAConst.UAC_UpldDtMsg),
    UNKNOWN("unknown", IDAConst.UAC_NrmlMsg);

    private final String key;
    private final int action;

    Intent(String key, int action) {
        this.key = key;
        this.action = action;
    }

    public static Intent getForKey(String key) {
        for (Intent intent : Intent.values()) {
            if (intent.key.equalsIgnoreCase(key))
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