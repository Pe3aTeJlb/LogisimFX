/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package LogisimFX.util;

import java.util.Locale;

public interface LocaleListener {
	public void localeChanged();
	public void localeChanged(Locale locale);
}
