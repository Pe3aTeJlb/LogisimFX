/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.memory;

import LogisimFX.data.*;
import LogisimFX.fpga.designrulecheck.netlistComponent;
import LogisimFX.instance.*;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.std.LC;
import LogisimFX.tools.key.BitWidthConfigurator;
import LogisimFX.tools.key.IntegerConfigurator;
import LogisimFX.tools.key.JoinedConfigurator;
import LogisimFX.util.GraphicsUtil;

public class ShiftRegister extends InstanceFactory {

	static final Attribute<Integer> ATTR_LENGTH = Attributes.forIntegerRange("length",
			LC.createStringBinding("shiftRegLengthAttr"), 1, 32);
	static final Attribute<Boolean> ATTR_LOAD = Attributes.forBoolean("parallel",
			LC.createStringBinding("shiftRegParallelAttr"));

	static final int IN  = 0;
	static final int SH  = 1;
	static final int CK  = 2;
	static final int CLR = 3;
	static final int OUT = 4;
	static final int LD  = 6;
	static final int DIR  = 5;

	public ShiftRegister() {

		super("Shift Register", LC.createStringBinding("shiftRegisterComponent"), new ShiftRegisterHdlGeneratorFactory());
		setAttributes(new Attribute[] {
				StdAttr.FPGA_SUPPORTED,
				StdAttr.WIDTH, ATTR_LENGTH, ATTR_LOAD, StdAttr.EDGE_TRIGGER,
				StdAttr.LABEL, StdAttr.LABEL_FONT
			}, new Object[] {
				Boolean.FALSE,
				BitWidth.ONE, Integer.valueOf(8), Boolean.TRUE,
				StdAttr.TRIG_RISING, "", StdAttr.DEFAULT_LABEL_FONT
			});
		setKeyConfigurator(JoinedConfigurator.create(
				new IntegerConfigurator(ATTR_LENGTH, 1, 32, null),
				new BitWidthConfigurator(StdAttr.WIDTH)));

		setIcon("shiftreg.gif");
		setInstanceLogger(ShiftRegisterLogger.class);
		setInstancePoker(ShiftRegisterPoker.class);

	}
	
	@Override
	public Bounds getOffsetBounds(AttributeSet attrs) {

		Object parallel = attrs.getValue(ATTR_LOAD);
		if (parallel == null || ((Boolean) parallel).booleanValue()) {
			int len = attrs.getValue(ATTR_LENGTH).intValue();
			return Bounds.create(0, -20, 20 + 10 * len, 50);
		} else {
			return Bounds.create(0, -20, 30, 50);
		}

	}
	
	@Override
	protected void configureNewInstance(Instance instance) {

		configurePorts(instance);
		instance.addAttributeListener();

	}
	
	@Override
	protected void instanceAttributeChanged(Instance instance, Attribute<?> attr) {

		if (attr == ATTR_LOAD || attr == ATTR_LENGTH || attr == StdAttr.WIDTH) {
			instance.recomputeBounds();
			configurePorts(instance);
		}

	}
	
	private void configurePorts(Instance instance) {

		BitWidth widthObj = instance.getAttributeValue(StdAttr.WIDTH);
		int width = widthObj.getWidth();
		Boolean parallelObj = instance.getAttributeValue(ATTR_LOAD);
		Bounds bds = instance.getBounds();
		Port[] ps;
		if (parallelObj == null || parallelObj.booleanValue()) {
			Integer lenObj = instance.getAttributeValue(ATTR_LENGTH);
			int len = lenObj == null ? 8 : lenObj.intValue();
			ps = new Port[7 + 2 * len];
			ps[LD] = new Port(10, -20, Port.INPUT, 1);
			ps[LD].setToolTip(LC.createStringBinding("shiftRegLoadTip"));
			for (int i = 0; i < len; i++) {
				ps[7 + 2 * i]     = new Port(20 + 10 * i, -20, Port.INPUT, width);
				ps[7 + 2 * i + 1] = new Port(20 + 10 * i,  30, Port.OUTPUT, width);
			}
		} else {
			ps = new Port[6];
		}
		ps[OUT] = new Port(bds.getWidth(), 0, Port.OUTPUT, width);
		ps[SH]  = new Port( 0, -10, Port.INPUT, 1);
		ps[IN]  = new Port( 0,   0, Port.INPUT, width);
		ps[CK]  = new Port( 0,  10, Port.INPUT, 1);
		ps[CLR] = new Port(10,  30, Port.INPUT, 1);
		ps[DIR] = new Port(0,  20, Port.INPUT, 1);
		ps[OUT].setToolTip(LC.createStringBinding("shiftRegOutTip"));
		ps[SH].setToolTip(LC.createStringBinding("shiftRegShiftTip"));
		ps[IN].setToolTip(LC.createStringBinding("shiftRegInTip"));
		ps[CK].setToolTip(LC.createStringBinding("shiftRegClockTip"));
		ps[CLR].setToolTip(LC.createStringBinding("shiftRegClearTip"));
		ps[DIR].setToolTip(LC.createStringBinding("shiftRegDirTip"));
		instance.setPorts(ps);

		instance.setTextField(StdAttr.LABEL, StdAttr.LABEL_FONT,
				bds.getX() + bds.getWidth() / 2,
				bds.getY() + bds.getHeight() / 4,
				GraphicsUtil.H_CENTER, GraphicsUtil.V_CENTER);

	}
	
	private ShiftRegisterData getData(InstanceState state) {

		BitWidth width = state.getAttributeValue(StdAttr.WIDTH);
		Integer lenObj = state.getAttributeValue(ATTR_LENGTH);

		int length = lenObj == null ? 8 : lenObj.intValue();
		ShiftRegisterData data = (ShiftRegisterData) state.getData();
		if (data == null) {
			data = new ShiftRegisterData(width, length, state.getPortValue(DIR));
			state.setData(data);
		} else {
			data.setDimensions(width, length, state.getPortValue(DIR));
		}
		return data;

	}

	@Override
	public void propagate(InstanceState state) {

		Object triggerType = state.getAttributeValue(StdAttr.EDGE_TRIGGER);
		boolean parallel = state.getAttributeValue(ATTR_LOAD).booleanValue();
		ShiftRegisterData data = getData(state);
		int len = data.getLength();

		boolean triggered = data.updateClock(state.getPortValue(CK), triggerType);
		if (state.getPortValue(CLR) == Value.TRUE) {
			data.clear();
		} else if (triggered) {
			if (parallel && state.getPortValue(LD) == Value.TRUE) {
				data.clear();
				for (int i = len - 1; i >= 0; i--) {
					data.push(state.getPortValue(7 + 2 * i));
				}
			} else if (state.getPortValue(SH) != Value.FALSE) {
				data.push(state.getPortValue(IN));
			}
		}

		if(state.getPortValue(DIR) == Value.TRUE) {
			state.setPort(OUT, data.get(0), 4);
		}else{
			state.setPort(OUT, data.get(len-1), 4);
		}

		if (parallel) {
			for (int i = 0; i < len; i++) {
				state.setPort(7 + 2 * i + 1, data.get(i), 4);
			}
		}

	}

	@Override
	public void paintInstance(InstancePainter painter) {

		// draw boundary, label
		painter.drawBounds();
		painter.drawLabel();

		// draw state
		boolean parallel = painter.getAttributeValue(ATTR_LOAD).booleanValue();
		if (parallel) {
			BitWidth widObj = painter.getAttributeValue(StdAttr.WIDTH);
			int wid = widObj.getWidth();
			Integer lenObj = painter.getAttributeValue(ATTR_LENGTH);
			int len = lenObj == null ? 8 : lenObj.intValue();
			if (painter.getShowState()) {
				if (wid <= 4) {
					ShiftRegisterData data = getData(painter);
					Bounds bds = painter.getBounds();
					int x = bds.getX() + 20;
					int y = bds.getY();
					Object label = painter.getAttributeValue(StdAttr.LABEL);
					if (label == null || label.equals("")) {
						y += bds.getHeight() / 2;
					} else {
						y += 3 * bds.getHeight() / 4;
					}
					Graphics g = painter.getGraphics();
					for (int i = 0; i < len; i++) {
						String s = data.get(i).toHexString();
						GraphicsUtil.drawCenteredText(g, s, x, y);
						x += 10;
					}
				}
			} else {
				Bounds bds = painter.getBounds();
				int x = bds.getX() + bds.getWidth() / 2;
				int y = bds.getY();
				int h = bds.getHeight();
				Graphics g = painter.getGraphics();
				Object label = painter.getAttributeValue(StdAttr.LABEL);
				if (label == null || label.equals("")) {
					String a = LC.get("shiftRegisterLabel1");
					GraphicsUtil.drawCenteredText(g, a, x, y + h / 4);
				}
				String b = LC.getFormatted("shiftRegisterLabel2", "" + len,
						"" + wid);
				GraphicsUtil.drawCenteredText(g, b, x, y + 3 * h / 4);
			}
		}

		// draw input and output ports
		int ports = painter.getInstance().getPorts().size();
		for (int i = 0; i < ports; i++) {
			if (i != CK) painter.drawPort(i);
		}
		painter.drawClock(CK, Direction.EAST);
		painter.getGraphics().toDefault();

	}


	@Override
	public boolean checkForGatedClocks(netlistComponent comp) {
		return true;
	}
	@Override
	public int[] clockPinIndex(netlistComponent comp) {
		return new int[] {CK};
	}

}