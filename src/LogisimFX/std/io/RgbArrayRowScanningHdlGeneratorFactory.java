/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package LogisimFX.std.io;

import LogisimFX.data.AttributeSet;
import LogisimFX.fpga.designrulecheck.Netlist;
import LogisimFX.fpga.hdlgenerator.TickComponentHdlGeneratorFactory;
import LogisimFX.instance.Port;
import LogisimFX.util.LineBuffer;

import java.util.HashMap;

public class RgbArrayRowScanningHdlGeneratorFactory extends LedArrayRowScanningHdlGeneratorFactory {

	public static final String HDL_IDENTIFIER = "RGBArrayRowScanning";

	public RgbArrayRowScanningHdlGeneratorFactory() {
		super();
		myWires
				.addWire("s_maxRedLedInputs", MAX_NR_LEDS_ID)
				.addWire("s_maxBlueLedInputs", MAX_NR_LEDS_ID)
				.addWire("s_maxGreenLedInputs", MAX_NR_LEDS_ID);
		myPorts.removePorts(); // remove the ports of the super class
		myPorts
				.add(Port.INPUT, TickComponentHdlGeneratorFactory.FPGA_CLOCK, 1, 0)
				.add(Port.INPUT, LedArrayGenericHdlGeneratorFactory.LedArrayRedInputs, NR_OF_LEDS_ID, 1)
				.add(Port.INPUT, LedArrayGenericHdlGeneratorFactory.LedArrayGreenInputs, NR_OF_LEDS_ID, 2)
				.add(Port.INPUT, LedArrayGenericHdlGeneratorFactory.LedArrayBlueInputs, NR_OF_LEDS_ID, 3)
				.add(Port.OUTPUT, LedArrayGenericHdlGeneratorFactory.LedArrayRowAddress, NR_OF_ROW_ADDRESS_BITS_ID, 4)
				.add(Port.OUTPUT, LedArrayGenericHdlGeneratorFactory.LedArrayColumnRedOutputs, NR_OF_COLUMS_ID, 5)
				.add(Port.OUTPUT, LedArrayGenericHdlGeneratorFactory.LedArrayColumnGreenOutputs, NR_OF_COLUMS_ID, 6)
				.add(Port.OUTPUT, LedArrayGenericHdlGeneratorFactory.LedArrayColumnBlueOutputs, NR_OF_COLUMS_ID, 7);
	}

	static final LineBuffer.Pairs sharedPairs =
			new LineBuffer.Pairs()
					.pair("insR", LedArrayGenericHdlGeneratorFactory.LedArrayRedInputs)
					.pair("insG", LedArrayGenericHdlGeneratorFactory.LedArrayGreenInputs)
					.pair("insB", LedArrayGenericHdlGeneratorFactory.LedArrayBlueInputs)
					.pair("outsR", LedArrayGenericHdlGeneratorFactory.LedArrayColumnRedOutputs)
					.pair("outsG", LedArrayGenericHdlGeneratorFactory.LedArrayColumnGreenOutputs)
					.pair("outsB", LedArrayGenericHdlGeneratorFactory.LedArrayColumnBlueOutputs);

	public static LineBuffer getPortMap(int id) {
		final var ports = new HashMap<String, String>();
		ports.put(LedArrayGenericHdlGeneratorFactory.LedArrayRowAddress, String.format("%s%d", LedArrayGenericHdlGeneratorFactory.LedArrayRowAddress, id));
		ports.put(LedArrayGenericHdlGeneratorFactory.LedArrayColumnRedOutputs, String.format("%s%d", LedArrayGenericHdlGeneratorFactory.LedArrayColumnRedOutputs, id));
		ports.put(LedArrayGenericHdlGeneratorFactory.LedArrayColumnGreenOutputs, String.format("%s%d", LedArrayGenericHdlGeneratorFactory.LedArrayColumnGreenOutputs, id));
		ports.put(LedArrayGenericHdlGeneratorFactory.LedArrayColumnBlueOutputs, String.format("%s%d", LedArrayGenericHdlGeneratorFactory.LedArrayColumnBlueOutputs, id));
		ports.put(TickComponentHdlGeneratorFactory.FPGA_CLOCK, TickComponentHdlGeneratorFactory.FPGA_CLOCK);
		ports.put(LedArrayGenericHdlGeneratorFactory.LedArrayRedInputs, String.format("s_%s%d", LedArrayGenericHdlGeneratorFactory.LedArrayRedInputs, id));
		ports.put(LedArrayGenericHdlGeneratorFactory.LedArrayGreenInputs, String.format("s_%s%d", LedArrayGenericHdlGeneratorFactory.LedArrayGreenInputs, id));
		ports.put(LedArrayGenericHdlGeneratorFactory.LedArrayBlueInputs, String.format("s_%s%d", LedArrayGenericHdlGeneratorFactory.LedArrayBlueInputs, id));
		return LedArrayGenericHdlGeneratorFactory.getGenericPortMapAlligned(ports, false);
	}

	@Override
	public LineBuffer getModuleFunctionality(Netlist theNetlist, AttributeSet attrs) {
		final var contents = LineBuffer.getHdlBuffer()
				.pair("activeLow", ACTIVE_LOW_STRING)
				.pair("nrOfLeds", NR_OF_LEDS_STRING)
				.pair("nrOfColumns", NR_OF_COLUMS_STRING);

		contents.add(getRowCounterCode());
		contents.add(
					"genvar i;\n"+
					"generate\n"+
					"    for (i = 0; i < {{nrOfColumns}}; i = i + 1)\n"+
					"    begin:outputs\n"+
					"        assign {{outsR}}[i] = (activeLow == 1)\n"+
					"           ? ~{{insR}}[{{nrOfColumns}} * s_rowCounterReg + i]\n"+
					"           :  {{insR}}[{{nrOfColumns}} * s_rowCounterReg + i];\n"+
					"        assign {{outsG}}[i] = (activeLow == 1)\n"+
					"           ? ~{{insG}}[{{nrOfColumns}} * s_rowCounterReg + i]\n"+
					"           :  {{insG}}[{{nrOfColumns}} * s_rowCounterReg + i];\n"+
					"       assign {{outsB}}[i] = (activeLow == 1)\n"+
					"           ? ~{{insB}}[{{nrOfColumns}} * s_rowCounterReg + i]\n"+
					"           :  {{insB}}[{{nrOfColumns}} * s_rowCounterReg + i];\n"+
					"    end\n"+
					"endgenerate"
					).empty();
		return contents;
	}
}
