/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.tools.key;

import LogisimFX.KeyEvents;
import LogisimFX.data.Attribute;
import LogisimFX.data.BitWidth;
import LogisimFX.data.Value;
import javafx.scene.input.KeyEvent;

public class BitWidthConfigurator extends NumericConfigurator<BitWidth> {
	public BitWidthConfigurator(Attribute<BitWidth> attr, int min, int max, KeyEvent modifiersEx) {
		super(attr, min, max, modifiersEx);
	}
	
	public BitWidthConfigurator(Attribute<BitWidth> attr, int min, int max) {
		super(attr, min, max, KeyEvents.ALT_DOWN);
	}
	
	public BitWidthConfigurator(Attribute<BitWidth> attr) {
		super(attr, 1, Value.MAX_WIDTH, KeyEvents.ALT_DOWN);
	}
	
	@Override
	protected BitWidth createValue(int val) {
		return BitWidth.create(val);
	}
}
