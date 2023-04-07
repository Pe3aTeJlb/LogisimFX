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
import LogisimFX.tools.key.IntegerConfigurator;
import LogisimFX.tools.key.JoinedConfigurator;

public class BitAdder extends InstanceFactory {

	static final Attribute<Integer> NUM_INPUTS
		= Attributes.forIntegerRange("inputs", LC.createStringBinding("gateInputsAttr"), 1, 32);
	
	public BitAdder() {

		super("BitAdder", LC.createStringBinding("bitAdderComponent"));
		setAttributes(new Attribute[] {
				StdAttr.FPGA_SUPPORTED, StdAttr.WIDTH, NUM_INPUTS
			}, new Object[] {
				Boolean.FALSE, BitWidth.create(8), Integer.valueOf(1)
			});
		setKeyConfigurator(JoinedConfigurator.create(
				new IntegerConfigurator(NUM_INPUTS, 1, 32, null),
				new BitWidthConfigurator(StdAttr.WIDTH)));
		setIcon("bitadder.gif");

	}
	
	@Override
	public Bounds getOffsetBounds(AttributeSet attrs) {

		int inputs = attrs.getValue(NUM_INPUTS).intValue();
		int h = Math.max(40, 10 * inputs);
		int y = inputs < 4 ? 20 : (((inputs - 1) / 2) * 10 + 5);
		return Bounds.create(-40, -y, 40, h);

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
		} else if (attr == NUM_INPUTS) {
			configurePorts(instance);
			instance.recomputeBounds();
		}

	}
	
	private void configurePorts(Instance instance) {

		BitWidth inWidth = instance.getAttributeValue(StdAttr.WIDTH);
		int inputs = instance.getAttributeValue(NUM_INPUTS).intValue();
		int outWidth = computeOutputBits(inWidth.getWidth(), inputs);

		int y;
		int dy = 10;
		switch (inputs) {
		case 1: y = 0; break;
		case 2: y = -10; dy = 20; break;
		case 3: y = -10; break;
		default: y = ((inputs - 1) / 2) * -10;
		}

		Port[] ps = new Port[inputs + 1];
		ps[0] = new Port(0, 0, Port.OUTPUT, BitWidth.create(outWidth));
		ps[0].setToolTip(LC.createStringBinding("bitAdderOutputManyTip"));
		for (int i = 0; i < inputs; i++) {
			ps[i + 1] = new Port(-40, y + i * dy, Port.INPUT, inWidth);
			ps[i + 1].setToolTip(LC.createStringBinding("bitAdderInputTip"));
		}
		instance.setPorts(ps);

	}
	
	private int computeOutputBits(int width, int inputs) {

		int maxBits = width * inputs;
		int outWidth = 1;
		while ((1 << outWidth) <= maxBits) outWidth++;
		return outWidth;

	}

	@Override
	public void propagate(InstanceState state) {

		int width = state.getAttributeValue(StdAttr.WIDTH).getWidth();
		int inputs = state.getAttributeValue(NUM_INPUTS).intValue();

		// compute the number of 1 bits
		int minCount = 0; // number that are definitely 1
		int maxCount = 0; // number that are definitely not 0 (incl X/Z)
		for (int i = 1; i <= inputs; i++) {
			Value v = state.getPortValue(i);
			Value[] bits = v.getAll();
			for (int j = 0; j < bits.length; j++) {
				Value b = bits[j];
				if (b == Value.TRUE) minCount++;
				if (b != Value.FALSE) maxCount++;
			}
		}

		// compute which output bits should be error bits
		int unknownMask = 0;
		for (int i = minCount + 1; i <= maxCount; i++) {
			unknownMask |= (minCount ^ i);
		}
		
		Value[] out = new Value[computeOutputBits(width, inputs)];
		for (int i = 0; i < out.length; i++) {
			if (((unknownMask >> i) & 1) != 0) {
				out[i] = Value.ERROR;
			} else if (((minCount >> i) & 1) != 0) {
				out[i] = Value.TRUE;
			} else {
				out[i] = Value.FALSE;
			}
		}

		int delay = out.length * Adder.PER_DELAY;
		state.setPort(0, Value.create(out), delay);

	}
	
	@Override
	public void paintInstance(InstancePainter painter) {

		Graphics g = painter.getGraphics();
		painter.drawBounds();
		painter.drawPorts();

		g.setLineWidth(2);
		Location loc = painter.getLocation();
		int x = loc.getX() - 10;
		int y = loc.getY();
		g.c.strokeLine(x - 2, y - 5, x - 2, y + 5);
		g.c.strokeLine(x + 2, y - 5, x + 2, y + 5);
		g.c.strokeLine(x - 5, y - 2, x + 5, y - 2);
		g.c.strokeLine(x - 5, y + 2, x + 5, y + 2);

		g.toDefault();

	}

}
