/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.instance;

import com.cburch.LogisimFX.data.*;
import com.cburch.LogisimFX.std.LC;

import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public interface StdAttr {

	Attribute<Direction> FACING = Attributes.forDirection("facing", LC.createStringBinding("stdFacingAttr"));

	Attribute<BitWidth> WIDTH = Attributes.forBitWidth("width", LC.createStringBinding("stdDataWidthAttr"));

	AttributeOption TRIG_RISING = new AttributeOption("rising", LC.createStringBinding("stdTriggerRising"));
	AttributeOption TRIG_FALLING = new AttributeOption("falling", LC.createStringBinding("stdTriggerFalling"));
	AttributeOption TRIG_HIGH = new AttributeOption("high", LC.createStringBinding("stdTriggerHigh"));
	AttributeOption TRIG_LOW = new AttributeOption("low", LC.createStringBinding("stdTriggerLow"));
	Attribute<AttributeOption> TRIGGER = Attributes.forOption("trigger", LC.createStringBinding("stdTriggerAttr"),
			new AttributeOption[] {
				TRIG_RISING, TRIG_FALLING, TRIG_HIGH, TRIG_LOW
			});
	Attribute<AttributeOption> EDGE_TRIGGER
		= Attributes.forOption("trigger", LC.createStringBinding("stdTriggerAttr"),
			new AttributeOption[] { TRIG_RISING, TRIG_FALLING });

	Attribute<String> LABEL = Attributes.forString("label", LC.createStringBinding("stdLabelAttr"));

	Attribute<Font> LABEL_FONT = Attributes.forFont("labelfont", LC.createStringBinding("stdLabelFontAttr"));
	Font DEFAULT_LABEL_FONT = Font.font("SansSerif", FontWeight.NORMAL, FontPosture.REGULAR, 12);

}
