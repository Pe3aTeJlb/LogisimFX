/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.data;

import com.cburch.LogisimFX.util.StringGetter;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;

import javax.swing.*;
import java.awt.*;

public abstract class Attribute<V> {

	private String name;
	private StringGetter disp;

	public Attribute(String name, StringGetter disp) {
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
		return disp.get();
	}

	public Node getCell(V value){
		return new TextField(toDisplayString(value));
	}

	public java.awt.Component getCellEditor(Window source, V value) {
		return getCellEditor(value);
	}

	public java.awt.Component getCellEditor(V value) {
		return new JTextField(toDisplayString(value));
	}

	public String toDisplayString(V value) {
		return value == null ? "" : value.toString();
	}

	public String toStandardString(V value) {
		return value.toString();
	}

	public abstract V parse(String value);

}
