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
import LogisimFX.fpga.hdlgenerator.HdlParameters;
import LogisimFX.instance.Port;
import LogisimFX.util.LineBuffer;

public class BitSelectorHdlGeneratorFactory extends AbstractHdlGeneratorFactory {

	private static final String INPUT_BITS_STRING = "nrOfInputBits";
	private static final int INPUT_BITS_ID = -1;
	private static final String OUTPUTS_BITS_STRING = "nrOfOutputBits";
	private static final int OUTPUT_BITS_ID = -2;
	private static final String SELECT_BITS_STRING = "nrOfselBits";
	private static final int SELECT_BITS_ID = -3;
	private static final String EXTENDED_BITS_STRING = "nrOfExtendedBits";
	private static final int EXTENDED_BITS_ID = -4;

	public BitSelectorHdlGeneratorFactory() {
		super();
		myParametersList
				.add(SELECT_BITS_STRING, SELECT_BITS_ID, HdlParameters.MAP_INT_ATTRIBUTE, BitSelector.SELECT_ATTR)
				.add(INPUT_BITS_STRING, INPUT_BITS_ID)
				.add(EXTENDED_BITS_STRING, EXTENDED_BITS_ID, HdlParameters.MAP_INT_ATTRIBUTE, BitSelector.EXTENDED_ATTR)
				.addBusOnly(BitSelector.GROUP_ATTR, OUTPUTS_BITS_STRING, OUTPUT_BITS_ID);
		myWires
				.addWire("s_extendedVector", EXTENDED_BITS_ID);
		myPorts
				.add(Port.INPUT, "dataIn", INPUT_BITS_ID, 1)
				.add(Port.INPUT, "sel", SELECT_BITS_ID, 2)
				.add(Port.OUTPUT, "dataOut", OUTPUT_BITS_ID, 0, BitSelector.GROUP_ATTR);
	}

	@Override
	public LineBuffer getModuleFunctionality(Netlist theNetlist, AttributeSet attrs) {
		final var contents =
				LineBuffer.getBuffer()
						.pair("extBits", EXTENDED_BITS_STRING)
						.pair("inBits", INPUT_BITS_STRING)
						.pair("outBits", OUTPUTS_BITS_STRING);
		final var outputBits = attrs.getValue(BitSelector.GROUP_ATTR).getWidth();

		contents.add(
						"assign s_extendedVector[{{extBits}}-1:{{inBits}}] = 0;\n" +
						"assign s_extendedVector[{{inBits}}-1:0] = dataIn;"
		);

		if (outputBits > 1) {
			contents.add(
							"wire[513:0] s_selectVector;\n" +
							"reg[{{outBits}}-1:0] s_selected_slice;\n" +
							"assign s_selectVector[513:{{extBits}}] = 0;\n" +
							"assign s_selectVector[{{extBits}}-1:0] = s_extendedVector;\n" +
							"assign dataOut = s_selected_slice;\n\n" +
							"always @(*)\n" +
							"begin\n" +
							"    case (sel)"
			);
			for (var i = 15; i > 0; i--) {
				contents.add("{{1}}{{2}} : s_selected_slice <= s_selectVector[({{3}}*{{outBits}})-1:{{2}}*{{outBits}}];", LineBuffer.getIndent(2), i, (i + 1));
			}
			contents.add(
							"        default : s_selected_slice <= s_selectVector[{{outBits}}-1:0];\n" +
							"    endcase\n" +
							"end"
			);
		} else {
			contents.add("assign dataOut = s_extendedVector[sel];");
		}

		return contents.empty();
	}
}
