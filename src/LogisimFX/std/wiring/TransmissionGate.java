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
import javafx.scene.paint.Paint;

public class TransmissionGate extends InstanceFactory {

	static final int OUTPUT = 0;
	static final int INPUT = 1;
	static final int GATE0 = 2;
	static final int GATE1 = 3;

	public TransmissionGate() {

		super("Transmission Gate", LC.createStringBinding("transmissionGateComponent"));
		setIcon("transmis.gif");
		setAttributes(new Attribute[] { StdAttr.FPGA_SUPPORTED, StdAttr.FACING, Wiring.ATTR_GATE, StdAttr.WIDTH },
				new Object[] { Boolean.FALSE, Direction.EAST, Wiring.GATE_TOP_LEFT, BitWidth.ONE });
		setFacingAttribute(StdAttr.FACING);
		setKeyConfigurator(new BitWidthConfigurator(StdAttr.WIDTH));

	}

	@Override
	protected void configureNewInstance(Instance instance) {

		instance.addAttributeListener();
		updatePorts(instance);

	}

	@Override
	protected void instanceAttributeChanged(Instance instance, Attribute<?> attr) {

		if (attr == StdAttr.FACING || attr == Wiring.ATTR_GATE) {
			instance.recomputeBounds();
			updatePorts(instance);
		} else if (attr == StdAttr.WIDTH) {
			instance.fireInvalidated();
		}

	}

	private void updatePorts(Instance instance) {

		int dx = 0;
		int dy = 0;
		Direction facing = instance.getAttributeValue(StdAttr.FACING);
		if (facing == Direction.NORTH) {
			dy = 1;
		} else if (facing == Direction.EAST) {
			dx = -1;
		} else if (facing == Direction.SOUTH) {
			dy = -1;
		} else if (facing == Direction.WEST) {
			dx = 1;
		}

		Object powerLoc = instance.getAttributeValue(Wiring.ATTR_GATE);
		boolean flip = (facing == Direction.SOUTH || facing == Direction.WEST)
			== (powerLoc == Wiring.GATE_TOP_LEFT);

		Port[] ports = new Port[4];
		ports[OUTPUT] = new Port(0, 0, Port.OUTPUT, StdAttr.WIDTH);
		ports[INPUT] = new Port(40 * dx, 40 * dy, Port.INPUT, StdAttr.WIDTH);
		if (flip) {
			ports[GATE1] = new Port(20 * (dx - dy), 20 * (dx + dy), Port.INPUT, 1);
			ports[GATE0] = new Port(20 * (dx + dy), 20 * (-dx + dy), Port.INPUT, 1);
		} else {
			ports[GATE0] = new Port(20 * (dx - dy), 20 * (dx + dy), Port.INPUT, 1);
			ports[GATE1] = new Port(20 * (dx + dy), 20 * (-dx + dy), Port.INPUT, 1);
		}
		instance.setPorts(ports);

	}

	@Override
	public Bounds getOffsetBounds(AttributeSet attrs) {

		Direction facing = attrs.getValue(StdAttr.FACING);
		return Bounds.create(0, -20, 40, 40).rotate(Direction.WEST, facing, 0, 0);

	}

	@Override
	public boolean contains(Location loc, AttributeSet attrs) {

		if (super.contains(loc, attrs)) {
			Direction facing = attrs.getValue(StdAttr.FACING);
			Location center = Location.create(0, 0).translate(facing, -20);
			return center.manhattanDistanceTo(loc) < 24;
		} else {
			return false;
		}

	}

	@Override
	public void propagate(InstanceState state) {
		state.setPort(OUTPUT, computeOutput(state), 1);
	}

	private Value computeOutput(InstanceState state) {

		BitWidth width = state.getAttributeValue(StdAttr.WIDTH);
		Value input = state.getPortValue(INPUT);
		Value gate0 = state.getPortValue(GATE0);
		Value gate1 = state.getPortValue(GATE1);

		if (gate0.isFullyDefined() && gate1.isFullyDefined() && gate0 != gate1) {
			if (gate0 == Value.TRUE) {
				return Value.createUnknown(width);
			} else {
				return input;
			}
		} else {
			if (input.isFullyDefined()) {
				return Value.createError(width);
			} else {
				Value[] v = input.getAll();
				for (int i = 0; i < v.length; i++) {
					if (v[i] != Value.UNKNOWN) {
						v[i] = Value.ERROR;
					}
				}
				return Value.create(v);
			}
		}

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

		Bounds bds = painter.getBounds();
		Object powerLoc = painter.getAttributeValue(Wiring.ATTR_GATE);
		Direction facing = painter.getAttributeValue(StdAttr.FACING);
		boolean flip = (facing == Direction.SOUTH || facing == Direction.WEST)
			== (powerLoc == Wiring.GATE_TOP_LEFT);

		int degrees = Direction.WEST.toDegrees() - facing.toDegrees();
		if (flip) degrees += 180;
		double radians = Math.toRadians((degrees + 360) % 360);

		Graphics g = painter.getGraphics();

		g.c.translate(bds.getX() + 20, bds.getY() + 20);
		g.c.rotate(degrees);
		g.c.translate(-(bds.getX() + 20), -(bds.getY() + 20));

		g.c.translate(bds.getX(), bds.getY());

		g.setLineWidth(Wire.WIDTH);
		
		Paint gate0 = g.getPaint();
		Paint gate1 = gate0;
		Paint input = gate0;
		Paint output = gate0;
		Paint platform = gate0;
		if (!isGhost && painter.getShowState()) {
			gate0 = painter.getPortValue(GATE0).getColor();
			gate1 = painter.getPortValue(GATE0).getColor();
			input = painter.getPortValue(INPUT).getColor();
			output = painter.getPortValue(OUTPUT).getColor();
			platform = computeOutput(painter).getColor();
		}

		g.setColor(flip ? input : output);
		g.c.strokeLine(0, 20, 11, 20);
		g.c.strokeLine(11, 13, 11, 27);

		g.setColor(flip ? output : input);
		g.c.strokeLine(29, 20, 40, 20);
		g.c.strokeLine(29, 13, 29, 27);

		g.setColor(gate0);
		g.c.strokeLine(20, 35, 20, 40);
		g.setLineWidth(1);
		g.c.strokeOval(18, 30, 4, 4);
		g.c.strokeLine(10, 30, 30, 30);
		g.setLineWidth(Wire.WIDTH);

		g.setColor(gate1);
		g.c.strokeLine(20, 9, 20, 0);
		g.setLineWidth(1);
		g.c.strokeLine(10, 10, 30, 10);

		g.setColor(platform);
		g.c.strokeLine(9, 12, 31, 12);
		g.c.strokeLine(9, 28, 31, 28);
		if (flip) { // arrow
			g.c.strokeLine(18, 17, 21, 20);
			g.c.strokeLine(18, 23, 21, 20);
		} else {
			g.c.strokeLine(22, 17, 19, 20);
			g.c.strokeLine(22, 23, 19, 20);
		}


		g.c.translate(-bds.getX(), -bds.getY());
		g.c.translate(bds.getX() + 20, bds.getY() + 20);
		g.c.rotate(-degrees);
		g.c.translate(-(bds.getX() + 20), -(bds.getY() + 20));

		g.toDefault();

	}

}
