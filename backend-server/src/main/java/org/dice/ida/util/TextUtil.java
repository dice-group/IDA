package org.dice.ida.util;

import org.apache.commons.text.similarity.LevenshteinDistance;

public class TextUtil {
	public static boolean matchString(String first, String second) {
		LevenshteinDistance d = new LevenshteinDistance();
		return d.apply(first.toLowerCase(), second.toLowerCase()) <= 1;
	}

}
