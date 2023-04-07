/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.wiring;

import LogisimFX.IconsManager;
import LogisimFX.comp.Component;
import LogisimFX.data.*;
import LogisimFX.instance.*;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.LayoutCanvas;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.std.LC;
import LogisimFX.circuit.CircuitState;
import LogisimFX.circuit.RadixOption;

import javafx.beans.binding.StringBinding;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class Clock extends InstanceFactory {

	public static final Attribute<Integer> ATTR_HIGH
		= new DurationAttribute("highDuration", LC.createStringBinding("clockHighAttr"),
				1, Integer.MAX_VALUE);

	public static final Attribute<Integer> ATTR_LOW
		= new DurationAttribute("lowDuration", LC.createStringBinding("clockLowAttr"),
				1, Integer.MAX_VALUE);

	public static final Attribute<Integer> ATTR_PHASE =
			new DurationAttribute("phaseOffset", LC.createStringBinding("clockPhaseAttr"), 0, Integer.MAX_VALUE);

	public static final Clock FACTORY = new Clock();

	private static final ImageView icon = IconsManager.getIcon("clock.gif");

	public static class ClockState implements InstanceData, Cloneable {

		public Value sending = Value.UNKNOWN;
		public int clicks = 0;

		protected ClockState(long curTick, AttributeSet attrs) {
			updateTick(curTick, attrs);
		}

		boolean updateTick(long ticks, AttributeSet attrs) {
			int durationHigh = attrs.getValue(ATTR_HIGH);
			int durationLow = attrs.getValue(ATTR_LOW);
			int cycle = durationHigh + durationLow;
			int phase = ((attrs.getValue(ATTR_PHASE) % cycle) + cycle) % cycle;
			boolean isLow = ((ticks + phase) % cycle) < durationLow;
			Value desired = (isLow ? Value.FALSE : Value.TRUE);
			if (sending.equals(desired)) return false;
			sending = desired;
			return true;
		}

		@Override
		public ClockState clone() {
			try {
				return (ClockState) super.clone();
			} catch (CloneNotSupportedException e) {
				return null;
			}
		}

	}

	public static class ClockLogger extends InstanceLogger {

		@Override
		public String getLogName(InstanceState state, Object option) {
			return state.getAttributeValue(StdAttr.LABEL);
		}

		@Override
		public Value getLogValue(InstanceState state, Object option) {
			ClockState s = getState(state);
			return s.sending;
		}

	}

	public static class ClockPoker extends InstancePoker {

		boolean isPressed = true;

		@Override
		public void mousePressed(InstanceState state, LayoutCanvas.CME e) {
			isPressed = isInside(state, e);
		}

		@Override
		public void mouseReleased(InstanceState state, LayoutCanvas.CME e) {

			if (isPressed && isInside(state, e)) {
				ClockState myState = (ClockState) state.getData();
				myState.sending = myState.sending.not();
				myState.clicks++;
				state.fireInvalidated();
			}
			isPressed = false;

		}

		private boolean isInside(InstanceState state, LayoutCanvas.CME e) {

			Bounds bds = state.getInstance().getBounds();
			return bds.contains(e.localX, e.localY);

		}

	}

	public Clock() {

		super("Clock", LC.createStringBinding("clockComponent"), new ClockHdlGeneratorFactory());
		setAttributes(new Attribute[] {
					StdAttr.FPGA_SUPPORTED,
					StdAttr.FACING, ATTR_HIGH, ATTR_LOW, ATTR_PHASE,
					StdAttr.LABEL, Pin.ATTR_LABEL_LOC, StdAttr.LABEL_FONT
				}, new Object[] {
					Boolean.FALSE,
					Direction.EAST, 1, 1, 0,
					"", Direction.WEST, StdAttr.DEFAULT_LABEL_FONT
				});
		setFacingAttribute(StdAttr.FACING);
		setInstanceLogger(ClockLogger.class);
		setInstancePoker(ClockPoker.class);

	}

	public Clock(String str, StringBinding stringBinding) {
		super(str, stringBinding);
	}

	@Override
	public Bounds getOffsetBounds(AttributeSet attrs) {

		return Probe.getOffsetBounds(
				attrs.getValue(StdAttr.FACING),
				BitWidth.ONE, RadixOption.RADIX_2);

	}

	//
	// graphics methods
	//
	@Override
	public ImageView getIcon(){
		return icon;
	}

	@Override
	public void paintInstance(InstancePainter painter) {

		Graphics g = painter.getGraphics();
		Bounds bds = painter.getInstance().getBounds(); // intentionally with no graphics object - we don't want label included
		int x = bds.getX();
		int y = bds.getY();
		g.setLineWidth(2);
		g.setColor(Color.BLACK);
		g.c.strokeRect(x, y, bds.getWidth(), bds.getHeight());

		painter.drawLabel();

		boolean drawUp;
		if (painter.getShowState()) {
			ClockState state = getState(painter);
			g.setColor(state.sending.getColor());
			drawUp = state.sending == Value.TRUE;
		} else {
			g.setColor(Color.BLACK);
			drawUp = true;
		}
		x += 10;
		y += 10;
		double[] xs = { x - 6, x - 6, x, x, x + 6, x + 6 };
		double[] ys;
		if (drawUp) {
			ys = new double[] { y, y - 4, y - 4, y + 4, y + 4, y };
		} else {
			ys = new double[] { y, y + 4, y + 4, y - 4, y - 4, y };
		}
		g.c.strokePolyline(xs, ys, xs.length);

		painter.drawPorts();

		g.toDefault();

	}

	//
	// methods for instances
	//
	@Override
	protected void configureNewInstance(Instance instance) {

		instance.addAttributeListener();
		instance.setPorts(new Port[] { new Port(0, 0, Port.OUTPUT, BitWidth.ONE) });
		configureLabel(instance);

	}
	
	@Override
	protected void instanceAttributeChanged(Instance instance, Attribute<?> attr) {

		if (attr == Pin.ATTR_LABEL_LOC) {
			configureLabel(instance);
		} else if (attr == StdAttr.FACING) {
			instance.recomputeBounds();
			configureLabel(instance);
		}

	}

	@Override
	public void propagate(InstanceState state) {

		Value val = state.getPortValue(0);
		ClockState q = getState(state);
		if (!val.equals(q.sending)) { // ignore if no change
			state.setPort(0, q.sending, 1);
		}

	}

	//
	// package methods
	//
	public static boolean tick(CircuitState circState, int ticks, Component comp) {

		AttributeSet attrs = comp.getAttributeSet();
		int durationHigh = attrs.getValue(ATTR_HIGH).intValue();
		int durationLow = attrs.getValue(ATTR_LOW).intValue();
		ClockState state = (ClockState) circState.getData(comp);
		if (state == null) {
			state = new ClockState(ticks, attrs);
			circState.setData(comp, state);
		}
		boolean curValue = ticks % (durationHigh + durationLow) < durationLow;
		if (state.clicks % 2 == 1) curValue = !curValue;
		Value desired = (curValue ? Value.FALSE : Value.TRUE);
		if (!state.sending.equals(desired)) {
			state.sending = desired;
			Instance.getInstanceFor(comp).fireInvalidated();
			return true;
		} else {
			return false;
		}

	}

	//
	// private methods
	//
	private void configureLabel(Instance instance) {

		Direction facing = instance.getAttributeValue(StdAttr.FACING);
		Direction labelLoc = instance.getAttributeValue(Pin.ATTR_LABEL_LOC);
		Probe.configureLabel(instance, labelLoc, facing);

	}

	private static ClockState getState(InstanceState state) {

		ClockState ret = (ClockState) state.getData();
		if (ret == null) {
			ret = new ClockState(state.getTickCount(), state.getAttributeSet());
			state.setData(ret);
		}
		return ret;

	}


	@Override
	public String getHDLName(AttributeSet attrs) {
		return "LogisimFXClockComponent";
	}

}
