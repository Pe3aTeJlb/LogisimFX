/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.tools;

import com.cburch.LogisimFX.data.Bounds;
import com.cburch.LogisimFX.newgui.MainFrame.LayoutCanvas;
import com.cburch.LogisimFX.newgui.MainFrame.Graphics;

import javafx.scene.input.KeyEvent;


import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class AbstractCaret implements Caret {

	private ArrayList<CaretListener> listeners = new ArrayList<CaretListener>();
	private List<CaretListener> listenersView;
	private Bounds bds = Bounds.EMPTY_BOUNDS;

	public AbstractCaret() {
		listenersView = Collections.unmodifiableList(listeners);
	}

	// listener methods
	public void addCaretListener(CaretListener e) { listeners.add(e); }
	public void removeCaretListener(CaretListener e) { listeners.remove(e); }
	protected List<CaretListener> getCaretListeners() { return listenersView; }

	// configuration methods
	public void setBounds(Bounds value) { bds = value; }

	// query/Graphics methods
	public String getText() { return ""; }
	public Bounds getBounds(Graphics g) { return bds; }
	public void draw(Graphics g) { }

	// finishing
	public void commitText(String text) { }
	public void cancelEditing() { }
	public void stopEditing() { }

	// events to handle
	public void mousePressed(LayoutCanvas.CME e) { }
	public void mouseDragged(LayoutCanvas.CME e) { }
	public void mouseReleased(LayoutCanvas.CME e) { }
	public void keyPressed(KeyEvent e) { }
	public void keyReleased(KeyEvent e) { }
	public void keyTyped(KeyEvent e) { }

}
