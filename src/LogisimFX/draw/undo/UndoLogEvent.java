/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.draw.undo;

import java.util.EventObject;

public class UndoLogEvent extends EventObject {
	public static final int ACTION_DONE = 0;
	public static final int ACTION_UNDONE = 1;
	
	private int action;
	private Action actionObject;

	public UndoLogEvent(UndoLog source, int action, Action actionObject) {
		super(source);
		this.action = action;
		this.actionObject = actionObject;
	}

	public UndoLog getUndoLog() {
		return (UndoLog) getSource();
	}
	
	public int getAction() {
		return action;
	}
	
	public Action getActionObject() {
		return actionObject;
	}
}
