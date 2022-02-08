/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.util;

class Strings {
	static LocaleManager source
		= new LocaleManager("resources/logisim", "util");

	public static LocaleManager getLocaleManager() {
		return source;
	}

}
