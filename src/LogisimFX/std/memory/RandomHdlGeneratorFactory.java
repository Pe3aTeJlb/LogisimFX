/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package LogisimFX.std.memory;

import LogisimFX.data.AttributeSet;
import LogisimFX.fpga.designrulecheck.Netlist;
import LogisimFX.fpga.designrulecheck.netlistComponent;
import LogisimFX.fpga.hdlgenerator.AbstractHdlGeneratorFactory;
import LogisimFX.fpga.hdlgenerator.Hdl;
import LogisimFX.fpga.hdlgenerator.HdlParameters;
import LogisimFX.fpga.hdlgenerator.HdlPorts;
import LogisimFX.instance.Port;
import LogisimFX.instance.StdAttr;
import LogisimFX.util.LineBuffer;

import java.util.SortedMap;
import java.util.TreeMap;

public class RandomHdlGeneratorFactory extends AbstractHdlGeneratorFactory {

	private static final String NR_OF_BITS_STR = "nrOfBits";
	private static final int NR_OF_BITS_ID = -1;
	private static final String SEED_STR = "seed";
	private static final int SEED_ID = -2;

	public RandomHdlGeneratorFactory() {
		super();
		myParametersList
				.add(NR_OF_BITS_STR, NR_OF_BITS_ID)
				// The seed parameter has 32 bits fixed
				.addVector(SEED_STR, SEED_ID, HdlParameters.MAP_INT_ATTRIBUTE, Random.ATTR_SEED, 32);
		myWires
				.addWire("s_initSeed", 48)
				.addWire("s_reset", 1)
				.addWire("s_resetNext", 3)
				.addWire("s_multShiftNext", 36)
				.addWire("s_seedShiftNext", 48)
				.addWire("s_multBusy", 1)
				.addWire("s_start", 1)
				.addWire("s_macLowIn1", 25)
				.addWire("s_macLowIn2", 25)
				.addWire("s_macHigh1Next", 24)
				.addWire("s_macHighIn2", 24)
				.addWire("s_busyPipeNext", 2)
				.addRegister("s_currentSeed", 48)
				.addRegister("s_resetReg", 3)
				.addRegister("s_multShiftReg", 36)
				.addRegister("s_seedShiftReg", 48)
				.addRegister("s_startReg", 1)
				.addRegister("s_macLowReg", 25)
				.addRegister("s_macHighReg", 24)
				.addRegister("s_macHighReg1", 24)
				.addRegister("s_busyPipeReg", 2)
				.addRegister("s_outputReg", NR_OF_BITS_ID);
		myPorts
				.add(Port.CLOCK, HdlPorts.getClockName(1), 1, Random.CK)
				.add(Port.INPUT, "clear", 1, Random.RST)
				.add(Port.INPUT, "enable", 1, Random.NXT, false)
				.add(Port.OUTPUT, "q", NR_OF_BITS_ID, Random.OUT);
	}

	@Override
	public LineBuffer getModuleFunctionality(Netlist nets, AttributeSet attrs) {
		final var contents =
				LineBuffer.getBuffer()
						.pair("seed", SEED_STR)
						.pair("nrOfBits", NR_OF_BITS_STR)
						.pair("GlobalClock", HdlPorts.getClockName(1))
						.pair("ClockEnable", HdlPorts.getTickName(1))
						.addRemarkBlock("This is a multicycle implementation of the Random Component")
						.empty();

		contents.add(
				"assign q = s_outputReg;\n" +
						"assign s_initSeed      = ({{seed}} == 0) ? 48'h5DEECE66D : {{seed}};\n" +
						"assign s_reset         = (s_resetReg==3'b010) ? 1'b1 : 1'b0;\n" +
						"assign s_resetNext     = (( (s_resetReg == 3'b101) | (s_resetReg == 3'b010)) & clear)\n" +
						"                            ? 3'b010\n" +
						"                            : (s_resetReg==3'b001) ? 3'b101 : 3'b001;\n" +
						"assign s_start         = (({{ClockEnable}}&enable)|((s_resetReg == 3'b101)&clear)) ? 1'b1 : 1'b0;\n" +
						"assign s_multShiftNext = (s_reset)\n" +
						"                            ? 36'd0\n" +
						"                            : (s_startReg) ? 36'h5DEECE66D : {1'b0,s_multShiftReg[35:1]};\n" +
						"assign s_seedShiftNext = (s_reset)\n" +
						"                            ? 48'd0\n" +
						"                            : (s_startReg) ? s_currentSeed : {s_seedShiftReg[46:0],1'b0};\n" +
						"assign s_multBusy      = (s_multShiftReg == 0) ? 1'b0 : 1'b1;\n" +
						"assign s_macLowIn1     = (s_startReg|s_reset) ? 25'd0 : {1'b0,s_macLowReg[23:0]};\n" +
						"assign s_macLowIn2     = (s_startReg) ? 25'hB\n" +
						"                            : (s_multShiftReg[0])\n" +
						"                            ? {1'b0,s_seedShiftReg[23:0]} : 25'd0;\n" +
						"assign s_macHighIn2    = (s_startReg) ? 0 : s_macHighReg;\n" +
						"assign s_macHigh1Next  = (s_multShiftReg[0]) ? s_seedShiftReg[47:24] : 0;\n" +
						"assign s_busyPipeNext  = (s_reset) ? 2'd0 : {s_busyPipeReg[0],s_multBusy};\n\n" +

						"always @(posedge {{GlobalClock}})\n" +
						"begin\n" +
						"    if (s_reset) s_currentSeed <= s_initSeed;\n" +
						"    else if (s_busyPipeReg == 2'b10) s_currentSeed <= {s_macHighReg,s_macLowReg[23:0]};\n" +
						"end\n\n" +

						"always @(posedge {{GlobalClock}})\n" +
						"begin\n" +
						"        s_multShiftReg  <= s_multShiftNext;\n" +
						"        s_seedShiftReg  <= s_seedShiftNext;\n" +
						"        s_macLowReg     <= s_macLowIn1+s_macLowIn2;\n" +
						"        s_macHighReg1   <= s_macHigh1Next;\n" +
						"        s_macHighReg    <= s_macHighReg1+s_macHighIn2+s_macLowReg[24];\n" +
						"        s_busyPipeReg   <= s_busyPipeNext;\n" +
						"        s_startReg      <= s_start;\n" +
						"        s_resetReg      <= s_resetNext;\n" +
						"end\n\n" +

						"always @(posedge {{GlobalClock}})\n" +
						"begin\n" +
						"    if (s_reset) s_outputReg <= s_initSeed[({{nrOfBits}}-1):0];\n" +
						"    else if ({{ClockEnable}}&enable) s_outputReg <= s_currentSeed[({{nrOfBits}}+11):12];\n" +
						"end"
		);
		return contents.empty();
	}
}
