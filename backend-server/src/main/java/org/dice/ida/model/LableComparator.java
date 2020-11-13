package org.dice.ida.model;

import org.apache.commons.lang3.time.DateUtils;
import org.dice.ida.constant.IDAConst;

import java.text.ParseException;
import java.util.Comparator;

public enum LableComparator {

	DATESTRING(IDAConst.COMPARATOR_TYPE_DATE, (String date1, String date2) -> {
		try {
			return DateUtils.parseDateStrictly(date1, IDAConst.DATE_PATTERNS).compareTo(DateUtils.parseDateStrictly(date2, IDAConst.DATE_PATTERNS));
		} catch (ParseException e) {
			return -1;
		}
	}),
	MONTHANDYEAR(IDAConst.COMPARATOR_TYPE_MONTH, Comparator.comparing(IDAConst.MONTHS_LIST::indexOf)),
	YEARSTRING(IDAConst.COMPARATOR_TYPE_YEAR, Comparator.comparing(Integer::parseInt)),
	DOUBLESTRING(IDAConst.COMPARATOR_TYPE_DOUBLE, (String num1, String num2) -> {
		if (IDAConst.NULL_VALUE_IDENTIFIER.equals(num1)) {
			return 1;
		} else if (IDAConst.NULL_VALUE_IDENTIFIER.equals(num2)) {
			return -1;
		}
		try {
			Double.parseDouble(num1);
		} catch (NumberFormatException ex) {
			return 1;
		}
		try {
			return Double.compare(Double.parseDouble(num1), Double.parseDouble(num2));
		} catch (NumberFormatException ex) {
			return -1;
		}
	}),
	DOUBLEBIN(IDAConst.COMPARATOR_TYPE_DOUBLE_BIN, (String bin1, String bin2) -> {
		if (IDAConst.NULL_VALUE_IDENTIFIER.equals(bin1)) {
			return 1;
		} else if (IDAConst.NULL_VALUE_IDENTIFIER.equals(bin2)) {
			return -1;
		}
		try {
			Double.parseDouble(bin1.split(" - ")[0]);
		} catch (NumberFormatException ex) {
			return 1;
		}
		try {
			return Double.compare(Double.parseDouble(bin1.split(" - ")[0]), Double.parseDouble(bin2.split(" - ")[0]));
		} catch (NumberFormatException ex) {
			return -1;
		}
	}),
	DATEBIN(IDAConst.COMPARATOR_TYPE_DATE_BIN, (String bin1, String bin2) -> {
		if (IDAConst.NULL_VALUE_IDENTIFIER.equals(bin1)) {
			return 1;
		} else if (IDAConst.NULL_VALUE_IDENTIFIER.equals(bin2)) {
			return -1;
		}
		try {
			return DateUtils.parseDateStrictly(bin1.split(" - ")[0], IDAConst.DATE_PATTERNS).compareTo(DateUtils.parseDateStrictly(bin2.split(" - ")[0], IDAConst.DATE_PATTERNS));
		} catch (ParseException e) {
			return 1;
		}
	}),
	UNKNOWN(IDAConst.COMPARATOR_TYPE_UNKNOWN, Comparator.naturalOrder());

	private final String key;
	private final Comparator<String> comparator;

	LableComparator(String key, Comparator<String> comparator) {
		this.key = key;
		this.comparator = comparator;
	}

	public static Comparator<String> getForKey(String key) {
		for (LableComparator lableComparator : LableComparator.values()) {
			if (lableComparator.key.equalsIgnoreCase(key))
				return lableComparator.comparator;
		}
		return UNKNOWN.comparator;
	}

	public String getKey() {
		return key;
	}

	public Comparator<String> getComparator() {
		return comparator;
	}
}
