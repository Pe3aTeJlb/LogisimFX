/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package LogisimFX.std.gates;

import LogisimFX.data.AttributeSet;
import LogisimFX.fpga.designrulecheck.Netlist;
import LogisimFX.fpga.designrulecheck.netlistComponent;
import LogisimFX.fpga.hdlgenerator.Hdl;
import LogisimFX.fpga.hdlgenerator.InlinedHdlGeneratorFactory;
import LogisimFX.instance.StdAttr;
import LogisimFX.util.LineBuffer;

public class AbstractBufferHdlGenerator extends InlinedHdlGeneratorFactory {

	private final boolean isInverter;

	public AbstractBufferHdlGenerator(boolean isInverter) {
		this.isInverter = isInverter;
	}

	@Override
	public LineBuffer getInlinedCode(
			Netlist nets, Long componentId, netlistComponent componentInfo, String circuitName) {
		final var nrOfBits =
				componentInfo.getComponent().getAttributeSet().getValue(StdAttr.WIDTH).getWidth();
		final var dest =
				(nrOfBits == 1)
						? Hdl.getNetName(componentInfo, 0, false, nets)
						: Hdl.getBusName(componentInfo, 0, nets);
		final var source =
				(nrOfBits == 1)
						? Hdl.getNetName(componentInfo, 1, false, nets)
						: Hdl.getBusName(componentInfo, 1, nets);
		return !componentInfo.isEndConnected(0)
				? LineBuffer.getBuffer()
				: LineBuffer.getHdlBuffer()
				.add("{{assign}}{{1}}{{=}}{{2}}{{3}};", dest, isInverter ? Hdl.notOperator() : "", source);
	}

	@Override
	public boolean isHdlSupportedTarget(AttributeSet attrs) {
		var supported = true;
		if (attrs.containsAttribute(GateAttributes.ATTR_OUTPUT))
			supported = attrs.getValue(GateAttributes.ATTR_OUTPUT).equals(GateAttributes.OUTPUT_01);
		return supported;
	}
}
