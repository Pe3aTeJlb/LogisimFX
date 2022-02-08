/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.memory;

import LogisimFX.data.*;
import LogisimFX.instance.*;
import LogisimFX.circuit.CircuitState;
import LogisimFX.newgui.FrameManager;
import LogisimFX.std.LC;
import LogisimFX.proj.Project;
import javafx.scene.paint.Color;

public class Ram extends Mem {

	static final AttributeOption BUS_COMBINED
		= new AttributeOption("combined", LC.createStringBinding("ramBusSynchCombined"));
	static final AttributeOption BUS_ASYNCH
		= new AttributeOption("asynch", LC.createStringBinding("ramBusAsynchCombined"));
	static final AttributeOption BUS_SEPARATE
		= new AttributeOption("separate", LC.createStringBinding("ramBusSeparate"));

	static final Attribute<AttributeOption> ATTR_BUS = Attributes.forOption("bus",
			LC.createStringBinding("ramBusAttr"),
			new AttributeOption[] { BUS_COMBINED, BUS_ASYNCH, BUS_SEPARATE });

	private static Attribute<?>[] ATTRIBUTES = {
		Mem.ADDR_ATTR, Mem.DATA_ATTR, ATTR_BUS
	};
	private static Object[] DEFAULTS = {
		BitWidth.create(8), BitWidth.create(8), BUS_COMBINED
	};
	
	private static final int OE  = MEM_INPUTS + 0;
	private static final int CLR = MEM_INPUTS + 1;
	private static final int CLK = MEM_INPUTS + 2;
	private static final int WE  = MEM_INPUTS + 3;
	private static final int DIN = MEM_INPUTS + 4;

	private static Object[][] logOptions = new Object[9][];

	public Ram() {

		super("RAM", LC.createStringBinding("ramComponent"), 3);
		setIcon("ram.gif");
		setInstanceLogger(Logger.class);

	}
	
	@Override
	protected void configureNewInstance(Instance instance) {

		super.configureNewInstance(instance);
		instance.addAttributeListener();

	}
	
	@Override
	protected void instanceAttributeChanged(Instance instance, Attribute<?> attr) {

		super.instanceAttributeChanged(instance, attr);
		configurePorts(instance);

	}
	
	@Override
	void configurePorts(Instance instance) {

		Object bus = instance.getAttributeValue(ATTR_BUS);
		if (bus == null) bus = BUS_COMBINED;
		boolean asynch = bus == null ? false : bus.equals(BUS_ASYNCH);
		boolean separate = bus == null ? false : bus.equals(BUS_SEPARATE);

		int portCount = MEM_INPUTS;
		if (asynch) portCount += 2;
		else if (separate) portCount += 5;
		else portCount += 3;
		Port[] ps = new Port[portCount];

		configureStandardPorts(instance, ps);
		ps[OE]  = new Port(-50, 40, Port.INPUT, 1);
		ps[OE].setToolTip(LC.createStringBinding("ramOETip"));
		ps[CLR] = new Port(-30, 40, Port.INPUT, 1);
		ps[CLR].setToolTip(LC.createStringBinding("ramClrTip"));
		if (!asynch) {
			ps[CLK] = new Port(-70, 40, Port.INPUT, 1);
			ps[CLK].setToolTip(LC.createStringBinding("ramClkTip"));
		}
		if (separate) {
			ps[WE] = new Port(-110, 40, Port.INPUT, 1);
			ps[WE].setToolTip(LC.createStringBinding("ramWETip"));
			ps[DIN] = new Port(-140, 20, Port.INPUT, DATA_ATTR);
			ps[DIN].setToolTip(LC.createStringBinding("ramInTip"));
		} else {
			ps[DATA].setToolTip(LC.createStringBinding("ramBusTip"));
		}
		instance.setPorts(ps);

	}

	@Override
	public AttributeSet createAttributeSet() {
		return AttributeSets.fixedSet(ATTRIBUTES, DEFAULTS);
	}

	@Override
	public MemState getState(InstanceState state) {

		BitWidth addrBits = state.getAttributeValue(ADDR_ATTR);
		BitWidth dataBits = state.getAttributeValue(DATA_ATTR);

		RamState myState = (RamState) state.getData();
		if (myState == null) {
			MemContents contents = MemContents.create(addrBits.getWidth(), dataBits.getWidth());
			Instance instance = state.getInstance();
			myState = new RamState(instance, contents, new MemListener(instance));
			state.setData(myState);
		} else {
			myState.setRam(state.getInstance());
		}
		return myState;

	}

	@Override
	public MemState getState(Instance instance, CircuitState state) {

		BitWidth addrBits = instance.getAttributeValue(ADDR_ATTR);
		BitWidth dataBits = instance.getAttributeValue(DATA_ATTR);

		RamState myState = (RamState) instance.getData(state);
		if (myState == null) {
			MemContents contents = MemContents.create(addrBits.getWidth(), dataBits.getWidth());
			myState = new RamState(instance, contents, new MemListener(instance));
			instance.setData(state, myState);
		} else {
			myState.setRam(instance);
		}
		return myState;

	}

	@Override
	public void createHexFrame(Project proj, Instance instance, CircuitState circState) {

		RamState state = (RamState) getState(instance, circState);
		state.createHexFrame(proj);

	}

	@Override
	public void propagate(InstanceState state) {

		RamState myState = (RamState) getState(state);
		BitWidth dataBits = state.getAttributeValue(DATA_ATTR);
		Object busVal = state.getAttributeValue(ATTR_BUS);
		boolean asynch = busVal == null ? false : busVal.equals(BUS_ASYNCH);
		boolean separate = busVal == null ? false : busVal.equals(BUS_SEPARATE);

		Value addrValue = state.getPort(ADDR);
		boolean chipSelect = state.getPort(CS) != Value.FALSE;
		boolean triggered = asynch || myState.setClock(state.getPort(CLK), StdAttr.TRIG_RISING);
		boolean outputEnabled = state.getPort(OE) != Value.FALSE;
		boolean shouldClear = state.getPort(CLR) == Value.TRUE;

		if (shouldClear) {
			myState.getContents().clear();
		}

		if (!chipSelect) {
			myState.setCurrent(-1);
			state.setPort(DATA, Value.createUnknown(dataBits), DELAY);
			return;
		}

		int addr = addrValue.toIntValue();
		if (!addrValue.isFullyDefined() || addr < 0)
			return;
		if (addr != myState.getCurrent()) {
			myState.setCurrent(addr);
			myState.scrollToShow(addr);
		}

		if (!shouldClear && triggered) {
			boolean shouldStore;
			if (separate) {
				shouldStore = state.getPort(WE) != Value.FALSE;
			} else {
				shouldStore = !outputEnabled;
			}
			if (shouldStore) {
				Value dataValue = state.getPort(separate ? DIN : DATA);
				myState.getContents().set(addr, dataValue.toIntValue());
			}
		}

		if (outputEnabled) {
			int val = myState.getContents().get(addr);
			state.setPort(DATA, Value.createKnown(dataBits, val), DELAY);
		} else {
			state.setPort(DATA, Value.createUnknown(dataBits), DELAY);
		}

	}

	@Override
	public void paintInstance(InstancePainter painter) {

		super.paintInstance(painter);
		Object busVal = painter.getAttributeValue(ATTR_BUS);
		boolean asynch = busVal == null ? false : busVal.equals(BUS_ASYNCH);
		boolean separate = busVal == null ? false : busVal.equals(BUS_SEPARATE);

		if (!asynch) painter.drawClock(CLK, Direction.NORTH);
		painter.drawPort(OE, LC.get("ramOELabel"), Direction.SOUTH);
		painter.drawPort(CLR, LC.get("ramClrLabel"), Direction.SOUTH);

		if (separate) {
			painter.drawPort(WE, LC.get("ramWELabel"), Direction.SOUTH);
			painter.getGraphics().setColor(Color.BLACK);
			painter.drawPort(DIN, LC.get("ramDataLabel"), Direction.EAST);
		}

		painter.getGraphics().toDefault();

	}

	private static class RamState extends MemState
			implements InstanceData, AttributeListener {

		private Instance parent;
		private MemListener listener;
		private ClockState clockState;

		RamState(Instance parent, MemContents contents, MemListener listener) {

			super(contents);
			this.parent = parent;
			this.listener = listener;
			this.clockState = new ClockState();
			if (parent != null) parent.getAttributeSet().addAttributeListener(this);
			contents.addHexModelListener(listener);

		}
		
		void setRam(Instance value) {

			if (parent == value) return;
			if (parent != null) parent.getAttributeSet().removeAttributeListener(this);
			parent = value;
			if (value != null) value.getAttributeSet().addAttributeListener(this);

		}
		
		@Override
		public RamState clone() {

			RamState ret = (RamState) super.clone();
			ret.parent = null;
			ret.clockState = this.clockState.clone();
			ret.getContents().addHexModelListener(listener);
			return ret;

		}
		
		// Retrieves a HexFrame for editing within a separate window
		public void createHexFrame(Project proj) {

			FrameManager.CreateHexEditorFrame(proj, getContents());
/*
			if (hexFrame == null) {
				hexFrame = new HexFrame(proj, getContents());
				hexFrame.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosed(WindowEvent e) {
						hexFrame = null;
					}
				});
			}
			return hexFrame;

 */

		}
		
		//
		// methods for accessing the write-enable data
		//
		public boolean setClock(Value newClock, Object trigger) {
			return clockState.updateClock(newClock, trigger);
		}

		public void attributeListChanged(AttributeEvent e) { }

		public void attributeValueChanged(AttributeEvent e) {

			AttributeSet attrs = e.getSource();
			BitWidth addrBits = attrs.getValue(Mem.ADDR_ATTR);
			BitWidth dataBits = attrs.getValue(Mem.DATA_ATTR);
			getContents().setDimensions(addrBits.getWidth(), dataBits.getWidth());

		}

	}
	
	public static class Logger extends InstanceLogger {

		@Override
		public Object[] getLogOptions(InstanceState state) {

			int addrBits = state.getAttributeValue(ADDR_ATTR).getWidth();
			if (addrBits >= logOptions.length) addrBits = logOptions.length - 1;
			synchronized(logOptions) {
				Object[] ret = logOptions[addrBits];
				if (ret == null) {
					ret = new Object[1 << addrBits];
					logOptions[addrBits] = ret;
					for (int i = 0; i < ret.length; i++) {
						ret[i] = Integer.valueOf(i);
					}
				}
				return ret;
			}

		}

		@Override
		public String getLogName(InstanceState state, Object option) {

			if (option instanceof Integer) {
				String disp = LC.get("ramComponent");
				Location loc = state.getInstance().getLocation();
				return disp + loc + "[" + option + "]";
			} else {
				return null;
			}

		}

		@Override
		public Value getLogValue(InstanceState state, Object option) {

			if (option instanceof Integer) {
				MemState s = (MemState) state.getData();
				int addr = ((Integer) option).intValue();
				return Value.createKnown(BitWidth.create(s.getDataBits()),
						s.getContents().get(addr));
			} else {
				BitWidth dataWidth = BitWidth.create(state.getAttributeValue(DATA_ATTR).getWidth()-
						state.getAttributeValue(ADDR_ATTR).getWidth());
				if (dataWidth == null) dataWidth = BitWidth.create(0);
				MemState data = (MemState) state.getData();
				if (data == null) return Value.createKnown(dataWidth, 0);
				return Value.createKnown(dataWidth, data.getContents().get(data.getCurrent()));
			}

		}

	}

}
