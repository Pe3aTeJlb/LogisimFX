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

public class DividerHdlGeneratorFactory extends AbstractHdlGeneratorFactory {
	private static final String NR_OF_BITS_STRING = "nrOfBits";
	private static final int NR_OF_BITS_ID = -1;
	private static final String CALC_BITS_STRING = "calcBits";
	private static final int CALC_BITS_ID = -2;
	private static final String UNSIGNED_STRING = "unsignedDivider";
	private static final int UNSIGNED_ID = -3;

	public DividerHdlGeneratorFactory() {
		super();
		myParametersList
				.add(NR_OF_BITS_STRING, NR_OF_BITS_ID)
				.add(CALC_BITS_STRING, CALC_BITS_ID, HdlParameters.MAP_MULTIPLY, 2)
				.add(UNSIGNED_STRING, UNSIGNED_ID, HdlParameters.MAP_ATTRIBUTE_OPTION, Comparator.MODE_ATTRIBUTE, ComparatorHdlGeneratorFactory.SIGNED_MAP);
		myWires
				.addWire("s_divResult", CALC_BITS_ID)
				.addWire("s_modResult", NR_OF_BITS_ID)
				.addWire("s_extendedDividend", CALC_BITS_ID);
		myPorts
				.add(Port.INPUT, "inputA", NR_OF_BITS_ID, Divider.IN0)
				.add(Port.INPUT, "inputB", NR_OF_BITS_ID, Divider.IN1)
				.add(Port.INPUT, "upper", NR_OF_BITS_ID, Divider.UPPER)
				.add(Port.OUTPUT, "quotient", NR_OF_BITS_ID, Divider.OUT)
				.add(Port.OUTPUT, "remainder", NR_OF_BITS_ID, Divider.REM);
	}

	@Override
	public boolean isHdlSupportedTarget(AttributeSet attrs) {
		return false;
	}
}
