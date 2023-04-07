/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.arith;

import LogisimFX.data.*;
import LogisimFX.fpga.designrulecheck.CorrectLabel;
import LogisimFX.instance.*;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.std.LC;
import LogisimFX.tools.key.BitWidthConfigurator;

import javafx.scene.paint.Color;

public class Subtractor extends InstanceFactory {

	static final int IN0   = 0;
	static final int IN1   = 1;
	static final int OUT   = 2;
	static final int B_IN  = 3;
	static final int B_OUT = 4;

	public Subtractor() {

		super("Subtractor", LC.createStringBinding("subtractorComponent"), new SubtractorHdlGeneratorFactory());
		setAttributes(new Attribute[] { StdAttr.FPGA_SUPPORTED, StdAttr.WIDTH },
				new Object[] { Boolean.FALSE, BitWidth.create(8) });
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
		Value a = state.getPortValue(IN0);
		Value b = state.getPortValue(IN1);
		Value b_in = state.getPortValue(B_IN);
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



	@Override
	public String getHDLName(AttributeSet attrs) {
		final var fullName = new StringBuilder();
		if (attrs.getValue(StdAttr.WIDTH).getWidth() == 1) fullName.append("FullSubtractor");
		else fullName.append(CorrectLabel.getCorrectLabel(this.getName()));
		return fullName.toString();
	}

}
