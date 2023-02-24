/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.arith;

import LogisimFX.data.*;
import LogisimFX.instance.*;
import LogisimFX.std.LC;
import LogisimFX.tools.key.BitWidthConfigurator;

public class Negator extends InstanceFactory {

	private static final int IN    = 0;
	private static final int OUT   = 1;

	public Negator() {

		super("Negator", LC.createStringBinding("negatorComponent"));
		setAttributes(new Attribute[] { StdAttr.FPGA_SUPPORTED, StdAttr.WIDTH },
					new Object[] { Boolean.FALSE, BitWidth.create(8) });
		setKeyConfigurator(new BitWidthConfigurator(StdAttr.WIDTH));
		setOffsetBounds(Bounds.create(-40, -20, 40, 40));
		setIcon("negator.gif");

		Port[] ps = new Port[2];
		ps[IN]  = new Port(-40,  0, Port.INPUT,  StdAttr.WIDTH);
		ps[OUT] = new Port(  0,  0, Port.OUTPUT, StdAttr.WIDTH);
		ps[IN].setToolTip(LC.createStringBinding("negatorInputTip"));
		ps[OUT].setToolTip(LC.createStringBinding("negatorOutputTip"));
		setPorts(ps);

	}

	@Override
	public void propagate(InstanceState state) {

		// get attributes
		BitWidth dataWidth = state.getAttributeValue(StdAttr.WIDTH);

		// compute outputs
		Value in = state.getPort(IN);
		Value out;
		if (in.isFullyDefined()) {
			out = Value.createKnown(in.getBitWidth(), -in.toIntValue());
		} else {
			Value[] bits = in.getAll();
			Value fill = Value.FALSE;
			int pos = 0;
			while (pos < bits.length) {
				if (bits[pos] == Value.FALSE) {
					bits[pos] = fill;
				} else if (bits[pos] == Value.TRUE) {
					if (fill != Value.FALSE) bits[pos] = fill;
					pos++;
					break;
				} else if (bits[pos] == Value.ERROR) {
					fill = Value.ERROR;
				} else {
					if (fill == Value.FALSE) fill = bits[pos];
					else bits[pos] = fill;
				}
				pos++;
			}
			while (pos < bits.length) {
				if (bits[pos] == Value.TRUE) {
					bits[pos] = Value.FALSE;
				} else if (bits[pos] == Value.FALSE) {
					bits[pos] = Value.TRUE;
				}
				pos++;
			}
			out = Value.create(bits);
		}

		// propagate them
		int delay = (dataWidth.getWidth() + 2) * Adder.PER_DELAY;
		state.setPort(OUT, out, delay);

	}

	@Override
	public void paintInstance(InstancePainter painter) {

		painter.drawBounds();
		painter.drawPort(IN);
		painter.drawPort(OUT, "-x", Direction.WEST);

	}

}
