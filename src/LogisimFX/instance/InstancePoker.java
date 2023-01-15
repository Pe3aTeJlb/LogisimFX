/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.instance;

import LogisimFX.data.Bounds;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.LayoutCanvas;

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
