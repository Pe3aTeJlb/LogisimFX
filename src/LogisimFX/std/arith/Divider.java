/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.arith;

import LogisimFX.data.*;
import LogisimFX.instance.*;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.std.LC;
import LogisimFX.tools.key.BitWidthConfigurator;

import javafx.scene.paint.Color;

public class Divider extends InstanceFactory {

	static final int PER_DELAY = 1;

	static final int IN0   = 0;
	static final int IN1   = 1;
	static final int OUT   = 2;
	static final int UPPER = 3;
	static final int REM   = 4;

	public Divider() {

		super("Divider", LC.createStringBinding("dividerComponent"));
		setAttributes(new Attribute[] { StdAttr.FPGA_SUPPORTED, StdAttr.WIDTH },
				new Object[] { Boolean.FALSE, BitWidth.create(8) });
		setKeyConfigurator(new BitWidthConfigurator(StdAttr.WIDTH));
		setOffsetBounds(Bounds.create(-40, -20, 40, 40));
		setIcon("divider.gif");

		Port[] ps = new Port[5];
		ps[IN0]   = new Port(-40, -10, Port.INPUT,  StdAttr.WIDTH);
		ps[IN1]   = new Port(-40,  10, Port.INPUT,  StdAttr.WIDTH);
		ps[OUT]   = new Port(  0,   0, Port.OUTPUT, StdAttr.WIDTH);
		ps[UPPER] = new Port(-20, -20, Port.INPUT,  StdAttr.WIDTH);
		ps[REM]   = new Port(-20,  20, Port.OUTPUT, StdAttr.WIDTH);
		ps[IN0].setToolTip(LC.createStringBinding("dividerDividendLowerTip"));
		ps[IN1].setToolTip(LC.createStringBinding("dividerDivisorTip"));
		ps[OUT].setToolTip(LC.createStringBinding("dividerOutputTip"));
		ps[UPPER].setToolTip(LC.createStringBinding("dividerDividendUpperTip"));
		ps[REM].setToolTip(LC.createStringBinding("dividerRemainderTip"));
		setPorts(ps);

	}

	@Override
	public void propagate(InstanceState state) {

		// get attributes
		BitWidth dataWidth = state.getAttributeValue(StdAttr.WIDTH);

		// compute outputs
		Value a = state.getPortValue(IN0);
		Value b = state.getPortValue(IN1);
		Value upper = state.getPortValue(UPPER);
		Value[] outs = Divider.computeResult(dataWidth, a, b, upper);

		// propagate them
		int delay = dataWidth.getWidth() * (dataWidth.getWidth() + 2) * PER_DELAY;
		state.setPort(OUT, outs[0], delay);
		state.setPort(REM, outs[1], delay);

	}

	@Override
	public void paintInstance(InstancePainter painter) {

		Graphics g = painter.getGraphics();
		painter.drawBounds();

		g.setColor(Color.GRAY);
		painter.drawPort(IN0);
		painter.drawPort(IN1);
		painter.drawPort(OUT);
		painter.drawPort(UPPER, LC.get("dividerUpperInput"),  Direction.NORTH);
		painter.drawPort(REM, LC.get("dividerRemainderOutput"), Direction.SOUTH);

		Location loc = painter.getLocation();
		int x = loc.getX();
		int y = loc.getY();
		g.setLineWidth(2);
		g.setColor(Color.BLACK);
		g.c.fillOval(x - 12, y - 7, 4, 4);
		g.c.strokeLine(x - 15, y, x - 5, y);
		g.c.fillOval(x - 12, y + 3, 4, 4);

		g.toDefault();

	}

	static Value[] computeResult(BitWidth width, Value a, Value b, Value upper) {

		int w = width.getWidth();
		if (upper == Value.NIL || upper.isUnknown()) upper = Value.createKnown(width, 0);
		if (a.isFullyDefined() && b.isFullyDefined() && upper.isFullyDefined()) {
			long num = ((long) upper.toIntValue() << w)
				| ((long) a.toIntValue() & 0xFFFFFFFFL);
			long den = (long) b.toIntValue() & 0xFFFFFFFFL;
			if (den == 0) den = 1;
			long result = num / den;
			long rem = num % den;
			if (rem < 0) {
				if (den >= 0) {
					rem += den;
					result--;
				} else {
					rem -= den;
					result++;
				}
			}
			return new Value[] { Value.createKnown(width, (int) result),
					Value.createKnown(width, (int) rem) };
		} else if (a.isErrorValue() || b.isErrorValue() || upper.isErrorValue()) {
			return new Value[] { Value.createError(width), Value.createError(width) };
		} else {
			return new Value[] { Value.createUnknown(width), Value.createUnknown(width) };
		}

	}

}
