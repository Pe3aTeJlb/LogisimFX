/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * Original code by Carl Burch (http://www.cburch.com), 2011.
 * License information is located in the Launch file
 */

/*
 * Based on PUCTools (v0.9 beta) by CRC - PUC - Minas (pucmg.crc at gmail.com)
 */

package LogisimFX.std.wiring;

import LogisimFX.data.*;
import LogisimFX.instance.*;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.std.LC;
import LogisimFX.tools.key.BitWidthConfigurator;
import LogisimFX.circuit.Wire;

public class Ground extends InstanceFactory {

	public Ground() {

		super("Ground", LC.createStringBinding("groundComponent"));
		setIcon("ground.gif");
		setAttributes(new Attribute[] { StdAttr.FACING, StdAttr.WIDTH },
				new Object[] { Direction.SOUTH, BitWidth.ONE });
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

		return Bounds.create(0, -8, 14, 16)
			.rotate(Direction.EAST, attrs.getValue(StdAttr.FACING), 0, 0);

	}

	@Override
	public void propagate(InstanceState state) {

		BitWidth width = state.getAttributeValue(StdAttr.WIDTH);
		state.setPort(0, Value.repeat(Value.FALSE, width.getWidth()), 1);

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
			g.setColor(Value.repeat(Value.FALSE, width.getWidth()).getColor());
		}
		g.c.strokeLine(6, -8, 6, 8);
		g.c.strokeLine(9, -5, 9, 5);
		g.c.strokeLine(12, -2, 12, 2);

		g.toDefault();

	}

}
