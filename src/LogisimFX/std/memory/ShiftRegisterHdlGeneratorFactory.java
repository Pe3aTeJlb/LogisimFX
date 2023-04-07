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

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ShiftRegisterHdlGeneratorFactory extends AbstractHdlGeneratorFactory {

	private static final String NEGATE_CLOCK_STRING = "negateClock";
	private static final int NEGATE_CLOCK_ID = -1;
	private static final String NR_OF_BITS_STRING = "nrOfBits";
	private static final int NR_OF_BITS_ID = -2;
	private static final String NR_OF_STAGES_STRING = "nrOfStages";
	private static final int NR_OF_STAGES_ID = -3;
	private static final String NR_OF_PAR_BITS_STRING = "nrOfParBits";
	private static final int NR_OF_PAR_BITS_ID = -4;

	public ShiftRegisterHdlGeneratorFactory() {
		super();
		myParametersList
				.add(NEGATE_CLOCK_STRING, NEGATE_CLOCK_ID, HdlParameters.MAP_ATTRIBUTE_OPTION, StdAttr.EDGE_TRIGGER, AbstractFlipFlopHdlGeneratorFactory.TRIGGER_MAP)
				.add(NR_OF_BITS_STRING, NR_OF_BITS_ID)
				.add(NR_OF_PAR_BITS_STRING, NR_OF_PAR_BITS_ID, HdlParameters.MAP_MULTIPLY, StdAttr.WIDTH, ShiftRegister.ATTR_LENGTH)
				.add(NR_OF_STAGES_STRING, NR_OF_STAGES_ID, HdlParameters.MAP_INT_ATTRIBUTE, ShiftRegister.ATTR_LENGTH);
		getWiresPortsDuringHDLWriting = true;
	}

	@Override
	public void getGenerationTimeWiresPorts(Netlist theNetlist, AttributeSet attrs) {
		final var hasParallelLoad = attrs.getValue(ShiftRegister.ATTR_LOAD);
		myPorts
				.add(Port.CLOCK, HdlPorts.getClockName(1), 1, ShiftRegister.CK)
				.add(Port.INPUT, "reset", 1, ShiftRegister.CLR)
				.add(Port.INPUT, "shiftEnable", 1, ShiftRegister.SH)
				.add(Port.INPUT, "shiftIn", NR_OF_BITS_ID, ShiftRegister.IN)
				.add(Port.INPUT, "d", NR_OF_PAR_BITS_ID, "DUMMY_MAP")
				.add(Port.OUTPUT, "shiftOut", NR_OF_BITS_ID, ShiftRegister.OUT)
				.add(Port.OUTPUT, "q", NR_OF_PAR_BITS_ID, "DUMMY_MAP")
				.add(Port.OUTPUT, "dir", 1, ShiftRegister.DIR);
		if (hasParallelLoad) {
			myPorts.add(Port.INPUT, "parLoad", 1, ShiftRegister.LD);
		} else {
			myPorts.add(Port.INPUT, "parLoad", 1, Hdl.zeroBit());
		}
	}

	@Override
	public SortedMap<String, String> getPortMap(Netlist nets, Object mapInfo) {
		final var map = new TreeMap<String, String>(super.getPortMap(nets, mapInfo));
		if (mapInfo instanceof netlistComponent){
			netlistComponent comp = (netlistComponent) mapInfo;
			final var attrs = comp.getComponent().getAttributeSet();
			final var nrOfBits = attrs.getValue(StdAttr.WIDTH).getWidth();
			final var nrOfStages = attrs.getValue(ShiftRegister.ATTR_LENGTH);
			final var hasParallelLoad = attrs.getValue(ShiftRegister.ATTR_LOAD);
			final var vector = new StringBuilder();
			map.remove("d");
			map.remove("q");
			if (hasParallelLoad) {
				if (nrOfBits == 1) {
					for (var stage = nrOfStages - 1; stage >= 0; stage--) {
						if (vector.length() != 0) vector.append(",");
						vector.append(Hdl.getNetName(comp, 6 + (2 * stage), true, nets));
					}
					map.put("d", vector.toString());
					vector.setLength(0);
					vector.append("open");
					for (var stage = nrOfStages - 2; stage >= 0; stage--) {
						if (vector.length() != 0) vector.append(",");
						vector.append(Hdl.getNetName(comp, 7 + (2 * stage), true, nets));
					}
					map.put("q", vector.toString());
				} else {
					vector.setLength(0);
					for (var bit = nrOfBits - 1; bit >= 0; bit--) {
						for (var stage = nrOfStages - 1; stage >= 0; stage--) {
							if (vector.length() != 0) vector.append(",");
							vector.append(Hdl.getBusEntryName(comp, 6 + (2 * stage), true, bit, nets));
						}
					}
					map.put("d", vector.toString());
					vector.setLength(0);
					for (var bit = nrOfBits - 1; bit >= 0; bit--) {
						if (vector.length() != 0) vector.append(",");
						vector.append("open");
						for (var stage = nrOfStages - 2; stage >= 0; stage--) {
							if (vector.length() != 0) vector.append(",");
							vector.append(Hdl.getBusEntryName(comp, 7 + (2 * stage), true, bit, nets));
						}
					}
					map.put("q", vector.toString());
				}
			} else {
				map.put("d", Hdl.getConstantVector(0, nrOfBits * nrOfStages));
				map.put("q", Hdl.unconnected(true));
			}
		}
		return map;
	}

	@Override
	public List<String> getArchitecture(Netlist nets, AttributeSet attrs, String componentName) {
		final var contents = LineBuffer.getHdlBuffer()
				.pair("clock", HdlPorts.getClockName(1))
				.pair("tick", HdlPorts.getTickName(1))
				.pair("nrOfStages", NR_OF_STAGES_STRING)
				.pair("invertClock", NEGATE_CLOCK_STRING)
				.add(super.getArchitecture(nets, attrs, componentName))
				.empty(3);
			contents
					.add(
							"module singleBitShiftReg ( reset,\n"+
							"                            {{tick}},\n"+
							"                            {{clock}},\n"+
							"                            shiftEnable,\n"+
							"                            parLoad,\n"+
							"                            shiftIn,\n"+
							"                            d,\n"+
							"                            shiftOut,\n"+
							"                            q);\n\n"+

							"    parameter {{nrOfStages}} = 1;\n"+
							"    parameter {{invertClock}} = 1;\n\n"+

							"    input reset;\n"+
							"    input {{tick}};\n"+
							"    input {{clock}};\n"+
							"    input shiftEnable;\n"+
							"    input parLoad;\n"+
							"    input shiftIn;\n"+
							"    input[{{nrOfStages}}:0] d;\n"+
							"    output shiftOut;\n"+
							"    output[{{nrOfStages}}:0] q;\n\n"+

							"    wire[{{nrOfStages}}:0] s_stateNext;\n"+
							"    wire s_clock;\n"+
							"    reg[{{nrOfStages}}:0] s_stateReg;\n\n"+

							"    assign q        = s_stateReg;\n"+
							"    assign shiftOut = s_stateReg[{{nrOfStages}}-1];\n"+
							"    assign s_clock  = {{invertClock}} == 0 ? {{clock}} : ~{{clock}};\n"+
							"    assign s_stateNext = (parLoad) ? d : {s_stateReg[{{nrOfStages}}-2:0],shiftIn};\n\n"+

							"    always @(posedge s_clock or posedge reset)\n"+
							"    begin\n"+
							"        if (reset) s_stateReg <= 0;\n"+
							"        else if ((shiftEnable|parLoad)&{{tick}}) s_stateReg <= s_stateNext;\n"+
							"    end\n\n"+

							"endmodule"
							);

		contents.empty();
		return contents.get();
	}

	@Override
	public LineBuffer getModuleFunctionality(Netlist nets, AttributeSet attrs) {
		final var contents = LineBuffer.getHdlBuffer()
				.pair("clock", HdlPorts.getClockName(1))
				.pair("tick", HdlPorts.getTickName(1))
				.pair("nrOfStages", NR_OF_STAGES_STRING)
				.pair("invertClock", NEGATE_CLOCK_STRING)
				.pair("nrOfBits", NR_OF_BITS_STRING);

		contents.add(
				"genvar n;\n"+
				"generate\n"+
				"    for (n = 0 ; n < {{nrOfBits}}; n=n+1)\n"+
				"    begin:Bit\n"+
				"        singleBitShiftReg #(.{{invertClock}}({{invertClock}}),\n"+
				"                            .{{nrOfStages}}({{nrOfStages}}))\n"+
				"        OneBit (.reset(reset),\n"+
				"                .{{tick}}({{tick}}),\n"+
				"                .{{clock}}({{clock}}),\n"+
				"                .shiftEnable(shiftEnable),\n"+
				"                .parLoad(parLoad),\n"+
				"                .shiftIn(shiftIn[n]),\n"+
				"                .d(d[((n+1)*{{nrOfStages}})-1:(n*{{nrOfStages}})]),\n"+
				"                .shiftOut(shiftOut[n]),\n"+
				"                .q(q[((n+1)*{{nrOfStages}})-1:(n*{{nrOfStages}})]) );\n"+
				"    end\n"+
				"endgenerate"
				);

		return contents.empty();
	}
}
