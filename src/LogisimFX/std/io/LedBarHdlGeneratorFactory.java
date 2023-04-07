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
import LogisimFX.fpga.designrulecheck.netlistComponent;
import LogisimFX.fpga.hdlgenerator.Hdl;
import LogisimFX.fpga.hdlgenerator.InlinedHdlGeneratorFactory;
import LogisimFX.util.LineBuffer;

import java.util.HashMap;

public class LedBarHdlGeneratorFactory extends InlinedHdlGeneratorFactory {

  @Override
  public LineBuffer getInlinedCode(
      Netlist netlist, Long componentId, netlistComponent componentInfo, String circuitName) {
    final var contents = LineBuffer.getHdlBuffer();
    final var isSingleBus =
        componentInfo
            .getComponent()
            .getAttributeSet()
            .getValue(LedBar.ATTR_INPUT_TYPE)
            .equals(LedBar.INPUT_ONE_WIRE);
    final var nrOfSegments =
        componentInfo.getComponent().getAttributeSet().getValue(LedBar.ATTR_MATRIX_COLS).getWidth();
    final var wires = new HashMap<String, String>();
    for (var pin = 0; pin < nrOfSegments; pin++) {
      final var destPin =
          LineBuffer.format(
              "{{1}}{{<}}{{2}}{{>}}",
              LOCAL_OUTPUT_BUBBLE_BUS_NAME, componentInfo.getLocalBubbleOutputStartId() + pin);
      final var sourcePin =
          isSingleBus
              ? Hdl.getBusEntryName(componentInfo, 0, true, pin, netlist)
              : Hdl.getNetName(componentInfo, pin, true, netlist);
      wires.put(destPin, sourcePin);
    }
    Hdl.addAllWiresSorted(contents, wires);
    return contents;
  }

  @Override
  public boolean isHdlSupportedTarget(AttributeSet attrs) {
    return attrs.getValue(DotMatrixBase.ATTR_PERSIST) == 0;
  }

}
