/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package LogisimFX.draw.undo;

import LogisimFX.newgui.MainFrame.Canvas.appearanceCanvas.ActionDispatcher;

public class UndoLogDispatcher implements ActionDispatcher {
	private UndoLog log;
	
	public UndoLogDispatcher(UndoLog log) {
		this.log = log;
	}
	
	public void doAction(Action action) {
		log.doAction(action);
	}
}
