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
import LogisimFX.fpga.hdlgenerator.Hdl;
import LogisimFX.fpga.hdlgenerator.TickComponentHdlGeneratorFactory;
import LogisimFX.instance.Port;
import LogisimFX.util.LineBuffer;

import java.util.HashMap;
import java.util.List;

public class LedArrayColumnScanningHdlGeneratorFactory extends AbstractHdlGeneratorFactory {

	public static final int NR_OF_LEDS_ID = -1;
	public static final int NR_OF_ROWS_ID = -2;
	public static final int NR_OF_COLUMNS_ID = -3;
	public static final int NR_OF_COLUMN_ADDRESS_BITS_ID = -4;
	public static final int ACTIVE_LOW_ID = -5;
	public static final int SCANNING_COUNTER_BITS_ID = -6;
	public static final int MAX_NR_LEDS_ID = -7;
	public static final int SCANNING_COUNTER_VALUE_ID = -8;
	public static final String NR_OF_ROWS_STRING = "nrOfRows";
	public static final String NR_OF_COLUMNS_STRING = "nrOfColumns";
	public static final String NR_OF_LEDS_STRING = "nrOfLeds";
	public static final String NR_OF_COLUMN_ADDRESS_BITS_STRING = "nrOfColumnAddressBits";
	public static final String SCANNING_COUNTER_BITS_STRING = "nrOfScanningCounterBits";
	public static final String SCANNING_COUNTER_VALUE_STRING = "scanningCounterReloadValue";
	public static final String MAX_NR_LEDS_STRING = "maxNrLedsAddrColumns";
	public static final String ACTIVE_LOW_STRING = "activeLow";
	public static final String HDL_IDENTIFIER = "LedArrayColumnScanning";

	public LedArrayColumnScanningHdlGeneratorFactory() {
		super();
		myParametersList
				.add(ACTIVE_LOW_STRING, ACTIVE_LOW_ID)
				.add(MAX_NR_LEDS_STRING, MAX_NR_LEDS_ID)
				.add(NR_OF_COLUMNS_STRING, NR_OF_COLUMNS_ID)
				.add(NR_OF_COLUMN_ADDRESS_BITS_STRING, NR_OF_COLUMN_ADDRESS_BITS_ID)
				.add(NR_OF_LEDS_STRING, NR_OF_LEDS_ID)
				.add(NR_OF_ROWS_STRING, NR_OF_ROWS_ID)
				.add(SCANNING_COUNTER_BITS_STRING, SCANNING_COUNTER_BITS_ID)
				.add(SCANNING_COUNTER_VALUE_STRING, SCANNING_COUNTER_VALUE_ID);
		myWires
				.addWire("s_columnCounterNext", NR_OF_COLUMN_ADDRESS_BITS_ID)
				.addWire("s_scanningCounterNext", SCANNING_COUNTER_BITS_ID)
				.addWire("s_tickNext", 1)
				.addWire("s_maxLedInputs", MAX_NR_LEDS_ID)
				.addRegister("s_columnCounterReg", NR_OF_COLUMN_ADDRESS_BITS_ID)
				.addRegister("s_scanningCounterReg", SCANNING_COUNTER_BITS_ID)
				.addRegister("s_tickReg", 1);
		myPorts
				.add(Port.INPUT, TickComponentHdlGeneratorFactory.FPGA_CLOCK, 1, 0)
				.add(Port.INPUT, LogisimFX.std.io.LedArrayGenericHdlGeneratorFactory.LedArrayInputs, NR_OF_LEDS_ID, 1)
				.add(Port.OUTPUT, LogisimFX.std.io.LedArrayGenericHdlGeneratorFactory.LedArrayColumnAddress, NR_OF_COLUMN_ADDRESS_BITS_ID, 2)
				.add(Port.OUTPUT, LogisimFX.std.io.LedArrayGenericHdlGeneratorFactory.LedArrayRowOutputs, NR_OF_ROWS_ID, 3);
	}

	public static LineBuffer getGenericMap(int nrOfRows, int nrOfColumns, long fpgaClockFrequency, boolean activeLow) {
		final var nrColAddrBits = LogisimFX.std.io.LedArrayGenericHdlGeneratorFactory.getNrOfBitsRequired(nrOfColumns);
		final var scanningReload = (int) (fpgaClockFrequency / 1000);
		final var nrOfScanningBitsCount = LogisimFX.std.io.LedArrayGenericHdlGeneratorFactory.getNrOfBitsRequired(scanningReload);
		final var maxNrLeds = ((int) Math.pow(2.0, nrColAddrBits)) * nrOfRows;
		final var generics = new HashMap<String, String>();
		generics.put(NR_OF_LEDS_STRING, Integer.toString(nrOfRows * nrOfColumns));
		generics.put(MAX_NR_LEDS_STRING, Integer.toString(maxNrLeds));
		generics.put(NR_OF_ROWS_STRING, Integer.toString(nrOfRows));
		generics.put(NR_OF_COLUMNS_STRING, Integer.toString(nrOfColumns));
		generics.put(ACTIVE_LOW_STRING, activeLow ? "1" : "0");
		generics.put(NR_OF_COLUMN_ADDRESS_BITS_STRING, Integer.toString(nrColAddrBits));
		generics.put(SCANNING_COUNTER_BITS_STRING, Integer.toString(nrOfScanningBitsCount));
		generics.put(SCANNING_COUNTER_VALUE_STRING, Integer.toString(scanningReload - 1));
		return LogisimFX.std.io.LedArrayGenericHdlGeneratorFactory.getGenericPortMapAlligned(generics, true);
	}

	public static LineBuffer getPortMap(int id) {
		final var ports = new HashMap<String, String>();
		ports.put(LogisimFX.std.io.LedArrayGenericHdlGeneratorFactory.LedArrayColumnAddress, String.format("%s%d", LogisimFX.std.io.LedArrayGenericHdlGeneratorFactory.LedArrayColumnAddress, id));
		ports.put(LogisimFX.std.io.LedArrayGenericHdlGeneratorFactory.LedArrayRowOutputs, String.format("%s%d", LogisimFX.std.io.LedArrayGenericHdlGeneratorFactory.LedArrayRowOutputs, id));
		ports.put(TickComponentHdlGeneratorFactory.FPGA_CLOCK, TickComponentHdlGeneratorFactory.FPGA_CLOCK);
		ports.put(LogisimFX.std.io.LedArrayGenericHdlGeneratorFactory.LedArrayInputs, String.format("s_%s%d", LogisimFX.std.io.LedArrayGenericHdlGeneratorFactory.LedArrayInputs, id));
		return LogisimFX.std.io.LedArrayGenericHdlGeneratorFactory.getGenericPortMapAlligned(ports, false);
	}

	public static List<String> getColumnCounterCode() {
		final var contents =
				LineBuffer.getHdlBuffer()
						.pair("columnAddress", LogisimFX.std.io.LedArrayGenericHdlGeneratorFactory.LedArrayColumnAddress)
						.pair("clock", TickComponentHdlGeneratorFactory.FPGA_CLOCK)
						.pair("counterBits", SCANNING_COUNTER_BITS_STRING)
						.pair("counterValue", SCANNING_COUNTER_VALUE_STRING);

		contents
				.add(

							"assign columnAddress = s_columnCounterReg;\n\n"+

							"assign s_tickNext = (s_scanningCounterReg == 0) ? 1'b1 : 1'b0;\n"+
							"assign s_scanningCounterNext = (s_scanningCounterReg == 0) ? {{counterValue}} : s_scanningCounterReg - 1;\n"+
							"assign s_columnCounterNext = (s_tickReg == 1'b0) ? s_columnCounterReg :\n"+
							"                             (s_columnCounterReg == 0) ? nrOfColumns-1 : s_columnCounterReg-1;"
							)
				.addRemarkBlock("Here the simulation only initial is defined")
				.add(
							"initial\n"+
							"begin\n"+
							"    s_columnCounterReg   = 0;\n"+
							"    s_scanningCounterReg = 0;\n"+
							"    s_tickReg            = 1'b0;\n"+
							"end\n\n"+

							"always @(posedge {{clock}})\n"+
							"begin\n"+
							"    s_columnCounterReg   = s_columnCounterNext;\n"+
							"    s_scanningCounterReg = s_scanningCounterNext;\n"+
							"    s_tickReg            = s_tickNext;\n"+
							"end"
							);
		return contents.get();
	}

	@Override
	public LineBuffer getModuleFunctionality(Netlist TheNetlist, AttributeSet attrs) {
		final var contents = LineBuffer.getHdlBuffer()
				.pair("ins", LogisimFX.std.io.LedArrayGenericHdlGeneratorFactory.LedArrayInputs)
				.pair("outs", LogisimFX.std.io.LedArrayGenericHdlGeneratorFactory.LedArrayRowOutputs)
				.pair("nrOfLeds", NR_OF_LEDS_STRING)
				.pair("nrOfRows", NR_OF_ROWS_STRING)
				.pair("activeLow", ACTIVE_LOW_STRING)
				.add(getColumnCounterCode());
		contents.add(
					"genvar i;\n"+
					"generate\n"+
					"    for (i = 0; i < {{nrOfRows}}; i = i + 1)\n"+
					"    begin: outputs\n"+
					"       assign {{outs}}[i] = (activeLow == 1)\n"+
					"            ? ~{{ins}}[i * nrOfColumns + s_columnCounterReg]\n"+
					"            :  {{ins}}[i * nrOfColumns + s_columnCounterReg];\n"+
					"    end\n"+
					"endgenerate"
					).empty();
		return contents;
	}
}
