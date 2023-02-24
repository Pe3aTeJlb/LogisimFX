/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.io;

import LogisimFX.data.*;
import LogisimFX.instance.*;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.std.LC;
import LogisimFX.util.GraphicsUtil;

import javafx.scene.paint.Color;

public class Led extends InstanceFactory {

	public Led() {

		super("LED", LC.createStringBinding("ledComponent"));
		setAttributes(new Attribute[] {
				StdAttr.FPGA_SUPPORTED,
				StdAttr.FACING, Io.ATTR_ON_COLOR, Io.ATTR_OFF_COLOR,
				Io.ATTR_ACTIVE,
				StdAttr.LABEL, Io.ATTR_LABEL_LOC,
				StdAttr.LABEL_FONT, Io.ATTR_LABEL_COLOR
			}, new Object[] {
				Boolean.FALSE,
				Direction.WEST, Color.color(0.941, 0, 0), Color.DARKGRAY,
				Boolean.TRUE,
				"", Io.LABEL_CENTER,
				StdAttr.DEFAULT_LABEL_FONT, Color.BLACK
			});
		setFacingAttribute(StdAttr.FACING);
		setIcon("led.gif");
		setPorts(new Port[] { new Port(0, 0, Port.INPUT, 1) });
		setInstanceLogger(Logger.class);

	}

	@Override
	public Bounds getOffsetBounds(AttributeSet attrs) {

		Direction facing = attrs.getValue(StdAttr.FACING);
		return Bounds.create(0, -10, 20, 20).rotate(Direction.WEST, facing, 0, 0);

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

		instance.setTextField(StdAttr.LABEL, StdAttr.LABEL_FONT,
				x, y, halign, valign);

	}

	@Override
	public void propagate(InstanceState state) {

		Value val = state.getPort(0);
		InstanceDataSingleton data = (InstanceDataSingleton) state.getData();
		if (data == null) {
			state.setData(new InstanceDataSingleton(val));
		} else {
			data.setValue(val);
		}

	}
	
	@Override
	public void paintGhost(InstancePainter painter) {

		Graphics g = painter.getGraphics();
		Bounds bds = painter.getBounds();
		g.setLineWidth(2);
		g.c.strokeOval(bds.getX() + 1, bds.getY() + 1,
				bds.getWidth() - 2, bds.getHeight() - 2);
		g.toDefaultFont();

	}

	@Override
	public void paintInstance(InstancePainter painter) {

		InstanceDataSingleton data = (InstanceDataSingleton) painter.getData();
		Value val = data == null ? Value.FALSE : (Value) data.getValue();
		Bounds bds = painter.getBounds().expand(-1);

		Graphics g = painter.getGraphics();
		if (painter.getShowState()) {
			Color onColor = painter.getAttributeValue(Io.ATTR_ON_COLOR);
			Color offColor = painter.getAttributeValue(Io.ATTR_OFF_COLOR);
			Boolean activ = painter.getAttributeValue(Io.ATTR_ACTIVE);
			Object desired = activ.booleanValue() ? Value.TRUE : Value.FALSE;
			g.setColor(val == desired ? onColor : offColor);
			g.c.fillOval(bds.getX(), bds.getY(), bds.getWidth(), bds.getHeight());
		}
		g.setColor(Color.BLACK);
		g.setLineWidth(2);
		g.c.strokeOval(bds.getX(), bds.getY(), bds.getWidth(), bds.getHeight());
		g.setLineWidth(1);
		g.setColor(painter.getAttributeValue(Io.ATTR_LABEL_COLOR));
		painter.drawLabel();
		painter.drawPorts();

		g.toDefault();

	}

	public static class Logger extends InstanceLogger {

		@Override
		public String getLogName(InstanceState state, Object option) {
			return state.getAttributeValue(StdAttr.LABEL);
		}
	
		@Override
		public Value getLogValue(InstanceState state, Object option) {
			InstanceDataSingleton data = (InstanceDataSingleton) state.getData();
			if (data == null) return Value.FALSE;
			return data.getValue() == Value.TRUE ? Value.TRUE : Value.FALSE;
		}

	}

}
