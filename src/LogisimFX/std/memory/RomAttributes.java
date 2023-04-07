/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * Original code by Carl Burch (http://www.cburch.com), 2011.
 * License information is located in the Launch file
 */

package LogisimFX.std.memory;

import LogisimFX.data.AbstractAttributeSet;
import LogisimFX.data.Attribute;
import LogisimFX.data.BitWidth;
import LogisimFX.instance.StdAttr;
import LogisimFX.newgui.FrameManager;
import LogisimFX.proj.Project;
import javafx.scene.text.Font;

import java.util.Arrays;
import java.util.List;
import java.util.WeakHashMap;

public class RomAttributes extends AbstractAttributeSet {

	private static List<Attribute<?>> ATTRIBUTES = Arrays.asList(
			StdAttr.FPGA_SUPPORTED,
			Mem.ADDR_ATTR,
			Mem.DATA_ATTR,
			Rom.CONTENTS_ATTR,
			StdAttr.LABEL,
			StdAttr.LABEL_FONT,
			StdAttr.LABEL_VISIBILITY
	);

	private static WeakHashMap<MemContents, RomContentsListener> listenerRegistry
			= new WeakHashMap<MemContents, RomContentsListener>();

	static void register(MemContents value, Project proj) {

		if (proj == null || listenerRegistry.containsKey(value)) return;
		RomContentsListener l = new RomContentsListener(proj);
		value.addHexModelListener(l);
		listenerRegistry.put(value, l);

	}

	static void createHexFrame(MemContents value, Project proj) {

		FrameManager.CreateHexEditorFrame(proj, value);
/*
		synchronized(windowRegistry) {
			HexFrame ret = windowRegistry.get(value);
			if (ret == null) {
				ret = new HexFrame(proj, value);
				windowRegistry.put(value, ret);
			}
			return ret;
		}

 */

	}

	private Boolean fpga = Boolean.FALSE;
	private BitWidth addrBits = BitWidth.create(8);
	private BitWidth dataBits = BitWidth.create(8);
	private MemContents contents;
	private String label = "";
	private Font labelFont = StdAttr.DEFAULT_LABEL_FONT;
	private Boolean labelVisible = false;

	RomAttributes() {
		contents = MemContents.create(addrBits.getWidth(), dataBits.getWidth());
	}

	public void setProject(Project proj) {
		register(contents, proj);
	}

	@Override
	protected void copyInto(AbstractAttributeSet dest) {

		RomAttributes d = (RomAttributes) dest;
		d.fpga = fpga;
		d.addrBits = addrBits;
		d.dataBits = dataBits;
		d.contents = contents.clone();

	}

	@Override
	public List<Attribute<?>> getAttributes() {
		return ATTRIBUTES;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> V getValue(Attribute<V> attr) {

		if (attr == StdAttr.FPGA_SUPPORTED) return (V) fpga;
		if (attr == Mem.ADDR_ATTR) return (V) addrBits;
		if (attr == Mem.DATA_ATTR) return (V) dataBits;
		if (attr == Rom.CONTENTS_ATTR) return (V) contents;
		if (attr == StdAttr.LABEL) return (V) label;
		if (attr == StdAttr.LABEL_FONT) return (V) labelFont;
		if (attr == StdAttr.LABEL_VISIBILITY) return (V) labelVisible;
		return null;

	}

	@Override
	public <V> void setValue(Attribute<V> attr, V value) {

		if (attr == Mem.ADDR_ATTR) {
			addrBits = (BitWidth) value;
			contents.setDimensions(addrBits.getWidth(), dataBits.getWidth());
			fireAttributeValueChanged(attr, value, null);
		} else if (attr == Mem.DATA_ATTR) {
			dataBits = (BitWidth) value;
			contents.setDimensions(addrBits.getWidth(), dataBits.getWidth());
			fireAttributeValueChanged(attr, value, null);
		} else if (attr == Rom.CONTENTS_ATTR) {
			contents = (MemContents) value;
			fireAttributeValueChanged(attr, value, null);
		} else if (attr == StdAttr.FPGA_SUPPORTED) {
			fpga = (Boolean) value;
			fireAttributeValueChanged(attr, value, null);
		} else if (attr == StdAttr.LABEL) {
			final var newLabel = (String) value;
			if (label.equals(newLabel)) return;
			V oldLabel = (V) label;
			label = newLabel;
			fireAttributeValueChanged(attr, value, oldLabel);
		} else if (attr == StdAttr.LABEL_FONT) {
			final var newFont = (Font) value;
			if (labelFont.equals(newFont)) return;
			labelFont = newFont;
			fireAttributeValueChanged(attr, value, null);
		} else if (attr == StdAttr.LABEL_VISIBILITY) {
			final var newVis = (Boolean) value;
			if (labelVisible.equals(newVis)) return;
			labelVisible = newVis;
			fireAttributeValueChanged(attr, value, null);
		}

	}

}
