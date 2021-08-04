/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.draw.tools;

import com.cburch.LogisimFX.IconsManager;
import com.cburch.LogisimFX.draw.model.CanvasObject;
import com.cburch.LogisimFX.draw.shapes.DrawAttr;
import com.cburch.LogisimFX.draw.shapes.Rectangle;
import com.cburch.LogisimFX.data.Attribute;
import com.cburch.LogisimFX.util.Icons;
import javafx.scene.image.ImageView;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RectangleTool extends RectangularTool {

	private DrawingAttributeSet attrs;
	
	public RectangleTool(DrawingAttributeSet attrs) {
		this.attrs = attrs;
	}
	
	@Override
	public ImageView getIcon() {
		return IconsManager.getIcon("drawrect.gif");
	}
	
	@Override
	public List<Attribute<?>> getAttributes() {
		return DrawAttr.getFillAttributes(attrs.getValue(DrawAttr.PAINT_TYPE));
	}
	
	@Override
	public CanvasObject createShape(int x, int y, int w, int h) {
		return attrs.applyTo(new Rectangle(x, y, w, h));
	}

	@Override
	public void drawShape(Graphics g, int x, int y, int w, int h) {
		g.drawRect(x, y, w, h);
	}

	@Override
	public void fillShape(Graphics g, int x, int y, int w, int h) {
		g.fillRect(x, y, w, h);
	}

}
