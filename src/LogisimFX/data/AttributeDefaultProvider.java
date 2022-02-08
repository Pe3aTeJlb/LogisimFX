/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.data;

import LogisimFX.LogisimVersion;

public interface AttributeDefaultProvider {
	public boolean isAllDefaultValues(AttributeSet attrs, LogisimVersion ver);
	public Object getDefaultAttributeValue(Attribute<?> attr, LogisimVersion ver);
}
