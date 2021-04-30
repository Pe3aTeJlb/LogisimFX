/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.data;

import com.cburch.LogisimFX.LogisimVersion;

public interface AttributeDefaultProvider {
	public boolean isAllDefaultValues(AttributeSet attrs, LogisimVersion ver);
	public Object getDefaultAttributeValue(Attribute<?> attr, LogisimVersion ver);
}
