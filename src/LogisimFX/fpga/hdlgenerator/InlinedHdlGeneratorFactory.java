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

public class InlinedHdlGeneratorFactory implements HdlGeneratorFactory {

	@Override
	public boolean generateAllHDLDescriptions(
			Project proj, Set<String> handledComponents, String workingDirectory, List<String> hierarchy) {
		throw new IllegalAccessError("BUG: generateAllHDLDescriptions not supported");
	}

	@Override
	public List<String> getArchitecture(Netlist theNetlist, AttributeSet attrs, String componentName) {
		throw new IllegalAccessError("BUG: getArchitecture not supported");
	}

	@Override
	public LineBuffer getComponentMap(
			Netlist nets, Long componentId, Object componentInfo, String name) {
		throw new IllegalAccessError("BUG: getComponentMap not supported");
	}

	@Override
	public LineBuffer getInlinedCode(
			Netlist nets, Long componentId, netlistComponent componentInfo, String circuitName) {
		return LineBuffer.getHdlBuffer();
	}

	@Override
	public String getRelativeDirectory() {
		throw new IllegalAccessError("BUG: getRelativeDirectory not supported");
	}

	@Override
	public boolean isHdlSupportedTarget(AttributeSet attrs) {
		return true;
	}

	@Override
	public boolean isOnlyInlined() {
		return true;
	}
}
