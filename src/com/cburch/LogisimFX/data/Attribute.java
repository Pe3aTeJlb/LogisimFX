/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.data;

import javafx.beans.binding.StringBinding;
import javafx.scene.Node;
import javafx.scene.control.TextField;

public abstract class Attribute<V> {

	private String name;
	private StringBinding disp;

	public Attribute(String name, StringBinding disp) {
		this.name = name;
		this.disp = disp;
	}

	@Override
	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return disp.getValue();
	}

	public Node getCell(V value){
		return new TextField(toDisplayString(value));
	}

	public String toDisplayString(V value) {
		return value == null ? "" : value.toString();
	}

	public String toStandardString(V value) {
		return value.toString();
	}

	public abstract V parse(String value);

}
