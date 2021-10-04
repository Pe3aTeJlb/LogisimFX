/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package LogisimFX.newgui.CircLogFrame;

import LogisimFX.circuit.CircuitState;
import LogisimFX.data.Value;

public interface Loggable {
	public Object[] getLogOptions(CircuitState state);
	public String getLogName(Object option);
	public Value getLogValue(CircuitState state, Object option);
}
