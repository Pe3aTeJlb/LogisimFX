/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.arith;

import java.util.List;

import LogisimFX.std.LC;
import LogisimFX.tools.FactoryDescription;
import LogisimFX.tools.Library;
import LogisimFX.tools.Tool;

import javafx.beans.binding.StringBinding;

public class Arithmetic extends Library {

	private static FactoryDescription[] DESCRIPTIONS = {

		new FactoryDescription("Adder", LC.createStringBinding("adderComponent"),
				"adder.gif", "Adder"),
		new FactoryDescription("Subtractor", LC.createStringBinding("subtractorComponent"),
				"subtractor.gif", "Subtractor"),
		new FactoryDescription("Multiplier", LC.createStringBinding("multiplierComponent"),
				"multiplier.gif", "Multiplier"),
		new FactoryDescription("Divider", LC.createStringBinding("dividerComponent"),
				"divider.gif", "Divider"),
		new FactoryDescription("Negator", LC.createStringBinding("negatorComponent"),
				"negator.gif", "Negator"),
		new FactoryDescription("Comparator", LC.createStringBinding("comparatorComponent"),
				"comparator.gif", "Comparator"),
		new FactoryDescription("Shifter", LC.createStringBinding("shifterComponent"),
				"shifter.gif", "Shifter"),
		new FactoryDescription("BitAdder", LC.createStringBinding("bitAdderComponent"),
				"bitadder.gif", "BitAdder"),
		new FactoryDescription("BitFinder", LC.createStringBinding("bitFinderComponent"),
				"bitfindr.gif", "BitFinder"),

	};
	
	private List<Tool> tools = null;

	public Arithmetic() { }

	@Override
	public String getName() { return "Arithmetic"; }

	@Override
	public StringBinding getDisplayName() { return LC.createStringBinding("arithmeticLibrary"); }

	@Override
	public List<Tool> getTools() {

		if (tools == null) {
			tools = FactoryDescription.getTools(Arithmetic.class, DESCRIPTIONS);
		}

		return tools;

	}

}
