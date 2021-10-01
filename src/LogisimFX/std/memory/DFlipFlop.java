/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

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
