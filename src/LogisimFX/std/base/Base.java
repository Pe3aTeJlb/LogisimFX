/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package LogisimFX.std.base;

import java.util.Arrays;
import java.util.List;

import LogisimFX.std.LC;
import LogisimFX.tools.*;

import javafx.beans.binding.StringBinding;

public class Base extends Library {

	private List<Tool> tools;

	public Base() {

		SelectTool select = new SelectTool();
		WiringTool wiring = new WiringTool();
		
		tools = Arrays.asList(
				new PokeTool(),
				new EditTool(select, wiring),
				select,
				wiring,
				new TextTool(),
				new MenuTool(),
				new AddTool(Text.FACTORY));
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
