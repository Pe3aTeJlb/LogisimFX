/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package LogisimFX.std.memory;

import LogisimFX.data.AttributeOption;
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

import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

public class CounterHdlGeneratorFactory extends AbstractHdlGeneratorFactory {

	private static final String NR_OF_BITS_STRING = "width";
	private static final int NR_OF_BITS_ID = -1;
	private static final String MAX_VALUE_STRING = "maxVal";
	private static final int MAX_VALUE_ID = -2;
	private static final String INVERT_CLOCK_STRING = "invertClock";
	private static final int INVERT_CLOCK_ID = -3;
	private static final String MODE_STRING = "mode";
	private static final int MODE_ID = -4;

	private static final String LOAD_DATA_INPUT = "loadData";
	private static final String COUNT_DATA_OUTPUT = "countValue";

	public CounterHdlGeneratorFactory() {
		super();
		myParametersList
				.add(NR_OF_BITS_STRING, NR_OF_BITS_ID)
				.addVector(MAX_VALUE_STRING, MAX_VALUE_ID, HdlParameters.MAP_INT_ATTRIBUTE, Counter.ATTR_MAX)
				.add(INVERT_CLOCK_STRING, INVERT_CLOCK_ID, HdlParameters.MAP_ATTRIBUTE_OPTION,
						StdAttr.EDGE_TRIGGER, LogisimFX.std.memory.AbstractFlipFlopHdlGeneratorFactory.TRIGGER_MAP)
				.add(MODE_STRING, MODE_ID, HdlParameters.MAP_ATTRIBUTE_OPTION, Counter.ATTR_ON_GOAL,
						new HashMap<AttributeOption, Integer>() {{
							put(Counter.ON_GOAL_WRAP, 0);
							put(Counter.ON_GOAL_STAY, 1);
							put(Counter.ON_GOAL_CONT, 2);
							put(Counter.ON_GOAL_LOAD, 3);
						}}
				);
		myWires
				.addWire("s_clock", 1)
				.addWire("s_realEnable", 1)
				.addRegister("s_nextCounterValue", NR_OF_BITS_ID)
				.addRegister("s_carry", 1)
				.addRegister("s_counterValue", NR_OF_BITS_ID);
		myPorts
				.add(Port.CLOCK, HdlPorts.CLOCK, 1, Counter.CK)
				.add(Port.INPUT, LOAD_DATA_INPUT, NR_OF_BITS_ID, Counter.IN)
				.add(Port.INPUT, "clear", 1, Counter.CLR)
				.add(Port.INPUT, "load", 1, Counter.LD)
				.add(Port.INPUT, "upNotDown", 1, Counter.UD)
				.add(Port.INPUT, "enable", 1, Counter.EN, false)
				.add(Port.OUTPUT, COUNT_DATA_OUTPUT, NR_OF_BITS_ID, Counter.OUT)
				.add(Port.OUTPUT, "compareOut", 1, Counter.CARRY);
	}

	@Override
	public LineBuffer getModuleFunctionality(Netlist TheNetlist, AttributeSet attrs) {
		final var contents = LineBuffer.getHdlBuffer()
				.pair("invertClock", INVERT_CLOCK_STRING)
				.pair("clock", HdlPorts.CLOCK)
				.pair("Tick", HdlPorts.TICK)
				.empty()
				.addRemarkBlock(
						"Functionality of the counter:\n"+
						"    Load Count | mode\n"+
						"    -----------+-------------------\n"+
						"    0    0   | halt\n"+
						"    0    1   | count up (default)\n"+
						"    1    0   | load\n"+
						"    1    1   | count down"
						)
				.empty();

		contents.add(
						"assign compareOut = s_carry;\n" +
						"assign countValue = s_counterValue;\n" +
						"assign s_clock = ({{invertClock}} == 0) ? {{clock}} : ~{{clock}};\n\n" +

						"always@(*)\n" +
						"begin\n" +
						"    if (upNotDown)\n" +
						"        s_carry = (s_counterValue == maxVal) ? 1'b1 : 1'b0;\n" +
						"    else\n" +
						"        s_carry = (s_counterValue == 0) ? 1'b1 : 1'b0;\n" +
						"end\n" +

						"assign s_realEnable = ((~(load)&~(enable))|\n" +
						"                        ((mode==1)&s_carry&~(load))) ? 1'b0 : {{Tick}};\n\n" +

						"always @(*)\n" +
						"begin\n" +
						"    if ((load)|((mode==3)&s_carry))\n" +
						"        s_nextCounterValue = loadData;\n" +
						"    else if ((mode==0)&s_carry&upNotDown)\n" +
						"        s_nextCounterValue = 0;\n" +
						"    else if ((mode==0)&s_carry)\n" +
						"        s_nextCounterValue = maxVal;\n" +
						"    else if (upNotDown)\n" +
						"        s_nextCounterValue = s_counterValue + 1;\n" +
						"    else\n" +
						"        s_nextCounterValue = s_counterValue - 1;\n" +
						"end\n\n" +

						"always @(posedge s_clock or posedge clear)\n" +
						"begin\n" +
						"    if (clear) s_counterValue <= 0;\n" +
						"    else if (s_realEnable) s_counterValue <= s_nextCounterValue;\n" +
						"end\n"

		);

		return contents.empty();
	}
}
