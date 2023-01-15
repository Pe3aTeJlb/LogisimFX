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
import LogisimFX.draw.shapes.Rectangle;
import LogisimFX.data.Attribute;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;

import javafx.scene.image.ImageView;

import java.util.List;

public class RectangleTool extends RectangularTool {

	private DrawingAttributeSet attrs;
	
	public RectangleTool(DrawingAttributeSet attrs) {
		this.attrs = attrs;
	}

	@Override
	public String getName(){
		return  LC.get("shapeRoundRect");
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
		g.c.strokeRect(x, y, w, h);
	}

	@Override
	public void fillShape(Graphics g, int x, int y, int w, int h) {
		g.c.fillRect(x, y, w, h);
	}

}
