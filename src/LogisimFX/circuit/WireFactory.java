/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.circuit;

import LogisimFX.comp.AbstractComponentFactory;
import LogisimFX.comp.Component;
import LogisimFX.comp.ComponentDrawContext;
import LogisimFX.data.AttributeSet;
import LogisimFX.data.Bounds;
import LogisimFX.data.Location;
import LogisimFX.instance.StdAttr;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;

import javafx.beans.binding.StringBinding;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;

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

		attrs.setValue(StdAttr.FPGA_SUPPORTED, Boolean.TRUE);

		Object dir = attrs.getValue(Wire.dir_attr);
		int len = attrs.getValue(Wire.len_attr).intValue();
		double rot = attrs.getValue(Wire.rot_attr).doubleValue();

		if (dir == Wire.VALUE_HORZ) {
			return Wire.create(loc, loc.translate(len, 0));
		} else if (dir == Wire.VALUE_VERT){
			return Wire.create(loc, loc.translate(0, len));
		} else if (dir == Wire.VALUE_DIAG){
			return  Wire.create(loc, loc.translate((int)Math.round(len * Math.sin(rot)), (int)Math.round(len * -Math.cos(rot))));
		} else { // meh, vert wire will be default. should not happen
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
		double rot = attrs.getValue(Wire.rot_attr).doubleValue();

		g.setColor(color);
		g.setLineWidth(3);
		g.setLineExtras(StrokeLineCap.ROUND);

		if (dir == Wire.VALUE_HORZ) {
			g.c.strokeLine(x, y, x + len, y);
		} else if(dir == Wire.VALUE_VERT){
			g.c.strokeLine(x, y, x, y + len);
		} else if(dir == Wire.VALUE_DIAG){
			g.c.strokeLine(x, y, x + Math.round(len * Math.sin(rot)), y + Math.round(len * -Math.cos(rot)));
		}

	}


	@Override
	public boolean isHDLSupportedComponent(AttributeSet attrs) {
		return true;
	}

}
