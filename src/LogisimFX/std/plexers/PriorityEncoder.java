/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.plexers;

import LogisimFX.data.*;
import LogisimFX.instance.*;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.std.LC;
import LogisimFX.tools.key.BitWidthConfigurator;
import LogisimFX.util.GraphicsUtil;
import javafx.scene.paint.Color;

public class PriorityEncoder extends InstanceFactory {

	static final int OUT = 0;
	static final int EN_IN = 1;
	static final int EN_OUT = 2;
	static final int GS = 3;
	
	public PriorityEncoder() {

		super("Priority Encoder", LC.createStringBinding("priorityEncoderComponent"), new PriorityEncoderHdlGeneratorFactory());
		setAttributes(new Attribute[] {
				StdAttr.FPGA_SUPPORTED,
				StdAttr.FACING, Plexers.ATTR_SELECT, Plexers.ATTR_DISABLED
			}, new Object[] {
				Boolean.FALSE,
				Direction.EAST, BitWidth.create(3), Plexers.DISABLED_FLOATING
			});
		setKeyConfigurator(new BitWidthConfigurator(Plexers.ATTR_SELECT, 1, 5, null));
		setIcon("priencod.gif");
		setFacingAttribute(StdAttr.FACING);

	}
	
	@Override
	public Bounds getOffsetBounds(AttributeSet attrs) {

		Direction dir = attrs.getValue(StdAttr.FACING);
		BitWidth select = attrs.getValue(Plexers.ATTR_SELECT);
		int inputs = 1 << select.getWidth();
		int offs = -5 * inputs;
		int len = 10 * inputs + 10;
		if (dir == Direction.NORTH) {
			return Bounds.create(offs,   0, len, 40);
		} else if (dir == Direction.SOUTH) {
			return Bounds.create(offs, -40, len, 40);
		} else if (dir == Direction.WEST) {
			return Bounds.create(  0, offs, 40, len);
		} else { // dir == Direction.EAST
			return Bounds.create(-40, offs, 40, len);
		}

	}

	@Override
	protected void configureNewInstance(Instance instance) {

		instance.addAttributeListener();
		updatePorts(instance);

	}
	
	@Override
	protected void instanceAttributeChanged(Instance instance, Attribute<?> attr) {

		if (attr == StdAttr.FACING || attr == Plexers.ATTR_SELECT) {
			instance.recomputeBounds();
			updatePorts(instance);
		} else if (attr == Plexers.ATTR_SELECT) {
			updatePorts(instance);
		} else if (attr == Plexers.ATTR_DISABLED) {
			instance.fireInvalidated();
		}

	}

	private void updatePorts(Instance instance) {

		Object dir = instance.getAttributeValue(StdAttr.FACING);
		BitWidth select = instance.getAttributeValue(Plexers.ATTR_SELECT);
		int n = 1 << select.getWidth();
		Port[] ps = new Port[n + 4];
		if (dir == Direction.NORTH || dir == Direction.SOUTH) {
			int x = -5 * n + 10;
			int y = dir == Direction.NORTH ? 40 : -40;
			for (int i = 0; i < n; i++) {
				ps[i] = new Port(x + 10 * i, y, Port.INPUT, 1);
			}
			ps[n + OUT] = new Port(0, 0, Port.OUTPUT, select.getWidth());
			ps[n + EN_IN] = new Port(x + 10 * n, y / 2, Port.INPUT, 1);
			ps[n + EN_OUT] = new Port(x - 10, y / 2, Port.OUTPUT, 1);
			ps[n + GS] = new Port(10, 0, Port.OUTPUT, 1);
		} else {
			int x = dir == Direction.EAST ? -40 : 40;
			int y = -5 * n + 10;
			for (int i = 0; i < n; i++) {
				ps[i] = new Port(x, y + 10 * i, Port.INPUT, 1);
			}
			ps[n + OUT] = new Port(0, 0, Port.OUTPUT, select.getWidth());
			ps[n + EN_IN] = new Port(x / 2, y + 10 * n, Port.INPUT, 1);
			ps[n + EN_OUT] = new Port(x / 2, y - 10, Port.OUTPUT, 1);
			ps[n + GS] = new Port(0, 10, Port.OUTPUT, 1);
		}

		for (int i = 0; i < n; i++) {
			ps[i].setToolTip(LC.createComplexStringBinding("priorityEncoderInTip", "" + i));
		}
		ps[n + OUT].setToolTip(LC.createStringBinding("priorityEncoderOutTip"));
		ps[n + EN_IN].setToolTip(LC.createStringBinding("priorityEncoderEnableInTip"));
		ps[n + EN_OUT].setToolTip(LC.createStringBinding("priorityEncoderEnableOutTip"));
		ps[n + GS].setToolTip(LC.createStringBinding("priorityEncoderGroupSignalTip"));

		instance.setPorts(ps);

	}

	@Override
	public void propagate(InstanceState state) {

		BitWidth select = state.getAttributeValue(Plexers.ATTR_SELECT);
		int n = 1 << select.getWidth();
		boolean enabled = state.getPortValue(n + EN_IN) != Value.FALSE;
		
		int out = -1;
		Value outDefault;
		if (enabled) {
			outDefault = Value.createUnknown(select);
			for (int i = n - 1; i >= 0; i--) {
				if (state.getPortValue(i) == Value.TRUE) {
					out = i;
					break;
				}
			}
		} else {
			Object opt = state.getAttributeValue(Plexers.ATTR_DISABLED);
			Value base = opt == Plexers.DISABLED_ZERO ? Value.FALSE : Value.UNKNOWN;
			outDefault = Value.repeat(base, select.getWidth());
		}
		if (out < 0) {
			state.setPort(n + OUT, outDefault, Plexers.DELAY);
			state.setPort(n + EN_OUT, enabled ? Value.TRUE : Value.FALSE, Plexers.DELAY);
			state.setPort(n + GS, Value.FALSE, Plexers.DELAY);
		} else {
			state.setPort(n + OUT, Value.createKnown(select, out), Plexers.DELAY);
			state.setPort(n + EN_OUT, Value.FALSE, Plexers.DELAY);
			state.setPort(n + GS, Value.TRUE, Plexers.DELAY);
		}

	}

	@Override
	public void paintInstance(InstancePainter painter) {

		Graphics g = painter.getGraphics();
		Direction facing = painter.getAttributeValue(StdAttr.FACING);

		painter.drawBounds();
		Bounds bds = painter.getBounds();
		g.setColor(Color.GRAY);
		int x0;
		int y0;
		int halign;
		if (facing == Direction.WEST) {
			x0 = bds.getX() + bds.getWidth() - 3;
			y0 = bds.getY() + 15;
			halign = GraphicsUtil.H_RIGHT;
		} else if (facing == Direction.NORTH) {
			x0 = bds.getX() + 10;
			y0 = bds.getY() + bds.getHeight() - 2;
			halign = GraphicsUtil.H_CENTER;
		} else if (facing == Direction.SOUTH) {
			x0 = bds.getX() + 10;
			y0 = bds.getY() + 12;
			halign = GraphicsUtil.H_CENTER;
		} else {
			x0 = bds.getX() + 3;
			y0 = bds.getY() + 15;
			halign = GraphicsUtil.H_LEFT;
		}
		GraphicsUtil.drawText(g, "0", x0, y0, halign, GraphicsUtil.V_BASELINE);
		g.setColor(Color.BLACK);
		GraphicsUtil.drawCenteredText(g, "Pri",
				bds.getX() + bds.getWidth() / 2,
				bds.getY() + bds.getHeight() / 2);
		painter.drawPorts();

		g.toDefault();

	}


	@Override
	public boolean hasThreeStateDrivers(AttributeSet attrs) {
		return (attrs.getValue(Plexers.ATTR_DISABLED) == Plexers.DISABLED_FLOATING);
	}

}

