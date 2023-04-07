/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package LogisimFX.std.plexers;

import LogisimFX.data.AttributeSet;
import LogisimFX.fpga.designrulecheck.Netlist;
import LogisimFX.fpga.hdlgenerator.AbstractHdlGeneratorFactory;
import LogisimFX.fpga.hdlgenerator.Hdl;
import LogisimFX.instance.Port;
import LogisimFX.instance.StdAttr;
import LogisimFX.util.LineBuffer;

public class DemultiplexerHdlGeneratorFactory extends AbstractHdlGeneratorFactory {

	private static final String NR_OF_BITS_STRING = "nrOfBits";
	private static final int NR_OF_BITS_ID = -1;

	public DemultiplexerHdlGeneratorFactory() {
		super();
		myParametersList.addBusOnly(NR_OF_BITS_STRING, NR_OF_BITS_ID);
		getWiresPortsDuringHDLWriting = true;
	}

	@Override
	public void getGenerationTimeWiresPorts(Netlist theNetlist, AttributeSet attrs) {
		final var nrOfSelectBits = attrs.getValue(Plexers.ATTR_SELECT).getWidth();
		final var nrOfBits = attrs.getValue(StdAttr.WIDTH).getWidth() == 1 ? 1 : NR_OF_BITS_ID;
		final var selectInputIndex = (1 << nrOfSelectBits);
		final var hasenable = attrs.getValue(Plexers.ATTR_ENABLE);
		for (var outp = 0; outp < selectInputIndex; outp++) {
			myPorts.add(Port.OUTPUT, String.format("demuxOut_%d", outp), nrOfBits, outp, StdAttr.WIDTH);
		}
		myPorts
				.add(Port.INPUT, "sel", nrOfSelectBits, selectInputIndex)
				.add(
						Port.INPUT,
						"demuxIn",
						nrOfBits,
						hasenable ? selectInputIndex + 2 : selectInputIndex + 1);
		if (hasenable) myPorts.add(Port.INPUT, "enable", 1, selectInputIndex + 1, false);
		else myPorts.add(Port.INPUT, "enable", 1, Hdl.oneBit());
	}

	@Override
	public LineBuffer getModuleFunctionality(Netlist theNetList, AttributeSet attrs) {
		final var contents = LineBuffer.getBuffer();
		var space = "  ";
		final var nrOfSelectBits = attrs.getValue(Plexers.ATTR_SELECT).getWidth();
		var numOutputs = (1 << nrOfSelectBits);
		for (var i = 0; i < numOutputs; i++) {
			if (i == 10) space = " ";
			final var binValue = Hdl.getConstantVector(i, nrOfSelectBits);
			contents.add(
					"assign demuxOut_{{1}}{{2}} = (enable&(sel == {{3}} )) ? demuxIn : 0;",
					i, space, binValue);
		}
		return contents;
	}
}
