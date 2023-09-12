package LogisimFX.std.yosys;

import LogisimFX.std.LC;
import LogisimFX.tools.FactoryDescription;
import LogisimFX.tools.Library;
import LogisimFX.tools.Tool;
import javafx.beans.binding.StringBinding;

import java.util.List;

public class YosysSpecial extends Library {

	private List<Tool> tools;

	private static FactoryDescription[] DESCRIPTIONS = {

			new FactoryDescription("ReduceAnd", LC.createStringBinding("reduceAndComponent"),
					"yosysReduceAnd.gif", "ReduceAnd")

	};

	public YosysSpecial() {
	}

	@Override
	public String getName() { return "Yosys"; }

	@Override
	public StringBinding getDisplayName() { return LC.createStringBinding("stdYosysSpecial"); }

	@Override
	public List<Tool> getTools() {

		if (tools == null) {
			tools = FactoryDescription.getTools(YosysSpecial.class, DESCRIPTIONS);
		}

		return tools;

	}

}
