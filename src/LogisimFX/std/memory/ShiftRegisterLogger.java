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
import LogisimFX.std.LC;

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

		if(option instanceof Integer) {
			BitWidth dataWidth = state.getAttributeValue(StdAttr.WIDTH);
			if (dataWidth == null) dataWidth = BitWidth.create(0);
			ShiftRegisterData data = (ShiftRegisterData) state.getData();
			if (data == null) {
				return Value.createKnown(dataWidth, 0);
			} else {
				int index = option == null ? 0 : ((Integer) option).intValue();
				return data.get(index);
			}
		}else{
			BitWidth dataWidth = BitWidth.create(state.getAttributeValue(ShiftRegister.ATTR_LENGTH));
			if (dataWidth == null) dataWidth = BitWidth.create(0);
			ShiftRegisterData data = (ShiftRegisterData) state.getData();
			if (data == null) return Value.createKnown(dataWidth, 0);
			return Value.createKnown(dataWidth, data.getAsInt());
		}

	}

}
