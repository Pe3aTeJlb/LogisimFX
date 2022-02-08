/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

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
