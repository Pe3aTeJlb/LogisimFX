/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package LogisimFX.tools.key;

import LogisimFX.data.Attribute;
import javafx.scene.input.KeyEvent;

public class IntegerConfigurator extends NumericConfigurator<Integer> {

	public IntegerConfigurator(Attribute<Integer> attr, int min, int max, KeyEvent modifiersEx) {
		super(attr, min, max, modifiersEx);
	}
	
	public IntegerConfigurator(Attribute<Integer> attr, int min, int max,
							   KeyEvent modifiersEx, int radix) {
		super(attr, min, max, modifiersEx, radix);
	}
	
	@Override
	protected Integer createValue(int val) {
		return Integer.valueOf(val);
	}

}
