/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.draw.undo;

import LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor.appearanceCanvas.ActionDispatcher;

public class UndoLogDispatcher implements ActionDispatcher {
	private UndoLog log;
	
	public UndoLogDispatcher(UndoLog log) {
		this.log = log;
	}
	
	public void doAction(Action action) {
		log.doAction(action);
	}
}
