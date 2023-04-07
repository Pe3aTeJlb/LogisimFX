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
import LogisimFX.util.LineBuffer;

public class DecoderHdlGeneratorFactory extends AbstractHdlGeneratorFactory {

	public DecoderHdlGeneratorFactory() {
		super();
		getWiresPortsDuringHDLWriting = true;
	}

	@Override
	public void getGenerationTimeWiresPorts(Netlist theNetlist, AttributeSet attrs) {
		final var nrOfselectBits = attrs.getValue(Plexers.ATTR_SELECT).getWidth();
		final var selectInputIndex = (1 << nrOfselectBits);
		for (var outp = 0; outp < selectInputIndex; outp++) {
			myPorts.add(Port.OUTPUT, String.format("decoderOut_%d", outp), 1, outp);
		}
		myPorts.add(Port.INPUT, "sel", nrOfselectBits, selectInputIndex);
		if (attrs.getValue(Plexers.ATTR_ENABLE))
			myPorts.add(Port.INPUT, "enable", 1, selectInputIndex + 1, false);
	}

	@Override
	public LineBuffer getModuleFunctionality(Netlist theNetList, AttributeSet attrs) {
		final var contents = LineBuffer.getBuffer();
		final var nrOfselectBits = attrs.getValue(Plexers.ATTR_SELECT).getWidth();
		final var numOutputs = (1 << nrOfselectBits);
		var space = " ";
		for (var i = 0; i < numOutputs; i++) {
			if (i == 10) space = "";
			contents.pair("bin", Hdl.getConstantVector(i, nrOfselectBits))
					.pair("i", i);
			contents.add("assign decoderOut_{{i}}{{1}} = (enable&(sel == {{bin}})) ? 1'b1 : 1'b0;", space);
		}
		return contents;
	}
}
