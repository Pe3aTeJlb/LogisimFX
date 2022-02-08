/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.memory;

import LogisimFX.data.Value;
import LogisimFX.std.LC;

public class SRFlipFlop extends AbstractFlipFlop {

	public SRFlipFlop() {

		super("S-R Flip-Flop", "srFlipFlop.gif",
				LC.createStringBinding("srFlipFlopComponent"), 2, true);

	}

	@Override
	protected String getInputName(int index) {
		return index == 0 ? "S" : "R";
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
				return Value.ERROR;
			}
		}

		return Value.UNKNOWN;

	}

}
