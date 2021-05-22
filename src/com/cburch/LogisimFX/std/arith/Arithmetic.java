/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.std.arith;

import java.util.List;

import com.cburch.LogisimFX.std.LC;
import com.cburch.LogisimFX.tools.FactoryDescription;
import com.cburch.LogisimFX.tools.Library;
import com.cburch.LogisimFX.tools.Tool;
import javafx.beans.binding.StringBinding;

public class Arithmetic extends Library {

	private static FactoryDescription[] DESCRIPTIONS = {

		new FactoryDescription("Adder", Strings.getter("adderComponent"),
				"adder.gif", "Adder"),
		new FactoryDescription("Subtractor", Strings.getter("subtractorComponent"),
				"subtractor.gif", "Subtractor"),
		new FactoryDescription("Multiplier", Strings.getter("multiplierComponent"),
				"multiplier.gif", "Multiplier"),
		new FactoryDescription("Divider", Strings.getter("dividerComponent"),
				"divider.gif", "Divider"),
		new FactoryDescription("Negator", Strings.getter("negatorComponent"),
				"negator.gif", "Negator"),
		new FactoryDescription("Comparator", Strings.getter("comparatorComponent"),
				"comparator.gif", "Comparator"),
		new FactoryDescription("Shifter", Strings.getter("shifterComponent"),
				"shifter.gif", "Shifter"),
		new FactoryDescription("BitAdder", Strings.getter("bitAdderComponent"),
				"bitadder.gif", "BitAdder"),
		new FactoryDescription("BitFinder", Strings.getter("bitFinderComponent"),
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
