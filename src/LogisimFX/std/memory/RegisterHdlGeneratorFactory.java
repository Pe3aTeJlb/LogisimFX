/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package LogisimFX.std.memory;

import LogisimFX.data.AttributeSet;
import LogisimFX.fpga.designrulecheck.Netlist;
import LogisimFX.fpga.designrulecheck.netlistComponent;
import LogisimFX.fpga.hdlgenerator.AbstractHdlGeneratorFactory;
import LogisimFX.fpga.hdlgenerator.Hdl;
import LogisimFX.fpga.hdlgenerator.HdlParameters;
import LogisimFX.fpga.hdlgenerator.HdlPorts;
import LogisimFX.instance.Port;
import LogisimFX.instance.StdAttr;
import LogisimFX.util.LineBuffer;

import java.util.SortedMap;
import java.util.TreeMap;

public class RegisterHdlGeneratorFactory extends AbstractHdlGeneratorFactory {

	private static final String NR_OF_BITS_STRING = "nrOfBits";
	private static final int NR_OF_BITS_Id = -1;
	private static final String INVERT_CLOCK_STRING = "invertClock";
	private static final int INVERT_CLOCK_Id = -2;

	public RegisterHdlGeneratorFactory() {
		super();
		myParametersList
				.add(NR_OF_BITS_STRING, NR_OF_BITS_Id)
				.add(INVERT_CLOCK_STRING, INVERT_CLOCK_Id, HdlParameters.MAP_ATTRIBUTE_OPTION, StdAttr.TRIGGER, AbstractFlipFlopHdlGeneratorFactory.TRIGGER_MAP);
		myWires
				.addWire("s_clock", 1)
				.addRegister("s_currentState", NR_OF_BITS_Id);
		myPorts
				.add(Port.CLOCK, HdlPorts.getClockName(1), 1, Register.CK)
				.add(Port.INPUT, "reset", 1, Register.CLR)
				.add(Port.INPUT, "clockEnable", 1, Register.EN, false)
				.add(Port.INPUT, "d", NR_OF_BITS_Id, Register.IN)
				.add(Port.OUTPUT, "q", NR_OF_BITS_Id, Register.OUT);
	}

	@Override
	public SortedMap<String, String> getPortMap(Netlist Nets, Object MapInfo) {
		return new TreeMap<>(super.getPortMap(Nets, MapInfo));
	}

	@Override
	public LineBuffer getModuleFunctionality(Netlist nets, AttributeSet attrs) {
		final var contents = LineBuffer.getBuffer()
				.pair("invertClock", INVERT_CLOCK_STRING)
				.pair("clock", HdlPorts.getClockName(1))
				.pair("Tick", HdlPorts.getTickName(1));

		contents.empty().add(
				"assign q = s_currentState;\n" +
						"assign s_clock = {{invertClock}} == 0 ? {{clock}} : ~{{clock}};"
		).empty();
		if (Netlist.isFlipFlop(attrs)) {
			contents.add(
							"always @(posedge s_clock or posedge reset)\n" +
							"begin\n" +
							"    if (reset) s_currentState <= 0;\n" +
							"    else if (clockEnable&{{Tick}}) s_currentState <= d;\n" +
							"end"
			);
		} else {
			contents.add(
							"always @(*)\n" +
							"begin\n" +
							"    if (reset) s_currentState <= 0;\n" +
							"    else if (s_Clock&clockEnable&{{Tick}}) s_currentState <= d;\n" +
							"end"
			);
		}

		return contents.empty();
	}
}
