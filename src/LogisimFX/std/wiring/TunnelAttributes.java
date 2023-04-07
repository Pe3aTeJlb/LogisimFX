/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.wiring;

import java.util.Arrays;
import java.util.List;

import LogisimFX.comp.TextField;
import LogisimFX.data.*;
import LogisimFX.instance.StdAttr;

import javafx.scene.text.Font;

class TunnelAttributes extends AbstractAttributeSet {

	private static final List<Attribute<?>> ATTRIBUTES
		= Arrays.asList(StdAttr.FPGA_SUPPORTED, StdAttr.FACING, StdAttr.WIDTH, StdAttr.LABEL, StdAttr.LABEL_FONT);

	private Boolean fpga;
	private Direction facing;
	private BitWidth width;
	private String label;
	private Font labelFont;
	private Bounds offsetBounds;
	private int labelX;
	private int labelY;
	private int labelHAlign;
	private int labelVAlign;
	
	public TunnelAttributes() {

		fpga = false;
		facing = Direction.WEST;
		width = BitWidth.ONE;
		label = "";
		labelFont = StdAttr.DEFAULT_LABEL_FONT;
		offsetBounds = null;
		configureLabel();

	}

	Direction getFacing() {
		return facing;
	}
	
	String getLabel() {
		return label;
	}
	
	Font getFont() {
		return labelFont;
	}
	
	Bounds getOffsetBounds() {
		return offsetBounds;
	}
	
	int getLabelX() { return labelX; }
	int getLabelY() { return labelY; }
	int getLabelHAlign() { return labelHAlign; }
	int getLabelVAlign() { return labelVAlign; }
	
	boolean setOffsetBounds(Bounds value) {

		Bounds old = offsetBounds;
		boolean same = old == null ? value == null : old.equals(value);
		if (!same) {
			offsetBounds = value;
		}

		return !same;

	}

	@Override
	protected void copyInto(AbstractAttributeSet destObj) {
		; // nothing to do
	}

	@Override
	public List<Attribute<?>> getAttributes() {
		return ATTRIBUTES;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> V getValue(Attribute<V> attr) {

		if (attr == StdAttr.FPGA_SUPPORTED) return (V) fpga;
		if (attr == StdAttr.FACING) return (V) facing;
		if (attr == StdAttr.WIDTH) return (V) width;
		if (attr == StdAttr.LABEL) return (V) label;
		if (attr == StdAttr.LABEL_FONT) return (V) labelFont;

		return null;

	}

	@Override
	public <V> void setValue(Attribute<V> attr, V value) {
		V Oldvalue = null;
		if (attr == StdAttr.FACING) {
			facing = (Direction) value;
			configureLabel();
		} else if (attr == StdAttr.WIDTH) {
			width = (BitWidth) value;
		} else if (attr == StdAttr.LABEL) {
			Oldvalue = (V) label;
			label = (String) value;
		} else if (attr == StdAttr.LABEL_FONT) {
			labelFont = (Font) value;
		} else if (attr == StdAttr.FPGA_SUPPORTED) {
			fpga = (Boolean) value;
		} else {
			throw new IllegalArgumentException("unknown attribute");
		}
		offsetBounds = null;
		fireAttributeValueChanged(attr, value, Oldvalue);

	}

	private void configureLabel() {

		Direction facing = this.facing;
		int x;
		int y;
		int halign;
		int valign;
		int margin = Tunnel.ARROW_MARGIN;
		if (facing == Direction.NORTH) {
			x = 0;
			y = margin;
			halign = TextField.H_CENTER;
			valign = TextField.V_TOP;
		} else if (facing == Direction.SOUTH) {
			x = 0;
			y = -margin;
			halign = TextField.H_CENTER;
			valign = TextField.V_BOTTOM;
		} else if (facing == Direction.EAST) {
			x = -margin;
			y = 0;
			halign = TextField.H_RIGHT;
			valign = TextField.V_CENTER_OVERALL;
		} else {
			x = margin;
			y = 0;
			halign = TextField.H_LEFT;
			valign = TextField.V_CENTER_OVERALL;
		}
		labelX = x;
		labelY = y;
		labelHAlign = halign;
		labelVAlign = valign;

	}

}
