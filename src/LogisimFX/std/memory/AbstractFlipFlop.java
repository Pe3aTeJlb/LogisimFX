/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.memory;

import LogisimFX.data.*;
import LogisimFX.instance.*;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.LayoutCanvas;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.std.LC;
import LogisimFX.util.GraphicsUtil;

import javafx.beans.binding.StringBinding;
import javafx.scene.paint.Color;

public abstract class AbstractFlipFlop extends InstanceFactory {

	private static final int STD_PORTS = 6;
	
	private Attribute<AttributeOption> triggerAttribute;
	
	protected AbstractFlipFlop(String name, String iconName, StringBinding desc,
			int numInputs, boolean allowLevelTriggers) {

		super(name, desc);
		setIcon(iconName);
		triggerAttribute = allowLevelTriggers ? StdAttr.TRIGGER : StdAttr.EDGE_TRIGGER;
		setAttributes(new Attribute[] {
				StdAttr.FPGA_SUPPORTED,
				triggerAttribute, StdAttr.LABEL, StdAttr.LABEL_FONT
			}, new Object[] {
				Boolean.FALSE,
				StdAttr.TRIG_RISING, "", StdAttr.DEFAULT_LABEL_FONT
			});
		setOffsetBounds(Bounds.create(-40, -10, 40, 40));
		setInstancePoker(Poker.class);
		setInstanceLogger(Logger.class);
		
		Port[] ps = new Port[numInputs + STD_PORTS];
		if (numInputs == 1) {
			ps[0] = new Port(-40, 20, Port.INPUT, 1);
			ps[1] = new Port(-40,  0, Port.INPUT, 1);
		} else if (numInputs == 2) {
			ps[0] = new Port(-40,  0, Port.INPUT, 1);
			ps[1] = new Port(-40, 20, Port.INPUT, 1);
			ps[2] = new Port(-40, 10, Port.INPUT, 1);
		} else {
			throw new RuntimeException("flip-flop input > 2");
		}
		ps[numInputs + 1] = new Port(  0,  0, Port.OUTPUT, 1);
		ps[numInputs + 2] = new Port(  0, 20, Port.OUTPUT, 1);
		ps[numInputs + 3] = new Port(-10, 30, Port.INPUT,  1);
		ps[numInputs + 4] = new Port(-30, 30, Port.INPUT,  1);
		ps[numInputs + 5] = new Port(-20, 30, Port.INPUT,  1);
		ps[numInputs].setToolTip(LC.createStringBinding("flipFlopClockTip"));
		ps[numInputs + 1].setToolTip(LC.createStringBinding("flipFlopQTip"));
		ps[numInputs + 2].setToolTip(LC.createStringBinding("flipFlopNotQTip"));
		ps[numInputs + 3].setToolTip(LC.createStringBinding("flipFlopResetTip"));
		ps[numInputs + 4].setToolTip(LC.createStringBinding("flipFlopPresetTip"));
		ps[numInputs + 5].setToolTip(LC.createStringBinding("flipFlopEnableTip"));
		setPorts(ps);

	}

	//
	// abstract methods intended to be implemented in subclasses
	//
	protected abstract String getInputName(int index);

	protected abstract Value computeValue(Value[] inputs,
                                          Value curValue);
	
	//
	// concrete methods not intended to be overridden
	//
	@Override
	protected void configureNewInstance(Instance instance) {

		Bounds bds = instance.getBounds();
		instance.setTextField(StdAttr.LABEL, StdAttr.LABEL_FONT,
				bds.getX() + bds.getWidth() / 2, bds.getY() - 3,
				GraphicsUtil.H_CENTER, GraphicsUtil.V_BASELINE);

	}

	@Override
	public void propagate(InstanceState state) {

		boolean changed = false;
		StateData data = (StateData) state.getData();
		if (data == null) {
			changed = true;
			data = new StateData();
			state.setData(data);
		}

		int n = getPorts().size() - STD_PORTS;
		Object triggerType = state.getAttributeValue(triggerAttribute);
		boolean triggered = data.updateClock(state.getPort(n), triggerType);
		
		if (state.getPort(n + 3) == Value.TRUE) { // clear requested
			changed |= data.curValue != Value.FALSE;
			data.curValue = Value.FALSE;
		} else if (state.getPort(n + 4) == Value.TRUE) { // preset requested
			changed |= data.curValue != Value.TRUE;
			data.curValue = Value.TRUE;
		} else if (triggered && state.getPort(n + 5) != Value.FALSE) {
			// Clock has triggered and flip-flop is enabled: Update the state
			Value[] inputs = new Value[n];
			for (int i = 0; i < n; i++) {
				inputs[i] = state.getPort(i);
			}

			Value newVal = computeValue(inputs, data.curValue);
			if (newVal == Value.TRUE || newVal == Value.FALSE) {
				changed |= data.curValue != newVal;
				data.curValue = newVal;
			}
		}

		state.setPort(n + 1, data.curValue, Memory.DELAY);
		state.setPort(n + 2, data.curValue.not(), Memory.DELAY);

	}

	@Override
	public void paintInstance(InstancePainter painter) {

		Graphics g = painter.getGraphics();
		painter.drawBounds();
		painter.drawLabel();
		if (painter.getShowState()) {
			Location loc = painter.getLocation();
			StateData myState = (StateData) painter.getData();
			if (myState != null) {
				int x = loc.getX();
				int y = loc.getY();
				g.setColor(myState.curValue.getColor());
				g.c.fillOval(x - 26, y + 4, 13, 13);
				g.setColor(Color.WHITE);
				GraphicsUtil.drawCenteredText(g,
					myState.curValue.toDisplayString(), x - 19, y + 7);
				g.setColor(Color.BLACK);
			}
		}
		
		int n = getPorts().size() - STD_PORTS;
		g.setColor(Color.GRAY);
		painter.drawPort(n + 3, "0", Direction.SOUTH);
		painter.drawPort(n + 4, "1", Direction.SOUTH);
		painter.drawPort(n + 5, LC.get("memEnableLabel"), Direction.SOUTH);

		g.setColor(Color.BLACK);
		for (int i = 0; i < n; i++) {
			painter.drawPort(i, getInputName(i), Direction.EAST);
		}
		painter.drawClock(n, Direction.EAST);
		painter.drawPort(n + 1, "Q", Direction.WEST);
		painter.drawPort(n + 2);

		g.toDefault();

	}

	private static class StateData extends ClockState implements InstanceData {

		Value curValue  = Value.FALSE;

	}

	public static class Logger extends InstanceLogger {

		@Override
		public String getLogName(InstanceState state, Object option) {

			String ret = state.getAttributeValue(StdAttr.LABEL);
			return ret != null && !ret.equals("") ? ret : null;

		}

		@Override
		public Value getLogValue(InstanceState state, Object option) {

			StateData s = (StateData) state.getData();
			return s == null ? Value.FALSE : s.curValue;

		}

	}

	public static class Poker extends InstancePoker {

		boolean isPressed = true;

		@Override
		public void mousePressed(InstanceState state, LayoutCanvas.CME e) {
			isPressed = isInside(state, e);
		}
		
		@Override
		public void mouseReleased(InstanceState state, LayoutCanvas.CME e) {

			if (isPressed && isInside(state, e)) {
				StateData myState = (StateData) state.getData();
				if (myState == null) return;

				myState.curValue = myState.curValue.not();
				state.fireInvalidated();
			}
			isPressed = false;

		}

		private boolean isInside(InstanceState state, LayoutCanvas.CME e) {

			Location loc = state.getInstance().getLocation();
			int dx = e.localX - (loc.getX() - 20);
			int dy = e.localY - (loc.getY() + 10);
			int d2 = dx * dx + dy * dy;
			return d2 < 8 * 8;

		}

	}

}
