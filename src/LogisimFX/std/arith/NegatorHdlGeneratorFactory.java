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
import LogisimFX.instance.Port;
import LogisimFX.instance.StdAttr;
import LogisimFX.util.LineBuffer;

public class NegatorHdlGeneratorFactory extends AbstractHdlGeneratorFactory {

	private static final String NR_OF_BITS_STRING = "nrOfBits";
	private static final int NR_OF_BITS_ID = -1;

	public NegatorHdlGeneratorFactory() {
		super();
		myParametersList.addBusOnly(NR_OF_BITS_STRING, NR_OF_BITS_ID);
		myPorts
				.add(Port.INPUT, "dataX", NR_OF_BITS_ID, Negator.IN, StdAttr.WIDTH)
				.add(Port.OUTPUT, "minDataX", NR_OF_BITS_ID, Negator.OUT, StdAttr.WIDTH);
	}

	@Override
	public LineBuffer getModuleFunctionality(Netlist TheNetlist, AttributeSet attrs) {
		final var contents = LineBuffer.getBuffer();
		contents.add("assign minDataX = -dataX;");
		return contents.empty();
	}
}
