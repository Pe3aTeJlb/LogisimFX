/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.data;

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
