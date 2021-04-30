/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.tools;

import com.cburch.LogisimFX.comp.ComponentUserEvent;
import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.proj.Action;

public interface TextEditable {
	public Caret getTextCaret(ComponentUserEvent event);
	public Action getCommitAction(Circuit circuit, String oldText, String newText);
}
