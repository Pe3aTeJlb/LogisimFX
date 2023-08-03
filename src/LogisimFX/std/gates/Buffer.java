/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.gates;

import java.util.Map;

import LogisimFX.data.*;
import LogisimFX.fpga.designrulecheck.CorrectLabel;
import LogisimFX.instance.*;
import LogisimFX.newgui.AnalyzeFrame.Expression;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.std.LC;
import LogisimFX.tools.key.BitWidthConfigurator;
import LogisimFX.circuit.ExpressionComputer;
import LogisimFX.file.Options;
import javafx.scene.paint.Color;

class Buffer extends InstanceFactory {

	public static InstanceFactory FACTORY = new Buffer();

	private Buffer() {

		super("Buffer", LC.createStringBinding("bufferComponent"), new AbstractBufferHdlGenerator(false));
		setAttributes(new Attribute[] {
				StdAttr.FPGA_SUPPORTED,
				StdAttr.FACING, StdAttr.WIDTH,
					GateAttributes.ATTR_OUTPUT, StdAttr.LABEL, StdAttr.LABEL_FONT },
				new Object[] { Boolean.FALSE, Direction.EAST, BitWidth.ONE,
					GateAttributes.OUTPUT_01, "", StdAttr.DEFAULT_LABEL_FONT });
		setIcon("bufferGate.gif");
		setFacingAttribute(StdAttr.FACING);
		setKeyConfigurator(new BitWidthConfigurator(StdAttr.WIDTH));
		setPorts(new Port[] {
				new Port(0, 0, Port.OUTPUT, StdAttr.WIDTH),
				new Port(0, -20, Port.INPUT, StdAttr.WIDTH),
		});

	}

	@Override
	public Bounds getOffsetBounds(AttributeSet attrs) {

		Direction facing = attrs.getValue(StdAttr.FACING);
		if (facing == Direction.SOUTH) return Bounds.create(-9, -20, 18, 20);
		if (facing == Direction.NORTH) return Bounds.create(-9, 0, 18, 20);
		if (facing == Direction.WEST) return Bounds.create(0, -9, 20, 18);
		return Bounds.create(-20, -9, 20, 18);

	}

	@Override
	public void propagate(InstanceState state) {

		Value in = state.getPortValue(1);
		in = Buffer.repair(state, in);
		state.setPort(0, in, GateAttributes.DELAY);

	}

	//
	// methods for instances
	//
	@Override
	protected void configureNewInstance(Instance instance) {

		configurePorts(instance);
		instance.addAttributeListener();
		NotGate.configureLabel(instance, false, null);

	}

	@Override
	protected void instanceAttributeChanged(Instance instance, Attribute<?> attr) {

		if (attr == StdAttr.FACING) {
			instance.recomputeBounds();
			configurePorts(instance);
			NotGate.configureLabel(instance, false, null);
		}

	}

	private void configurePorts(Instance instance) {

		Direction facing = instance.getAttributeValue(StdAttr.FACING);

		Port[] ports = new Port[2];
		ports[0] = new Port(0, 0, Port.OUTPUT, StdAttr.WIDTH);
		Location out = Location.create(0, 0).translate(facing, -20);
		ports[1] = new Port(out.getX(), out.getY(), Port.INPUT, StdAttr.WIDTH);
		instance.setPorts(ports);

	}

	@Override
	public Object getInstanceFeature(final Instance instance, Object key) {

		if (key == ExpressionComputer.class) {
			return new ExpressionComputer() {
				public void computeExpression(Map<Location, Expression> expressionMap) {
					Expression e = expressionMap.get(instance.getPortLocation(1));
					if (e != null) {
						expressionMap.put(instance.getPortLocation(0), e);
					}
				}
			};
		}
		return super.getInstanceFeature(instance, key);

	}

	//
	// painting methods
	//
	@Override
	public void paintGhost(InstancePainter painter) {
		paintBase(painter);
	}

	@Override
	public void paintInstance(InstancePainter painter) {

		Graphics g = painter.getGraphics();
		g.setColor(Color.BLACK);
		paintBase(painter);
		painter.drawPorts();
		painter.drawLabel();
		g.toDefault();

	}

	private void paintBase(InstancePainter painter) {

		Direction facing = painter.getAttributeValue(StdAttr.FACING);
		Location loc = painter.getLocation();
		int x = loc.getX();
		int y = loc.getY();
		Graphics g = painter.getGraphics();
		g.c.translate(x, y);
		double rotate = 0.0;
		if (facing != Direction.EAST) {
			rotate = -facing.toDegrees();
			g.c.rotate(rotate);
		}

		g.setLineWidth(2);
		double[] xp = new double[4];
		double[] yp = new double[4];
		xp[0] = 0;   yp[0] =  0;
		xp[1] = -19; yp[1] = -7;
		xp[2] = -19; yp[2] =  7;
		xp[3] = 0;   yp[3] =  0;
		g.c.strokePolyline(xp, yp, 4);

		if (rotate != 0.0) {
			g.c.rotate(-rotate);
		}
		g.c.translate(-x, -y);

		g.toDefault();

	}

	//
	// static methods - shared with other classes
	//
	static Value repair(InstanceState state, Value v) {

		AttributeSet opts = state.getProject().getOptions().getAttributeSet();
		Object onUndefined = opts.getValue(Options.ATTR_GATE_UNDEFINED);
		boolean errorIfUndefined = onUndefined.equals(Options.GATE_UNDEFINED_ERROR);
		Value repaired;
		if (errorIfUndefined) {
			int vw = v.getWidth();
			BitWidth w = state.getAttributeValue(StdAttr.WIDTH);
			int ww = w.getWidth();
			if (vw == ww && v.isFullyDefined()) return v;
			Value[] vs = new Value[w.getWidth()];
			for (int i = 0; i < vs.length; i++) {
				Value ini = i < vw ? v.get(i) : Value.ERROR;
				vs[i] = ini.isFullyDefined() ? ini : Value.ERROR;
			}
			repaired = Value.create(vs);
		} else {
			repaired = v;
		}

		Object outType = state.getAttributeValue(GateAttributes.ATTR_OUTPUT);
		return AbstractGate.pullOutput(repaired, outType);

	}



	@Override
	public String getHDLName(AttributeSet attrs) {
		final var completeName = new StringBuilder();
		completeName.append(CorrectLabel.getCorrectLabel(this.getName()).toUpperCase());
		completeName.append("_COMPONENT");
		final var width = attrs.getValue(StdAttr.WIDTH);
		if (width.getWidth() > 1) completeName.append("_BUS");
		return completeName.toString();
	}

	@Override
	public boolean hasThreeStateDrivers(AttributeSet attrs) {
		return attrs.containsAttribute(GateAttributes.ATTR_OUTPUT)
				? attrs.getValue(GateAttributes.ATTR_OUTPUT) != GateAttributes.OUTPUT_01
				: false;
	}

}
