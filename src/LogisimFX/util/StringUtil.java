/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.util;

public class StringUtil {

	private StringUtil() { }
	
	public static String capitalize(String a) {
		return Character.toTitleCase(a.charAt(0)) + a.substring(1);
	}

	public static String format(String fmt, String... args){

		switch (args.length){

			case 1:
				return format(fmt, args[0], null, null);
			case 2:
				return format(fmt, args[0], args[1], null);
			case 3:
				return format(fmt, args[0],args[1],args[2]);
			default:
				return null;

		}

	}

	public static String format(String fmt, String a1) {
		return format(fmt, a1, null, null);
	}

	public static String format(String fmt, String a1, String a2) {
		return format(fmt, a1, a2, null);
	}

	public static String format(String fmt, String a1, String a2,
			String a3) {
		StringBuilder ret = new StringBuilder();
		if (a1 == null) a1 = "(null)";
		if (a2 == null) a2 = "(null)";
		if (a3 == null) a3 = "(null)";
		int arg = 0;
		int pos = 0;
		int next = fmt.indexOf('%');
		while (next >= 0) {
			ret.append(fmt.substring(pos, next));
			char c = fmt.charAt(next + 1);
			if (c == 's') {
				pos = next + 2;
				switch (arg) {
				case 0:     ret.append(a1); break;
				case 1:     ret.append(a2); break;
				default:    ret.append(a3);
				}
				++arg;
			} else if (c == '$') {
				switch (fmt.charAt(next + 2)) {
				case '1':   ret.append(a1); pos = next + 3; break;
				case '2':   ret.append(a2); pos = next + 3; break;
				case '3':   ret.append(a3); pos = next + 3; break;
				default:    ret.append("%$"); pos = next + 2;
				}
			} else if (c == '%') {
				ret.append('%'); pos = next + 2;
			} else {
				ret.append('%'); pos = next + 1;
			}
			next = fmt.indexOf('%', pos);
		}
		ret.append(fmt.substring(pos));
		return ret.toString();
	}

	public static String toHexString(int bits, int value) {
		if (bits < 32) value &= (1 << bits) - 1;
		StringBuilder ret = new StringBuilder(Integer.toHexString(value));
		int len = (bits + 3) / 4;
		while (ret.length() < len) ret.insert(0, "0");
		if (ret.length() > len) ret = new StringBuilder(ret.substring(ret.length() - len));
		return ret.toString();
	}

	/** Checks if given char sequence is either null or empty. */
	public static boolean isNullOrEmpty(CharSequence str) {
		return str == null || str.length() == 0;
	}

	/** Checks if given char sequence is not null and not empty. */
	public static boolean isNotEmpty(CharSequence seq) {
		return seq != null && seq.length() != 0;
	}

	/** Null safe version of `String.startsWith()` */
	public static boolean startsWith(String seq, String prefix) {
		return seq != null && seq.startsWith(prefix);
	}


}
