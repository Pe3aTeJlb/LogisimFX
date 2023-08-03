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
import LogisimFX.fpga.hdlgenerator.Hdl;
import LogisimFX.fpga.hdlgenerator.InlinedHdlGeneratorFactory;
import LogisimFX.fpga.hdlgenerator.WithSelectHdlGenerator;
import LogisimFX.instance.StdAttr;
import LogisimFX.util.LineBuffer;

public class RomHdlGeneratorFactory extends InlinedHdlGeneratorFactory {

	@Override
	public LineBuffer getInlinedCode(
			Netlist nets, Long componentId, netlistComponent componentInfo, String circuitName) {
		AttributeSet attrs = componentInfo.getComponent().getAttributeSet();
		final var addressWidth = attrs.getValue(Mem.ADDR_ATTR).getWidth();
		final var dataWidth = attrs.getValue(Mem.DATA_ATTR).getWidth();
		final var romContents = attrs.getValue(Rom.CONTENTS_ATTR);

		if (componentInfo.isEndConnected(Mem.ADDR) && componentInfo.isEndConnected(Mem.DATA)){
			final var generator =
					(new WithSelectHdlGenerator(
							componentInfo.getComponent().getAttributeSet().getValue(StdAttr.LABEL),
							Hdl.getBusName(componentInfo, Mem.ADDR, nets),
							addressWidth,
							Hdl.getBusName(componentInfo, Mem.DATA, nets),
							dataWidth))
							.setDefault(0L);
			for (var addr = 0L; addr < (1L << addressWidth); addr++) {
				final var romValue = romContents.get(addr);
				if (romValue != 0L) generator.add(addr, (long) romValue);
			}
			return LineBuffer.getBuffer().add(generator.getHdlCode());
		} else {
			return LineBuffer.getBuffer();
		}
	}

	@Override
	public boolean isHdlSupportedTarget(AttributeSet attrs) {
		return attrs != null;
	}
}
