/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package LogisimFX.circuit;

import LogisimFX.comp.AbstractComponentFactory;
import LogisimFX.comp.Component;
import LogisimFX.comp.ComponentDrawContext;
import LogisimFX.data.AttributeSet;
import LogisimFX.data.Bounds;
import LogisimFX.data.Location;
import LogisimFX.newgui.MainFrame.Canvas.Graphics;

import javafx.beans.binding.StringBinding;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;


class WireFactory extends AbstractComponentFactory {

	public static final WireFactory instance = new WireFactory();

	private WireFactory() { }

	@Override
	public String getName() { return "Wire"; }

	@Override
	public ImageView getIcon() {
		return null;
	}

	@Override
	public StringBinding getDisplayGetter() {
		return LC.createStringBinding("wireComponent");
	}

	@Override
	public AttributeSet createAttributeSet() {
		return Wire.create(Location.create(0, 0), Location.create(100, 0));
	}

	@Override
	public Component createComponent(Location loc, AttributeSet attrs) {

		Object dir = attrs.getValue(Wire.dir_attr);
		int len = attrs.getValue(Wire.len_attr).intValue();

		if (dir == Wire.VALUE_HORZ) {
			return Wire.create(loc, loc.translate(len, 0));
		} else {
			return Wire.create(loc, loc.translate(0, len));
		}

	}

	@Override
	public Bounds getOffsetBounds(AttributeSet attrs) {

		Object dir = attrs.getValue(Wire.dir_attr);
		int len = attrs.getValue(Wire.len_attr).intValue();

		if (dir == Wire.VALUE_HORZ) {
			return Bounds.create(0, -2, len, 5);
		} else {
			return Bounds.create(-2, 0, 5, len);
		}

	}

	//
	// user interface methods
	//
	@Override
	public void drawGhost(ComponentDrawContext context,
						  Color color, int x, int y, AttributeSet attrs) {

		Graphics g = context.getGraphics();
		Object dir = attrs.getValue(Wire.dir_attr);
		int len = attrs.getValue(Wire.len_attr).intValue();

		g.setColor(color);
		g.setLineWidth(3);
		if (dir == Wire.VALUE_HORZ) {
			g.c.strokeLine(x, y, x + len, y);
		} else {
			g.c.strokeLine(x, y, x, y + len);
		}

	}

}
