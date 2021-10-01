/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package LogisimFX.std.wiring;

import LogisimFX.analyze.model.Expression;
import LogisimFX.analyze.model.Expressions;
import LogisimFX.data.*;
import LogisimFX.instance.*;
import LogisimFX.newgui.MainFrame.Canvas.Graphics;
import LogisimFX.std.LC;
import LogisimFX.tools.key.BitWidthConfigurator;
import LogisimFX.tools.key.JoinedConfigurator;
import LogisimFX.util.GraphicsUtil;
import LogisimFX.circuit.ExpressionComputer;

import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Constant extends InstanceFactory {

	public static final Attribute<Integer> ATTR_VALUE
		= Attributes.forHexInteger("value", LC.createStringBinding("constantValueAttr"));

	public static InstanceFactory FACTORY = new Constant();

	private static final Color BACKGROUND_COLOR = Color.color(0.902, 0.902, 0.902);

	private static final List<Attribute<?>> ATTRIBUTES
		= Arrays.asList(new Attribute<?>[] {
				StdAttr.FACING, StdAttr.WIDTH, ATTR_VALUE
		});
	
	private static class ConstantAttributes extends AbstractAttributeSet {

		private Direction facing = Direction.EAST;;
		private BitWidth width = BitWidth.ONE;
		private Value value = Value.TRUE;

		@Override
		protected void copyInto(AbstractAttributeSet destObj) {

			ConstantAttributes dest = (ConstantAttributes) destObj;
			dest.facing = this.facing;
			dest.width = this.width;
			dest.value = this.value;

		}

		@Override
		public List<Attribute<?>> getAttributes() {
			return ATTRIBUTES;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <V> V getValue(Attribute<V> attr) {

			if (attr == StdAttr.FACING) return (V) facing;
			if (attr == StdAttr.WIDTH) return (V) width;
			if (attr == ATTR_VALUE) return (V) Integer.valueOf(value.toIntValue());
			return null;

		}

		@Override
		public <V> void setValue(Attribute<V> attr, V value) {

			if (attr == StdAttr.FACING) {
				facing = (Direction) value;
			} else if (attr == StdAttr.WIDTH) {
				width = (BitWidth) value;
				this.value = this.value.extendWidth(width.getWidth(),
						this.value.get(this.value.getWidth() - 1));
			} else if (attr == ATTR_VALUE) {
				int val = ((Integer) value).intValue();
				this.value = Value.createKnown(width, val);
			} else {
				throw new IllegalArgumentException("unknown attribute " + attr);
			}
			fireAttributeValueChanged(attr, value);

		}

	}

	private static class ConstantExpression implements ExpressionComputer {

		private Instance instance;
		
		public ConstantExpression(Instance instance) {
			this.instance = instance;
		}
		
		public void computeExpression(Map<Location, Expression> expressionMap) {
			AttributeSet attrs = instance.getAttributeSet();
			int intValue = attrs.getValue(ATTR_VALUE).intValue();

			expressionMap.put(instance.getLocation(),
					Expressions.constant(intValue));
		}

	}
	
	public Constant() {

		super("Constant", LC.createStringBinding("constantComponent"));
		setFacingAttribute(StdAttr.FACING);
		setKeyConfigurator(JoinedConfigurator.create(
				new ConstantConfigurator(),
				new BitWidthConfigurator(StdAttr.WIDTH)));
		setIcon("constant.gif");

	}

	@Override
	public AttributeSet createAttributeSet() {
		return new ConstantAttributes();
	}

	@Override
	protected void configureNewInstance(Instance instance) {

		instance.addAttributeListener();
		updatePorts(instance);

	}
	
	private void updatePorts(Instance instance) {

		Port[] ps = { new Port(0, 0, Port.OUTPUT, StdAttr.WIDTH) };
		instance.setPorts(ps);

	}

	@Override
	protected void instanceAttributeChanged(Instance instance, Attribute<?> attr) {

		if (attr == StdAttr.WIDTH) {
			instance.recomputeBounds();
			updatePorts(instance);
		} else if (attr == StdAttr.FACING) {
			instance.recomputeBounds();
		} else if (attr == ATTR_VALUE) {
			instance.fireInvalidated();
		}

	}
	
	@Override
	protected Object getInstanceFeature(Instance instance, Object key) {

		if (key == ExpressionComputer.class) return new ConstantExpression(instance);
		return super.getInstanceFeature(instance, key);

	}

	@Override
	public void propagate(InstanceState state) {

		BitWidth width = state.getAttributeValue(StdAttr.WIDTH);
		int value = state.getAttributeValue(ATTR_VALUE).intValue();
		state.setPort(0, Value.createKnown(width, value), 1);

	}

	@Override
	public Bounds getOffsetBounds(AttributeSet attrs) {

		Direction facing = attrs.getValue(StdAttr.FACING);
		BitWidth width = attrs.getValue(StdAttr.WIDTH);
		int chars = (width.getWidth() + 3) / 4;

		Bounds ret = null;
		if (facing == Direction.EAST) {
			switch (chars) {
			case 1: ret = Bounds.create(-16, -8, 16, 16); break;
			case 2: ret = Bounds.create(-16, -8, 16, 16); break;
			case 3: ret = Bounds.create(-26, -8, 26, 16); break;
			case 4: ret = Bounds.create(-36, -8, 36, 16); break;
			case 5: ret = Bounds.create(-46, -8, 46, 16); break;
			case 6: ret = Bounds.create(-56, -8, 56, 16); break;
			case 7: ret = Bounds.create(-66, -8, 66, 16); break;
			case 8: ret = Bounds.create(-76, -8, 76, 16); break;
			}
		} else if (facing == Direction.WEST) {
			switch (chars) {
			case 1: ret = Bounds.create(  0, -8, 16, 16); break;
			case 2: ret = Bounds.create(  0, -8, 16, 16); break;
			case 3: ret = Bounds.create(  0, -8, 26, 16); break;
			case 4: ret = Bounds.create(  0, -8, 36, 16); break;
			case 5: ret = Bounds.create(  0, -8, 46, 16); break;
			case 6: ret = Bounds.create(  0, -8, 56, 16); break;
			case 7: ret = Bounds.create(  0, -8, 66, 16); break;
			case 8: ret = Bounds.create(  0, -8, 76, 16); break;
			}
		} else if (facing == Direction.SOUTH) {
			switch (chars) {
			case 1: ret = Bounds.create(-8, -16, 16, 16); break;
			case 2: ret = Bounds.create(-8, -16, 16, 16); break;
			case 3: ret = Bounds.create(-13, -16, 26, 16); break;
			case 4: ret = Bounds.create(-18, -16, 36, 16); break;
			case 5: ret = Bounds.create(-23, -16, 46, 16); break;
			case 6: ret = Bounds.create(-28, -16, 56, 16); break;
			case 7: ret = Bounds.create(-33, -16, 66, 16); break;
			case 8: ret = Bounds.create(-38, -16, 76, 16); break;
			}
		} else if (facing == Direction.NORTH) {
			switch (chars) {
			case 1: ret = Bounds.create(-8,   0, 16, 16); break;
			case 2: ret = Bounds.create(-8,   0, 16, 16); break;
			case 3: ret = Bounds.create(-13,   0, 26, 16); break;
			case 4: ret = Bounds.create(-18,   0, 36, 16); break;
			case 5: ret = Bounds.create(-23,   0, 46, 16); break;
			case 6: ret = Bounds.create(-28,   0, 56, 16); break;
			case 7: ret = Bounds.create(-33,   0, 66, 16); break;
			case 8: ret = Bounds.create(-38,   0, 76, 16); break;
			}
		}
		if (ret == null) {
			throw new IllegalArgumentException("unrecognized arguments " + facing + " " + width);
		}
		return ret;

	}

	//
	// painting methods
	//

	@Override
	public void paintGhost(InstancePainter painter) {

		int v = painter.getAttributeValue(ATTR_VALUE).intValue();
		String vStr = Integer.toHexString(v);
		Bounds bds = getOffsetBounds(painter.getAttributeSet());

		Graphics g = painter.getGraphics();
		g.setLineWidth(2);
		g.c.fillOval(-2, -2, 5, 5);
		GraphicsUtil.drawCenteredText(g, vStr, bds.getX() + bds.getWidth() / 2,
				bds.getY() + bds.getHeight() / 2);

	}
	
	@Override
	public void paintInstance(InstancePainter painter) {

		Bounds bds = painter.getOffsetBounds();
		BitWidth width = painter.getAttributeValue(StdAttr.WIDTH);
		int intValue = painter.getAttributeValue(ATTR_VALUE).intValue();
		Value v = Value.createKnown(width, intValue);
		Location loc = painter.getLocation();
		int x = loc.getX();
		int y = loc.getY();

		Graphics g = painter.getGraphics();
		if (painter.shouldDrawColor()) {
			g.setColor(BACKGROUND_COLOR);
			g.c.fillRect(x + bds.getX(), y + bds.getY(), bds.getWidth(), bds.getHeight());
		}
		if (v.getWidth() == 1) {
			if (painter.shouldDrawColor()){
				g.setColor(v.getColor());
			}
			GraphicsUtil.drawCenteredText(g, v.toString(),
				x + bds.getX() + bds.getWidth() / 2,
				y + bds.getY() + bds.getHeight() / 2 - 2);
		} else {
			g.setColor(Color.BLACK);
			GraphicsUtil.drawCenteredText(g, v.toHexString(),
				x + bds.getX() + bds.getWidth() / 2,
				y + bds.getY() + bds.getHeight() / 2 - 2);
		}
		painter.drawPorts();

		g.toDefault();

	}

	//TODO: Allow editing of value via text tool/attribute table
}
