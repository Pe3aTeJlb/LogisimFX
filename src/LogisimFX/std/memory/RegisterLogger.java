/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.memory;

import LogisimFX.data.BitWidth;
import LogisimFX.data.Value;
import LogisimFX.instance.InstanceLogger;
import LogisimFX.instance.InstanceState;
import LogisimFX.instance.StdAttr;

public class RegisterLogger extends InstanceLogger {

	@Override
	public String getLogName(InstanceState state, Object option) {

		String ret = state.getAttributeValue(StdAttr.LABEL);
		return ret != null && !ret.equals("") ? ret : null;

	}

	@Override
	public Value getLogValue(InstanceState state, Object option) {

		BitWidth dataWidth = state.getAttributeValue(StdAttr.WIDTH);
		if (dataWidth == null) dataWidth = BitWidth.create(0);
		RegisterData data = (RegisterData) state.getData();
		if (data == null) return Value.createKnown(dataWidth, 0);
		return Value.createKnown(dataWidth, data.value);

	}

}
