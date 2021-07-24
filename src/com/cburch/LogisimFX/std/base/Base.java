/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.std.base;

import java.util.Arrays;
import java.util.List;

import com.cburch.LogisimFX.std.LC;
import com.cburch.LogisimFX.tools.*;

import javafx.beans.binding.StringBinding;

public class Base extends Library {

	private List<Tool> tools = null;

	public Base() {

		SelectTool select = new SelectTool();
		WiringTool wiring = new WiringTool();
		
		tools = Arrays.asList(new Tool[] {
			new PokeTool(),
			new EditTool(select, wiring),
			select,
			wiring,
			new TextTool(),
			new MenuTool(),
			new AddTool(Text.FACTORY),
		});

	}

	@Override
	public String getName() { return "Base"; }

	@Override
	public StringBinding getDisplayName() { return LC.createStringBinding("baseLibrary"); }

	@Override
	public List<Tool> getTools() {
		return tools;
	}

}
