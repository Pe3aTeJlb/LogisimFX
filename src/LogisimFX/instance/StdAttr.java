/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.instance;

import LogisimFX.data.*;
import LogisimFX.std.LC;

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

	AttributeOption EDITING_MODE = new AttributeOption("editingmode", LC.createStringBinding("stdEditingMode"));
	AttributeOption PROTECTION_MODE = new AttributeOption("protectionmode", LC.createStringBinding("stdProtectionMode"));

	Attribute<AttributeOption> ACCESS_MODE =
			Attributes.forOption("accessmode", LC.createStringBinding("stdAccessModeAttr"),
					new AttributeOption[]{EDITING_MODE, PROTECTION_MODE});

}
