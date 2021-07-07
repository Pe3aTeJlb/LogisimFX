/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.draw.tools;

import com.cburch.LogisimFX.IconsManager;
import com.cburch.LogisimFX.draw.model.CanvasObject;
import com.cburch.LogisimFX.draw.shapes.DrawAttr;
import com.cburch.LogisimFX.draw.shapes.RoundRectangle;
import com.cburch.LogisimFX.data.Attribute;
import com.cburch.LogisimFX.util.Icons;
import javafx.scene.image.ImageView;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RoundRectangleTool extends RectangularTool {

	private DrawingAttributeSet attrs;
	
	public RoundRectangleTool(DrawingAttributeSet attrs) {
		this.attrs = attrs;
	}
	
	@Override
	public ImageView getIcon() {
		return IconsManager.getIcon("drawrrct.gif");
	}
	
	@Override
	public List<Attribute<?>> getAttributes() {
		return DrawAttr.getRoundRectAttributes(attrs.getValue(DrawAttr.PAINT_TYPE));
	}

	@Override
	public CanvasObject createShape(int x, int y, int w, int h) {
		return attrs.applyTo(new RoundRectangle(x, y, w, h));
	}

	@Override
	public void drawShape(Graphics g, int x, int y, int w, int h) {
		int r = 2 * attrs.getValue(DrawAttr.CORNER_RADIUS).intValue();
		g.drawRoundRect(x, y, w, h, r, r);
	}

	@Override
	public void fillShape(Graphics g, int x, int y, int w, int h) {
		int r = 2 * attrs.getValue(DrawAttr.CORNER_RADIUS).intValue();
		g.fillRoundRect(x, y, w, h, r, r);
	}
}