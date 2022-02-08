/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

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