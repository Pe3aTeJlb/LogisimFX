/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.draw.shapes;

import LogisimFX.draw.LC;
import LogisimFX.draw.model.CanvasObject;
import LogisimFX.data.Attribute;
import LogisimFX.data.Bounds;
import LogisimFX.data.Location;

import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Random;

public class Rectangle extends Rectangular {

	public Rectangle(int x, int y, int w, int h) {
		super(x, y, w, h);
	}

	@Override
	public boolean matches(CanvasObject other) {

		if (other instanceof Rectangle) {
			return super.matches(other);
		} else {
			return false;
		}

	}

	@Override
	public int matchesHashCode() {
		return super.matchesHashCode();
	}

	@Override
	public String toString() {
		return "Rectangle:" + getBounds();
	}

	@Override
	public String getDisplayName() {
		return LC.get("shapeRect");
	}

	@Override
	public Element toSvgElement(Document doc) {
		return SvgCreator.createRectangle(doc, this);
	}

	@Override
	public List<Attribute<?>> getAttributes() {
		return DrawAttr.getFillAttributes(getPaintType());
	}
	
	@Override
	protected boolean contains(int x, int y, int w, int h, Location q) {
		return isInRect(q.getX(), q.getY(), x, y, w, h);
	}

	@Override
	protected Location getRandomPoint(Bounds bds, Random rand) {

		if (getPaintType() == DrawAttr.PAINT_STROKE) {
			int w = getWidth();
			int h = getHeight();
			int u = rand.nextInt(2 * w + 2 * h);
			int x = getX();
			int y = getY();
			if (u < w) {
				x += u;
			} else if (u < 2 * w) {
				x += (u - w);
				y += h;
			} else if (u < 2 * w + h) {
				y += (u - 2 * w);
			} else {
				x += w;
				y += (u - 2 * w - h);
			}
			int d = getStrokeWidth();
			if (d > 1) {
				x += rand.nextInt(d) - d / 2;
				y += rand.nextInt(d) - d / 2;
			}
			return Location.create(x, y);
		} else {
			return super.getRandomPoint(bds, rand);
		}

	}
	
	@Override
	public void draw(Graphics g, int x, int y, int w, int h) {

		if (setForFill(g)) g.c.fillRect(x, y, w, h);
		if (setForStroke(g)) g.c.strokeRect(x, y, w, h);

	}

}
