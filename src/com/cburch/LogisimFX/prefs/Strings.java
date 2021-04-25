/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.prefs;

import com.cburch.LogisimFX.util.LocaleManager;
import com.cburch.LogisimFX.util.StringGetter;
import com.cburch.LogisimFX.util.StringUtil;

class Strings {
	private static LocaleManager source
		= new LocaleManager("resources/logisim", "proj");

	public static String get(String key) {
		return source.get(key);
	}
	public static String get(String key, String arg) {
		return StringUtil.format(source.get(key), arg);
	}
	public static StringGetter getter(String key) {
		return source.getter(key);
	}
}
