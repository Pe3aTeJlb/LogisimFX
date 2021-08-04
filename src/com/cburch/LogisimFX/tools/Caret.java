/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.tools;

import com.cburch.LogisimFX.data.Bounds;
import com.cburch.LogisimFX.newgui.MainFrame.LayoutCanvas;
import com.cburch.LogisimFX.newgui.MainFrame.Graphics;

import javafx.scene.input.KeyEvent;

public interface Caret {

	// listener methods
	public void addCaretListener(CaretListener e);
	public void removeCaretListener(CaretListener e);

	// query/Graphics methods
	public String getText();
	public Bounds getBounds(Graphics g);
	public void draw(Graphics g);

	// finishing
	public void commitText(String text);
	public void cancelEditing();
	public void stopEditing();

	// events to handle
	public void mousePressed(LayoutCanvas.CME e);
	public void mouseDragged(LayoutCanvas.CME e);
	public void mouseReleased(LayoutCanvas.CME e);
	public void keyPressed(KeyEvent e);
	public void keyReleased(KeyEvent e);
	public void keyTyped(KeyEvent e);

}
