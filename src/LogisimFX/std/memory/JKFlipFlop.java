/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.memory;

import LogisimFX.data.Value;
import LogisimFX.std.LC;

public class JKFlipFlop extends AbstractFlipFlop {

	public JKFlipFlop() {

		super("J-K Flip-Flop", "jkFlipFlop.gif",
				LC.createStringBinding("jkFlipFlopComponent"), 2, false);

	}

	@Override
	protected String getInputName(int index) {
		return index == 0 ? "J" : "K";
	}

	@Override
	protected Value computeValue(Value[] inputs, Value curValue) {

		if (inputs[0] == Value.FALSE) {
			if (inputs[1] == Value.FALSE) {
				return curValue;
			} else if (inputs[1] == Value.TRUE) {
				return Value.FALSE;
			}
		} else if (inputs[0] == Value.TRUE) {
			if (inputs[1] == Value.FALSE) {
				return Value.TRUE;
			} else if (inputs[1] == Value.TRUE) {
				return curValue.not();
			}
		}

		return Value.UNKNOWN;

	}
}
