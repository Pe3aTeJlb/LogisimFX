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
import LogisimFX.fpga.hdlgenerator.AbstractHdlGeneratorFactory;
import LogisimFX.instance.Port;
import LogisimFX.util.LineBuffer;

import java.util.HashMap;

public class LedArrayLedDefaultHdlGeneratorFactory extends AbstractHdlGeneratorFactory {

	public static final int NR_OF_LEDS_ID = -1;
	public static final int ACTIVE_LOW_ID = -2;
	public static final String NR_OF_LEDS_STRING = "nrOfLeds";
	public static final String ACTIVE_LOW_STRING = "activeLow";
	public static final String HDL_IDENTIFIER = "LedArrayLedDefault";

	public LedArrayLedDefaultHdlGeneratorFactory() {
		super();
		myParametersList
				.add(NR_OF_LEDS_STRING, NR_OF_LEDS_ID)
				.add(ACTIVE_LOW_STRING, ACTIVE_LOW_ID);
		myPorts
				.add(Port.INPUT, LogisimFX.std.io.LedArrayGenericHdlGeneratorFactory.LedArrayInputs, NR_OF_LEDS_ID, 0)
				.add(Port.OUTPUT, LogisimFX.std.io.LedArrayGenericHdlGeneratorFactory.LedArrayOutputs, NR_OF_LEDS_ID, 1);
	}

	public static LineBuffer getGenericMap(int nrOfRows, int nrOfColumns, long fpgaClockFrequency, boolean activeLow) {
		final var generics = new HashMap<String, String>();
		generics.put(NR_OF_LEDS_STRING, Integer.toString(nrOfRows * nrOfColumns));
		generics.put(ACTIVE_LOW_STRING, activeLow ? "1" : "0");
		return LogisimFX.std.io.LedArrayGenericHdlGeneratorFactory.getGenericPortMapAlligned(generics, true);
	}

	public static LineBuffer getPortMap(int id) {
		final var ports = new HashMap<String, String>();
		ports.put(LogisimFX.std.io.LedArrayGenericHdlGeneratorFactory.LedArrayOutputs, String.format("%s%d", LogisimFX.std.io.LedArrayGenericHdlGeneratorFactory.LedArrayOutputs, id));
		ports.put(LogisimFX.std.io.LedArrayGenericHdlGeneratorFactory.LedArrayInputs, String.format("s_%s%d", LogisimFX.std.io.LedArrayGenericHdlGeneratorFactory.LedArrayInputs, id));
		return LogisimFX.std.io.LedArrayGenericHdlGeneratorFactory.getGenericPortMapAlligned(ports, false);
	}

	@Override
	public LineBuffer getModuleFunctionality(Netlist TheNetlist, AttributeSet attrs) {
		final var contents = LineBuffer.getHdlBuffer()
				.pair("ins", LogisimFX.std.io.LedArrayGenericHdlGeneratorFactory.LedArrayInputs)
				.pair("outs", LogisimFX.std.io.LedArrayGenericHdlGeneratorFactory.LedArrayOutputs);

		contents.add(
					"genvar i;\n"+
					"generate\n"+
					"    for (i = 0; i < nrOfLeds; i = i + 1)\n"+
					"    begin:outputs\n"+
					"        assign {{outs}}[i] = (activeLow == 1) ? ~{{ins}}[i] : {{ins}}[i];\n"+
					"    end\n"+
					"endgenerate"
					).empty();
		return contents;
	}
}
