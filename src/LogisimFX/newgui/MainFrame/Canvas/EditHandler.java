/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package LogisimFX.newgui.MainFrame.Canvas;

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
