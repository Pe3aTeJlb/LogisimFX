/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.tools;

import LogisimFX.comp.Component;
import LogisimFX.comp.ComponentDrawContext;
import LogisimFX.data.Attribute;
import LogisimFX.data.AttributeDefaultProvider;
import LogisimFX.data.AttributeSet;
import LogisimFX.LogisimVersion;
import LogisimFX.newgui.MainFrame.Canvas.layoutCanvas.LayoutCanvas;
import LogisimFX.newgui.MainFrame.Canvas.Graphics;

import javafx.beans.binding.StringBinding;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

import java.util.Set;

//
// DRAWING TOOLS
//
public abstract class Tool implements AttributeDefaultProvider {

	private static Cursor dflt_cursor = Cursor.CROSSHAIR;

	public abstract String getName();
	public abstract StringBinding getDisplayName();
	public abstract StringBinding getDescription();
	public ImageView getIcon(){ return null; }
	public Tool cloneTool() { return this; }
	public boolean sharesSource(Tool other) { return this == other; }
	public AttributeSet getAttributeSet() { return null; }
	public AttributeSet getAttributeSet(LayoutCanvas canvas) { return getAttributeSet(); }
	public boolean isAllDefaultValues(AttributeSet attrs, LogisimVersion ver) {
		return false;
	}
	public Object getDefaultAttributeValue(Attribute<?> attr, LogisimVersion ver) {
		return null;
	}
	public void setAttributeSet(AttributeSet attrs) { }
	public void paintIcon(ComponentDrawContext c, int x, int y) { }

	@Override
	public String toString() { return getName(); }

	// This was the draw method until 2.0.4 - As of 2.0.5, you should
	// use the other draw method.
	public void draw(ComponentDrawContext context) { }
	public void draw(LayoutCanvas canvas, ComponentDrawContext context) {
		draw(context);
	}
	public Set<Component> getHiddenComponents(LayoutCanvas canvas) {
		return null;
	}
	public void select(LayoutCanvas canvas) { }
	public void deselect(LayoutCanvas canvas) { }

	public void mousePressed(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) { }
	public void mouseDragged(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) { }
	public void mouseReleased(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) { }
	public void mouseEntered(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) { }
	public void mouseExited(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) { }
	public void mouseMoved(LayoutCanvas canvas, Graphics g, LayoutCanvas.CME e) { }

	public void keyTyped(LayoutCanvas canvas, KeyEvent e) { }
	public void keyPressed(LayoutCanvas canvas, KeyEvent e) { }
	public void keyReleased(LayoutCanvas canvas, KeyEvent e) { }
	public Cursor getCursor() { return dflt_cursor; }

}
