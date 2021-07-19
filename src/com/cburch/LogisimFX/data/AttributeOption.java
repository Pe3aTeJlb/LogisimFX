/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.data;

import javafx.beans.binding.StringBinding;

public class AttributeOption implements AttributeOptionInterface {

	private Object value;
	private String name;
	private StringBinding desc;

	public AttributeOption(Object value, StringBinding desc) {
		this.value = value;
		this.name = value.toString();
		this.desc = desc;
	}

	public AttributeOption(Object value, String name, StringBinding desc) {
		this.value = value;
		this.name = name;
		this.desc = desc;
	}

	public Object getValue() { return value; }

	@Override
	public String toString() { return name; }

	public StringBinding getStringBinding() {return desc;}

	public String toDisplayString() { return desc.get(); }

}
