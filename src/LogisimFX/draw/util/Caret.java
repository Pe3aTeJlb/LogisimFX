/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package LogisimFX.draw.util;

import LogisimFX.data.Bounds;
import LogisimFX.newgui.MainFrame.Canvas.Graphics;
import LogisimFX.newgui.MainFrame.Canvas.appearanceCanvas.AppearanceCanvas;

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
	void mousePressed(AppearanceCanvas.CME e);
	void mouseDragged(AppearanceCanvas.CME e);
	void mouseReleased(AppearanceCanvas.CME e);
	void keyPressed(KeyEvent e);
	void keyReleased(KeyEvent e);
	void keyTyped(KeyEvent e);

}
