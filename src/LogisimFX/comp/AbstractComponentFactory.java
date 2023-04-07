/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.comp;

import LogisimFX.IconsManager;
import LogisimFX.data.*;
import LogisimFX.fpga.designrulecheck.CorrectLabel;
import LogisimFX.fpga.designrulecheck.netlistComponent;
import LogisimFX.fpga.hdlgenerator.HdlGeneratorFactory;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.std.LC;
import LogisimFX.LogisimVersion;

import javafx.beans.binding.StringBinding;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public abstract class AbstractComponentFactory implements ComponentFactory {

	public ImageView icon = IconsManager.getIcon("subcirc.gif");

	private AttributeSet defaultSet;
	private final HdlGeneratorFactory myHDLGenerator;
	private final boolean requiresLabel;
	private final boolean requiresGlobalClockConnection;

	protected AbstractComponentFactory() {
		this(null, false, false);
	}

	protected AbstractComponentFactory(
			HdlGeneratorFactory generator, boolean requiresLabel, boolean requiresGlobalClock) {
		defaultSet = null;
		myHDLGenerator = generator;
		this.requiresLabel = requiresLabel;
		requiresGlobalClockConnection = requiresGlobalClock;
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


	@Override
	public HdlGeneratorFactory getHDLGenerator(AttributeSet attrs) {
		if (isHDLSupportedComponent(attrs)) return myHDLGenerator;
		else return null;
	}

	@Override
	public String getHDLName(AttributeSet attrs) {
		return CorrectLabel.getCorrectLabel(this.getName());
	}

	@Override
	public boolean activeOnHigh(AttributeSet attrs) {
		return true;
	}

	@Override
	public boolean checkForGatedClocks(netlistComponent comp) {
		return false;
	}

	@Override
	public int[] clockPinIndex(netlistComponent comp) {
		return new int[] {0};
	}

	@Override
	public boolean hasThreeStateDrivers(AttributeSet attrs) {
		return false;
	}

	@Override
	public boolean isHDLSupportedComponent(AttributeSet attrs) {
		if (myHDLGenerator != null) return myHDLGenerator.isHdlSupportedTarget(attrs);
		return false;
	}

	@Override
	public boolean isHDLGeneratorAvailable() {
		return myHDLGenerator != null;
	}

	@Override
	public boolean requiresGlobalClock() {
		return requiresGlobalClockConnection;
	}

	@Override
	public boolean requiresNonZeroLabel() {
		return requiresLabel;
	}

}
