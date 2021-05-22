/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.std.io;

import com.cburch.LogisimFX.data.Attribute;
import com.cburch.LogisimFX.data.AttributeOption;
import com.cburch.LogisimFX.data.Attributes;
import com.cburch.LogisimFX.data.Direction;
import com.cburch.LogisimFX.std.LC;
import com.cburch.LogisimFX.tools.FactoryDescription;
import com.cburch.LogisimFX.tools.Library;
import com.cburch.LogisimFX.tools.Tool;
import javafx.beans.binding.StringBinding;

import java.awt.*;
import java.util.List;

public class Io extends Library {
	static final AttributeOption LABEL_CENTER = new AttributeOption("center", "center", Strings.getter("ioLabelCenter"));
	
	static final Attribute<Color> ATTR_COLOR = Attributes.forColor("color",
			Strings.getter("ioColorAttr"));
	static final Attribute<Color> ATTR_ON_COLOR
		= Attributes.forColor("color", Strings.getter("ioOnColor"));
	static final Attribute<Color> ATTR_OFF_COLOR
		= Attributes.forColor("offcolor", Strings.getter("ioOffColor"));
	static final Attribute<Color> ATTR_BACKGROUND
		= Attributes.forColor("bg", Strings.getter("ioBackgroundColor"));
	static final Attribute<Object> ATTR_LABEL_LOC = Attributes.forOption("labelloc",
			Strings.getter("ioLabelLocAttr"),
			new Object[] { LABEL_CENTER, Direction.NORTH, Direction.SOUTH,
				Direction.EAST, Direction.WEST });
	static final Attribute<Color> ATTR_LABEL_COLOR = Attributes.forColor("labelcolor",
			Strings.getter("ioLabelColorAttr"));
	static final Attribute<Boolean> ATTR_ACTIVE = Attributes.forBoolean("active",
			Strings.getter("ioActiveAttr"));

	static final Color DEFAULT_BACKGROUND = new Color(255, 255, 255, 0);
	
	private static FactoryDescription[] DESCRIPTIONS = {
		new FactoryDescription("Button", LC.createStringBinding("buttonComponent"),
				"button.gif", "Button"),
		new FactoryDescription("Joystick", LC.createStringBinding("joystickComponent"),
				"joystick.gif", "Joystick"),
		new FactoryDescription("Keyboard", LC.createStringBinding("keyboardComponent"),
				"keyboard.gif", "Keyboard"),
		new FactoryDescription("LED", LC.createStringBinding("ledComponent"),
				"led.gif", "Led"),
		new FactoryDescription("7-Segment Display", LC.createStringBinding("sevenSegmentComponent"),
				"7seg.gif", "SevenSegment"),
		new FactoryDescription("Hex Digit Display", LC.createStringBinding("hexDigitComponent"),
				"hexdig.gif", "HexDigit"),
		new FactoryDescription("DotMatrix", LC.createStringBinding("dotMatrixComponent"),
				"dotmat.gif", "DotMatrix"),
		new FactoryDescription("TTY", LC.createStringBinding("ttyComponent"),
				"tty.gif", "Tty"),
	};

	private List<Tool> tools = null;

	public Io() { }

	@Override
	public String getName() { return "I/O"; }

	@Override
	public StringBinding getDisplayName() { return LC.createStringBinding("ioLibrary"); }

	@Override
	public List<Tool> getTools() {
		if (tools == null) {
			tools = FactoryDescription.getTools(Io.class, DESCRIPTIONS);
		}
		return tools;
	}
}
