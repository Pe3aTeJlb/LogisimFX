/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.instance;

import com.cburch.LogisimFX.data.Bounds;
import com.cburch.LogisimFX.newgui.MainFrame.CustomCanvas;

import javafx.scene.input.KeyEvent;

public abstract class InstancePoker {

	public boolean init(InstanceState state, CustomCanvas.CME e) { return true; }
	public Bounds getBounds(InstancePainter painter) {
		return painter.getInstance().getBounds();
	}
	public void paint(InstancePainter painter) { }
	public void mousePressed(InstanceState state, CustomCanvas.CME e) { }
	public void mouseReleased(InstanceState state, CustomCanvas.CME e) { }
	public void mouseDragged(InstanceState state, CustomCanvas.CME e) { }
	public void keyPressed(InstanceState state, KeyEvent e) { }
	public void keyReleased(InstanceState state, KeyEvent e) { }
	public void keyTyped(InstanceState state, KeyEvent e) { }
	public void stopEditing(InstanceState state) { }

}
