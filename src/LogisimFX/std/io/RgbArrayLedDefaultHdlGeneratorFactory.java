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

public class RgbArrayLedDefaultHdlGeneratorFactory extends LedArrayLedDefaultHdlGeneratorFactory {

	public static final String HDL_IDENTIFIER = "RGBArrayLedDefault";

	public RgbArrayLedDefaultHdlGeneratorFactory() {
		super();
		myPorts.removePorts(); // remove the ports from the super class
		myPorts
				.add(Port.INPUT, LedArrayGenericHdlGeneratorFactory.LedArrayRedInputs, NR_OF_LEDS_ID, 0)
				.add(Port.INPUT, LedArrayGenericHdlGeneratorFactory.LedArrayGreenInputs, NR_OF_LEDS_ID, 1)
				.add(Port.INPUT, LedArrayGenericHdlGeneratorFactory.LedArrayBlueInputs, NR_OF_LEDS_ID, 2)
				.add(Port.OUTPUT, LedArrayGenericHdlGeneratorFactory.LedArrayRedOutputs, NR_OF_LEDS_ID, 3)
				.add(Port.OUTPUT, LedArrayGenericHdlGeneratorFactory.LedArrayGreenOutputs, NR_OF_LEDS_ID, 4)
				.add(Port.OUTPUT, LedArrayGenericHdlGeneratorFactory.LedArrayBlueOutputs, NR_OF_LEDS_ID, 5);
	}

	public static LineBuffer getPortMap(int id) {
		final var ports = new HashMap<String, String>();
		ports.put(LedArrayGenericHdlGeneratorFactory.LedArrayRedOutputs, String.format("%s%d", LedArrayGenericHdlGeneratorFactory.LedArrayRedOutputs, id));
		ports.put(LedArrayGenericHdlGeneratorFactory.LedArrayGreenOutputs, String.format("%s%d", LedArrayGenericHdlGeneratorFactory.LedArrayGreenOutputs, id));
		ports.put(LedArrayGenericHdlGeneratorFactory.LedArrayBlueOutputs, String.format("%s%d", LedArrayGenericHdlGeneratorFactory.LedArrayBlueOutputs, id));
		ports.put(LedArrayGenericHdlGeneratorFactory.LedArrayRedInputs, String.format("s_%s%d", LedArrayGenericHdlGeneratorFactory.LedArrayRedInputs, id));
		ports.put(LedArrayGenericHdlGeneratorFactory.LedArrayGreenInputs, String.format("s_%s%d", LedArrayGenericHdlGeneratorFactory.LedArrayGreenInputs, id));
		ports.put(LedArrayGenericHdlGeneratorFactory.LedArrayBlueInputs, String.format("s_%s%d", LedArrayGenericHdlGeneratorFactory.LedArrayBlueInputs, id));
		return LedArrayGenericHdlGeneratorFactory.getGenericPortMapAlligned(ports, false);
	}

	@Override
	public LineBuffer getModuleFunctionality(Netlist TheNetlist, AttributeSet attrs) {
		final var contents = LineBuffer.getHdlBuffer()
				.pair("outsR", LedArrayGenericHdlGeneratorFactory.LedArrayRedOutputs)
				.pair("outsG", LedArrayGenericHdlGeneratorFactory.LedArrayGreenOutputs)
				.pair("outsB", LedArrayGenericHdlGeneratorFactory.LedArrayBlueOutputs)
				.pair("insR", LedArrayGenericHdlGeneratorFactory.LedArrayRedInputs)
				.pair("insG", LedArrayGenericHdlGeneratorFactory.LedArrayGreenInputs)
				.pair("insB", LedArrayGenericHdlGeneratorFactory.LedArrayBlueInputs)
				.pair("clock", TickComponentHdlGeneratorFactory.FPGA_CLOCK);

		contents.add(
				"genvar i;\n"+
				"generate\n"+
				"    for (i = 0; i < nrOfLeds; i = i + 1)\n"+
				"    begin:outputs\n"+
				"        assign {{outsR}}[i] = (activeLow == 1) ? ~{{insR}}[n] : {{insR}}[n];\n"+
				"        assign {{outsG}}[i] = (activeLow == 1) ? ~{{insG}}[n] : {{insG}}[n];\n"+
				"        assign {{outsB}}[i] = (activeLow == 1) ? ~{{insB}}[n] : {{insB}}[n];\n"+
				"    end\n"+
				"endgenerate"
				).empty();
		return contents;
	}
}
