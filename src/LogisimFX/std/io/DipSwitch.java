/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package LogisimFX.std.io;

import LogisimFX.KeyEvents;
import LogisimFX.data.*;
import LogisimFX.draw.shapes.DrawAttr;
import LogisimFX.fpga.data.ComponentMapInformationContainer;
import LogisimFX.instance.*;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.LayoutCanvas;
import LogisimFX.std.LC;
import LogisimFX.tools.key.BitWidthConfigurator;
import LogisimFX.tools.key.DirectionConfigurator;
import LogisimFX.tools.key.JoinedConfigurator;
import LogisimFX.util.GraphicsUtil;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class DipSwitch extends InstanceFactory {

	public static class Poker extends InstancePoker {

		@Override
		public void mousePressed(InstanceState state, LayoutCanvas.CME e) {
			final var val = (State) state.getData();
			final var loc = state.getInstance().getLocation();
			final var facing = state.getInstance().getAttributeValue(StdAttr.FACING);
			final var n = state.getInstance().getAttributeValue(ATTR_SIZE).getWidth();
			int i;
			if (facing == Direction.SOUTH) {
				i = n + (e.localX - loc.getX() - 5) / 10;
			} else if (facing == Direction.EAST) {
				i = (e.localY - loc.getY() - 5) / 10;
			} else if (facing == Direction.WEST) {
				i = (loc.getY() - e.localY - 5) / 10;
			} else {
				i = (e.localX - loc.getX() - 5) / 10;
			}
			val.toggleBit(i);
			state.getInstance().fireInvalidated();
		}
	}

	private static class State implements InstanceData, Cloneable {

		private int Value;
		private final int size;

		public State(int value, int size) {
			Value = value;
			this.size = size;
		}

		public boolean isBitSet(int bitindex) {
			if (bitindex >= size) {
				return false;
			}
			int mask = 1 << bitindex;
			return (Value & mask) != 0;
		}

		@Override
		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				return null;
			}
		}

		public void toggleBit(int bitindex) {
			if ((bitindex < 0) || (bitindex >= size)) {
				return;
			}
			int mask = 1 << bitindex;
			Value ^= mask;
		}
	}

	public static ArrayList<String> getLabels(int size) {
		final var labelNames = new ArrayList<String>();
		for (var i = 0; i < size; i++) {
			labelNames.add(getInputLabel(i));
		}
		return labelNames;
	}

	public static String getInputLabel(int id) {
		return "sw_" + (id + 1);
	}

	public static final int MAX_SWITCH = 32;
	public static final int MIN_SWITCH = 2;

	public static final Attribute<BitWidth> ATTR_SIZE =
			Attributes.forBitWidth("number", LC.createStringBinding("nrOfSwitch"), MIN_SWITCH, MAX_SWITCH);

	public DipSwitch() {
		super("DipSwitch", LC.createStringBinding("DipSwitchComponent"), new AbstractSimpleIoHdlGeneratorFactory(true), true);
		var dipSize = 8;
		setAttributes(
				new Attribute[]{
						StdAttr.FPGA_SUPPORTED,
						StdAttr.FACING,
						StdAttr.LABEL,
						StdAttr.LABEL_LOC,
						StdAttr.LABEL_FONT,
						StdAttr.LABEL_VISIBILITY,
						ATTR_SIZE,
						StdAttr.MAPINFO
				},
				new Object[]{
						Boolean.FALSE,
						Direction.NORTH,
						"",
						Direction.EAST,
						StdAttr.DEFAULT_LABEL_FONT,
						Boolean.FALSE,
						BitWidth.create(dipSize),
						new ComponentMapInformationContainer(dipSize, 0, 0, getLabels(dipSize), null, null)
				});
		setFacingAttribute(StdAttr.FACING);
		setIcon("dipswitch.gif");
		setKeyConfigurator(
				JoinedConfigurator.create(
						new BitWidthConfigurator(ATTR_SIZE),
						new DirectionConfigurator(StdAttr.LABEL_LOC, KeyEvents.ALT_DOWN)));
		setInstancePoker(Poker.class);
	}

	@Override
	protected void configureNewInstance(Instance instance) {
		instance.addAttributeListener();
		updatePorts(instance);
		instance.computeLabelTextField(Instance.AVOID_LEFT);
		int dipSize = instance.getAttributeValue(ATTR_SIZE).getWidth();
		instance
				.getAttributeSet()
				.setValue(
						StdAttr.MAPINFO,
						new ComponentMapInformationContainer(dipSize, 0, 0, getLabels(dipSize), null, null));
	}

	private void updatePorts(Instance instance) {
		final var facing = instance.getAttributeValue(StdAttr.FACING);
		final var n = instance.getAttributeValue(ATTR_SIZE).getWidth();
		int cx = 0, cy = 0, dx = 0, dy = 0;
		if (facing == Direction.WEST) {
			// cy = -10*(n+1); dy = 10;
			dy = -10;
		} else if (facing == Direction.EAST) {
			// cy = 10*(n+1); dy = -10;
			dy = 10;
		} else if (facing == Direction.SOUTH) {
			cx = -10 * (n + 1);
			dx = 10;
		} else {
			dx = 10;
		}
		final var ps = new Port[n];
		for (var i = 0; i < ps.length; i++) {
			ps[i] = new Port(cx + (i + 1) * dx, cy + (i + 1) * dy, Port.OUTPUT, 1);
			ps[i].setToolTip(LC.castToBind("DIP" + (i + 1)));
		}
		instance.setPorts(ps);
	}

	@Override
	public Bounds getOffsetBounds(AttributeSet attrs) {
		final var facing = attrs.getValue(StdAttr.FACING);
		final var n = attrs.getValue(ATTR_SIZE).getWidth();
		return Bounds.create(0, 0, (n + 1) * 10, 40).rotate(Direction.NORTH, facing, 0, 0);
	}

	@Override
	protected void instanceAttributeChanged(Instance instance, Attribute<?> attr) {
		if (attr == StdAttr.LABEL_LOC) {
			instance.computeLabelTextField(Instance.AVOID_LEFT);
		} else if (attr == ATTR_SIZE) {
			instance.recomputeBounds();
			updatePorts(instance);
			instance.computeLabelTextField(Instance.AVOID_LEFT);
			ComponentMapInformationContainer map = instance.getAttributeValue(StdAttr.MAPINFO);
			if (map != null) {
				map.setNrOfInports(
						instance.getAttributeValue(ATTR_SIZE).getWidth(),
						getLabels(instance.getAttributeValue(ATTR_SIZE).getWidth()));
			}
		} else if (attr == StdAttr.FACING) {
			instance.recomputeBounds();
			updatePorts(instance);
			instance.computeLabelTextField(Instance.AVOID_LEFT);
		}
	}

	@Override
	public void paintInstance(InstancePainter painter) {
		final var segmentWidth = 10;

		var state = (State) painter.getData();
		if (state == null || state.size != painter.getAttributeValue(ATTR_SIZE).getWidth()) {
			final var val = (state == null) ? 0 : state.Value;
			state = new State(val, painter.getAttributeValue(ATTR_SIZE).getWidth());
			painter.setData(state);
		}
		int n = painter.getAttributeValue(ATTR_SIZE).getWidth();

		final var facing = painter.getAttributeValue(StdAttr.FACING);
		final var loc = painter.getLocation();
		var x = loc.getX();
		var y = loc.getY();
		if (facing == Direction.SOUTH) {
			x -= segmentWidth * (n + 1);
			y -= 40;
		}
		final var g = painter.getGraphics();
		g.c.translate(x, y);
		var rotate = 0.0;
		if (facing != Direction.NORTH && facing != Direction.SOUTH) {
			rotate = -facing.getRight().toRadians();
			g.rotate(rotate);
		}

		// draw switch background
		g.setColor(Color.DARKGRAY);
		g.c.fillRect(1, 1, (n + 1) * segmentWidth - 2, 40 - 2);

		// switch bg and labels
		g.setFont(DrawAttr.DEFAULT_FONT);
		/*
		if (n > 9) {
			g.setFont(g.getFont().deriveFont(g.getFont().getSize2D() * 0.6f));
		}*/
		for (var i = 0; i < n; i++) {
			g.setColor(state.isBitSet(i) ? Value.TRUE_COLOR : Color.WHITE);
			g.c.fillRect(7 + (i * segmentWidth), 16, 6, 20);

			g.setColor(Color.WHITE);
			final var s = Integer.toString(i + 1);
			GraphicsUtil.drawCenteredText(g, s, 9 + (i * segmentWidth), 8);
		}

		// draw each switch state
		for (var i = 0; i < n; i++) {
			g.setColor(state.isBitSet(i) ? Color.DARKGRAY : Color.GRAY);
			int ypos = state.isBitSet(i) ? 17 : 26;
			g.c.fillRect(8 + (i * segmentWidth), ypos, 4, 9);
		}

		if (rotate != 0.0) {
			g.rotate(-rotate);
		}
		g.c.translate(-x, -y);

		painter.drawLabel();
		painter.drawPorts();
	}

	@Override
	public void propagate(InstanceState state) {
		var pins = (State) state.getData();
		if (pins == null || pins.size != state.getAttributeValue(ATTR_SIZE).getWidth()) {
			int val = (pins == null) ? 0 : pins.Value;
			pins = new State(val, state.getAttributeValue(ATTR_SIZE).getWidth());
			state.setData(pins);
		}
		for (var i = 0; i < pins.size; i++) {
			Value pinstate = (pins.isBitSet(i)) ? Value.TRUE : Value.FALSE;
			state.setPort(i, pinstate, 1);
		}
	}
}
