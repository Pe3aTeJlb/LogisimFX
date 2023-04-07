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
import LogisimFX.util.LineBuffer;

public class MultiplierHdlGeneratorFactory extends AbstractHdlGeneratorFactory {

	private static final String NR_OF_BITS_STRING = "nrOfBits";
	private static final int NR_OF_BITS_ID = -1;
	private static final String CALC_BITS_STRING = "calcBits";
	private static final int CALC_BITS_ID = -2;
	private static final String UNSIGNED_STRING = "unsignedMultiplier";
	private static final int UNSIGNED_ID = -3;

	public MultiplierHdlGeneratorFactory() {
		super();
		myParametersList
				.add(NR_OF_BITS_STRING, NR_OF_BITS_ID)
				.add(CALC_BITS_STRING, CALC_BITS_ID, HdlParameters.MAP_MULTIPLY, 2)
				.add(UNSIGNED_STRING, UNSIGNED_ID, HdlParameters.MAP_ATTRIBUTE_OPTION, Comparator.MODE_ATTRIBUTE, ComparatorHdlGeneratorFactory.SIGNED_MAP);
		myWires
				.addWire("s_multResult", CALC_BITS_ID)
				.addWire("s_extendedcarryIn", CALC_BITS_ID)
				.addWire("s_newResult", CALC_BITS_ID);
		myPorts
				.add(Port.INPUT, "inputA", NR_OF_BITS_ID, Multiplier.IN0)
				.add(Port.INPUT, "inputB", NR_OF_BITS_ID, Multiplier.IN1)
				.add(Port.INPUT, "carryIn", NR_OF_BITS_ID, Multiplier.C_IN)
				.add(Port.OUTPUT, "multLow", NR_OF_BITS_ID, Multiplier.OUT)
				.add(Port.OUTPUT, "multHigh", NR_OF_BITS_ID, Multiplier.C_OUT);
	}

	@Override
	public LineBuffer getModuleFunctionality(Netlist TheNetlist, AttributeSet attrs) {
		final var contents = LineBuffer.getHdlBuffer()
				.pair("nrOfBits", NR_OF_BITS_STRING)
				.pair("unsigned", UNSIGNED_STRING)
				.pair("calcBits", CALC_BITS_STRING);

		contents.add(
				"reg[{{calcBits}}-1:0] s_carryIn;\n"+
						"reg[{{calcBits}}-1:0] s_multUnsigned;\n"+
						"reg[{{calcBits}}-1:0] s_intermediateResult;\n"+
						"reg signed[{{calcBits}}-1:0] s_multSigned;\n\n"+
						"always @(*)\n"+
						"begin\n"+
						"    s_carryIn[{{nrOfBits}}-1:0] = carryIn;\n"+
						"    if ({{unsigned}}== 1)\n"+
						"        begin\n"+
						"            s_carryIn[{{calcBits}}-1:{{nrOfBits}}] = 0;\n"+
						"            s_multUnsigned = $unsigned(inputA) * $unsigned(inputB);\n"+
						"            s_intermediateResult = $unsigned(s_multUnsigned) + $unsigned(s_carryIn);\n"+
						"        end\n"+
						"    else\n"+
						"        begin\n"+
						"            if (carryIn[{{nrOfBits}}-1] == 1)\n"+
						"                s_carryIn[{{calcBits}}-1:{{nrOfBits}}] = -1;\n"+
						"            else\n"+
						"                s_carryIn[{{calcBits}}-1:{{nrOfBits}}] = 0;\n"+
						"                s_multSigned = $signed(inputA) * $signed(inputB);\n"+
						"                s_intermediateResult = $signed(s_multSigned) + $signed(s_carryIn);\n"+
						"        end\n"+
						"end\n\n"+
						"assign multHigh = s_intermediateResult[{{calcBits}}-1:{{nrOfBits}}];\n"+
						"assign multLow  = s_intermediateResult[{{nrOfBits}}-1:0];"
		);

		return contents.empty();
	}
}
