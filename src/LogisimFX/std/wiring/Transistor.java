/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * Original code by Carl Burch (http://www.cburch.com), 2011.
 * License information is located in the Launch file
 */

/*
 * Based on PUCTools (v0.9 beta) by CRC - PUC - Minas (pucmg.crc at gmail.com)
 */

package LogisimFX.std.wiring;

import LogisimFX.IconsManager;
import LogisimFX.data.*;
import LogisimFX.instance.*;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.std.LC;
import LogisimFX.tools.key.BitWidthConfigurator;
import LogisimFX.circuit.Wire;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;

public class Transistor extends InstanceFactory {

	static final AttributeOption TYPE_P
		= new AttributeOption("p", LC.createStringBinding("transistorTypeP"));
	static final AttributeOption TYPE_N
		= new AttributeOption("n", LC.createStringBinding("transistorTypeN"));
	static final Attribute<AttributeOption> ATTR_TYPE
		= Attributes.forOption("type", LC.createStringBinding("transistorTypeAttr"),
				new AttributeOption[] { TYPE_P, TYPE_N });
	
	static final int OUTPUT = 0;
	static final int INPUT = 1;
	static final int GATE = 2;
	
	private static final ImageView ICON_N = IconsManager.getIcon("trans1.gif");
	private static final ImageView ICON_P = IconsManager.getIcon("trans0.gif");

	public Transistor() {

		super("Transistor", LC.createStringBinding("transistorComponent"));
		setAttributes(
				new Attribute[] { StdAttr.FPGA_SUPPORTED, ATTR_TYPE, StdAttr.FACING,
						Wiring.ATTR_GATE, StdAttr.WIDTH },
				new Object[] { Boolean.FALSE, TYPE_P, Direction.EAST,
						Wiring.GATE_TOP_LEFT, BitWidth.ONE });
		setFacingAttribute(StdAttr.FACING);
		setKeyConfigurator(new BitWidthConfigurator(StdAttr.WIDTH));
		setIcon("trans0.gif");

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
			updatePorts(instance);
		} else if (attr == ATTR_TYPE) {
			instance.fireInvalidated();
		}

	}

	private void updatePorts(Instance instance) {

		Direction facing = instance.getAttributeValue(StdAttr.FACING);
		int dx = 0;
		int dy = 0;
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

		Port[] ports = new Port[3];
		ports[OUTPUT] = new Port(0, 0, Port.OUTPUT, StdAttr.WIDTH);
		ports[INPUT] = new Port(40 * dx, 40 * dy, Port.INPUT, StdAttr.WIDTH);
		if (flip) {
			ports[GATE] = new Port(20 * (dx + dy), 20 * (-dx + dy), Port.INPUT, 1);
		} else {
			ports[GATE] = new Port(20 * (dx - dy), 20 * (dx + dy), Port.INPUT, 1);
		}
		instance.setPorts(ports);

	}

	@Override
	public Bounds getOffsetBounds(AttributeSet attrs) {

		Direction facing = attrs.getValue(StdAttr.FACING);
		Object gateLoc = attrs.getValue(Wiring.ATTR_GATE);
		int delta = gateLoc == Wiring.GATE_TOP_LEFT ? -20 : 0;
		if (facing == Direction.NORTH) {
			return Bounds.create(delta, 0, 20, 40);
		} else if (facing == Direction.SOUTH) {
			return Bounds.create(delta, -40, 20, 40);
		} else if (facing == Direction.WEST) {
			return Bounds.create(0, delta, 40, 20);
		} else { // facing == Direction.EAST
			return Bounds.create(-40, delta, 40, 20);
		}

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
		Value gate = state.getPortValue(GATE);
		Value input = state.getPortValue(INPUT);
		Value desired = state.getAttributeValue(ATTR_TYPE) == TYPE_P
			? Value.FALSE : Value.TRUE;

		if (!gate.isFullyDefined()) {
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
		} else if (gate != desired) {
			return Value.createUnknown(width);
		} else {
			return input;
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

		Object type = painter.getAttributeValue(ATTR_TYPE);
		Object powerLoc = painter.getAttributeValue(Wiring.ATTR_GATE);
		Direction from = painter.getAttributeValue(StdAttr.FACING);
		Direction facing = painter.getAttributeValue(StdAttr.FACING);
		boolean flip = (facing == Direction.SOUTH || facing == Direction.WEST)
			== (powerLoc == Wiring.GATE_TOP_LEFT);

		int degrees = Direction.EAST.toDegrees() - from.toDegrees();
		double radians = Math.toRadians((degrees + 360) % 360);
		int m = flip ? 1 : -1;

		Graphics g = painter.getGraphics();
		Location loc = painter.getLocation();
		g.c.translate(loc.getX(), loc.getY());
		g.rotate(degrees);

		Paint gate;
		Paint input;
		Paint output;
		Paint platform;
		if (!isGhost && painter.getShowState()) {
			gate = painter.getPortValue(GATE).getColor();
			input = painter.getPortValue(INPUT).getColor();
			output = painter.getPortValue(OUTPUT).getColor();
			Value out = computeOutput(painter);
			platform = out.isUnknown() ? Value.UNKNOWN.getColor() : out.getColor();
		} else {
			Paint base = g.getPaint();
			gate = base;
			input = base;
			output = base;
			platform = base;
		}
		
		// input and output lines
		g.setLineWidth(Wire.WIDTH);
		g.setColor(output);
		g.c.strokeLine(0, 0, -11, 0);
		g.c.strokeLine(-11, m * 7, -11, 0);

		g.setColor(input);
		g.c.strokeLine(-40, 0, -29, 0);
		g.c.strokeLine(-29, m * 7, -29, 0);

		// gate line
		g.setColor(gate);
		if (type == TYPE_P) {
			g.c.strokeLine(-20, m * 20, -20, m * 15);
			g.setLineWidth(1);
			g.c.strokeOval(-22, m * 12 - 2, 4, 4);
		} else {
			g.c.strokeLine(-20, m * 20, -20, m * 11);
			g.setLineWidth(1);
		}
		
		// draw platforms
		g.c.strokeLine(-10, m * 10, -30, m * 10); // gate platform
		g.setColor(platform);
		g.c.strokeLine(-9, m * 8, -31, m * 8); // input/output platform

		// arrow (same color as platform)
		g.c.strokeLine(-21, m * 6, -18, m * 3);
		g.c.strokeLine(-21, 0, -18, m * 3);

		g.toDefault();
		g.c.translate(-loc.getX(), -loc.getY());

	}

}
