/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.std.arith;

import com.cburch.LogisimFX.data.*;
import com.cburch.LogisimFX.instance.*;
import com.cburch.LogisimFX.newgui.MainFrame.Graphics;
import com.cburch.LogisimFX.std.LC;
import com.cburch.LogisimFX.tools.key.BitWidthConfigurator;
import com.cburch.LogisimFX.util.GraphicsUtil;

import javafx.scene.canvas.GraphicsContext;


public class BitFinder extends InstanceFactory {

	static final AttributeOption LOW_ONE
		= new AttributeOption("low1", LC.createStringBinding("bitFinderLowOption", "1"));
	static final AttributeOption HIGH_ONE
	= new AttributeOption("high1", LC.createStringBinding("bitFinderHighOption", "1"));
	static final AttributeOption LOW_ZERO
		= new AttributeOption("low0", LC.createStringBinding("bitFinderLowOption", "0"));
	static final AttributeOption HIGH_ZERO
		= new AttributeOption("high0", LC.createStringBinding("bitFinderHighOption", "0"));
	static final Attribute<AttributeOption> TYPE
		= Attributes.forOption("type", LC.createStringBinding("bitFinderTypeAttr"),
				new AttributeOption[] { LOW_ONE, HIGH_ONE, LOW_ZERO, HIGH_ZERO });
	
	public BitFinder() {

		super("BitFinder", LC.createStringBinding("bitFinderComponent"));
		setAttributes(new Attribute[] {
				StdAttr.WIDTH, TYPE
			}, new Object[] {
				BitWidth.create(8), LOW_ONE
			});
		setKeyConfigurator(new BitWidthConfigurator(StdAttr.WIDTH));
		setIcon("bitfindr.gif");

	}
	
	@Override
	public Bounds getOffsetBounds(AttributeSet attrs) {
		return Bounds.create(-40, -20, 40, 40);
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
		} else if (attr == TYPE) {
			instance.fireInvalidated();
		}

	}
	
	private void configurePorts(Instance instance) {

		BitWidth inWidth = instance.getAttributeValue(StdAttr.WIDTH);
		int outWidth = computeOutputBits(inWidth.getWidth() - 1);

		Port[] ps = new Port[3];
		ps[0] = new Port(-20,  20, Port.OUTPUT, BitWidth.ONE);
		ps[1] = new Port(  0,   0, Port.OUTPUT, BitWidth.create(outWidth));
		ps[2] = new Port(-40,   0, Port.INPUT,  inWidth);
		
		Object type = instance.getAttributeValue(TYPE);
		if (type == HIGH_ZERO) {
			ps[0].setToolTip(LC.createStringBinding("bitFinderPresentTip", "0"));
			ps[1].setToolTip(LC.createStringBinding("bitFinderIndexHighTip", "0"));
		} else if (type == LOW_ZERO) {
			ps[0].setToolTip(LC.createStringBinding("bitFinderPresentTip", "0"));
			ps[1].setToolTip(LC.createStringBinding("bitFinderIndexLowTip", "0"));
		} else if (type == HIGH_ONE) {
			ps[0].setToolTip(LC.createStringBinding("bitFinderPresentTip", "1"));
			ps[1].setToolTip(LC.createStringBinding("bitFinderIndexHighTip", "1"));
		} else {
			ps[0].setToolTip(LC.createStringBinding("bitFinderPresentTip", "1"));
			ps[1].setToolTip(LC.createStringBinding("bitFinderIndexLowTip", "1"));
		}
		ps[2].setToolTip(LC.createStringBinding("bitFinderInputTip"));
		instance.setPorts(ps);

	}
	
	private int computeOutputBits(int maxBits) {

		int outWidth = 1;
		while ((1 << outWidth) <= maxBits) outWidth++;
		return outWidth;

	}

	@Override
	public void propagate(InstanceState state) {

		int width = state.getAttributeValue(StdAttr.WIDTH).getWidth();
		int outWidth = computeOutputBits(width - 1);
		Object type = state.getAttributeValue(TYPE);

		Value[] bits = state.getPort(2).getAll();
		Value want;
		int i;
		if (type == HIGH_ZERO) {
			want = Value.FALSE;
			for (i = bits.length - 1; i >= 0 && bits[i] == Value.TRUE; i--) { }
		} else if (type == LOW_ZERO) {
			want = Value.FALSE;
			for (i = 0; i < bits.length && bits[i] == Value.TRUE; i++) { }
		} else if (type == HIGH_ONE) {
			want = Value.TRUE;
			for (i = bits.length - 1; i >= 0 && bits[i] == Value.FALSE; i--) { }
		} else {
			want = Value.TRUE;
			for (i = 0; i < bits.length && bits[i] == Value.FALSE; i++) { }
		}
		
		Value present;
		Value index;
		if (i < 0 || i >= bits.length) {
			present = Value.FALSE;
			index = Value.createKnown(BitWidth.create(outWidth), 0);
		} else if (bits[i] == want) {
			present = Value.TRUE;
			index = Value.createKnown(BitWidth.create(outWidth), i);
		} else {
			present = Value.ERROR;
			index = Value.createError(BitWidth.create(outWidth));
		}
		
		int delay = outWidth * Adder.PER_DELAY;
		state.setPort(0, present, delay);
		state.setPort(1, index, delay);

	}
	
	@Override
	public void paintInstance(InstancePainter painter) {

		Graphics g = painter.getGraphics();
		painter.drawBounds();
		painter.drawPorts();
		
		String top = LC.get("bitFinderFindLabel");
		String mid;
		String bot;
		Object type = painter.getAttributeValue(TYPE);
		if (type == HIGH_ZERO) {
			mid = LC.get("bitFinderHighLabel");
			bot = "0";
		} else if (type == LOW_ZERO) {
			mid = LC.get("bitFinderLowLabel");
			bot = "0";
		} else if (type == HIGH_ONE) {
			mid = LC.get("bitFinderHighLabel");
			bot = "1";
		} else {
			mid = LC.get("bitFinderLowLabel");
			bot = "1";
		}
		
		Bounds bds = painter.getBounds();
		int x = bds.getX() + bds.getWidth() / 2;
		int y0 = bds.getY();
		GraphicsUtil.drawCenteredText(g, top, x, y0 + 8);
		GraphicsUtil.drawCenteredText(g, mid, x, y0 + 20);
		GraphicsUtil.drawCenteredText(g, bot, x, y0 + 32);

		g.toDefault();

	}

}
