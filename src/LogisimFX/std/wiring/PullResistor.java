/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.wiring;

import LogisimFX.IconsManager;
import LogisimFX.data.*;
import LogisimFX.instance.*;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.std.LC;
import LogisimFX.util.GraphicsUtil;
import LogisimFX.prefs.AppPreferences;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class PullResistor extends InstanceFactory {

	public static final Attribute<AttributeOption> ATTR_PULL_TYPE
		= Attributes.forOption("pull", LC.createStringBinding("pullTypeAttr"),
				new AttributeOption[] {
					new AttributeOption(Value.FALSE, "0", LC.createStringBinding("pullZeroType")),
					new AttributeOption(Value.TRUE,  "1", LC.createStringBinding("pullOneType")),
					new AttributeOption(Value.ERROR, "X", LC.createStringBinding("pullErrorType"))
			});

	public static final PullResistor FACTORY = new PullResistor();
	
	private static final ImageView ICON_SHAPED = IconsManager.getIcon("pullshap.gif");
	private static final ImageView ICON_RECTANGULAR = IconsManager.getIcon("pullrect.gif");

	public PullResistor() {
		super("Pull Resistor", LC.createStringBinding("pullComponent"));
		setAttributes(new Attribute[] { StdAttr.FPGA_SUPPORTED, StdAttr.FACING, ATTR_PULL_TYPE },
				new Object[] { Boolean.FALSE, Direction.SOUTH, ATTR_PULL_TYPE.parse("0") });
		setFacingAttribute(StdAttr.FACING);
	}

	@Override
	public Bounds getOffsetBounds(AttributeSet attrs) {

		Direction facing = attrs.getValue(StdAttr.FACING);
		if (facing == Direction.EAST) {
			return Bounds.create(-42, -6, 42, 12);
		} else if (facing == Direction.WEST) {
			return Bounds.create(0, -6, 42, 12);
		} else if (facing == Direction.NORTH) {
			return Bounds.create(-6, 0, 12, 42);
		} else {
			return Bounds.create(-6, -42, 12, 42);
		}

	}
	
	//
	// graphics methods
	//

	@Override
	public ImageView getIcon(){

		if (AppPreferences.GATE_SHAPE.get().equals(AppPreferences.SHAPE_SHAPED)) {
			return ICON_SHAPED;
		} else {
			return ICON_RECTANGULAR;
		}

	}
	
	@Override
	public void paintGhost(InstancePainter painter) {

		Value pull = getPullValue(painter.getAttributeSet());
		paintBase(painter, pull, null, null);

	}

	@Override
	public void paintInstance(InstancePainter painter) {

		Location loc = painter.getLocation();
		int x = loc.getX();
		int y = loc.getY();
		Graphics g = painter.getGraphics();
		g.c.translate(x, y);
		Value pull = getPullValue(painter.getAttributeSet());
		Value actual = painter.getPortValue(0);
		paintBase(painter, pull, pull.getColor(), actual.getColor());
		g.c.translate(-x, -y);
		painter.drawPorts();

		g.toDefault();

	}
	
	private void paintBase(InstancePainter painter, Value pullValue,
						   Paint inColor, Paint outColor) {

		boolean color = painter.shouldDrawColor();
		Direction facing = painter.getAttributeValue(StdAttr.FACING);
		Graphics g = painter.getGraphics();
		Color baseColor = g.getColor();
		g.setLineWidth(3);
		if (color && inColor != null){
			g.setColor(inColor);
		}
		if (facing == Direction.EAST) {
			GraphicsUtil.drawText(g, pullValue.toDisplayString(), -32, 0,
					GraphicsUtil.H_RIGHT, GraphicsUtil.V_CENTER);
		} else if (facing == Direction.WEST) {
			GraphicsUtil.drawText(g, pullValue.toDisplayString(), 32, 0,
					GraphicsUtil.H_LEFT, GraphicsUtil.V_CENTER);
		} else if (facing == Direction.NORTH) {
			GraphicsUtil.drawText(g, pullValue.toDisplayString(), 0, 32,
					GraphicsUtil.H_CENTER, GraphicsUtil.V_TOP);
		} else {
			GraphicsUtil.drawText(g, pullValue.toDisplayString(), 0, -32,
					GraphicsUtil.H_CENTER, GraphicsUtil.V_BASELINE);
		}
		
		double rotate = 0.0;

			rotate = Direction.SOUTH.toDegrees() - facing.toDegrees();
			if (rotate != 0.0) g.c.rotate(rotate);

		g.c.strokeLine(0, -30, 0, -26);
		g.c.strokeLine(-6, -30, 6, -30);
		if (color && outColor != null){
			g.setColor(outColor);
		}
		g.c.strokeLine(0, -4, 0, 0);
		g.setColor(baseColor);
		g.setLineWidth(2);

		if (painter.getGateShape() == AppPreferences.SHAPE_SHAPED) {
			double[] xp = {   0,  -5,   5,  -5,   5, -5,  0 };
			double[] yp = { -25, -23, -19, -15, -11, -7, -5};
			g.c.strokePolyline(xp, yp, xp.length);
		} else {
			g.c.strokeRect(-5, -25, 10, 20);
		}
		if (rotate != 0.0) {
			g.c.rotate(-rotate);
		}
		g.toDefault();

	}
	
	//
	// methods for instances
	//
	@Override
	protected void configureNewInstance(Instance instance) {

		instance.addAttributeListener();
		instance.setPorts(new Port[] {
				new Port(0, 0, Port.INOUT, BitWidth.UNKNOWN)
			});

	}
	
	@Override
	protected void instanceAttributeChanged(Instance instance, Attribute<?> attr) {

		if (attr == StdAttr.FACING) {
			instance.recomputeBounds();
		} else if (attr == ATTR_PULL_TYPE) {
			instance.fireInvalidated();
		}

	}
	
	@Override
	public void propagate(InstanceState state) {
		; // nothing to do - handled by CircuitWires
	}
	
	public static Value getPullValue(Instance instance) {
		return getPullValue(instance.getAttributeSet());
	}
	
	private static Value getPullValue(AttributeSet attrs) {

		AttributeOption opt = attrs.getValue(ATTR_PULL_TYPE);
		return (Value) opt.getValue();

	}

}