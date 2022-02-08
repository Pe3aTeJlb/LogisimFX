/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.memory;

import java.io.File;
import java.io.IOException;
import java.util.WeakHashMap;

import LogisimFX.data.*;
import LogisimFX.newgui.HexEditorFrame.HexFile;
import LogisimFX.newgui.HexEditorFrame.HexModel;
import LogisimFX.newgui.HexEditorFrame.HexModelListener;
import LogisimFX.instance.*;
import LogisimFX.newgui.ContextMenuManager;
import LogisimFX.newgui.MainFrame.Canvas.Graphics;
import LogisimFX.std.LC;
import LogisimFX.tools.MenuExtender;
import LogisimFX.tools.key.BitWidthConfigurator;
import LogisimFX.tools.key.JoinedConfigurator;
import LogisimFX.util.GraphicsUtil;
import LogisimFX.circuit.CircuitState;
import LogisimFX.proj.Project;

import javafx.beans.binding.StringBinding;
import javafx.scene.paint.Color;

public abstract class Mem extends InstanceFactory {

	// Note: The code is meant to be able to handle up to 32-bit addresses, but it
	// hasn't been debugged thoroughly. There are two definite changes I would
	// make if I were to extend the address bits: First, there would need to be some
	// modification to the memory's graphical representation, because there isn't
	// room in the box to include such long memory addresses with the current font
	// size. And second, I'd alter the MemContents class's PAGE_SIZE_BITS constant
	// to 14 so that its "page table" isn't quite so big.
	public static final Attribute<BitWidth> ADDR_ATTR = Attributes.forBitWidth(
			"addrWidth", LC.createStringBinding("ramAddrWidthAttr"), 2, 24);
	public static final Attribute<BitWidth> DATA_ATTR = Attributes.forBitWidth(
			"dataWidth", LC.createStringBinding("ramDataWidthAttr"));
	
	// port-related constants
	static final int DATA = 0;
	static final int ADDR = 1;
	static final int CS = 2;
	static final int MEM_INPUTS = 3;

	// other constants
	static final int DELAY = 10;

	private WeakHashMap<Instance,File> currentInstanceFiles;

	Mem(String name, StringBinding desc, int extraPorts) {

		super(name, desc);
		currentInstanceFiles = new WeakHashMap<Instance,File>();
		setInstancePoker(MemPoker.class);
		setKeyConfigurator(JoinedConfigurator.create(
				new BitWidthConfigurator(ADDR_ATTR, 2, 24, null),
				new BitWidthConfigurator(DATA_ATTR)));

		setOffsetBounds(Bounds.create(-140, -40, 140, 80));

	}
	
	abstract void configurePorts(Instance instance);
	@Override
	public abstract AttributeSet createAttributeSet();
	public abstract MemState getState(InstanceState state);
	public abstract MemState getState(Instance instance, CircuitState state);
	public abstract void createHexFrame(Project proj, Instance instance, CircuitState state);
	@Override
	public abstract void propagate(InstanceState state);

	@Override
	protected void configureNewInstance(Instance instance) {
		configurePorts(instance);
	}
	
	void configureStandardPorts(Instance instance, Port[] ps) {

		ps[DATA] = new Port(   0,  0, Port.INOUT, DATA_ATTR);
		ps[ADDR] = new Port(-140,  0, Port.INPUT, ADDR_ATTR);
		ps[CS]   = new Port( -90, 40, Port.INPUT, 1);
		ps[DATA].setToolTip(LC.createStringBinding("memDataTip"));
		ps[ADDR].setToolTip(LC.createStringBinding("memAddrTip"));
		ps[CS].setToolTip(LC.createStringBinding("memCSTip"));

	}

	@Override
	public void paintInstance(InstancePainter painter) {

		Graphics g = painter.getGraphics();
		Bounds bds = painter.getBounds();

		// draw boundary
		painter.drawBounds();

		// draw contents
		if (painter.getShowState()) {
			MemState state = getState(painter);
			state.paint(painter.getGraphics(), bds.getX(), bds.getY());
		} else {
			BitWidth addr = painter.getAttributeValue(ADDR_ATTR);
			int addrBits = addr.getWidth();
			int bytes = 1 << addrBits;
			String label;
			if (this instanceof Rom) {
				if (addrBits >= 30) {
					label = LC.getFormatted("romGigabyteLabel", ""
							+ (bytes >>> 30));
				} else if (addrBits >= 20) {
					label = LC.getFormatted("romMegabyteLabel", ""
							+ (bytes >> 20));
				} else if (addrBits >= 10) {
					label = LC.getFormatted("romKilobyteLabel", ""
							+ (bytes >> 10));
				} else {
					label = LC.getFormatted("romByteLabel", ""
							+ bytes);
				}
			} else {
				if (addrBits >= 30) {
					label = LC.getFormatted("ramGigabyteLabel", ""
							+ (bytes >>> 30));
				} else if (addrBits >= 20) {
					label = LC.getFormatted("ramMegabyteLabel", ""
							+ (bytes >> 20));
				} else if (addrBits >= 10) {
					label = LC.getFormatted("ramKilobyteLabel", ""
							+ (bytes >> 10));
				} else {
					label = LC.getFormatted("ramByteLabel", ""
							+ bytes);
				}
			}
			GraphicsUtil.drawCenteredText(g, label, bds.getX() + bds.getWidth()
					/ 2, bds.getY() + bds.getHeight() / 2);
		}

		// draw input and output ports
		painter.drawPort(DATA, LC.get("ramDataLabel"), Direction.WEST);
		painter.drawPort(ADDR, LC.get("ramAddrLabel"), Direction.EAST);
		g.setColor(Color.GRAY);
		painter.drawPort(CS, LC.get("ramCSLabel"), Direction.SOUTH);

		g.toDefault();

	}
	
	public File getCurrentImage(Instance instance) {
		return currentInstanceFiles.get(instance);
	}
	
	public void setCurrentImage(Instance instance, File value) {
		currentInstanceFiles.put(instance, value);
	}
	
	public void loadImage(InstanceState instanceState, File imageFile)
			throws IOException {

		MemState s = this.getState(instanceState);
		HexFile.open(s.getContents(), imageFile);
		this.setCurrentImage(instanceState.getInstance(), imageFile);

	}

	@Override
	protected Object getInstanceFeature(Instance instance, Object key) {
		if (key == MenuExtender.class)
			return new ContextMenuManager.MemoryComponentContextMenu(this, instance);
		return super.getInstanceFeature(instance, key);

	}
	
	static class MemListener implements HexModelListener {

		Instance instance;
		
		MemListener(Instance instance) { this.instance = instance; }
		
		public void metainfoChanged(HexModel source) { }

		public void bytesChanged(HexModel source, long start,
				long numBytes, int[] values) {
			instance.fireInvalidated();
		}

	}

}
