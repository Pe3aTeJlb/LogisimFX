/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * Original code by Carl Burch (http://www.cburch.com), 2011.
 * License information is located in the Launch file
 */

package LogisimFX.std.memory;

import LogisimFX.data.Value;
import LogisimFX.instance.Port;
import LogisimFX.instance.StdAttr;
import LogisimFX.std.LC;
import LogisimFX.util.LineBuffer;

public class DFlipFlop extends AbstractFlipFlop {

	private static class DFFHDLGeneratorFactory extends AbstractFlipFlopHdlGeneratorFactory {

		public DFFHDLGeneratorFactory() {
			super(1, StdAttr.TRIGGER);
			myPorts.add(Port.INPUT, "d", 1, 0);
		}

		@Override
		public LineBuffer getUpdateLogic() {
			return LineBuffer.getHdlBuffer().add("{{assign}}s_nextState {{=}} d;");
		}
	}

	public DFlipFlop() {

		super("D Flip-Flop", "dFlipFlop.gif",
				LC.createStringBinding("dFlipFlopComponent"), 1, true, new DFFHDLGeneratorFactory());

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
