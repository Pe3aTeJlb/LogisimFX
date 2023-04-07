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

public class TFlipFlop extends AbstractFlipFlop {

	private static class TFFHDLGeneratorFactory extends AbstractFlipFlopHdlGeneratorFactory {

		public TFFHDLGeneratorFactory() {
			super(1, StdAttr.EDGE_TRIGGER);
			myPorts.add(Port.INPUT, "t", 1, 0);
		}

		@Override
		public LineBuffer getUpdateLogic() {
			return LineBuffer.getHdlBuffer().add("{{assign}}s_nextState{{=}}s_currentState{{xor}}t;");
		}
	}

	public TFlipFlop() {

		super("T Flip-Flop", "tFlipFlop.gif",
				LC.createStringBinding("tFlipFlopComponent"), 1, false, new TFFHDLGeneratorFactory());

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
