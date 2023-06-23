/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package LogisimFX.fpga.hdlgenerator;

import LogisimFX.data.AttributeSet;
import LogisimFX.fpga.designrulecheck.Netlist;
import LogisimFX.fpga.designrulecheck.netlistComponent;
import LogisimFX.proj.Project;
import LogisimFX.util.LineBuffer;

import java.util.List;
import java.util.Set;

public interface HdlGeneratorFactory {

	String NET_NAME = Hdl.NET_NAME;
	String BUS_NAME = Hdl.BUS_NAME;
	String CLOCK_TREE_NAME = "logisimfxClockTree";
	String VERILOG = "Verilog";
	String LOCAL_INPUT_BUBBLE_BUS_NAME = "logisimfxInputBubbles";
	String LOCAL_OUTPUT_BUBBLE_BUS_NAME = "logisimfxOutputBubbles";
	String LOCAL_INOUT_BUBBLE_BUS_NAME = "logisimfxInOutBubbles";
	String FPGA_TOP_LEVEL_NAME = "TopLevelShell";

	boolean generateAllHDLDescriptions(
			Project proj, Set<String> handledComponents, String workingDirectory, List<String> hierarchy);

	List<String> getArchitecture(Netlist theNetlist, AttributeSet attrs, String componentName);

	LineBuffer getComponentMap(Netlist nets, Long componentId, Object componentInfo, String name);

	LineBuffer getInlinedCode(Netlist nets, Long componentId, netlistComponent componentInfo, String circuitName);

	String getRelativeDirectory();

	boolean isHdlSupportedTarget(AttributeSet attrs);

	boolean isOnlyInlined();

}
