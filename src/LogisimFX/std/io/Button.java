/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.io;

import LogisimFX.data.*;
import LogisimFX.instance.*;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.LayoutCanvas;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.std.LC;
import LogisimFX.util.GraphicsUtil;
import LogisimFX.circuit.Wire;

import javafx.scene.paint.Color;

public class Button extends InstanceFactory {

	private static final int DEPTH = 3;

	public static final AttributeOption BUTTON_PRESS_ACTIVE =
			new AttributeOption("active", LC.createStringBinding("buttonPressActive"));
	public static final AttributeOption BUTTON_PRESS_PASSIVE =
			new AttributeOption("passive", LC.createStringBinding("buttonPressPassive"));
	public static final Attribute<AttributeOption> ATTR_PRESS =
			Attributes.forOption(
					"press",
					LC.createStringBinding("buttonPressAttr"),
					new AttributeOption[] {BUTTON_PRESS_ACTIVE, BUTTON_PRESS_PASSIVE});

	public Button() {

		super("Button", LC.createStringBinding("buttonComponent"), new AbstractSimpleIoHdlGeneratorFactory(true), true);
		setAttributes(new Attribute[] {
				StdAttr.FPGA_SUPPORTED,
				StdAttr.FACING,
				ATTR_PRESS,
				Io.ATTR_COLOR,
				StdAttr.LABEL,
				Io.ATTR_LABEL_LOC,
				StdAttr.LABEL_FONT,
				Io.ATTR_LABEL_COLOR,
				StdAttr.LABEL_VISIBILITY
			}, new Object[] {
				Boolean.FALSE,
				Direction.EAST, BUTTON_PRESS_ACTIVE, Color.WHITE,
				"", Io.LABEL_CENTER,
				StdAttr.DEFAULT_LABEL_FONT, Color.BLACK,
				Boolean.FALSE
			});
		setFacingAttribute(StdAttr.FACING);
		setIcon("button.gif");
		setPorts(new Port[] { new Port(0, 0, Port.OUTPUT, 1) });
		setInstancePoker(Poker.class);
		setInstanceLogger(Logger.class);

	}

	@Override
	public Bounds getOffsetBounds(AttributeSet attrs) {

		Direction facing = attrs.getValue(StdAttr.FACING);
		return Bounds.create(-20, -10, 20, 20).rotate(Direction.EAST, facing, 0, 0);

	}

	@Override
	protected void configureNewInstance(Instance instance) {

		instance.addAttributeListener();
		computeTextField(instance);

	}

	@Override
	protected void instanceAttributeChanged(Instance instance, Attribute<?> attr) {

		if (attr == StdAttr.FACING) {
			instance.recomputeBounds();
			computeTextField(instance);
		} else if (attr == Io.ATTR_LABEL_LOC) {
			computeTextField(instance);
		}

	}

	private void computeTextField(Instance instance) {

		Direction facing = instance.getAttributeValue(StdAttr.FACING);
		Object labelLoc = instance.getAttributeValue(Io.ATTR_LABEL_LOC);

		Bounds bds = instance.getBounds();
		int x = bds.getX() + bds.getWidth() / 2;
		int y = bds.getY() + bds.getHeight() / 2;
		int halign = GraphicsUtil.H_CENTER;
		int valign = GraphicsUtil.V_CENTER;
		if (labelLoc == Io.LABEL_CENTER) {
			x = bds.getX() + (bds.getWidth() - DEPTH) / 2;
			y = bds.getY() + (bds.getHeight() - DEPTH) / 2;
		} else if (labelLoc == Direction.NORTH) {
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

		instance.setTextField(StdAttr.LABEL, StdAttr.LABEL_FONT,
				x, y, halign, valign);

	}

	@Override
	public void propagate(InstanceState state) {

		InstanceDataSingleton data = (InstanceDataSingleton) state.getData();
		Value val = data == null ? state.getAttributeValue(ATTR_PRESS) == BUTTON_PRESS_ACTIVE ? Value.FALSE : Value.TRUE : (Value) data.getValue();
		state.setPort(0, val, 1);

	}

	@Override
	public void paintInstance(InstancePainter painter) {

		final var defaultButtonState =
				painter.getAttributeValue(ATTR_PRESS) == BUTTON_PRESS_ACTIVE ? Value.FALSE : Value.TRUE;

		Bounds bds = painter.getBounds();
		int x = bds.getX();
		int y = bds.getY();
		int w = bds.getWidth();
		int h = bds.getHeight();

		Value val;
		if (painter.getShowState()) {
			InstanceDataSingleton data = (InstanceDataSingleton) painter.getData();
			val = data == null ? defaultButtonState : (Value) data.getValue();
		} else {
			val = defaultButtonState;
		}

		Color color = painter.getAttributeValue(Io.ATTR_COLOR);
		if (!painter.shouldDrawColor()) {
			int hue = (int)(color.getRed() + color.getGreen() + color.getBlue()) / 3;
			color = Color.color(hue, hue, hue);
		}

		Graphics g = painter.getGraphics();
		int depress;
		if (val != defaultButtonState) {
			x += DEPTH;
			y += DEPTH;
			Object labelLoc = painter.getAttributeValue(Io.ATTR_LABEL_LOC);
			if (labelLoc == Io.LABEL_CENTER || labelLoc == Direction.NORTH
					|| labelLoc == Direction.WEST) {
				depress = DEPTH;
			} else {
				depress = 0;
			}

			Object facing = painter.getAttributeValue(StdAttr.FACING);
			if (facing == Direction.NORTH || facing == Direction.WEST) {
				Location p = painter.getLocation();
				int px = p.getX();
				int py = p.getY();
				g.setLineWidth(Wire.WIDTH);
				g.setColor(Value.TRUE_COLOR);
				if (facing == Direction.NORTH) g.c.strokeLine(px, py, px, py + 10);
				else                          g.c.strokeLine(px, py, px + 10, py);
				g.setLineWidth(1);
			}

			g.setColor(color);
			g.c.fillRect(x, y, w - DEPTH, h - DEPTH);
			g.setColor(Color.BLACK);
			g.c.strokeRect(x, y, w - DEPTH, h - DEPTH);
		} else {
			depress = 0;
			double[] xp = new double[] { x, x + w - DEPTH, x + w, x + w, x + DEPTH, x };
			double[] yp = new double[] { y, y, y + DEPTH, y + h, y + h, y + h - DEPTH };
			g.setColor(color.darker());
			g.c.fillPolygon(xp, yp, xp.length);
			g.setColor(color);
			g.c.fillRect(x, y, w - DEPTH, h - DEPTH);
			g.setColor(Color.BLACK);
			g.c.strokeRect(x, y, w - DEPTH, h - DEPTH);
			g.c.strokeLine(x + w - DEPTH, y + h - DEPTH, x + w, y + h);
			g.c.strokePolygon(xp, yp, xp.length);
		}

		g.c.translate(depress, depress);
		g.setColor(painter.getAttributeValue(Io.ATTR_LABEL_COLOR));
		painter.drawLabel();
		g.c.translate(-depress, -depress);
		painter.drawPorts();

		g.toDefault();

	}
	
	public static class Poker extends InstancePoker {

		@Override
		public void mousePressed(InstanceState state, LayoutCanvas.CME e) {
			setValue(
					state,
					state.getAttributeValue(ATTR_PRESS) == BUTTON_PRESS_PASSIVE ? Value.FALSE : Value.TRUE);
		}

		@Override
		public void mouseReleased(InstanceState state, LayoutCanvas.CME e) {
			setValue(
					state,
					state.getAttributeValue(ATTR_PRESS) == BUTTON_PRESS_PASSIVE ? Value.TRUE : Value.FALSE);
		}
		
		private void setValue(InstanceState state, Value val) {
			InstanceDataSingleton data = (InstanceDataSingleton) state.getData();
			if (data == null) {
				state.setData(new InstanceDataSingleton(val));
			} else {
				data.setValue(val);
			}
			state.getInstance().fireInvalidated();
		}

	}

	public static class Logger extends InstanceLogger {

		@Override
		public String getLogName(InstanceState state, Object option) {
			return state.getAttributeValue(StdAttr.LABEL);
		}

		@Override
		public Value getLogValue(InstanceState state, Object option) {
			InstanceDataSingleton data = (InstanceDataSingleton) state.getData();
			final var defaultButtonState =
					state.getAttributeValue(ATTR_PRESS) == BUTTON_PRESS_ACTIVE ? Value.FALSE : Value.TRUE;
			return data == null ? defaultButtonState : (Value) data.getValue();
		}

	}

}
