/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * Original code by Carl Burch (http://www.cburch.com), 2011.
 * License information is located in the Launch file
 */

package LogisimFX.std.io;

import LogisimFX.KeyEvents;
import LogisimFX.data.*;
import LogisimFX.instance.*;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.std.LC;

import LogisimFX.tools.key.DirectionConfigurator;
import LogisimFX.util.GraphicsUtil;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class SevenSegment extends InstanceFactory {

	public static final int Segment_A = 0;
	public static final int Segment_B = 1;
	public static final int Segment_C = 2;
	public static final int Segment_D = 3;
	public static final int Segment_E = 4;
	public static final int Segment_F = 5;
	public static final int Segment_G = 6;

	public static final int DP = 7;

	static Bounds[] SEGMENTS = null;
	static Color DEFAULT_OFF = Color.color(0.863, 0.863, 0.863);

	public SevenSegment() {

		super("7-Segment Display", LC.createStringBinding("sevenSegmentComponent"), new AbstractSimpleIoHdlGeneratorFactory(false), true);
		setAttributes(new Attribute[]{
						StdAttr.FPGA_SUPPORTED,
						Io.ATTR_ON_COLOR,
						Io.ATTR_OFF_COLOR,
						Io.ATTR_BACKGROUND,
						Io.ATTR_ACTIVE,
						StdAttr.LABEL,
						StdAttr.LABEL_LOC,
						StdAttr.LABEL_FONT,
						StdAttr.LABEL_VISIBILITY
				},
				new Object[]{
						Boolean.FALSE,
						Color.color(0.941, 0, 0),
						DEFAULT_OFF,
						Io.DEFAULT_BACKGROUND,
						Boolean.TRUE,
						"",
						Direction.EAST,
						StdAttr.DEFAULT_LABEL_FONT,
						Boolean.FALSE
				});
		setOffsetBounds(Bounds.create(-5, 0, 40, 60));
		setIcon("7seg.gif");
		setKeyConfigurator(new DirectionConfigurator(StdAttr.LABEL_LOC, KeyEvents.ALT_DOWN));
	}

	@Override
	public void propagate(InstanceState state) {

		int summary = 0;
		for (int i = 0; i < 8; i++) {
			Value val = state.getPortValue(i);
			if (val == Value.TRUE) summary |= 1 << i;
		}
		Object value = Integer.valueOf(summary);
		InstanceDataSingleton data = (InstanceDataSingleton) state.getData();
		if (data == null) {
			state.setData(new InstanceDataSingleton(value));
		} else {
			data.setValue(value);
		}

	}

	private void updatePorts(Instance instance) {
		final var ps = new Port[8];
		ps[Segment_A] = new Port(20, 0, Port.INPUT, 1);
		ps[Segment_B] = new Port(30, 0, Port.INPUT, 1);
		ps[Segment_C] = new Port(20, 60, Port.INPUT, 1);
		ps[Segment_D] = new Port(10, 60, Port.INPUT, 1);
		ps[Segment_E] = new Port(0, 60, Port.INPUT, 1);
		ps[Segment_F] = new Port(10, 0, Port.INPUT, 1);
		ps[Segment_G] = new Port(0, 0, Port.INPUT, 1);
		ps[Segment_A].setToolTip(LC.createStringBinding("Segment_A"));
		ps[Segment_B].setToolTip(LC.createStringBinding("Segment_B"));
		ps[Segment_C].setToolTip(LC.createStringBinding("Segment_C"));
		ps[Segment_D].setToolTip(LC.createStringBinding("Segment_D"));
		ps[Segment_E].setToolTip(LC.createStringBinding("Segment_E"));
		ps[Segment_F].setToolTip(LC.createStringBinding("Segment_F"));
		ps[Segment_G].setToolTip(LC.createStringBinding("Segment_G"));
		ps[DP] = new Port(30, 60, Port.INPUT, 1);
		ps[DP].setToolTip(LC.createStringBinding("DecimalPoint"));
		instance.setPorts(ps);
		//instance.getAttributeValue(StdAttr.MAPINFO).setNrOfOutports(8, getLabels());
	}

	@Override
	public void paintInstance(InstancePainter painter) {
		drawBase(painter);
	}

	static void drawBase(InstancePainter painter) {

		ensureSegments();
		InstanceDataSingleton data = (InstanceDataSingleton) painter.getData();
		int summ = (data == null ? 0 : ((Integer) data.getValue()).intValue());
		Boolean active = painter.getAttributeValue(Io.ATTR_ACTIVE);
		int desired = active == null || active.booleanValue() ? 1 : 0;

		Bounds bds = painter.getBounds();
		int x = bds.getX() + 5;
		int y = bds.getY();

		Graphics g = painter.getGraphics();
		Color onColor = painter.getAttributeValue(Io.ATTR_ON_COLOR);
		Color offColor = painter.getAttributeValue(Io.ATTR_OFF_COLOR);
		Color bgColor = painter.getAttributeValue(Io.ATTR_BACKGROUND);
		if (painter.shouldDrawColor() && bgColor.getOpacity() != 0) {
			g.setColor(bgColor);
			g.c.fillRect(bds.getX(), bds.getY(), bds.getWidth(), bds.getHeight());
			g.setColor(Color.BLACK);
		}
		painter.drawBounds();
		g.setColor(Color.DARKGRAY);
		for (int i = 0; i <= 7; i++) {
			if (painter.getShowState()) {
				Color p = ((summ >> i) & 1) == desired ? onColor : offColor;
				g.setColor(p);
			}
			if (i < 7) {
				Bounds seg = SEGMENTS[i];
				g.c.fillRect(x + seg.getX(), y + seg.getY(), seg.getWidth(), seg.getHeight());
			} else {
				g.c.fillOval(x + 28, y + 48, 5, 5); // draw decimal point
			}
		}
		g.setColor(Color.BLACK);
		painter.drawLabel();
		painter.drawPorts();

		g.toDefault();

	}

	static void ensureSegments() {

		if (SEGMENTS == null) {
			SEGMENTS = new Bounds[]{
					Bounds.create(3, 8, 19, 4),
					Bounds.create(23, 10, 4, 19),
					Bounds.create(23, 30, 4, 19),
					Bounds.create(3, 47, 19, 4),
					Bounds.create(-2, 30, 4, 19),
					Bounds.create(-2, 10, 4, 19),
					Bounds.create(3, 28, 19, 4)
			};
		}

	}

	public static List<String> getLabels() {
		final var labelNames = new ArrayList<String>();
		for (int i = 0; i < 8; i++) labelNames.add("");
		labelNames.set(Segment_A, "Segment_A");
		labelNames.set(Segment_B, "Segment_B");
		labelNames.set(Segment_C, "Segment_C");
		labelNames.set(Segment_D, "Segment_D");
		labelNames.set(Segment_E, "Segment_E");
		labelNames.set(Segment_F, "Segment_F");
		labelNames.set(Segment_G, "Segment_G");
		labelNames.set(DP, "DecimalPoint");
		return labelNames;
	}

	public static String getOutputLabel(int id) {
		if (id < 0 || id > getLabels().size()) return "Undefined";
		return getLabels().get(id);
	}



	public static void computeTextField(Instance instance) {
		final var facing = instance.getAttributeValue(StdAttr.FACING);
		Object labelLoc = instance.getAttributeValue(StdAttr.LABEL_LOC);

		final var bds = instance.getBounds();
		int x = bds.getX() + bds.getWidth() / 2;
		int y = bds.getY() + bds.getHeight() / 2;
		int halign = GraphicsUtil.H_CENTER;
		int valign = GraphicsUtil.V_CENTER;
		if (labelLoc == Direction.NORTH) {
			y = bds.getY() - 2;
			valign = GraphicsUtil.V_BOTTOM;
		} else if (labelLoc == Direction.SOUTH) {
			y = bds.getY() + bds.getHeight() + 2;
			valign = GraphicsUtil.V_TOP;
		} else if (labelLoc == Direction.EAST) {
			x = bds.getX() + bds.getWidth() + 2;
			halign = GraphicsUtil.H_LEFT;
		} else if (labelLoc == Direction.WEST) {
			x = bds.getX() - 2;
			halign = GraphicsUtil.H_RIGHT;
		}
		if (labelLoc == facing) {
			if (labelLoc == Direction.NORTH || labelLoc == Direction.SOUTH) {
				x += 2;
				halign = GraphicsUtil.H_LEFT;
			} else {
				y -= 2;
				valign = GraphicsUtil.V_BOTTOM;
			}
		}

		instance.setTextField(StdAttr.LABEL, StdAttr.LABEL_FONT, x, y, halign, valign);
	}

	@Override
	protected void configureNewInstance(Instance instance) {
		//instance.getAttributeSet().setValue(StdAttr.MAPINFO, new ComponentMapInformationContainer(0, 8, 0, null, getLabels(), null));
		instance.addAttributeListener();
		updatePorts(instance);
		computeTextField(instance);
	}


	@Override
	protected void instanceAttributeChanged(Instance instance, Attribute<?> attr) {
		if (attr == StdAttr.FACING) {
			instance.recomputeBounds();
			computeTextField(instance);
		} else if (attr == StdAttr.LABEL_LOC) {
			computeTextField(instance);
		}
	}

	@Override
	public boolean activeOnHigh(AttributeSet attrs) {
		return attrs.getValue(Io.ATTR_ACTIVE);
	}

}
