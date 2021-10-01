/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package LogisimFX.tools;

import LogisimFX.comp.ComponentUserEvent;
import LogisimFX.circuit.Circuit;
import LogisimFX.proj.Action;

public interface TextEditable {
	public Caret getTextCaret(ComponentUserEvent event);
	public Action getCommitAction(Circuit circuit, String oldText, String newText);
}
