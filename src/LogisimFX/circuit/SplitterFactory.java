/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.circuit;

import LogisimFX.IconsManager;
import LogisimFX.KeyEvents;
import LogisimFX.LogisimVersion;
import LogisimFX.comp.AbstractComponentFactory;
import LogisimFX.comp.Component;
import LogisimFX.comp.ComponentDrawContext;
import LogisimFX.data.Attribute;
import LogisimFX.data.AttributeSet;
import LogisimFX.data.Bounds;
import LogisimFX.data.Location;
import LogisimFX.instance.StdAttr;
import LogisimFX.tools.key.*;
import javafx.beans.binding.StringBinding;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class SplitterFactory extends AbstractComponentFactory {

	public static final SplitterFactory instance = new SplitterFactory();

	private static final ImageView icon = IconsManager.getIcon("splitter.gif");

	private SplitterFactory() { }

	@Override
	public String getName() { return "Splitter"; }

	@Override
	public ImageView getIcon() {
		return icon;
	}

	@Override
	public StringBinding getDisplayGetter() {
		return LC.createStringBinding("splitterComponent");
	}

	@Override
	public AttributeSet createAttributeSet() {
		return new SplitterAttributes();
	}
	
	@Override
	public Object getDefaultAttributeValue(Attribute<?> attr, LogisimVersion ver) {
		if (attr == SplitterAttributes.ATTR_APPEARANCE) {
			return SplitterAttributes.APPEAR_LEFT;
		} else if (attr == StdAttr.FPGA_SUPPORTED){
			return this.isHDLSupportedComponent(null);
		} else if (attr instanceof SplitterAttributes.BitOutAttribute) {
			SplitterAttributes.BitOutAttribute a;
			a = (SplitterAttributes.BitOutAttribute) attr;
			return a.getDefault();
		} else {
			return super.getDefaultAttributeValue(attr, ver);
		}
	}

	@Override
	public Component createComponent(Location loc, AttributeSet attrs) {
		return new Splitter(loc, attrs);
	}

	@Override
	public Bounds getOffsetBounds(AttributeSet attrsBase) {
		SplitterAttributes attrs = (SplitterAttributes) attrsBase;
		int fanout = attrs.fanout;
		SplitterParameters parms = attrs.getParameters();
		int xEnd0 = parms.getEnd0X();
		int yEnd0 = parms.getEnd0Y();
		Bounds bds = Bounds.create(0, 0, 1, 1);
		bds = bds.add(xEnd0, yEnd0);
		bds = bds.add(xEnd0 + (fanout - 1) * parms.getEndToEndDeltaX(),
				yEnd0 + (fanout - 1) * parms.getEndToEndDeltaY());
		return bds;
	}

	//
	// user interface methods
	//
	@Override
	public void drawGhost(ComponentDrawContext context,
						  Color color, int x, int y, AttributeSet attrsBase) {

		SplitterAttributes attrs = (SplitterAttributes) attrsBase;
		context.getGraphics().setColor(color);
		Location loc = Location.create(x, y);
		if (attrs.appear == SplitterAttributes.APPEAR_LEGACY) {
			SplitterPainter.drawLegacy(context, attrs, loc);
		} else {
			SplitterPainter.drawLines(context, attrs, loc);
		}

		context.getGraphics().toDefault();

	}

	@Override
	public Object getFeature(Object key, AttributeSet attrs) {
		if (key == FACING_ATTRIBUTE_KEY) {
			return StdAttr.FACING;
		} else if (key == KeyConfigurator.class) {
			KeyConfigurator altConfig = ParallelConfigurator.create(
					new BitWidthConfigurator(SplitterAttributes.ATTR_WIDTH),
					new IntegerConfigurator(SplitterAttributes.ATTR_FANOUT,
							1, 32, KeyEvents.ALT_DOWN));
			return JoinedConfigurator.create(
				new IntegerConfigurator(SplitterAttributes.ATTR_FANOUT, 1, 32, null),
				altConfig);
		}
		return super.getFeature(key, attrs);
	}


	@Override
	public boolean isHDLSupportedComponent(AttributeSet attrs) {
		return true;
	}

}
