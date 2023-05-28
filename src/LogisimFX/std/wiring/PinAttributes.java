/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.wiring;

import LogisimFX.circuit.RadixOption;
import LogisimFX.comp.EndData;
import LogisimFX.data.Attribute;
import LogisimFX.data.BitWidth;
import LogisimFX.instance.StdAttr;

import java.util.Arrays;
import java.util.List;

class PinAttributes extends ProbeAttributes {

	public static PinAttributes instance = new PinAttributes();

	private static final List<Attribute<?>> ATTRIBUTES
		= Arrays.asList(StdAttr.FPGA_SUPPORTED,
			StdAttr.FACING, Pin.ATTR_TYPE, StdAttr.WIDTH, RadixOption.ATTRIBUTE, Pin.ATTR_TRISTATE,
			Pin.ATTR_PULL, StdAttr.LABEL, Pin.ATTR_LABEL_LOC, StdAttr.LABEL_FONT);

	BitWidth width = BitWidth.ONE;
	boolean threeState = true;
	int type = EndData.INPUT_ONLY;
	Object pull = Pin.PULL_NONE;

	public PinAttributes() { }

	@Override
	public List<Attribute<?>> getAttributes() {
		return ATTRIBUTES;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> V getValue(Attribute<V> attr) {

		if (attr == StdAttr.FPGA_SUPPORTED) return (V) fpga;
		if (attr == StdAttr.WIDTH) return (V) width;
		if (attr == Pin.ATTR_TRISTATE) return (V) Boolean.valueOf(threeState);
		if (attr == Pin.ATTR_TYPE) return (V) Boolean.valueOf(type == EndData.OUTPUT_ONLY);
		if (attr == Pin.ATTR_PULL) return (V) pull;
		return super.getValue(attr);

	}
	
	boolean isOutput() {
		return type != EndData.INPUT_ONLY;
	}
	
	boolean isInput() {
		return type != EndData.OUTPUT_ONLY;
	}

	@Override
	public <V> void setValue(Attribute<V> attr, V value) {

		if (attr == StdAttr.WIDTH) {
			width = (BitWidth) value;
		} else if (attr == Pin.ATTR_TRISTATE) {
			threeState = ((Boolean) value).booleanValue();
		} else if (attr == Pin.ATTR_TYPE) {
			type = ((Boolean) value).booleanValue() ? EndData.OUTPUT_ONLY : EndData.INPUT_ONLY;
		} else if (attr == Pin.ATTR_PULL) {
			pull = value;
		} else if (attr == StdAttr.FPGA_SUPPORTED){
			fpga = (Boolean) value;
		} else if (attr == RadixOption.ATTRIBUTE) {
			if (width.getWidth() == 1) {
				super.setValue(RadixOption.ATTRIBUTE, RadixOption.RADIX_2);
			} else  {
				super.setValue(attr, value);
			}
		} else {
			super.setValue(attr, value);
			return;
		}
		fireAttributeValueChanged(attr, value,null);

	}

}


