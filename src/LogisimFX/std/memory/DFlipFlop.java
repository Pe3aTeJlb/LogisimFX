/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.memory;

import LogisimFX.data.Value;
import LogisimFX.std.LC;

public class DFlipFlop extends AbstractFlipFlop {

	public DFlipFlop() {

		super("D Flip-Flop", "dFlipFlop.gif",
				LC.createStringBinding("dFlipFlopComponent"), 1, true);

	}

	@Override
	protected String getInputName(int index) {
		return "D";
	}

	@Override
	protected Value computeValue(Value[] inputs, Value curValue) {
		return inputs[0];
	}

}
