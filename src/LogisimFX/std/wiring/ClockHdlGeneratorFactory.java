/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package LogisimFX.std.wiring;

import LogisimFX.comp.Component;
import LogisimFX.data.AttributeSet;
import LogisimFX.fpga.designrulecheck.Netlist;
import LogisimFX.fpga.designrulecheck.netlistComponent;
import LogisimFX.fpga.hdlgenerator.*;
import LogisimFX.instance.Port;
import LogisimFX.util.LineBuffer;

import java.util.SortedMap;
import java.util.TreeMap;

public class ClockHdlGeneratorFactory extends AbstractHdlGeneratorFactory {

	public static final int NR_OF_CLOCK_BITS = 5;
	public static final int DERIVED_CLOCK_INDEX = 0;
	public static final int INVERTED_DERIVED_CLOCK_INDEX = 1;
	public static final int POSITIVE_EDGE_TICK_INDEX = 2;
	public static final int NEGATIVE_EDGE_TICK_INDEX = 3;
	public static final int GLOBAL_CLOCK_INDEX = 4;
	private static final String HIGH_TICK_STR = "highTicks";
	private static final int HIGH_TICK_ID = -1;
	private static final String LOW_TICK_STR = "lowTicks";
	private static final int LOW_TICK_ID = -2;
	private static final String PHASE_STR = "phase";
	private static final int PHASE_ID = -3;
	private static final String NR_OF_BITS_STR = "nrOfBits";
	private static final int NR_OF_BITS_ID = -4;

	public ClockHdlGeneratorFactory() {
		super("base");
		myParametersList
				.add(HIGH_TICK_STR, HIGH_TICK_ID, HdlParameters.MAP_INT_ATTRIBUTE, Clock.ATTR_HIGH)
				.add(LOW_TICK_STR, LOW_TICK_ID, HdlParameters.MAP_INT_ATTRIBUTE, Clock.ATTR_LOW)
				.add(PHASE_STR, PHASE_ID, HdlParameters.MAP_INT_ATTRIBUTE, Clock.ATTR_PHASE, 1)
				.add(NR_OF_BITS_STR, NR_OF_BITS_ID, HdlParameters.MAP_LN2, Clock.ATTR_HIGH, Clock.ATTR_LOW);
		myWires
				.addWire("s_counterNext", NR_OF_BITS_ID)
				.addWire("s_counterIsZero", 1)
				.addRegister("s_outputRegs", NR_OF_CLOCK_BITS - 1)
				.addRegister("s_bufferRegs", 2)
				.addRegister("s_counterValue", NR_OF_BITS_ID)
				.addRegister("s_derivedClock", PHASE_ID);
		myPorts
				.add(Port.INPUT, "globalClock", 1, 0)
				.add(Port.INPUT, "clockTick", 1, 1)
				.add(Port.OUTPUT, "clockBus", NR_OF_CLOCK_BITS, 2);
	}

	@Override
	public SortedMap<String, String> getPortMap(Netlist nets, Object mapInfo) {
		final var map = new TreeMap<String, String>();
		netlistComponent componentInfo;
		if (!(mapInfo instanceof netlistComponent)) return map;
		else componentInfo = (netlistComponent) mapInfo;
		map.put("globalClock", TickComponentHdlGeneratorFactory.FPGA_CLOCK);
		map.put("clockTick", TickComponentHdlGeneratorFactory.FPGA_TICK);
		map.put("clockBus", getClockNetName(componentInfo.getComponent(), nets));
		return map;
	}

	private static String getClockNetName(Component comp, Netlist theNets) {
		final var contents = new StringBuilder();
		int clockNetId = theNets.getClockSourceId(comp);
		if (clockNetId >= 0) {
			contents.append("s_").append(HdlGeneratorFactory.CLOCK_TREE_NAME).append(clockNetId);
		}
		return contents.toString();
	}

	@Override
	public LineBuffer getModuleFunctionality(Netlist theNetlist, AttributeSet attrs) {
		final var contents = LineBuffer.getHdlBuffer()
				.pair("phase", PHASE_STR)
				.pair("nrOfBits", NR_OF_BITS_STR)
				.pair("lowTick", LOW_TICK_STR)
				.pair("highTick", HIGH_TICK_STR)
				.addRemarkBlock("The output signals are defined here; we synchronize them all on the main clock")
				.empty();

		contents.add(
						"assign clockBus = {globalClock,s_outputRegs};\n" +
						"always @(posedge globalClock)\n" +
						"begin\n" +
						"    s_bufferRegs[0] <= s_derivedClock[{{phase}} - 1];\n" +
						"    s_bufferRegs[1] <= ~s_derivedClock[{{phase}} - 1];\n" +
						"    s_outputRegs[0] <= s_bufferRegs[0];\n" +
						"    s_outputRegs[1] <= s_outputRegs[1];\n" +
						"    s_outputRegs[2] <= ~s_bufferRegs[0] & s_derivedClock[{{phase}} - 1];\n" +
						"    s_outputRegs[3] <= ~s_derivedClock[{{phase}} - 1] & s_bufferRegs[0];\n" +
						"end"
		);

		contents.empty().addRemarkBlock("The control signals are defined here");

		contents.add(
						"assign s_counterIsZero = (s_counterValue == 0) ? 1'b1 : 1'b0;\n" +
						"assign s_counterNext = (s_counterIsZero == 1'b0)\n" +
						"                        ? s_counterValue - 1\n" +
						"                        : (s_derivedClock[0] == 1'b1)\n" +
						"                            ? {{lowTick}} - 1\n" +
						"                            : {{highTick}} - 1;"

		)
				.empty()
				.addRemarkBlock("The initial values are defined here (for simulation only)")
				.add(
								"initial\n" +
								"begin\n" +
								"    s_outputRegs = 0;\n" +
								"    s_derivedClock = 0;\n" +
								"    s_counterValue = 0;\n" +
								"end"
				);

		contents.empty().addRemarkBlock("The state registers are defined here");

		contents.add(
						"integer n;\n" +
						"always @(posedge globalClock)\n" +
						"begin\n" +
						"    if (clockTick)\n" +
						"    begin\n" +
						"        s_derivedClock[0] <= s_derivedClock[0] ^ s_counterIsZero;\n" +
						"        for (n = 1; n < {{phase}}; n = n+1) begin\n" +
						"            s_derivedClock[n] <= s_derivedClock[n-1];\n" +
						"        end\n" +
						"    end\n" +
						"end\n\n" +

						"always @(posedge globalClock)\n" +
						"begin\n" +
						"    if (clockTick)\n" +
						"    begin\n" +
						"        s_counterValue <= s_counterNext;\n" +
						"    end\n" +
						"end"
		);

		return contents.empty();
	}
}
