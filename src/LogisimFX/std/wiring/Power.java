/* Copyright (c) 2011, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

/**
 * Based on PUCTools (v0.9 beta) by CRC - PUC - Minas (pucmg.crc at gmail.com)
 */

package LogisimFX.std.wiring;

import LogisimFX.data.*;
import LogisimFX.instance.*;
import LogisimFX.newgui.MainFrame.Canvas.Graphics;
import LogisimFX.std.LC;
import LogisimFX.tools.key.BitWidthConfigurator;
import LogisimFX.circuit.Wire;

public class Power extends InstanceFactory {

	public Power() {

		super("Power", LC.createStringBinding("powerComponent"));
		setIcon("power.gif");
		setAttributes(new Attribute[] { StdAttr.FACING, StdAttr.WIDTH },
				new Object[] { Direction.NORTH, BitWidth.ONE });
		setFacingAttribute(StdAttr.FACING);
		setKeyConfigurator(new BitWidthConfigurator(StdAttr.WIDTH));
		setPorts(new Port[] { new Port(0, 0, Port.OUTPUT, StdAttr.WIDTH) });

	}

	@Override
	protected void configureNewInstance(Instance instance) {
		instance.addAttributeListener();
	}

	@Override
	protected void instanceAttributeChanged(Instance instance, Attribute<?> attr) {

		if (attr == StdAttr.FACING) {
			instance.recomputeBounds();
		}

	}

	@Override
	public Bounds getOffsetBounds(AttributeSet attrs) {

		return Bounds.create(0, -8, 15, 16)
			.rotate(Direction.EAST, attrs.getValue(StdAttr.FACING), 0, 0);

	}

	@Override
	public void propagate(InstanceState state) {

		BitWidth width = state.getAttributeValue(StdAttr.WIDTH);
		state.setPort(0, Value.repeat(Value.TRUE, width.getWidth()), 1);

	}

	@Override
	public void paintInstance(InstancePainter painter) {

		drawInstance(painter, false);
		painter.drawPorts();

		painter.getGraphics().toDefault();

	}

	@Override
	public void paintGhost(InstancePainter painter) {
		drawInstance(painter, true);
	}

	private void drawInstance(InstancePainter painter, boolean isGhost) {

		Graphics g = painter.getGraphics();
		Location loc = painter.getLocation();
		g.translate(loc.getX(), loc.getY());

		Direction from = painter.getAttributeValue(StdAttr.FACING);
		int degrees = Direction.EAST.toDegrees() - from.toDegrees();
		double radians = Math.toRadians((degrees + 360) % 360);
		g.rotate(degrees);

		g.setLineWidth(Wire.WIDTH);
		if (!isGhost && painter.getShowState()) {
			g.setColor(painter.getPort(0).getColor());
		}
		g.c.strokeLine(0, 0, 5, 0);

		g.setLineWidth(1);
		if (!isGhost && painter.shouldDrawColor()) {
			BitWidth width = painter.getAttributeValue(StdAttr.WIDTH);
			g.setColor(Value.repeat(Value.TRUE, width.getWidth()).getColor());
		}
		g.c.strokePolygon(new double[] { 6, 14, 6 }, new double[] { -8, 0, 8 }, 3);

		g.toDefault();

	}

}
