/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.memory;

import java.util.List;
import java.util.Objects;

import LogisimFX.data.*;
import LogisimFX.instance.StdAttr;

public class CounterAttributes extends AbstractAttributeSet {

	private AttributeSet base;
	
	public CounterAttributes() {

		base = AttributeSets.fixedSet(new Attribute<?>[] {
				StdAttr.FPGA_SUPPORTED,
				StdAttr.WIDTH, Counter.ATTR_MAX, Counter.ATTR_ON_GOAL,
				StdAttr.EDGE_TRIGGER,
				StdAttr.LABEL, StdAttr.LABEL_FONT
			}, new Object[] {
				Boolean.FALSE,
				BitWidth.create(8), Integer.valueOf(0xFF),
				Counter.ON_GOAL_WRAP,
				StdAttr.TRIG_RISING,
				"", StdAttr.DEFAULT_LABEL_FONT
			});

	}

	@Override
	public void copyInto(AbstractAttributeSet dest) {
		((CounterAttributes) dest).base = (AttributeSet) this.base.clone();
	}

	@Override
	public List<Attribute<?>> getAttributes() {
		return base.getAttributes();
	}

	@Override
	public <V> V getValue(Attribute<V> attr) {
		return base.getValue(attr);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <V> void setValue(Attribute<V> attr, V value) {
		V oldValue = base.getValue(attr);
		if (Objects.equals(oldValue, value)) {
			return;
		}
		V newValue = value;

		if (attr == StdAttr.WIDTH) {
			final var oldWidth = (BitWidth) oldValue;
			final var newWidth = (BitWidth) newValue;
			final var mask = newWidth.getMask();
			final var oldMax = base.getValue(Counter.ATTR_MAX);
			final var newMax = (newWidth.getWidth() < oldWidth.getWidth()) ? (mask & oldMax) : mask;
			if (oldMax != newMax) {
				base.setValue(Counter.ATTR_MAX, newMax);
				fireAttributeValueChanged(Counter.ATTR_MAX, newMax, oldMax);
			}
		} else if (attr == Counter.ATTR_MAX) {
			final var width = base.getValue(StdAttr.WIDTH);
			newValue = (V) Long.valueOf(width.getMask() & (Long) newValue);
			if (Objects.equals(oldValue, newValue)) {
				return;
			}
		}
		base.setValue(attr, newValue);
		fireAttributeValueChanged(attr, newValue, oldValue);
	}

	@Override
	public boolean containsAttribute(Attribute<?> attr) {
		return base.containsAttribute(attr);
	}

	@Override
	public Attribute<?> getAttribute(String name) {
		return base.getAttribute(name);
	}

	@Override
	public boolean isReadOnly(Attribute<?> attr) {
		return base.isReadOnly(attr);
	}

	@Override
	public void setReadOnly(Attribute<?> attr, boolean value) {
		base.setReadOnly(attr, value);
	}

}
