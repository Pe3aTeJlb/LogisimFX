/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package LogisimFX.std.arith;

import LogisimFX.data.AttributeOption;
import LogisimFX.data.AttributeSet;
import LogisimFX.fpga.designrulecheck.Netlist;
import LogisimFX.fpga.hdlgenerator.AbstractHdlGeneratorFactory;
import LogisimFX.fpga.hdlgenerator.HdlParameters;
import LogisimFX.instance.Port;
import LogisimFX.instance.StdAttr;
import LogisimFX.util.LineBuffer;

import java.util.HashMap;
import java.util.Map;

public class ComparatorHdlGeneratorFactory extends AbstractHdlGeneratorFactory {

	private static final String NR_OF_BITS_STRING = "nrOfBits";
	private static final int NR_OF_BITS_ID = -1;
	private static final String TWOS_COMPLEMENT_STRING = "twosComplement";
	private static final int TWOS_COMPLEMENT_ID = -2;

	public static final Map<AttributeOption, Integer> SIGNED_MAP = new HashMap<>() {{
		put(Comparator.UNSIGNED_OPTION, 0);
		put(Comparator.SIGNED_OPTION, 1);
	}};


	public ComparatorHdlGeneratorFactory() {
		super();
		myParametersList
				.addBusOnly(NR_OF_BITS_STRING, NR_OF_BITS_ID)
				.add(TWOS_COMPLEMENT_STRING, TWOS_COMPLEMENT_ID, HdlParameters.MAP_ATTRIBUTE_OPTION, Comparator.MODE_ATTRIBUTE,
						SIGNED_MAP);
		getWiresPortsDuringHDLWriting = true;
	}

	@Override
	public void getGenerationTimeWiresPorts(Netlist theNetlist, AttributeSet attrs) {
		myPorts
				.add(Port.INPUT, "dataA", NR_OF_BITS_ID, Comparator.IN0, StdAttr.WIDTH)
				.add(Port.INPUT, "dataB", NR_OF_BITS_ID, Comparator.IN1, StdAttr.WIDTH)
				.add(Port.OUTPUT, "aGreaterThanB", 1, Comparator.GT)
				.add(Port.OUTPUT, "aEqualsB", 1, Comparator.EQ)
				.add(Port.OUTPUT, "aLessThanB", 1, Comparator.LT);
		if (attrs.getValue(StdAttr.WIDTH).getWidth() > 1)
			myWires
					.addWire("s_signedLess", 1)
					.addWire("s_unsignedLess", 1)
					.addWire("s_signedGreater", 1)
					.addWire("s_unsignedGreater", 1);
	}


	@Override
	public LineBuffer getModuleFunctionality(Netlist theNetlist, AttributeSet attrs) {
		final var contents = LineBuffer.getBuffer().pair("twosComplement", TWOS_COMPLEMENT_STRING);
		final var nrOfBits = attrs.getValue(StdAttr.WIDTH).getWidth();
		if (nrOfBits == 1) {
			contents.add(
							"assign aEqualsB      = (dataA == dataB);\n" +
							"assign aLessThanB    = (dataA < dataB);\n" +
							"assign aGreaterThanB = (dataA > dataB);"
			);
		} else {
			contents.add(
							"assign s_signedLess          = ($signed(dataA) < $signed(dataB));\n" +
							"assign s_unsignedLess        = (dataA < dataB);\n" +
							"assign s_signedGreater       = ($signed(dataA) > $signed(dataB));\n" +
							"assign s_unsignedGreater     = (dataA > dataB);\n\n" +
							"assign aEqualsB              = (dataA == dataB);\n" +
							"assign aGreaterThanB         = ({{twosComplement}}==1) ? s_signedGreater : s_unsignedGreater;\n" +
							"assign aLessThanB            = ({{twosComplement}}==1) ? s_signedLess : s_unsignedLess;"
			);
		}

		return contents.empty();
	}
}
