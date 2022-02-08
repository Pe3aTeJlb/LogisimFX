/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.draw.undo;

import java.util.EventListener;

public interface UndoLogListener extends EventListener {
	public void undoLogChanged(UndoLogEvent e);
}
