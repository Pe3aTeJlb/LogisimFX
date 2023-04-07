/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package LogisimFX.std.plexers;

import LogisimFX.data.AttributeSet;
import LogisimFX.fpga.designrulecheck.Netlist;
import LogisimFX.fpga.designrulecheck.netlistComponent;
import LogisimFX.fpga.hdlgenerator.AbstractHdlGeneratorFactory;
import LogisimFX.fpga.hdlgenerator.Hdl;
import LogisimFX.fpga.hdlgenerator.HdlParameters;
import LogisimFX.instance.Port;
import LogisimFX.util.LineBuffer;

import java.util.SortedMap;
import java.util.TreeMap;

public class PriorityEncoderHdlGeneratorFactory extends AbstractHdlGeneratorFactory {

	private static final String NR_OF_SELECT_BITS_STRING = "nrOfSelectBits";
	private static final int NR_OF_SELECT_BITS_ID = -1;
	private static final String NR_OF_INPUT_BITS_STRING = "nrOfInputBits";
	private static final int NR_OF_INPUT_BITS_ID = -2;

	public PriorityEncoderHdlGeneratorFactory() {
		super();
		myParametersList
				.add(NR_OF_INPUT_BITS_STRING, NR_OF_INPUT_BITS_ID, HdlParameters.MAP_POW2, Plexers.ATTR_SELECT)
				.add(NR_OF_SELECT_BITS_STRING, NR_OF_SELECT_BITS_ID, HdlParameters.MAP_INT_ATTRIBUTE, Plexers.ATTR_SELECT);
		myWires
				.addWire("s_inIsZero", 1)
				.addWire("s_address", 6)
				.addWire("s_selectVector0", 64)
				.addWire("s_selectVector1", 32)
				.addWire("s_selectVector2", 16)
				.addWire("s_selectVector3", 8)
				.addWire("s_selectVector4", 4);
		myPorts
				.add(Port.INPUT, "enable", 1, 0)
				.add(Port.INPUT, "inputVector", NR_OF_INPUT_BITS_ID, 0)
				.add(Port.OUTPUT, "groupSelect", 1, 0)
				.add(Port.OUTPUT, "enableOut", 1, 0)
				.add(Port.OUTPUT, "address", NR_OF_SELECT_BITS_ID, 0);
	}

	@Override
	public SortedMap<String, String> getPortMap(Netlist nets, Object mapInfo) {
		final var map = new TreeMap<String, String>();
		netlistComponent comp;
		if (!(mapInfo instanceof netlistComponent)) return map;
		else comp = (netlistComponent) mapInfo;
		final var nrOfBits = comp.nrOfEnds() - 4;
		map.putAll(Hdl.getNetMap("enable", false, comp, nrOfBits + PriorityEncoder.EN_IN, nets));
		final var vectorList = new StringBuilder();
		for (var i = nrOfBits - 1; i >= 0; i--) {
			if (vectorList.length() > 0) vectorList.append(",");
			vectorList.append(Hdl.getNetName(comp, i, true, nets));
		}
		map.put("inputVector", vectorList.toString());
		map.putAll(Hdl.getNetMap("groupSelect", true, comp, nrOfBits + PriorityEncoder.GS, nets));
		map.putAll(Hdl.getNetMap("enableOut", true, comp, nrOfBits + PriorityEncoder.EN_OUT, nets));
		map.putAll(Hdl.getNetMap("address", true, comp, nrOfBits + PriorityEncoder.OUT, nets));
		return map;
	}

	@Override
	public LineBuffer getModuleFunctionality(Netlist nets, AttributeSet attrs) {
		final var contents = LineBuffer.getBuffer()
				.pair("selBits", NR_OF_SELECT_BITS_STRING)
				.pair("inBits", NR_OF_INPUT_BITS_STRING);

		contents.add(
						"assign groupSelect = ~s_inIsZero&enable;\n" +
						"assign enableOut = s_inIsZero&enable;\n" +
						"assign address = (~enable) ? 0 : s_address[{{selBits}}-1:0];\n" +
						"assign s_inIsZero = (inputVector == 0) ? 1'b1 : 1'b0;\n\n" +

						"assign s_selectVector0[63:{{selBits}}] = 0;\n" +
						"assign s_selectVector0[{{selBits}}-1:0] = inputVector;\n" +
						"assign s_address[5] = (s_selectVector0[63:32] == 0) ? 1'b0 : 1'b1;\n" +
						"assign s_selectVector1 = (s_selectVector0[63:32] == 0) ? s_selectVector0[31:0] : s_selectVector0[63:32];\n" +
						"assign s_address[4] = (s_selectVector1[31:16] == 0) ? 1'b0 : 1'b1;\n" +
						"assign s_selectVector2 = (s_selectVector1[31:16] == 0) ? s_selectVector1[15:0] : s_selectVector1[31:16];\n" +
						"assign s_address[3] = (s_selectVector2[15:8] == 0) ? 1'b0 : 1'b1;\n" +
						"assign s_selectVector3 = (s_selectVector2[15:8] == 0) ? s_selectVector2[7:0] : s_selectVector2[15:8];\n" +
						"assign s_address[2] = (s_selectVector3[7:4] == 0) ? 1'b0 : 1'b1;\n" +
						"assign s_selectVector4 = (s_selectVector3[7:4] == 0) ? s_selectVector3[3:0] : s_selectVector2[7:4];\n" +
						"assign s_address[1] = (s_selectVector4[3:2] == 0) ? 1'b0 : 1'b1;\n" +
						"assign s_address[0] = (s_selectVector4[3:2] == 0) ? s_selectVector4[1] : s_selectVector4[3];"

		);

		return contents.empty();
	}
}
