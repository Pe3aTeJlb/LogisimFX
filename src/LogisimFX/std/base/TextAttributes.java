/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.base;

import LogisimFX.data.AbstractAttributeSet;
import LogisimFX.data.Attribute;
import LogisimFX.data.AttributeOption;
import LogisimFX.data.Bounds;
import LogisimFX.instance.StdAttr;

import javafx.scene.text.Font;

import java.util.Arrays;
import java.util.List;

class TextAttributes extends AbstractAttributeSet {

	private static final List<Attribute<?>> ATTRIBUTES
		= Arrays.asList(new Attribute<?>[] {
			Text.ATTR_TEXT, Text.ATTR_FONT, Text.ATTR_HALIGN, Text.ATTR_VALIGN
		});

	private String text;
	private Font font;
	private AttributeOption halign;
	private AttributeOption valign;
	private Bounds offsetBounds;

	public TextAttributes() {

		text = "";
		font = StdAttr.DEFAULT_LABEL_FONT;
		halign = Text.ATTR_HALIGN.parse("center");
		valign = Text.ATTR_VALIGN.parse("base");
		offsetBounds = null;

	}

	String getText() {
		return text;
	}

	Font getFont() {
		return font;
	}

	int getHorizontalAlign() {
		return ((Integer) halign.getValue()).intValue();
	}

	int getVerticalAlign() {
		return ((Integer) valign.getValue()).intValue();
	}

	Bounds getOffsetBounds() {
		return offsetBounds;
	}

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

		if (attr == Text.ATTR_TEXT) return (V) text;
		if (attr == Text.ATTR_FONT) return (V) font;
		if (attr == Text.ATTR_HALIGN) return (V) halign;
		if (attr == Text.ATTR_VALIGN) return (V) valign;

		return null;

	}

	@Override
	public <V> void setValue(Attribute<V> attr, V value) {

		if (attr == Text.ATTR_TEXT) {
			text = (String) value;
		} else if (attr == Text.ATTR_FONT) {
			font = (Font) value;
		} else if (attr == Text.ATTR_HALIGN) {
			halign = (AttributeOption) value;
		} else if (attr == Text.ATTR_VALIGN) {
			valign = (AttributeOption) value;
		} else {
			throw new IllegalArgumentException("unknown attribute");
		}
		offsetBounds = null;
		fireAttributeValueChanged(attr, value, null);

	}

}
