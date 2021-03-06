/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.tools.key;

import LogisimFX.data.Attribute;
import LogisimFX.data.AttributeSet;
import javafx.scene.input.KeyEvent;

public abstract class NumericConfigurator<V> implements KeyConfigurator, Cloneable {

	private static final int MAX_TIME_KEY_LASTS = 800;
	
	private Attribute<V> attr;
	private int minValue;
	private int maxValue;
	private int curValue;
	private int radix;
	private KeyEvent modsEx;
	private long whenTyped;
	
	public NumericConfigurator(Attribute<V> attr, int min, int max, KeyEvent modifiersEx) {
		this(attr, min, max, modifiersEx, 10);
	}
	
	public NumericConfigurator(Attribute<V> attr, int min, int max,
							   KeyEvent modifiersEx, int radix) {

		this.attr = attr;
		this.minValue = min;
		this.maxValue = max;
		this.radix = radix;
		this.modsEx = modifiersEx;
		this.curValue = 0;
		this.whenTyped = 0;

	}
	
	@Override
	public NumericConfigurator<V> clone() {

		try {
			@SuppressWarnings("unchecked")
            NumericConfigurator<V> ret = (NumericConfigurator<V>) super.clone();
			ret.whenTyped = 0;
			ret.curValue = 0;
			return ret;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}

	}
	
	protected int getMinimumValue(AttributeSet attrs) {
		return minValue;
	}
	
	protected int getMaximumValue(AttributeSet attrs) {
		return maxValue;
	}
	
	protected abstract V createValue(int value);
	
	public KeyConfigurationResult keyEventReceived(KeyConfigurationEvent event) {

		if (event.getType() == KeyConfigurationEvent.KEY_TYPED) {
			KeyEvent e = event.getKeyEvent();
			int digit = Character.digit(e.getCharacter().toCharArray()[0], radix);
			if (digit >= 0 && e == modsEx) {
				long now = System.currentTimeMillis();
				long sinceLast = now - whenTyped;
				AttributeSet attrs = event.getAttributeSet();
				int min = getMinimumValue(attrs);
				int max = getMaximumValue(attrs);
				int val = 0;
				if (sinceLast < MAX_TIME_KEY_LASTS) {
					val = radix * curValue;
					if (val > max) {
						val = 0;
					}
				}
				val += digit;
				if (val > max) {
					val = digit;
					if (val > max) {
						return null;
					}
				}
				event.consume();
				whenTyped = now;
				curValue = val;
	
				if (val >= min) {
					Object valObj = createValue(val);
					return new KeyConfigurationResult(event, attr, valObj);
				}
			}
		}

		return null;

	}

}
