/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package LogisimFX.instance;

import LogisimFX.comp.ComponentState;

public interface InstanceData extends ComponentState {
	public Object clone();
}
