/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.memory;

import LogisimFX.data.Value;
import LogisimFX.std.LC;

public class TFlipFlop extends AbstractFlipFlop {

	public TFlipFlop() {

		super("T Flip-Flop", "tFlipFlop.gif",
				LC.createStringBinding("tFlipFlopComponent"), 1, false);

	}

	@Override
	protected String getInputName(int index) {
		return "T";
	}

	@Override
	protected Value computeValue(Value[] inputs, Value curValue) {

		if (curValue == Value.UNKNOWN) curValue = Value.FALSE;
		if (inputs[0] == Value.TRUE) {
			return curValue.not();
		} else {
			return curValue;
		}

	}

}
