/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.comp;

import com.cburch.LogisimFX.IconsManager;
import com.cburch.LogisimFX.data.*;
import com.cburch.LogisimFX.newgui.MainFrame.Canvas.Graphics;
import com.cburch.LogisimFX.std.LC;
import com.cburch.LogisimFX.LogisimVersion;

import javafx.beans.binding.StringBinding;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public abstract class AbstractComponentFactory implements ComponentFactory {

	public ImageView icon = IconsManager.getIcon("subcirc.gif");

	private AttributeSet defaultSet;
	
	protected AbstractComponentFactory() {
		defaultSet = null;
	}

	@Override
	public String toString() { return getName(); }

	public abstract String getName();
	public ImageView getIcon(){ return icon; }
	public StringBinding getDisplayName() { return getDisplayGetter(); }
	public StringBinding getDisplayGetter() { return LC.createStringBinding(getName()); }
	public abstract Component createComponent(Location loc, AttributeSet attrs);
	public abstract Bounds getOffsetBounds(AttributeSet attrs);

	public AttributeSet createAttributeSet() {
		return AttributeSets.EMPTY;
	}
	
	public boolean isAllDefaultValues(AttributeSet attrs, LogisimVersion ver) {
		return false;
	}

	public Object getDefaultAttributeValue(Attribute<?> attr, LogisimVersion ver) {
		AttributeSet dfltSet = defaultSet;
		if (dfltSet == null) {
			dfltSet = (AttributeSet) createAttributeSet().clone();
			defaultSet = dfltSet;
		}
		return dfltSet.getValue(attr);
	}

	//
	// user interface methods
	//
	public void drawGhost(ComponentDrawContext context, Color color,
                          int x, int y, AttributeSet attrs) {
		Graphics g = context.getGraphics();
		Bounds bds = getOffsetBounds(attrs);;
		g.setColor(color);
		g.setLineWidth(2);
		g.c.strokeRect(x + bds.getX(), y + bds.getY(),
			bds.getWidth(), bds.getHeight());

		g.toDefault();

	}

	public Object getFeature(Object key, AttributeSet attrs) {
		return null;
	}

}
