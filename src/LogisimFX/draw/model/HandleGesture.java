/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
*  License information is located in the Launch file 
*/

package LogisimFX.draw.model;

import LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor.appearanceCanvas.AppearanceCanvas;

import java.awt.event.InputEvent;

public class HandleGesture {

	private Handle handle;
	private int dx;
	private int dy;
	private int modifiersEx;
	private Handle resultingHandle;

	public HandleGesture(Handle handle, int dx, int dy, AppearanceCanvas.CME e) {

		this.handle = handle;
		this.dx = dx;
		this.dy = dy;
		this.modifiersEx = modifiersEx;

	}

	public Handle getHandle() {
		return handle;
	}

	public int getDeltaX() {
		return dx;
	}

	public int getDeltaY() {
		return dy;
	}

	public int getModifiersEx() {
		return modifiersEx;
	}

	public boolean isShiftDown() {
		return (modifiersEx & InputEvent.SHIFT_DOWN_MASK) != 0;
	}

	public boolean isControlDown() {
		return (modifiersEx & InputEvent.CTRL_DOWN_MASK) != 0;
	}

	public boolean isAltDown() {
		return (modifiersEx & InputEvent.ALT_DOWN_MASK) != 0;
	}

	public void setResultingHandle(Handle value) {
		resultingHandle = value;
	}
	
	public Handle getResultingHandle() {
		return resultingHandle;
	}

}
