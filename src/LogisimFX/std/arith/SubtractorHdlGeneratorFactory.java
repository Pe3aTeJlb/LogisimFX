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
import LogisimFX.fpga.hdlgenerator.HdlParameters;
import LogisimFX.instance.Port;
import LogisimFX.instance.StdAttr;
import LogisimFX.util.LineBuffer;

public class SubtractorHdlGeneratorFactory extends AbstractHdlGeneratorFactory {

	private static final String NR_OF_BITS_STRING = "nrOfBits";
	private static final int NR_OF_BITS_ID = -1;
	private static final String EXTENDED_BITS_STRING = "extendedBits";
	private static final int EXTENDED_BITS_ID = -2;

	public SubtractorHdlGeneratorFactory() {
		super();
		myParametersList
				.addBusOnly(NR_OF_BITS_STRING, NR_OF_BITS_ID)
				.add(EXTENDED_BITS_STRING, EXTENDED_BITS_ID, HdlParameters.MAP_OFFSET, 1);
		myWires
				.addWire("s_extendeddataA", EXTENDED_BITS_ID)
				.addWire("s_extendeddataB", EXTENDED_BITS_ID)
				.addWire("s_sumresult", EXTENDED_BITS_ID)
				.addWire("s_carry", 1);
		myPorts
				.add(Port.INPUT, "dataA", NR_OF_BITS_ID, Subtractor.IN0, StdAttr.WIDTH)
				.add(Port.INPUT, "dataB", NR_OF_BITS_ID, Subtractor.IN1, StdAttr.WIDTH)
				.add(Port.INPUT, "borrowIn", 1, Subtractor.B_IN)
				.add(Port.OUTPUT, "result", NR_OF_BITS_ID, Subtractor.OUT, StdAttr.WIDTH)
				.add(Port.OUTPUT, "borrowOut", 1, Subtractor.B_OUT);
	}

	@Override
	public LineBuffer getModuleFunctionality(Netlist TheNetlist, AttributeSet attrs) {
		final var contents = LineBuffer.getBuffer();
		final var nrOfBits = attrs.getValue(StdAttr.WIDTH).getWidth();
		contents.add(
				"assign {s_carry,result} = dataA + ~(dataB) + ~(borrowIn);\n" +
						"assign borrowOut                            = ~s_carry;"
		);
		return contents.empty();
	}
}
