/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.comp;

public class TextFieldEvent {
	private TextField field;
	private String oldval;
	private String newval;

	public TextFieldEvent(TextField field, String old, String val) {
		this.field = field;
		this.oldval = old;
		this.newval = val;
	}

	public TextField getTextField() {
		return field;
	}

	public String getOldText() {
		return oldval;
	}

	public String getText() {
		return newval;
	}
}
