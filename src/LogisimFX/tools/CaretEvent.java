/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.tools;

public class CaretEvent {
	private Caret caret;
	private String oldtext;
	private String newtext;

	public CaretEvent(Caret caret, String oldtext, String newtext) {
		this.caret = caret;
		this.oldtext = oldtext;
		this.newtext = newtext;
	}

	public Caret getCaret() {
		return caret;
	}

	public String getOldText() {
		return oldtext;
	}

	public String getText() {
		return newtext;
	}
}
