/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.memory;

import LogisimFX.data.*;
import LogisimFX.instance.*;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.std.LC;
import LogisimFX.tools.key.BitWidthConfigurator;
import LogisimFX.util.GraphicsUtil;
import LogisimFX.util.StringUtil;

import javafx.scene.paint.Color;

public class Register extends InstanceFactory {

	private static final int DELAY = 8;
	private static final int OUT = 0;
	private static final int IN  = 1;
	private static final int CK  = 2;
	private static final int CLR = 3;
	private static final int EN  = 4;

	public Register() {

		super("Register", LC.createStringBinding("registerComponent"));
		setAttributes(new Attribute[] {
				StdAttr.WIDTH, StdAttr.TRIGGER,
				StdAttr.LABEL, StdAttr.LABEL_FONT
			}, new Object[] {
				BitWidth.create(8), StdAttr.TRIG_RISING,
				"", StdAttr.DEFAULT_LABEL_FONT
			});
		setKeyConfigurator(new BitWidthConfigurator(StdAttr.WIDTH));
		setOffsetBounds(Bounds.create(-30, -20, 30, 40));
		setIcon("register.gif");
		setInstancePoker(RegisterPoker.class);
		setInstanceLogger(RegisterLogger.class);
		
		Port[] ps = new Port[5];
		ps[OUT] = new Port(  0,  0, Port.OUTPUT, StdAttr.WIDTH);
		ps[IN]  = new Port(-30,  0, Port.INPUT, StdAttr.WIDTH);
		ps[CK]  = new Port(-20, 20, Port.INPUT, 1);
		ps[CLR] = new Port(-10, 20, Port.INPUT, 1);
		ps[EN]  = new Port(-30, 10, Port.INPUT, 1);
		ps[OUT].setToolTip(LC.createStringBinding("registerQTip"));
		ps[IN].setToolTip(LC.createStringBinding("registerDTip"));
		ps[CK].setToolTip(LC.createStringBinding("registerClkTip"));
		ps[CLR].setToolTip(LC.createStringBinding("registerClrTip"));
		ps[EN].setToolTip(LC.createStringBinding("registerEnableTip"));
		setPorts(ps);

	}
	
	@Override
	protected void configureNewInstance(Instance instance) {

		Bounds bds = instance.getBounds();
		instance.setTextField(StdAttr.LABEL, StdAttr.LABEL_FONT,
				bds.getX() + bds.getWidth() / 2, bds.getY() - 3,
				GraphicsUtil.H_CENTER, GraphicsUtil.V_BASELINE);

	}

	@Override
	public void propagate(InstanceState state) {

		RegisterData data = (RegisterData) state.getData();
		if (data == null) {
			data = new RegisterData();
			state.setData(data);
		}

		BitWidth dataWidth = state.getAttributeValue(StdAttr.WIDTH);
		Object triggerType = state.getAttributeValue(StdAttr.TRIGGER);
		boolean triggered = data.updateClock(state.getPort(CK), triggerType);

		if (state.getPort(CLR) == Value.TRUE) {
			data.value = 0;
		} else if (triggered && state.getPort(EN) != Value.FALSE) {
			Value in = state.getPort(IN);
			if (in.isFullyDefined()) data.value = in.toIntValue();
		} 

		state.setPort(OUT, Value.createKnown(dataWidth, data.value), DELAY);

	}

	@Override
	public void paintInstance(InstancePainter painter) {

		Graphics g = painter.getGraphics();
		Bounds bds = painter.getBounds();
		RegisterData state = (RegisterData) painter.getData();
		BitWidth widthVal = painter.getAttributeValue(StdAttr.WIDTH);
		int width = widthVal == null ? 8 : widthVal.getWidth();

		// determine text to draw in label
		String a;
		String b = null;
		if (painter.getShowState()) {
			int val = state == null ? 0 : state.value;
			String str = StringUtil.toHexString(width, val);
			if (str.length() <= 4) {
				a = str;
			} else {
				int split = str.length() - 4;
				a = str.substring(0, split);
				b = str.substring(split);
			}
		} else {
			a = LC.get("registerLabel");
			b = LC.getFormatted("registerWidthLabel", "" + widthVal.getWidth());
		}

		// draw boundary, label
		painter.drawBounds();
		painter.drawLabel();

		// draw input and output ports
		if (b == null) {
			painter.drawPort(IN,  "D", Direction.EAST);
			painter.drawPort(OUT, "Q", Direction.WEST);
		} else {
			painter.drawPort(IN);
			painter.drawPort(OUT);
		}
		g.setColor(Color.GRAY);
		painter.drawPort(CLR, "0", Direction.SOUTH);
		painter.drawPort(EN, LC.get("memEnableLabel"), Direction.EAST);
		g.setColor(Color.BLACK);
		painter.drawClock(CK, Direction.NORTH);

		// draw contents
		if (b == null) {
			GraphicsUtil.drawText(g, a, bds.getX() + 15, bds.getY() + 4,
					GraphicsUtil.H_CENTER, GraphicsUtil.V_TOP);
		} else {
			GraphicsUtil.drawText(g, a, bds.getX() + 15, bds.getY() + 3,
					GraphicsUtil.H_CENTER, GraphicsUtil.V_TOP);
			GraphicsUtil.drawText(g, b, bds.getX() + 15, bds.getY() + 15,
					GraphicsUtil.H_CENTER, GraphicsUtil.V_TOP);
		}

		g.toDefault();

	}

}