/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.tools;

import LogisimFX.comp.ComponentUserEvent;
import LogisimFX.circuit.Circuit;
import LogisimFX.proj.Action;

public interface TextEditable {
	public Caret getTextCaret(ComponentUserEvent event);
	public Action getCommitAction(Circuit circuit, String oldText, String newText);
}
