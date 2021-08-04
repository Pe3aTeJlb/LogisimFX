/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.std.memory;

import com.cburch.LogisimFX.instance.InstanceData;

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

}