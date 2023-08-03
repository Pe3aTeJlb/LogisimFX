/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package LogisimFX.std.memory;

import LogisimFX.data.Attribute;
import LogisimFX.data.AttributeOption;
import LogisimFX.data.AttributeSet;
import LogisimFX.fpga.designrulecheck.Netlist;
import LogisimFX.fpga.hdlgenerator.AbstractHdlGeneratorFactory;
import LogisimFX.fpga.hdlgenerator.HdlParameters;
import LogisimFX.fpga.hdlgenerator.HdlPorts;
import LogisimFX.instance.Port;
import LogisimFX.instance.StdAttr;
import LogisimFX.util.LineBuffer;

import java.util.HashMap;
import java.util.Map;

public class AbstractFlipFlopHdlGeneratorFactory extends AbstractHdlGeneratorFactory {

	private static final String INVERT_CLOCK_STRING = "invertClockEnable";
	private static final int INVERT_CLOCK_ID = -1;
	private final int nrOfInputs;

	public static final Map<AttributeOption, Integer> TRIGGER_MAP = new HashMap<>() {{
		put(StdAttr.TRIG_HIGH, 0);
		put(StdAttr.TRIG_LOW, 1);
		put(StdAttr.TRIG_FALLING, 1);
		put(StdAttr.TRIG_RISING, 0);
	}};

	public AbstractFlipFlopHdlGeneratorFactory(int numInputs, Attribute<AttributeOption> triggerAttr) {
		super();
		nrOfInputs = numInputs;
		myParametersList
				.add(INVERT_CLOCK_STRING, INVERT_CLOCK_ID, HdlParameters.MAP_ATTRIBUTE_OPTION, triggerAttr, TRIGGER_MAP);
		myWires
				.addWire("s_clock", 1)
				.addWire("s_nextState", 1)
				.addRegister("s_currentState", 1);
		myPorts
				.add(Port.INPUT, "reset", 1, nrOfInputs + 3)
				.add(Port.INPUT, "preset", 1, nrOfInputs + 4)
				.add(Port.CLOCK, HdlPorts.CLOCK, 1, nrOfInputs)
				.add(Port.OUTPUT, "q", 1, nrOfInputs + 1)
				.add(Port.OUTPUT, "qBar", 1, nrOfInputs + 2);
	}

	@Override
	public LineBuffer getModuleFunctionality(Netlist nets, AttributeSet attrs) {
		final var contents = LineBuffer.getHdlBuffer();
		contents
				.pair("invertClock", INVERT_CLOCK_STRING)
				.pair("Clock", HdlPorts.CLOCK)
				.pair("Tick", HdlPorts.TICK)
				.empty()
				.addRemarkBlock("Here the output signals are defined")
				.add(
								"{{assign}}q       {{=}}s_currentState;\n" +
								"{{assign}}qBar    {{=}}{{not}}(s_currentState);"
				);
		contents
				.add("assign s_clock {{=}}({{invertClock}} == 0) ? {{Clock}} : ~{{Clock}};")
				.empty()
				.addRemarkBlock("Here the initial register value is defined; for simulation only")
				.add(
								"initial\n" +
								"begin\n" +
								"    s_currentState = 0;\n" +
								"end\n"
				).empty();
		contents
				.addRemarkBlock("Here the update logic is defined")
				.add(getUpdateLogic())
				.empty()
				.addRemarkBlock("Here the actual state register is defined");

		if (Netlist.isFlipFlop(attrs)) {
			contents.add(
					"always @(posedge reset or posedge preset or posedge s_clock)\n" +
							"begin\n" +
							"    if (reset) s_currentState <= 1'b0;\n" +
							"    else if (preset) s_currentState <= 1'b1;\n" +
							"    else if ({{Tick}}) s_currentState <= s_nextState;\n" +
							"end\n"
			);
		} else {
			contents.add(
					"always @(*)\n" +
							"begin\n" +
							"    if (reset) s_currentState <= 1'b0;\n" +
							"    else if (preset) s_currentState <= 1'b1;\n" +
							"    else if ({{Tick}} & (s_clock == 1'b1)) s_currentState <= s_nextState;" +
							"end\n"
			);
		}

		return contents.empty();
	}

	public LineBuffer getUpdateLogic() {
		return LineBuffer.getHdlBuffer();
	}
}
