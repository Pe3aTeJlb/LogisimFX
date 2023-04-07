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
import LogisimFX.util.GraphicsUtil;
import LogisimFX.util.StringUtil;
import javafx.scene.paint.Color;

public class Counter extends InstanceFactory {

	static final AttributeOption ON_GOAL_WRAP = new AttributeOption("wrap",
			"wrap", LC.createStringBinding("counterGoalWrap"));
	static final AttributeOption ON_GOAL_STAY = new AttributeOption("stay",
			"stay", LC.createStringBinding("counterGoalStay"));
	static final AttributeOption ON_GOAL_CONT = new AttributeOption("continue",
			"continue", LC.createStringBinding("counterGoalContinue"));
	static final AttributeOption ON_GOAL_LOAD = new AttributeOption("load",
			"load", LC.createStringBinding("counterGoalLoad"));

	static final Attribute<Integer> ATTR_MAX = Attributes.forHexInteger("max",
			LC.createStringBinding("counterMaxAttr"));
	static final Attribute<AttributeOption> ATTR_ON_GOAL = Attributes.forOption("ongoal",
			LC.createStringBinding("counterGoalAttr"),
			new AttributeOption[] { ON_GOAL_WRAP, ON_GOAL_STAY, ON_GOAL_CONT,
				ON_GOAL_LOAD });

	static final int DELAY = 8;
	public static final int OUT = 0;
	public static final int IN = 1;
	public static final int CK = 2;
	public static final int CLR = 3;
	public static final int LD = 4;
	public static final int UD = 5;
	public static final int EN = 6;

	static final int CARRY = 7;

	public Counter() {

		super("Counter", LC.createStringBinding("counterComponent"), new CounterHdlGeneratorFactory());
		//setOffsetBounds(Bounds.create(-30, -30, 30, 60));
		setOffsetBounds(Bounds.create(-30, -20, 30, 40));
		setIcon("counter.gif");
		setInstancePoker(RegisterPoker.class);
		setInstanceLogger(Logger.class);
		setKeyConfigurator(new BitWidthConfigurator(StdAttr.WIDTH, 1, Value.MAX_WIDTH_EXTENDED, null));
		
		Port[] ps = new Port[8];
		/* new version. dab back compability
		ps[OUT] = new Port(  0,   10, Port.OUTPUT, StdAttr.WIDTH);
		ps[IN]  = new Port(-30,   10, Port.INPUT, StdAttr.WIDTH);
		ps[CK]  = new Port(-20,  30, Port.INPUT, 1);
		ps[CLR] = new Port(-10,  30, Port.INPUT, 1);
		ps[LD]  = new Port(-30, 0, Port.INPUT, 1);
		ps[CT]  = new Port(-30,  20, Port.INPUT, 1);
		ps[CARRY] = new Port(0,  20, Port.OUTPUT, 1);
		 */
		ps[OUT] = new Port(0, 0, Port.OUTPUT, StdAttr.WIDTH);
		ps[IN] = new Port(-30, 0, Port.INPUT, StdAttr.WIDTH);
		ps[CK] = new Port(-20, 20, Port.INPUT, 1);
		ps[CLR] = new Port(-10, 20, Port.INPUT, 1);
		ps[LD] = new Port(-30, -10, Port.INPUT, 1);
		ps[UD] = new Port(-20, -20, Port.INPUT, 1);
		ps[EN] = new Port(-30, 10, Port.INPUT, 1);
		ps[CARRY] = new Port(0, 10, Port.OUTPUT, 1);

		ps[OUT].setToolTip(LC.createStringBinding("counterQTip"));
		ps[IN].setToolTip(LC.createStringBinding("counterDataTip"));
		ps[CK].setToolTip(LC.createStringBinding("counterClockTip"));
		ps[CLR].setToolTip(LC.createStringBinding("counterResetTip"));
		ps[LD].setToolTip(LC.createStringBinding("counterLoadTip"));
		ps[UD].setToolTip(LC.createStringBinding("counterUpDownTip"));
		ps[EN].setToolTip(LC.createStringBinding("counterEnableTip"));
		ps[CARRY].setToolTip(LC.createStringBinding("counterCarryTip"));

		setPorts(ps);

	}
	
	@Override
	public AttributeSet createAttributeSet() {
		return new CounterAttributes();
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
		Object triggerType = state.getAttributeValue(StdAttr.EDGE_TRIGGER);
		int max = state.getAttributeValue(ATTR_MAX).intValue();
		Value clock = state.getPortValue(CK);
		boolean triggered = data.updateClock(clock, triggerType);

		Value newValue;
		boolean carry;
		if (state.getPortValue(CLR) == Value.TRUE) {
			newValue = Value.createKnown(dataWidth, 0);
			carry = false;
		} else {
			boolean ld = state.getPortValue(LD) == Value.TRUE;
			boolean ct = state.getPortValue(EN) != Value.FALSE;
			int oldVal = data.value;
			int newVal;
			if (!triggered) {
				newVal = oldVal;
			} else if (ct) { // trigger, enable = 1: should increment or decrement
				int goal = ld ? 0 : max;
				if (oldVal == goal) {
					Object onGoal = state.getAttributeValue(ATTR_ON_GOAL);
					if (onGoal == ON_GOAL_WRAP) {
						newVal = ld ? max : 0;
					} else if (onGoal == ON_GOAL_STAY) {
						newVal = oldVal;
					} else if (onGoal == ON_GOAL_LOAD) {
						Value in = state.getPortValue(IN);
						newVal = in.isFullyDefined() ? in.toIntValue() : 0;
						if (newVal > max) newVal &= max;
					} else if (onGoal == ON_GOAL_CONT) {
						newVal = ld ? oldVal - 1 : oldVal + 1;
					} else {
						System.err.println("Invalid goal attribute " + onGoal); //OK
						newVal = ld ? max : 0;
					}
				} else {
					newVal = ld ? oldVal - 1 : oldVal + 1;
				}
			} else if (ld) { // trigger, enable = 0, load = 1: should load
				Value in = state.getPortValue(IN);
				newVal = in.isFullyDefined() ? in.toIntValue() : 0;
				if (newVal > max) newVal &= max;
			} else { // trigger, enable = 0, load = 0: no change
				newVal = oldVal;
			}
			newValue = Value.createKnown(dataWidth, newVal);
			newVal = newValue.toIntValue();
			carry = newVal == (ld && ct ? 0 : max);
			/* I would want this if I were worried about the carry signal
			 * outrunning the clock. But the component's delay should be
			 * enough to take care of it.
			if (carry) {
				if (triggerType == StdAttr.TRIG_FALLING) {
					carry = clock == Value.TRUE;
				} else {
					carry = clock == Value.FALSE;
				}
			}
			*/
		}
		
		data.value = newValue.toIntValue();
		state.setPort(OUT, newValue, DELAY);
		state.setPort(CARRY, carry ? Value.TRUE : Value.FALSE, DELAY);

	}

	@Override
	public void paintInstance(InstancePainter painter) {

		Graphics g = painter.getGraphics();
		Bounds bds = painter.getBounds();
		// draw boundary, label
		painter.drawBounds();
		painter.drawLabel();

		RegisterData state = (RegisterData) painter.getData();
		BitWidth widthVal = painter.getAttributeValue(StdAttr.WIDTH);
		int width = widthVal == null ? 8 : widthVal.getWidth();

		// determine text to draw in label
		String[] data = new String[]{"","","",""};
		if (painter.getShowState()) {
			int val = state == null ? 0 : state.value;
			String str = StringUtil.toHexString(width, val);
			if (str.length() <= 4) {
				data[0] = str;
			} else {

				int cnt = 0;
				int i = 0;
				int q = 0;
				while(cnt < str.length()){

					if(q == 4){
						q = 0;
						i++;
					}

					data[i] += str.charAt(cnt);

					q++;
					cnt++;

				}

			}
		} else {
			data[0] = LC.get("counterLabel");
			data[1] = LC.getFormatted("registerWidthLabel", "" + widthVal.getWidth());
		}

		// draw input and output ports
		if (data[1] == null) {
			painter.drawPort(IN,  "D", Direction.EAST);
			painter.drawPort(OUT, "Q", Direction.WEST);
		} else {
			painter.drawPort(IN);
			painter.drawPort(OUT);
		}
		g.setColor(Color.GRAY);
		painter.drawPort(LD);
		painter.drawPort(CARRY);
		painter.drawPort(CLR, "0", Direction.SOUTH);
		painter.drawPort(EN, LC.get("counterEnableLabel"), Direction.EAST);
		g.setColor(Color.BLACK);
		painter.drawClock(CK, Direction.NORTH);

		int cnt = 0;
		for (String datum : data) {

			if(datum != null)
			GraphicsUtil.drawText(g, datum, bds.getX() + 15, bds.getY() + 3+cnt*10,
					GraphicsUtil.H_CENTER, GraphicsUtil.V_TOP);

			cnt++;
		}

		g.toDefault();

	}

	public static class Logger extends RegisterLogger{

		@Override
		public Object[] getLogOptions(InstanceState state) {

			int stages = state.getAttributeValue(StdAttr.WIDTH).getWidth();
			Object[] ret = new Object[stages];
			stages -= 1;
			for (int i = 0; i < ret.length; i++) {
				ret[i] = Integer.valueOf(stages);
				stages--;
			}

			return ret;

		}

		@Override
		public String getLogName(InstanceState state, Object option) {

			String inName = state.getAttributeValue(StdAttr.LABEL);
			if (inName == null || inName.equals("")) {
				inName = LC.get("counterComponent")
						+ state.getInstance().getLocation();
			}
			if (option instanceof Integer) {
				return inName + "[" + option + "]";
			} else {
				return inName;
			}

		}

		@Override
		public Value getLogValue(InstanceState state, Object option) {

			if(option instanceof Integer){
				BitWidth dataWidth = state.getAttributeValue(StdAttr.WIDTH);
				if (dataWidth == null) dataWidth = BitWidth.create(0);
				RegisterData data = (RegisterData) state.getData();
				if (data == null) {
					return Value.createKnown(dataWidth, 0);
				} else {
					int index = option == null ? 0 : ((Integer) option).intValue();
					return data.getByIndex(index, dataWidth.getWidth());
				}
			}else {
				BitWidth dataWidth = state.getAttributeValue(StdAttr.WIDTH);
				if (dataWidth == null) dataWidth = BitWidth.create(0);
				RegisterData data = (RegisterData) state.getData();
				if (data == null) return Value.createKnown(dataWidth, 0);
				return Value.createKnown(dataWidth, data.value);
			}

		}

	}


	@Override
	public String getHDLName(AttributeSet attrs) {
		return "LogisimFXCounter";
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