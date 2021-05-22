/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.std.memory;

import java.util.List;

import com.cburch.LogisimFX.std.LC;
import com.cburch.LogisimFX.tools.FactoryDescription;
import com.cburch.LogisimFX.tools.Library;
import com.cburch.LogisimFX.tools.Tool;
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
