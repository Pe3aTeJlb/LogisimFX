/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.memory;

import LogisimFX.data.Attribute;
import LogisimFX.data.AttributeSet;
import LogisimFX.data.BitWidth;
import LogisimFX.data.Value;
import LogisimFX.newgui.HexEditorFrame.HexFile;
import LogisimFX.instance.Instance;
import LogisimFX.instance.InstanceState;
import LogisimFX.instance.Port;
import LogisimFX.circuit.CircuitState;
import LogisimFX.std.LC;
import LogisimFX.proj.Project;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.WeakHashMap;

public class Rom extends Mem {

	public static Attribute<MemContents> CONTENTS_ATTR = new ContentsAttribute();
	
	// The following is so that instance's MemListeners aren't freed by the
	// garbage collector until the instance itself is ready to be freed.
	private WeakHashMap<Instance,MemListener> memListeners;
	
	public Rom() {

		super("ROM", LC.createStringBinding("romComponent"), 0);
		setIcon("rom.gif");
		memListeners = new WeakHashMap<Instance,MemListener>();

	}

	@Override
	void configurePorts(Instance instance) {

		Port[] ps = new Port[MEM_INPUTS];
		configureStandardPorts(instance, ps);
		instance.setPorts(ps);

	}

	@Override
	public AttributeSet createAttributeSet() {
		return new RomAttributes();
	}

	@Override
	public MemState getState(Instance instance, CircuitState state) {

		MemState ret = (MemState) instance.getData(state);
		if (ret == null) {
			MemContents contents = getMemContents(instance);
			ret = new MemState(contents);
			instance.setData(state, ret);
		}
		return ret;

	}

	@Override
	public MemState getState(InstanceState state) {

		MemState ret = (MemState) state.getData();
		if (ret == null) {
			MemContents contents = getMemContents(state.getInstance());
			ret = new MemState(contents);
			state.setData(ret);
		}
		return ret;

	}

	@Override
	public void createHexFrame(Project proj, Instance instance, CircuitState state) {

		RomAttributes.createHexFrame(getMemContents(instance), proj);

	}

	// TODO - maybe delete this method?
	MemContents getMemContents(Instance instance) {
		return instance.getAttributeValue(CONTENTS_ATTR);
	}

	@Override
	public void propagate(InstanceState state) {

		MemState myState = getState(state);
		BitWidth dataBits = state.getAttributeValue(DATA_ATTR);

		Value addrValue = state.getPort(ADDR);
		boolean chipSelect = state.getPort(CS) != Value.FALSE;

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

		int val = myState.getContents().get(addr);
		state.setPort(DATA, Value.createKnown(dataBits, val), DELAY);

	}

	@Override
	protected void configureNewInstance(Instance instance) {

		super.configureNewInstance(instance);
		MemContents contents = getMemContents(instance);
		MemListener listener = new MemListener(instance);
		memListeners.put(instance, listener);
		contents.addHexModelListener(listener);

	}


	private static class ContentsAttribute extends Attribute<MemContents> {

		public ContentsAttribute() {
			super("contents", LC.createStringBinding("romContentsAttr"));
		}

		@Override
		public String toDisplayString(MemContents value) {
			return LC.get("romContentsValue");
		}

		@Override
		public String toStandardString(MemContents state) {

			int addr = state.getLogLength();
			int data = state.getWidth();
			StringWriter ret = new StringWriter();
			ret.write("addr/data: " + addr + " " + data + "\n");
			try {
				HexFile.save(ret, state);
			} catch (IOException e) { }
			return ret.toString();

		}

		@Override
		public MemContents parse(String value) {

			int lineBreak = value.indexOf('\n');
			String first = lineBreak < 0 ? value : value.substring(0, lineBreak);
			String rest = lineBreak < 0 ? "" : value.substring(lineBreak + 1);
			StringTokenizer toks = new StringTokenizer(first);
			try {
				String header = toks.nextToken();
				if (!header.equals("addr/data:")) return null;
				int addr = Integer.parseInt(toks.nextToken());
				int data = Integer.parseInt(toks.nextToken());
				MemContents ret = MemContents.create(addr, data);
				HexFile.open(ret, new StringReader(rest));
				return ret;
			} catch (IOException e) {
				return null;
			} catch (NumberFormatException e) {
				return null;
			} catch (NoSuchElementException e) {
				return null;
			}
		}

	}


}
