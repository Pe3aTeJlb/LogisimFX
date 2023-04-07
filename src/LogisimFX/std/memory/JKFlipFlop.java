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

public class JKFlipFlop extends AbstractFlipFlop {

	private static class JKFFHDLGeneratorFactory extends AbstractFlipFlopHdlGeneratorFactory {

		public JKFFHDLGeneratorFactory() {
			super(2, StdAttr.EDGE_TRIGGER);
			myPorts.add(Port.INPUT, "j", 1, 0).add(Port.INPUT, "k", 1, 1);
		}

		@Override
		public LineBuffer getUpdateLogic() {
			final var contents = LineBuffer.getHdlBuffer();
			final var preamble = LineBuffer.formatHdl("{{assign}}s_nextState{{=}}");
			contents
					.add("{{1}}({{not}}(s_currentState){{and}}j){{or}}", preamble)
					.add("{{1}}(s_currentState{{and}}{{not}}(k));", " ".repeat(preamble.length()));
			return contents;
		}
	}

	public JKFlipFlop() {

		super("J-K Flip-Flop", "jkFlipFlop.gif",
				LC.createStringBinding("jkFlipFlopComponent"), 2, false, new JKFFHDLGeneratorFactory());

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
