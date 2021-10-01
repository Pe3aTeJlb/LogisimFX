/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package LogisimFX.instance;

import LogisimFX.data.Bounds;
import LogisimFX.newgui.MainFrame.Canvas.layoutCanvas.LayoutCanvas;

import javafx.scene.input.KeyEvent;

public abstract class InstancePoker {

	public boolean init(InstanceState state, LayoutCanvas.CME e) { return true; }
	public Bounds getBounds(InstancePainter painter) {
		return painter.getInstance().getBounds();
	}
	public void paint(InstancePainter painter) { }
	public void mousePressed(InstanceState state, LayoutCanvas.CME e) { }
	public void mouseReleased(InstanceState state, LayoutCanvas.CME e) { }
	public void mouseDragged(InstanceState state, LayoutCanvas.CME e) { }
	public void keyPressed(InstanceState state, KeyEvent e) { }
	public void keyReleased(InstanceState state, KeyEvent e) { }
	public void keyTyped(InstanceState state, KeyEvent e) { }
	public void stopEditing(InstanceState state) { }

}
