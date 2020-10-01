package org.dice.ida.util;

import org.dice.ida.constant.IDAConst;

public class DbUtils {
	public static String manageNullValues(String value) {
		return (value == null || ValidatorUtil.isStringEmpty(value) || value.equals("?")) ? IDAConst.NULL_VALUE_IDENTIFIER : value;
	}
}
