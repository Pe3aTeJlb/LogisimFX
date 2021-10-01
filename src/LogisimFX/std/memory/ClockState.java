/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package LogisimFX.std.memory;

import LogisimFX.data.Value;
import LogisimFX.instance.StdAttr;

public class ClockState implements Cloneable {

	private Value lastClock;
	
	public ClockState() {
		lastClock = Value.FALSE;
	}
	
	@Override
	public ClockState clone() {

		try {
			return (ClockState) super.clone();
		} catch (CloneNotSupportedException e) { return null; }

	}
		
	public boolean updateClock(Value newClock, Object trigger) {

		Value oldClock = lastClock;
		lastClock = newClock;
		if (trigger == null || trigger == StdAttr.TRIG_RISING) {
			return oldClock == Value.FALSE && newClock == Value.TRUE;
		} else if (trigger == StdAttr.TRIG_FALLING) {
			return oldClock == Value.TRUE && newClock == Value.FALSE;
		} else if (trigger == StdAttr.TRIG_HIGH) {
			return newClock == Value.TRUE;
		} else if (trigger == StdAttr.TRIG_LOW) {
			return newClock == Value.FALSE;
		} else {
			return oldClock == Value.FALSE && newClock == Value.TRUE;
		}

	}

}
