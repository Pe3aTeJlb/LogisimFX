/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.std.memory;

import com.cburch.LogisimFX.data.BitWidth;
import com.cburch.LogisimFX.data.Value;
import com.cburch.LogisimFX.instance.InstanceLogger;
import com.cburch.LogisimFX.instance.InstanceState;
import com.cburch.LogisimFX.instance.StdAttr;
import com.cburch.LogisimFX.std.LC;

public class ShiftRegisterLogger extends InstanceLogger {

	@Override
	public Object[] getLogOptions(InstanceState state) {

		Integer stages = state.getAttributeValue(ShiftRegister.ATTR_LENGTH);
		Object[] ret = new Object[stages.intValue()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = Integer.valueOf(i);
		}

		return ret;

	}
	
	@Override
	public String getLogName(InstanceState state, Object option) {

		String inName = state.getAttributeValue(StdAttr.LABEL);
		if (inName == null || inName.equals("")) {
			inName = LC.get("shiftRegisterComponent")
				+ state.getInstance().getLocation();
		}
		if (option instanceof Integer) {
			return inName + "[" + option + "]";
		} else {
			return inName;
		}

	}

	@Override
	public Value getLogValue(InstanceState state, Object option) {

		BitWidth dataWidth = state.getAttributeValue(StdAttr.WIDTH);
		if (dataWidth == null) dataWidth = BitWidth.create(0);
		ShiftRegisterData data = (ShiftRegisterData) state.getData();
		if (data == null) {
			return Value.createKnown(dataWidth, 0);
		} else {
			int index = option == null ? 0 : ((Integer) option).intValue(); 
			return data.get(index);
		}

	}

}
