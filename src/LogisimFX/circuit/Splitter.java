/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * Original code by Carl Burch (http://www.cburch.com), 2011.
 * License information is located in the Launch file
 */

package LogisimFX.circuit;

import LogisimFX.comp.*;
import LogisimFX.data.*;
import LogisimFX.fpga.designrulecheck.Netlist;
import LogisimFX.instance.StdAttr;
import LogisimFX.proj.Project;
import LogisimFX.tools.MenuExtender;
import LogisimFX.tools.ToolTipMaker;
import LogisimFX.tools.WireRepair;
import LogisimFX.tools.WireRepairData;
import LogisimFX.util.StringUtil;
import javafx.beans.binding.StringBinding;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SeparatorMenuItem;

public class Splitter extends ManagedComponent
		implements WireRepair, MenuExtender, ToolTipMaker, AttributeListener {

	private boolean isMarked = false;

	public void setMarked(boolean value) {
		isMarked = value;
	}

	public boolean isMarked() {
		return isMarked;
	}

	// basic data
	byte[] bit_thread; // how each bit maps to thread within end

	// derived data
	CircuitWires.SplitterData wire_data;

	public Splitter(Location loc, AttributeSet attrs) {

		super(loc, attrs, 3);
		configureComponent();
		attrs.addAttributeListener(this);

	}

	//
	// abstract ManagedComponent methods
	//
	@Override
	public ComponentFactory getFactory() {
		return SplitterFactory.instance;
	}

	@Override
	public void propagate(CircuitState state) {
		; // handled by CircuitWires, nothing to do
	}

	@Override
	public boolean contains(Location loc) {

		if (super.contains(loc)) {
			Location myLoc = getLocation();
			Direction facing = getAttributeSet().getValue(StdAttr.FACING);
			if (facing == Direction.EAST || facing == Direction.WEST) {
				return Math.abs(loc.getX() - myLoc.getX()) > 5
						|| loc.manhattanDistanceTo(myLoc) <= 5;
			} else {
				return Math.abs(loc.getY() - myLoc.getY()) > 5
						|| loc.manhattanDistanceTo(myLoc) <= 5;
			}
		} else {
			return false;
		}

	}

	private synchronized void configureComponent() {

		SplitterAttributes attrs = (SplitterAttributes) getAttributeSet();
		SplitterParameters parms = attrs.getParameters();

		int fanout = attrs.fanout;
		byte[] bit_end = attrs.bitEnd;

		// compute width of each end
		bit_thread = new byte[bit_end.length];
		byte[] end_width = new byte[fanout + 1];
		end_width[0] = (byte) bit_end.length;
		for (int i = 0; i < bit_end.length; i++) {
			byte thr = bit_end[i];
			if (thr > 0) {
				bit_thread[i] = end_width[thr];
				end_width[thr]++;
			} else {
				bit_thread[i] = -1;
			}
		}

		// compute end positions
		Location origin = getLocation();
		int x = origin.getX() + parms.getEnd0X();
		int y = origin.getY() + parms.getEnd0Y();
		int dx = parms.getEndToEndDeltaX();
		int dy = parms.getEndToEndDeltaY();

		EndData[] ends = new EndData[fanout + 1];
		ends[0] = new EndData(origin, BitWidth.create(bit_end.length), EndData.INPUT_OUTPUT);
		for (int i = 0; i < fanout; i++) {
			ends[i + 1] = new EndData(Location.create(x, y),
					BitWidth.create(end_width[i + 1]), EndData.INPUT_OUTPUT);
			x += dx;
			y += dy;
		}
		wire_data = new CircuitWires.SplitterData(fanout);
		setEnds(ends);
		recomputeBounds();
		fireComponentInvalidated(new ComponentEvent(this));

	}

	//
	// user interface methods
	//
	public void draw(ComponentDrawContext context) {

		SplitterAttributes attrs = (SplitterAttributes) getAttributeSet();
		if (attrs.appear == SplitterAttributes.APPEAR_LEGACY) {
			SplitterPainter.drawLegacy(context, attrs, getLocation());
		} else {
			Location loc = getLocation();
			SplitterPainter.drawLines(context, attrs, loc);
			SplitterPainter.drawLabels(context, attrs, loc);
			context.drawPins(this);
		}
		if (isMarked) {
			final var g = context.getGraphics();
			final var bds = this.getBounds();
			g.setColor(Netlist.DRC_INSTANCE_MARK_COLOR);
			g.setLineWidth(2);
			g.c.strokeRoundRect(bds.getX() - 10, bds.getY() - 10, bds.getWidth() + 20, bds.getHeight() + 20, 20, 20);
		}
		context.getGraphics().toDefault();

	}

	public byte[] getEndpoints() {
		return ((SplitterAttributes) getAttributeSet()).bitEnd;
	}

	@Override
	public Object getFeature(Object key) {
		if (key == WireRepair.class) return this;
		if (key == ToolTipMaker.class) return this;
		if (key == MenuExtender.class) return this;
		else return super.getFeature(key);
	}

	public boolean shouldRepairWire(WireRepairData data) {
		return true;
	}

	public StringBinding getToolTip(ComponentUserEvent e) {
		int end = -1;
		for (int i = getEnds().size() - 1; i >= 0; i--) {
			if (getEndLocation(i).manhattanDistanceTo(e.getX(), e.getY()) < 10) {
				end = i;
				break;
			}
		}

		if (end == 0) {
			return LC.createStringBinding("splitterCombinedTip");
		} else if (end > 0) {
			int bits = 0;
			StringBuilder buf = new StringBuilder();
			SplitterAttributes attrs = (SplitterAttributes) getAttributeSet();
			byte[] bit_end = attrs.bitEnd;
			boolean inString = false;
			int beginString = 0;
			for (int i = 0; i < bit_end.length; i++) {
				if (bit_end[i] == end) {
					bits++;
					if (!inString) {
						inString = true;
						beginString = i;
					}
				} else {
					if (inString) {
						appendBuf(buf, beginString, i - 1);
						inString = false;
					}
				}
			}
			if (inString) appendBuf(buf, beginString, bit_end.length - 1);
			String base;
			switch (bits) {
				case 0:
					base = LC.get("splitterSplit0Tip");
					break;
				case 1:
					base = LC.get("splitterSplit1Tip");
					break;
				default:
					base = LC.get("splitterSplitManyTip");
					break;
			}
			return LC.castToBind(StringUtil.format(base, buf.toString()));
		} else {
			return null;
		}
	}

	private static void appendBuf(StringBuilder buf, int start, int end) {
		if (buf.length() > 0) buf.append(",");
		if (start == end) {
			buf.append(start);
		} else {
			buf.append(start + "-" + end);
		}
	}

	public void configureMenu(ContextMenu menu, Project proj) {

		menu.getItems().addAll(
				new SeparatorMenuItem(),
				new SplitterDistributeItem(proj, this, 1),
				new SplitterDistributeItem(proj, this, -1)
		);

	}

	//
	// AttributeListener methods
	//
	public void attributeListChanged(AttributeEvent e) {
	}

	public void attributeValueChanged(AttributeEvent e) {
		configureComponent();
	}

}