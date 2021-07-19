/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.instance;

import com.cburch.LogisimFX.data.*;
import com.cburch.LogisimFX.std.LC;

import java.awt.*;

public interface StdAttr {
	public static final Attribute<Direction> FACING
		= Attributes.forDirection("facing", LC.createStringBinding("stdFacingAttr"));

	public static final Attribute<BitWidth> WIDTH
		= Attributes.forBitWidth("width", LC.createStringBinding("stdDataWidthAttr"));

	public static final AttributeOption TRIG_RISING
		= new AttributeOption("rising", LC.createStringBinding("stdTriggerRising"));
	public static final AttributeOption TRIG_FALLING
		= new AttributeOption("falling", LC.createStringBinding("stdTriggerFalling"));
	public static final AttributeOption TRIG_HIGH
		= new AttributeOption("high", LC.createStringBinding("stdTriggerHigh"));
	public static final AttributeOption TRIG_LOW
		= new AttributeOption("low", LC.createStringBinding("stdTriggerLow"));
	public static final Attribute<AttributeOption> TRIGGER
		= Attributes.forOption("trigger", LC.createStringBinding("stdTriggerAttr"),
			new AttributeOption[] {
				TRIG_RISING, TRIG_FALLING, TRIG_HIGH, TRIG_LOW
			});
	public static final Attribute<AttributeOption> EDGE_TRIGGER
		= Attributes.forOption("trigger", LC.createStringBinding("stdTriggerAttr"),
			new AttributeOption[] { TRIG_RISING, TRIG_FALLING });

	public static final Attribute<String> LABEL
		= Attributes.forString("label", LC.createStringBinding("stdLabelAttr"));

	public static final Attribute<Font> LABEL_FONT
		= Attributes.forFont("labelfont", LC.createStringBinding("stdLabelFontAttr"));
	public static final Font DEFAULT_LABEL_FONT
		= new Font("SansSerif", Font.PLAIN, 12);
}
