/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package LogisimFX.std.memory;

import LogisimFX.data.BitWidth;
import LogisimFX.data.Value;
import LogisimFX.instance.InstanceData;

public class RegisterData extends ClockState implements InstanceData {

	int value;

	public RegisterData() {
		value = 0;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}

	public Value getByIndex(int index, int datalen){

		//int bit = (value >> (datalen-index-1)) & 1;
		int bit = (value >> index) & 1;
		return Value.createKnown(BitWidth.create(1), bit);

	}

}