/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.gates;

import LogisimFX.std.LC;
import LogisimFX.tools.AddTool;
import LogisimFX.tools.Library;
import LogisimFX.tools.Tool;
import javafx.beans.binding.StringBinding;

import java.util.Arrays;
import java.util.List;

public class Gates extends Library {

	private List<Tool> tools = null;

	public Gates() {
		tools = Arrays.asList(new Tool[] {
			new AddTool(NotGate.FACTORY),
			new AddTool(Buffer.FACTORY),
			new AddTool(AndGate.FACTORY),
			new AddTool(OrGate.FACTORY),
			new AddTool(NandGate.FACTORY),
			new AddTool(NorGate.FACTORY),
			new AddTool(XorGate.FACTORY),
			new AddTool(XnorGate.FACTORY),
			new AddTool(OddParityGate.FACTORY),
			new AddTool(EvenParityGate.FACTORY),
			new AddTool(ControlledBuffer.FACTORY_BUFFER),
			new AddTool(ControlledBuffer.FACTORY_INVERTER),
		});
	}

	@Override
	public String getName() {
		return "Gates";
	}

	@Override
	public StringBinding getDisplayName() {
		return LC.createStringBinding("gatesLibrary");
	}

	@Override
	public List<Tool> getTools() {
		return tools;
	}

}
