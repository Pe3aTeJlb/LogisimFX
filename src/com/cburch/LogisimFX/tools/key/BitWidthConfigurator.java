/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.tools.key;

import com.cburch.LogisimFX.KeyEvents;
import com.cburch.LogisimFX.data.Attribute;
import com.cburch.LogisimFX.data.BitWidth;
import com.cburch.LogisimFX.data.Value;
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
