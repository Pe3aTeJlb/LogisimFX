/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.instance;

import LogisimFX.data.Value;

public abstract class InstanceLogger {
	public Object[] getLogOptions(InstanceState state) { return null; }
	public abstract String getLogName(InstanceState state, Object option);
	public abstract Value getLogValue(InstanceState state, Object option);
}
