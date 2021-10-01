/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package LogisimFX.std.wiring;

import LogisimFX.IconsManager;
import LogisimFX.comp.Component;
import LogisimFX.data.*;
import LogisimFX.instance.*;
import LogisimFX.newgui.MainFrame.Canvas.layoutCanvas.LayoutCanvas;
import LogisimFX.newgui.MainFrame.Canvas.Graphics;
import LogisimFX.std.LC;
import LogisimFX.circuit.CircuitState;
import LogisimFX.circuit.RadixOption;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class Clock extends InstanceFactory {

	public static final Attribute<Integer> ATTR_HIGH
		= new DurationAttribute("highDuration", LC.createStringBinding("clockHighAttr"),
				1, Integer.MAX_VALUE);

	public static final Attribute<Integer> ATTR_LOW
		= new DurationAttribute("lowDuration", LC.createStringBinding("clockLowAttr"),
				1, Integer.MAX_VALUE);

	public static final Clock FACTORY = new Clock();

	private static final ImageView icon = IconsManager.getIcon("clock.gif");

	private static class ClockState implements InstanceData, Cloneable {

		Value sending = Value.FALSE;
		int clicks = 0;

		@Override
		public ClockState clone() {
			try { return (ClockState) super.clone(); }
			catch (CloneNotSupportedException e) { return null; }
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

		super("Clock", LC.createStringBinding("clockComponent"));
		setAttributes(new Attribute[] {
					StdAttr.FACING, ATTR_HIGH, ATTR_LOW,
					StdAttr.LABEL, Pin.ATTR_LABEL_LOC, StdAttr.LABEL_FONT
				}, new Object[] {
					Direction.EAST, Integer.valueOf(1), Integer.valueOf(1),
					"", Direction.WEST, StdAttr.DEFAULT_LABEL_FONT
				});
		setFacingAttribute(StdAttr.FACING);
		setInstanceLogger(ClockLogger.class);
		setInstancePoker(ClockPoker.class);

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

		Value val = state.getPort(0);
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
			state = new ClockState();
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
			ret = new ClockState();
			state.setData(ret);
		}
		return ret;

	}

}
