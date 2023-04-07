/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package LogisimFX.std.arith;

import LogisimFX.data.AttributeSet;
import LogisimFX.fpga.designrulecheck.Netlist;
import LogisimFX.fpga.hdlgenerator.AbstractHdlGeneratorFactory;
import LogisimFX.fpga.hdlgenerator.Hdl;
import LogisimFX.fpga.hdlgenerator.HdlParameters;
import LogisimFX.instance.Port;
import LogisimFX.instance.StdAttr;
import LogisimFX.util.LineBuffer;

public class AdderHdlGeneratorFactory extends AbstractHdlGeneratorFactory {

	private static final String NR_OF_BITS_STRING = "nrOfBits";
	private static final int NR_OF_BITS_ID = -1;
	private static final String EXTENDED_BITS_STRING = "extendedBits";
	private static final int EXTENDED_BITS_ID = -2;

	public AdderHdlGeneratorFactory() {
		super();
		myParametersList
				.add(EXTENDED_BITS_STRING, EXTENDED_BITS_ID, HdlParameters.MAP_OFFSET, 1)
				.addBusOnly(NR_OF_BITS_STRING, NR_OF_BITS_ID);
		myWires
				.addWire("s_extendedDataA", EXTENDED_BITS_ID)
				.addWire("s_extendedDataB", EXTENDED_BITS_ID)
				.addWire("s_sumResult", EXTENDED_BITS_ID);
		myPorts
				.add(Port.INPUT, "dataA", NR_OF_BITS_ID, Adder.IN0, StdAttr.WIDTH)
				.add(Port.INPUT, "dataB", NR_OF_BITS_ID, Adder.IN1, StdAttr.WIDTH)
				.add(Port.INPUT, "carryIn", 1, Adder.C_IN)
				.add(Port.OUTPUT, "result", NR_OF_BITS_ID, Adder.OUT, StdAttr.WIDTH)
				.add(Port.OUTPUT, "carryOut", 1, Adder.C_OUT);
	}

	@Override
	public LineBuffer getModuleFunctionality(Netlist theNetlist, AttributeSet attrs) {
		final var contents = LineBuffer.getBuffer();
		contents.add("assign   {carryOut, result} = dataA + dataB + carryIn;");
		return contents.empty();
	}
}
