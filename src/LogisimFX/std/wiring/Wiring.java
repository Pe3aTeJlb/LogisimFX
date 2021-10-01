/* Copyright (c) 2011, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package LogisimFX.std.wiring;

import LogisimFX.data.Attribute;
import LogisimFX.data.AttributeOption;
import LogisimFX.data.Attributes;
import LogisimFX.std.LC;
import LogisimFX.tools.AddTool;
import LogisimFX.tools.FactoryDescription;
import LogisimFX.tools.Library;
import LogisimFX.tools.Tool;
import LogisimFX.circuit.SplitterFactory;
import javafx.beans.binding.StringBinding;

import java.util.ArrayList;
import java.util.List;

public class Wiring extends Library {

	static final AttributeOption GATE_TOP_LEFT
		= new AttributeOption("tl", LC.createStringBinding("wiringGateTopLeftOption"));
	static final AttributeOption GATE_BOTTOM_RIGHT
		= new AttributeOption("br", LC.createStringBinding("wiringGateBottomRightOption"));
	static final Attribute<AttributeOption> ATTR_GATE = Attributes.forOption("gate",
			LC.createStringBinding("wiringGateAttr"),
			new AttributeOption[] { GATE_TOP_LEFT, GATE_BOTTOM_RIGHT });

	private static Tool[] ADD_TOOLS = {
		new AddTool(SplitterFactory.instance),
		new AddTool(Pin.FACTORY),
		new AddTool(Probe.FACTORY),
		new AddTool(Tunnel.FACTORY),
		new AddTool(PullResistor.FACTORY),
		new AddTool(Clock.FACTORY),
		new AddTool(Constant.FACTORY),
	};
	
	private static FactoryDescription[] DESCRIPTIONS = {
		new FactoryDescription("Power", LC.createStringBinding("powerComponent"),
				"power.gif", "Power"),
		new FactoryDescription("Ground", LC.createStringBinding("groundComponent"),
				"ground.gif", "Ground"),
		new FactoryDescription("Transistor", LC.createStringBinding("transistorComponent"),
				"trans0.gif", "Transistor"),
		new FactoryDescription("Transmission Gate", LC.createStringBinding("transmissionGateComponent"),
				"transmis.gif", "TransmissionGate"),
		new FactoryDescription("Bit Extender", LC.createStringBinding("extenderComponent"),
				"extender.gif", "BitExtender"),
	};

	private List<Tool> tools = null;

	public Wiring() { }

	@Override
	public String getName() { return "Wiring"; }

	@Override
	public StringBinding getDisplayName() { return LC.createStringBinding("wiringLibrary"); }

	@Override
	public List<Tool> getTools() {

		if (tools == null) {
			List<Tool> ret = new ArrayList<Tool>(ADD_TOOLS.length + DESCRIPTIONS.length);
			for (Tool a : ADD_TOOLS) {
				ret.add(a);
			}
			ret.addAll(FactoryDescription.getTools(Wiring.class, DESCRIPTIONS));
			tools = ret;
		}

		return tools;

	}

}
