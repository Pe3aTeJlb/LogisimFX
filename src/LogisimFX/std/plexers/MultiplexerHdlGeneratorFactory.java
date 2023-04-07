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

public class MultiplexerHdlGeneratorFactory extends AbstractHdlGeneratorFactory {

	private static final String NR_OF_BITS_STRING = "nrOfBits";
	private static final int NR_OF_BITS_ID = -1;

	public MultiplexerHdlGeneratorFactory() {
		super();
		myParametersList.addBusOnly(NR_OF_BITS_STRING, NR_OF_BITS_ID);
		getWiresPortsDuringHDLWriting = true;
	}

	@Override
	public void getGenerationTimeWiresPorts(Netlist theNetlist, AttributeSet attrs) {
		final var nrOfSelectBits = attrs.getValue(Plexers.ATTR_SELECT).getWidth();
		final var selectInputIndex = (1 << nrOfSelectBits);
		final var hasenable = attrs.getValue(Plexers.ATTR_ENABLE);
		for (var inp = 0; inp < selectInputIndex; inp++)
			myPorts.add(Port.INPUT, String.format("muxIn_%d", inp), NR_OF_BITS_ID, inp, StdAttr.WIDTH);
		myPorts
				.add(Port.INPUT, "sel", nrOfSelectBits, selectInputIndex)
				.add(Port.OUTPUT, "muxOut", NR_OF_BITS_ID, hasenable ? selectInputIndex + 2 : selectInputIndex + 1, StdAttr.WIDTH);
		if (hasenable)
			myPorts.add(Port.INPUT, "enable", 1, selectInputIndex + 1);
		else
			myPorts.add(Port.INPUT, "enable", 1, Hdl.oneBit());
	}

	@Override
	public LineBuffer getModuleFunctionality(Netlist theNetList, AttributeSet attrs) {
		final var contents = LineBuffer.getBuffer();
		final var nrOfSelectBits = attrs.getValue(Plexers.ATTR_SELECT).getWidth();
		final var nrOfBits = attrs.getValue(StdAttr.WIDTH).getWidth();

		if (nrOfBits == 1)
			contents.add("reg s_selected_vector;");
		else
			contents.add("reg [{{1}}:0] s_selected_vector;", NR_OF_BITS_STRING);
		contents.add(
						"assign muxOut = s_selected_vector;\n\n" +
						"always @(*)\n" +
						"begin\n" +
						"    if (~enable) s_selected_vector <= 0;\n" +
						"    else case (sel)"

		);
		for (var i = 0; i < (1 << nrOfSelectBits) - 1; i++) {
			contents
					.add("        {{1}}:", Hdl.getConstantVector(i, nrOfSelectBits))
					.add("        s_selected_vector <= muxIn_{{1}};", i);
		}
		contents
				.add("        default:")
				.add("            s_selected_vector <= muxIn_{{1}};", (1 << nrOfSelectBits) - 1)
				.add("        endcase")
				.add("end");

		return contents.empty();
	}
}
