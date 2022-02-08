/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.arith;

import LogisimFX.data.*;
import LogisimFX.instance.*;
import LogisimFX.newgui.MainFrame.Canvas.Graphics;
import LogisimFX.std.LC;
import LogisimFX.tools.key.BitWidthConfigurator;

import javafx.scene.paint.Color;

import java.util.Arrays;

public class Shifter extends InstanceFactory {

	static final AttributeOption SHIFT_LOGICAL_LEFT
		= new AttributeOption("ll", LC.createStringBinding("shiftLogicalLeft"));
	static final AttributeOption SHIFT_LOGICAL_RIGHT
		= new AttributeOption("lr", LC.createStringBinding("shiftLogicalRight"));
	static final AttributeOption SHIFT_ARITHMETIC_RIGHT
		= new AttributeOption("ar", LC.createStringBinding("shiftArithmeticRight"));
	static final AttributeOption SHIFT_ROLL_LEFT
		= new AttributeOption("rl", LC.createStringBinding("shiftRollLeft"));
	static final AttributeOption SHIFT_ROLL_RIGHT
		= new AttributeOption("rr", LC.createStringBinding("shiftRollRight"));
	static final Attribute<AttributeOption> ATTR_SHIFT
		= Attributes.forOption("shift", LC.createStringBinding("shifterShiftAttr"),
				new AttributeOption[] { SHIFT_LOGICAL_LEFT, SHIFT_LOGICAL_RIGHT,
					SHIFT_ARITHMETIC_RIGHT, SHIFT_ROLL_LEFT, SHIFT_ROLL_RIGHT });

	private static final int IN0   = 0;
	private static final int IN1   = 1;
	private static final int OUT   = 2;

	public Shifter() {

		super("Shifter", LC.createStringBinding("shifterComponent"));
		setAttributes(new Attribute[] {
				StdAttr.WIDTH, ATTR_SHIFT
			}, new Object[] {
				BitWidth.create(8), SHIFT_LOGICAL_LEFT
			});
		setKeyConfigurator(new BitWidthConfigurator(StdAttr.WIDTH));
		setOffsetBounds(Bounds.create(-40, -20, 40, 40));
		setIcon("shifter.gif");

	}

	@Override
	protected void configureNewInstance(Instance instance) {

		configurePorts(instance);
		instance.addAttributeListener();

	}

	@Override
	protected void instanceAttributeChanged(Instance instance, Attribute<?> attr) {

		if (attr == StdAttr.WIDTH) {
			configurePorts(instance);
		}

	}

	private void configurePorts(Instance instance) {

		BitWidth dataWid = instance.getAttributeValue(StdAttr.WIDTH);
		int data = dataWid == null ? 32 : dataWid.getWidth();
		int shift = 1;
		while ((1 << shift) < data) shift++;

		Port[] ps = new Port[3];
		ps[IN0]   = new Port(-40, -10, Port.INPUT,  data);
		ps[IN1]   = new Port(-40,  10, Port.INPUT,  shift);
		ps[OUT]   = new Port(  0,   0, Port.OUTPUT, data);
		ps[IN0].setToolTip(LC.createStringBinding("shifterInputTip"));
		ps[IN1].setToolTip(LC.createStringBinding("shifterDistanceTip"));
		ps[OUT].setToolTip(LC.createStringBinding("shifterOutputTip"));
		instance.setPorts(ps);

	}

	@Override
	public void propagate(InstanceState state) {

		// compute output
		BitWidth dataWidth = state.getAttributeValue(StdAttr.WIDTH);
		int bits = dataWidth == null ? 32 : dataWidth.getWidth();
		Value vx = state.getPort(IN0);
		Value vd = state.getPort(IN1);
		Value vy; // y will by x shifted by d
		if (vd.isFullyDefined() && vx.getWidth() == bits) {
			int d = vd.toIntValue();
			Object shift = state.getAttributeValue(ATTR_SHIFT);
			if (d == 0) {
				vy = vx;
			} else if (vx.isFullyDefined()) {
				int x = vx.toIntValue();
				int y;
				if (shift == SHIFT_LOGICAL_RIGHT) {
					y = x >>> d;
				} else if (shift == SHIFT_ARITHMETIC_RIGHT) {
					if (d >= bits) d = bits - 1;
					y = x >> d | ((x << (32 - bits)) >> (32 - bits + d));
				} else if (shift == SHIFT_ROLL_RIGHT) {
					if (d >= bits) d -= bits;
					y = (x >>> d) | (x << (bits - d));
				} else if (shift == SHIFT_ROLL_LEFT) {
					if (d >= bits) d -= bits;
					y = (x << d) | (x >>> (bits - d));
				} else { // SHIFT_LOGICAL_LEFT
					y = x << d;
				}
				vy = Value.createKnown(dataWidth, y);
			} else {
				Value[] x = vx.getAll();
				Value[] y = new Value[bits];
				if (shift == SHIFT_LOGICAL_RIGHT) {
					if (d >= bits) d = bits;
					System.arraycopy(x, d, y, 0, bits - d);
					Arrays.fill(y, bits - d, bits, Value.FALSE);
				} else if (shift == SHIFT_ARITHMETIC_RIGHT) {
					if (d >= bits) d = bits;
					System.arraycopy(x, d, y, 0, x.length - d);
					Arrays.fill(y, bits - d, y.length, x[bits - 1]);
				} else if (shift == SHIFT_ROLL_RIGHT) {
					if (d >= bits) d -= bits;
					System.arraycopy(x, d, y, 0, bits - d);
					System.arraycopy(x, 0, y, bits - d, d);
				} else if (shift == SHIFT_ROLL_LEFT) {
					if (d >= bits) d -= bits;
					System.arraycopy(x, x.length - d, y, 0, d);
					System.arraycopy(x, 0, y, d, bits - d);
				} else { // SHIFT_LOGICAL_LEFT
					if (d >= bits) d = bits;
					Arrays.fill(y, 0, d, Value.FALSE);
					System.arraycopy(x, 0, y, d, bits - d);
				}
				vy = Value.create(y);
			}
		} else {
			vy = Value.createError(dataWidth);
		}

		// propagate them
		int delay = dataWidth.getWidth() * (3 * Adder.PER_DELAY);
		state.setPort(OUT, vy, delay);

	}
	
	@Override
	public void paintInstance(InstancePainter painter) {

		Graphics g = painter.getGraphics();
		painter.drawBounds();

		painter.drawPorts();

		Location loc = painter.getLocation();
		int x = loc.getX() - 15;
		int y = loc.getY();
		Object shift = painter.getAttributeValue(ATTR_SHIFT);
		g.setColor(Color.BLACK);
		if (shift == SHIFT_LOGICAL_RIGHT) {
			g.c.fillRect(x, y - 1, 8, 3);
			drawArrow(g, x + 10, y, -4);
		} else if (shift == SHIFT_ARITHMETIC_RIGHT) {
			g.c.fillRect(x, y - 1, 2, 3);
			g.c.fillRect(x + 3, y - 1, 5, 3);
			drawArrow(g, x + 10, y, -4);
		} else if (shift == SHIFT_ROLL_RIGHT) {
			g.c.fillRect(x, y - 1, 5, 3);
			g.c.fillRect(x + 8, y - 7, 2, 8);
			g.c.fillRect(x, y - 7, 2, 8);
			g.c.fillRect(x, y - 7, 10, 2);
			drawArrow(g, x + 8, y, -4);
		} else if (shift == SHIFT_ROLL_LEFT) {
			g.c.fillRect(x + 6, y - 1, 4, 3);
			g.c.fillRect(x + 8, y - 7, 2, 8);
			g.c.fillRect(x, y - 7, 2, 8);
			g.c.fillRect(x, y - 7, 10, 2);
			drawArrow(g, x + 3, y, 4);
		} else { // SHIFT_LOGICAL_LEFT
			g.c.fillRect(x + 2, y - 1, 8, 3);
			drawArrow(g, x, y, 4);
		}

		g.toDefault();

	}
	
	private void drawArrow(Graphics g, int x, int y, int d) {

		double[] px = { x + d, x, x + d };
		double[] py = { y + d, y, y - d };
		g.c.fillPolygon(px, py, 3);

	}

}
