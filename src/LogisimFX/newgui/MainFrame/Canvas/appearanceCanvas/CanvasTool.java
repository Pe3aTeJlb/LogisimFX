/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.newgui.MainFrame.Canvas.appearanceCanvas;

import javafx.scene.Cursor;
import javafx.scene.input.KeyEvent;

public abstract class CanvasTool {

	public abstract Cursor getCursor();
	public void draw(AppearanceCanvas canvas) { }

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

}
