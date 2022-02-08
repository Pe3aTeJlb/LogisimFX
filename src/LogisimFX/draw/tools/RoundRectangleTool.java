/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.draw.tools;

import LogisimFX.IconsManager;
import LogisimFX.draw.LC;
import LogisimFX.draw.model.CanvasObject;
import LogisimFX.draw.shapes.DrawAttr;
import LogisimFX.draw.shapes.RoundRectangle;
import LogisimFX.data.Attribute;

import LogisimFX.newgui.MainFrame.Canvas.Graphics;
import javafx.scene.image.ImageView;

import java.util.List;

public class RoundRectangleTool extends RectangularTool {

	private DrawingAttributeSet attrs;
	
	public RoundRectangleTool(DrawingAttributeSet attrs) {
		this.attrs = attrs;
	}

	@Override
	public String getName(){
		return  LC.get("shapeRect");
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
		g.c.strokeRoundRect(x, y, w, h, r, r);
		g.toDefault();

	}

	@Override
	public void fillShape(Graphics g, int x, int y, int w, int h) {

		int r = 2 * attrs.getValue(DrawAttr.CORNER_RADIUS).intValue();
		g.c.fillRoundRect(x, y, w, h, r, r);
		g.toDefault();

	}
	
}
