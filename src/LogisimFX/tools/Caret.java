/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.tools;

import LogisimFX.data.Bounds;
import LogisimFX.newgui.MainFrame.Canvas.layoutCanvas.LayoutCanvas;
import LogisimFX.newgui.MainFrame.Canvas.Graphics;

import javafx.scene.input.KeyEvent;

public interface Caret {

	// listener methods
	void addCaretListener(CaretListener e);
	void removeCaretListener(CaretListener e);

	// query/Graphics methods
	String getText();
	Bounds getBounds(Graphics g);
	void draw(Graphics g);

	// finishing
	void commitText(String text);
	void cancelEditing();
	void stopEditing();

	// events to handle
	void mousePressed(LayoutCanvas.CME e);
	void mouseDragged(LayoutCanvas.CME e);
	void mouseReleased(LayoutCanvas.CME e);
	void keyPressed(KeyEvent e);
	void keyReleased(KeyEvent e);
	void keyTyped(KeyEvent e);

}
