/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.newgui.MainFrame.EditorTabs;

public abstract class EditHandler {

	public abstract boolean computeEnabled(String from);
	public abstract void cut();
	public abstract void copy();
	public abstract void paste();
	public abstract void delete();
	public abstract void duplicate();
	public abstract void selectAll();
	public abstract void raise();
	public abstract void lower();
	public abstract void raiseTop();
	public abstract void lowerBottom();
	public abstract void addControlPoint();
	public abstract void removeControlPoint();

}
