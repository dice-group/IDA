package org.dice.ida.util;

public class DbUtils {
	public static String manageNullValues(String value) {
		return value.equals("?") ? "<ida-unk>" : value;
	}
}
