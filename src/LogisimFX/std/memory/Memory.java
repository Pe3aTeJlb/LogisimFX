/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.memory;

import java.util.List;

import LogisimFX.std.LC;
import LogisimFX.tools.FactoryDescription;
import LogisimFX.tools.Library;
import LogisimFX.tools.Tool;
import javafx.beans.binding.StringBinding;

public class Memory extends Library {

	protected static final int DELAY = 5;
	
	private static FactoryDescription[] DESCRIPTIONS = {
		new FactoryDescription("D Flip-Flop", LC.createStringBinding("dFlipFlopComponent"),
				"dFlipFlop.gif", "DFlipFlop"),
		new FactoryDescription("T Flip-Flop", LC.createStringBinding("tFlipFlopComponent"),
				"tFlipFlop.gif", "TFlipFlop"),
		new FactoryDescription("J-K Flip-Flop", LC.createStringBinding("jkFlipFlopComponent"),
				"jkFlipFlop.gif", "JKFlipFlop"),
		new FactoryDescription("S-R Flip-Flop", LC.createStringBinding("srFlipFlopComponent"),
				"srFlipFlop.gif", "SRFlipFlop"),
		new FactoryDescription("Register", LC.createStringBinding("registerComponent"),
				"register.gif", "Register"),
		new FactoryDescription("Counter", LC.createStringBinding("counterComponent"),
				"counter.gif", "Counter"),
		new FactoryDescription("Shift Register", LC.createStringBinding("shiftRegisterComponent"),
				"shiftreg.gif", "ShiftRegister"),
		new FactoryDescription("Random", LC.createStringBinding("randomComponent"),
				"random.gif", "Random"),
		new FactoryDescription("RAM", LC.createStringBinding("ramComponent"),
				"ram.gif", "Ram"),
		new FactoryDescription("ROM", LC.createStringBinding("romComponent"),
				"rom.gif", "Rom"),
	};

	private List<Tool> tools = null;

	public Memory() { }

	@Override
	public String getName() { return "Memory"; }

	@Override
	public StringBinding getDisplayName() { return LC.createStringBinding("memoryLibrary"); }

	@Override
	public List<Tool> getTools() {

		if (tools == null) {
			tools = FactoryDescription.getTools(Memory.class, DESCRIPTIONS);
		}

		return tools;

	}

}
