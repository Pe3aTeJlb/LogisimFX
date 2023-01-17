/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.newgui.WaveformFrame;

import LogisimFX.circuit.CircuitState;
import LogisimFX.data.Value;

public interface Loggable {
	public Object[] getLogOptions(CircuitState state);
	public String getLogName(Object option);
	public Value getLogValue(CircuitState state, Object option);
}
