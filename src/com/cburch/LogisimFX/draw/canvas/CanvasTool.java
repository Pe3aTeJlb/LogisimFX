/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.draw.canvas;

import com.cburch.LogisimFX.newgui.MainFrame.Graphics;
import javafx.scene.Cursor;
import javafx.scene.input.KeyEvent;

public abstract class CanvasTool {

	public abstract Cursor getCursor();
	public void draw(Graphics g) { }

	public void toolSelected(AppearanceCanvas canvas) { }
	public void toolDeselected(AppearanceCanvas canvas) { }

	public void mouseMoved(AppearanceCanvas canvas, AppearanceCanvas.CME e) { }
	public void mousePressed(AppearanceCanvas canvas, AppearanceCanvas.CME e) { }
	public void mouseDragged(AppearanceCanvas canvas, AppearanceCanvas.CME e) { }
	public void mouseReleased(AppearanceCanvas canvas, AppearanceCanvas.CME e) { }
	public void mouseEntered(AppearanceCanvas canvas, AppearanceCanvas.CME e) { }
	public void mouseExited(AppearanceCanvas canvas, AppearanceCanvas.CME e) { }

	/** This is because a popup menu may result from the subsequent mouse release */
	public void cancelMousePress(AppearanceCanvas canvas) { }


	public void keyPressed(AppearanceCanvas canvas, KeyEvent e) { }
	public void keyReleased(AppearanceCanvas canvas, KeyEvent e) { }
	public void keyTyped(AppearanceCanvas canvas, KeyEvent e) { }
	
	public void zoomFactorChanged(AppearanceCanvas canvas) { }

}
