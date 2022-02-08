/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.newgui.MainFrame.Canvas.appearanceCanvas;

import LogisimFX.draw.model.CanvasObject;

import java.util.Collection;
import java.util.EventObject;

public class SelectionEvent extends EventObject {
	public static final int ACTION_ADDED = 0;
	public static final int ACTION_REMOVED = 1;
	public static final int ACTION_HANDLE = 2;
	
	private int action;
	private Collection<CanvasObject> affected;
	
	public SelectionEvent(Selection source, int action,
                          Collection<CanvasObject> affected) {
		super(source);
		this.action = action;
		this.affected = affected;
	}

	public Selection getSelection() {
		return (Selection) getSource();
	}
	
	public int getAction() {
		return action;
	}
	
	public Collection<CanvasObject> getAffected() {
		return affected;
	}
}
