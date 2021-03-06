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

public class Subtractor extends InstanceFactory {

	private static final int IN0   = 0;
	private static final int IN1   = 1;
	private static final int OUT   = 2;
	private static final int B_IN  = 3;
	private static final int B_OUT = 4;

	public Subtractor() {

		super("Subtractor", LC.createStringBinding("subtractorComponent"));
		setAttributes(new Attribute[] { StdAttr.WIDTH },
				new Object[] { BitWidth.create(8) });
		setKeyConfigurator(new BitWidthConfigurator(StdAttr.WIDTH));
		setOffsetBounds(Bounds.create(-40, -20, 40, 40));
		setIcon("subtractor.gif");

		Port[] ps = new Port[5];
		ps[IN0]   = new Port(-40, -10, Port.INPUT,  StdAttr.WIDTH);
		ps[IN1]   = new Port(-40,  10, Port.INPUT,  StdAttr.WIDTH);
		ps[OUT]   = new Port(  0,   0, Port.OUTPUT, StdAttr.WIDTH);
		ps[B_IN]  = new Port(-20, -20, Port.INPUT,  1);
		ps[B_OUT] = new Port(-20,  20, Port.OUTPUT, 1);
		ps[IN0].setToolTip(LC.createStringBinding("subtractorMinuendTip"));
		ps[IN1].setToolTip(LC.createStringBinding("subtractorSubtrahendTip"));
		ps[OUT].setToolTip(LC.createStringBinding("subtractorOutputTip"));
		ps[B_IN].setToolTip(LC.createStringBinding("subtractorBorrowInTip"));
		ps[B_OUT].setToolTip(LC.createStringBinding("subtractorBorrowOutTip"));
		setPorts(ps);

	}

	@Override
	public void propagate(InstanceState state) {

		// get attributes
		BitWidth data = state.getAttributeValue(StdAttr.WIDTH);

		// compute outputs
		Value a = state.getPort(IN0);
		Value b = state.getPort(IN1);
		Value b_in = state.getPort(B_IN);
		if (b_in == Value.UNKNOWN || b_in == Value.NIL) b_in = Value.FALSE;
		Value[] outs = Adder.computeSum(data, a, b.not(), b_in.not());

		// propagate them
		int delay = (data.getWidth() + 4) * Adder.PER_DELAY;
		state.setPort(OUT,   outs[0],       delay);
		state.setPort(B_OUT, outs[1].not(), delay);

	}

	@Override
	public void paintInstance(InstancePainter painter) {

		Graphics g = painter.getGraphics();
		painter.drawBounds();

		g.setColor(Color.GRAY);

		painter.drawPort(IN0);
		painter.drawPort(IN1);
		painter.drawPort(OUT);
		painter.drawPort(B_IN,  "b in",  Direction.NORTH);
		painter.drawPort(B_OUT, "b out", Direction.SOUTH);

		Location loc = painter.getLocation();
		int x = loc.getX();
		int y = loc.getY();
		g.setLineWidth(2);
		g.setColor(Color.BLACK);
		g.c.strokeLine(x - 15, y, x - 5, y);

		g.toDefault();

	}

}
