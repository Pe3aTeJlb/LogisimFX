/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.std.plexers;

import com.cburch.LogisimFX.data.*;
import com.cburch.LogisimFX.instance.*;
import com.cburch.LogisimFX.newgui.MainFrame.Graphics;
import com.cburch.LogisimFX.std.LC;
import com.cburch.LogisimFX.tools.key.BitWidthConfigurator;
import com.cburch.LogisimFX.tools.key.JoinedConfigurator;
import com.cburch.LogisimFX.util.GraphicsUtil;

import javafx.scene.paint.Color;

public class BitSelector extends InstanceFactory {

	public static final Attribute<BitWidth> GROUP_ATTR
		= Attributes.forBitWidth("group", LC.createStringBinding("bitSelectorGroupAttr"));

	public BitSelector() {

		super("BitSelector", LC.createStringBinding("bitSelectorComponent"));
		setAttributes(new Attribute[] {
				StdAttr.FACING, StdAttr.WIDTH, GROUP_ATTR
			}, new Object[] {
				Direction.EAST, BitWidth.create(8), BitWidth.ONE
			});
		setKeyConfigurator(JoinedConfigurator.create(
				new BitWidthConfigurator(GROUP_ATTR, 1, Value.MAX_WIDTH, null),
				new BitWidthConfigurator(StdAttr.WIDTH)));

		setIcon("bitSelector.gif");
		setFacingAttribute(StdAttr.FACING);

	}
	
	@Override
	public Bounds getOffsetBounds(AttributeSet attrs) {

		Direction facing = attrs.getValue(StdAttr.FACING);
		Bounds base = Bounds.create(-30, -15, 30, 30);
		return base.rotate(Direction.EAST, facing, 0, 0);

	}
	
	@Override
	protected void configureNewInstance(Instance instance) {

		instance.addAttributeListener();
		updatePorts(instance);

	}
	
	@Override
	protected void instanceAttributeChanged(Instance instance, Attribute<?> attr) {

		if (attr == StdAttr.FACING) {
			instance.recomputeBounds();
			updatePorts(instance);
		} else if (attr == StdAttr.WIDTH || attr == GROUP_ATTR) {
			updatePorts(instance);
		}

	}

	private void updatePorts(Instance instance) {

		Direction facing = instance.getAttributeValue(StdAttr.FACING);
		BitWidth data = instance.getAttributeValue(StdAttr.WIDTH);
		BitWidth group = instance.getAttributeValue(GROUP_ATTR);
		int groups = (data.getWidth() + group.getWidth() - 1) / group.getWidth() - 1;
		int selectBits = 1;
		if (groups > 0) {
			while (groups != 1) { groups >>= 1; selectBits++; }
		}
		BitWidth select = BitWidth.create(selectBits);

		Location inPt;
		Location selPt;
		if (facing == Direction.WEST) {
			inPt  = Location.create(30, 0);
			selPt = Location.create(10, 10);
		} else if (facing == Direction.NORTH) {
			inPt  = Location.create(  0, 30);
			selPt = Location.create(-10, 10);
		} else if (facing == Direction.SOUTH) {
			inPt  = Location.create(  0, -30);
			selPt = Location.create(-10, -10);
		} else {
			inPt  = Location.create(-30, 0);
			selPt = Location.create(-10, 10);
		}
		
		Port[] ps = new Port[3];
		ps[0] = new Port(0, 0, Port.OUTPUT, group.getWidth());
		ps[1] = new Port(inPt.getX(), inPt.getY(), Port.INPUT, data.getWidth());
		ps[2] = new Port(selPt.getX(), selPt.getY(), Port.INPUT, select.getWidth());
		ps[0].setToolTip(LC.createStringBinding("bitSelectorOutputTip"));
		ps[1].setToolTip(LC.createStringBinding("bitSelectorDataTip"));
		ps[2].setToolTip(LC.createStringBinding("bitSelectorSelectTip"));
		instance.setPorts(ps);

	}

	@Override
	public void propagate(InstanceState state) {

		Value data = state.getPort(1);
		Value select = state.getPort(2);
		BitWidth groupBits = state.getAttributeValue(GROUP_ATTR);
		Value group;
		if (!select.isFullyDefined()) {
			group = Value.createUnknown(groupBits);
		} else {
			int shift = select.toIntValue() * groupBits.getWidth();
			if (shift >= data.getWidth()) {
				group = Value.createKnown(groupBits, 0);
			} else if (groupBits.getWidth() == 1) {
				group = data.get(shift);
			} else {
				Value[] bits = new Value[groupBits.getWidth()];
				for (int i = 0; i < bits.length; i++) {
					if (shift + i >= data.getWidth()) {
						bits[i] = Value.FALSE;
					} else {
						bits[i] = data.get(shift + i);
					}
				}
				group = Value.create(bits);
			}
		}
		state.setPort(0, group, Plexers.DELAY);

	}

	@Override
	public void paintGhost(InstancePainter painter) {

		Plexers.drawTrapezoid(painter.getGraphics(), painter.getBounds(),
				painter.getAttributeValue(StdAttr.FACING), 9);

	}

	@Override
	public void paintInstance(InstancePainter painter) {

		Graphics g = painter.getGraphics();
		Direction facing = painter.getAttributeValue(StdAttr.FACING);

		Plexers.drawTrapezoid(g, painter.getBounds(), facing, 9);
		Bounds bds = painter.getBounds();
		g.setColor(Color.BLACK);
		GraphicsUtil.drawCenteredText(g, "Sel",
				bds.getX() + bds.getWidth() / 2,
				bds.getY() + bds.getHeight() / 2);
		painter.drawPorts();

		g.toDefault();

	}

}
