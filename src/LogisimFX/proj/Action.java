/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.proj;

public abstract class Action {

	public final static int OPTIONS_ACTION = 1;
	public final static int CANVAS_ACTION_ADAPTER = 2;
	public final static int CLIPBOARD_ACTION = 3;
	public final static int REVERT_APPEARANCE_ACTION = 4;
	public final static int SET_ATTRIBUTE_ACTION = 5;
	public final static int CIRCUIT_ACTION = 6;
	public final static int LAYOUT_SELECTION_ACTION = 7;
	public final static int APPEARANCE_SELECTION_ACTION = 8;
	public final static int TOOL_ATTRIBUTE_ACTION = 9;
	public final static int LOGISIM_FILE_ACTION = 10;
	public final static int ROM_CONTENTS_ACTION = 11;
	public final static int TOOLBAR_ACTION = 12;
	public final static int JOINED_ACTION = 13;
	public final static int LOGISIM_LIBRARY_ACTION = 14;

	public boolean isModification() { return true; }

	public abstract String getName();

	public abstract int getActionType();

	public abstract void doIt(Project proj);

	public abstract void undo(Project proj);

	public boolean shouldAppendTo(Action other) { return false; }

	public Action append(Action other) {
		return new JoinedAction(this, other);
	}

}