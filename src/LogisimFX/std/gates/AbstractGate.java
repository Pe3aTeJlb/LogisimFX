/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.gates;

import java.util.Map;

import LogisimFX.IconsManager;
import LogisimFX.comp.TextField;
import LogisimFX.data.*;
import LogisimFX.fpga.designrulecheck.CorrectLabel;
import LogisimFX.fpga.hdlgenerator.HdlGeneratorFactory;
import LogisimFX.instance.*;
import LogisimFX.newgui.AnalyzeFrame.Expression;
import LogisimFX.newgui.AnalyzeFrame.Expressions;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;
import LogisimFX.tools.WireRepair;
import LogisimFX.tools.WireRepairData;
import LogisimFX.tools.key.BitWidthConfigurator;
import LogisimFX.tools.key.IntegerConfigurator;
import LogisimFX.tools.key.JoinedConfigurator;
import LogisimFX.LogisimVersion;
import LogisimFX.circuit.ExpressionComputer;
import LogisimFX.file.Options;
import LogisimFX.prefs.AppPreferences;

import javafx.beans.binding.StringBinding;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;

abstract class AbstractGate extends InstanceFactory {

	private String[] iconNames = new String[3];
	private ImageView[] icons = new ImageView[3];
	private int bonusWidth = 0;
	private boolean negateOutput = false;
	private boolean isXor = false;
	private String rectLabel = "";
	private boolean paintInputLines;

	protected AbstractGate(String name, StringBinding desc, HdlGeneratorFactory generator) {
		this(name, desc, false, generator);
	}
	
	protected AbstractGate(String name, StringBinding desc, boolean isXor, HdlGeneratorFactory generator) {

		super(name, desc, generator);
		this.isXor = isXor;
		setFacingAttribute(StdAttr.FACING);
		setKeyConfigurator(JoinedConfigurator.create(
			new IntegerConfigurator(GateAttributes.ATTR_INPUTS, 2,
					GateAttributes.MAX_INPUTS, null),
			new BitWidthConfigurator(StdAttr.WIDTH)));

	}


	@Override
	public AttributeSet createAttributeSet() {
		return new GateAttributes(isXor);
	}

	@Override
	public Object getDefaultAttributeValue(Attribute<?> attr, LogisimVersion ver) {

		if (attr instanceof NegateAttribute) {
			return Boolean.FALSE;
		} else {
			return super.getDefaultAttributeValue(attr, ver);
		}

	}

	@Override
	public Bounds getOffsetBounds(AttributeSet attrsBase) {

		GateAttributes attrs = (GateAttributes) attrsBase;
		Direction facing = attrs.facing;
		int size = ((Integer) attrs.size.getValue()).intValue();
		int inputs = attrs.inputs;
		if (inputs % 2 == 0) {
			inputs++;
		}
		int negated = attrs.negated;

		int width = size + bonusWidth + (negateOutput ? 10 : 0);
		if (negated != 0) {
			width += 10;
		}
		int height = Math.max(10 * inputs, size);
		if (facing == Direction.SOUTH) {
			return Bounds.create(-height / 2, -width, height, width);
		} else if (facing == Direction.NORTH) {
			return Bounds.create(-height / 2, 0, height, width);
		} else if (facing == Direction.WEST) {
			return Bounds.create(0, -height / 2, width, height);
		} else {
			return Bounds.create(-width, -height / 2, width, height);
		}

	}

	@Override
	public boolean contains(Location loc, AttributeSet attrsBase) {

		GateAttributes attrs = (GateAttributes) attrsBase;
		if (super.contains(loc, attrs)) {
			if (attrs.negated == 0) {
				return true;
			} else {
				Direction facing = attrs.facing;
				Bounds bds = getOffsetBounds(attrsBase);
				int delt;
				if (facing == Direction.NORTH) {
					delt = loc.getY() - (bds.getY() + bds.getHeight());
				} else if (facing == Direction.SOUTH) {
					delt = loc.getY() - bds.getY();
				} else if (facing == Direction.WEST) {
					delt = loc.getX() - (bds.getX() + bds.getHeight());
				} else {
					delt = loc.getX() - bds.getX();
				}
				if (Math.abs(delt) > 5) {
					return true;
				} else {
					int inputs = attrs.inputs;
					for (int i = 1; i <= inputs; i++) {
						Location offs = getInputOffset(attrs, i);
						if (loc.manhattanDistanceTo(offs) <= 5) return true;
					}
					return false;
				}
			}
		} else {
			return false;
		}

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

		paintBase(painter);
		if (!painter.isPrintView() || painter.getGateShape() == AppPreferences.SHAPE_RECTANGULAR) {
			painter.drawPorts();
		}

		painter.getGraphics().toDefault();

	}

	private void paintBase(InstancePainter painter) {

		GateAttributes attrs = (GateAttributes) painter.getAttributeSet();
		Direction facing = attrs.facing;
		int inputs = attrs.inputs;
		int negated = attrs.negated;

		Object shape = painter.getGateShape();
		Location loc = painter.getLocation();
		Bounds bds = painter.getOffsetBounds();
		int width = bds.getWidth();
		int height = bds.getHeight();
		if (facing == Direction.NORTH || facing == Direction.SOUTH) {
			int t = width; width = height; height = t;
		}
		if (negated != 0) {
			width -= 10;
		}

		Graphics g = painter.getGraphics();
		Paint baseColor = g.getPaint();
		if (shape == AppPreferences.SHAPE_SHAPED && paintInputLines) {
			PainterShaped.paintInputLines(painter, this);
		} else if (negated != 0) {
			for (int i = 0; i < inputs; i++) {
				int negatedBit = (negated >> i) & 1;
				if (negatedBit == 1) {
					Location in = getInputOffset(attrs, i);
					Location cen = in.translate(facing, 5);
					painter.drawDongle(loc.getX() + cen.getX(),
							loc.getY() + cen.getY());
				}
			}
		}

		g.setColor(baseColor);
		g.c.translate(loc.getX(), loc.getY());
		double rotate = 0.0;
		if (facing != Direction.EAST) {
			rotate = -facing.toDegrees();
			g.c.rotate(rotate);
			//Graphics2D g2 = (Graphics2D) g;
			//g2.rotate(rotate);
		}

		if (shape == AppPreferences.SHAPE_RECTANGULAR) {
			paintRectangular(painter, width, height);
		} else if (shape == AppPreferences.SHAPE_DIN40700) {
			paintDinShape(painter, width, height, inputs);
		} else { // SHAPE_SHAPED
			if (negateOutput) {
				g.c.translate(-10, 0);
				paintShape(painter, width - 10, height);
				painter.drawDongle(5, 0);
				g.c.translate(10, 0);
			} else {
				paintShape(painter, width, height);
			}
		}

		if (rotate != 0.0) {
			g.c.rotate(-rotate);
		}
		g.c.translate(-loc.getX(), -loc.getY());

		painter.drawLabel();

		g.toDefault();

	}

	protected void setIconNames(String all) {
		setIconNames(all, all, all);
	}

	protected void setIconNames(String shaped, String rect, String din) {

		iconNames[0] = shaped;
		iconNames[1] = rect;
		iconNames[2] = din;

	}

	@Override
	public ImageView getIcon() {

		if (AppPreferences.GATE_SHAPE.get().equals(AppPreferences.SHAPE_RECTANGULAR)) {
			ImageView iconRect = getIconRectangular();
			if (iconRect != null) {
				return iconRect;
			}
		} else if (AppPreferences.GATE_SHAPE.get().equals(AppPreferences.SHAPE_DIN40700)) {
			ImageView iconDin = getIconDin40700();
			if (iconDin != null) {
				return iconDin;
			}
		} else {
			ImageView iconShaped = getIconShaped();
			if (iconShaped != null) {
				return iconShaped;
			}
		}
		return IconsManager.getIcon(iconNames[0]);

	}

	private ImageView getIcon(int type) {

		ImageView ret = icons[type];
		if (ret != null) {
			return ret;
		} else {
			String iconName = iconNames[type];
			if (iconName == null) {
				return null;
			} else {
				ret = IconsManager.getIcon(iconName);
				if (ret == null) {
					iconNames[type] = null;
				} else {
					icons[type] = ret;
				}
				return ret;
			}
		}

	}

	private ImageView getIconShaped() {
		return getIcon(0);
	}

	private ImageView getIconRectangular() {
		return getIcon(1);
	}

	private ImageView getIconDin40700() {
		return getIcon(2);
	}

	protected void setPaintInputLines(boolean value) {
		paintInputLines = value;
	}

	protected void setAdditionalWidth(int value) {
		bonusWidth = value;
	}

	protected void setNegateOutput(boolean value) {
		negateOutput = value;
	}

	protected void setRectangularLabel(String value) {
		rectLabel = value;
	}

	protected String getRectangularLabel(AttributeSet attrs) {
		return rectLabel;
	}

	//
	// protected methods intended to be overridden
	//
	protected abstract Value getIdentity();

	protected abstract void paintShape(InstancePainter painter,
                                       int width, int height);

	protected void paintRectangular(InstancePainter painter,
                                    int width, int height) {

		int don = negateOutput ? 10 : 0;
		AttributeSet attrs = painter.getAttributeSet();
		painter.drawRectangle(-width, -height / 2, width - don, height,
				getRectangularLabel(attrs));
		if (negateOutput) {
			painter.drawDongle(-5, 0);
		}

	}

	protected abstract void paintDinShape(InstancePainter painter,
                                          int width, int height, int inputs);

	protected abstract Value computeOutput(Value[] inputs, int numInputs,
                                           InstanceState state);

	protected abstract Expression computeExpression(Expression[] inputs,
                                                    int numInputs);

	protected boolean shouldRepairWire(Instance instance, WireRepairData data) {
		return false;
	}

	//
	// methods for instances
	//
	@Override
	protected void configureNewInstance(Instance instance) {

		instance.addAttributeListener();
		computePorts(instance);
		computeLabel(instance);

	}

	@Override
	protected void instanceAttributeChanged(Instance instance, Attribute<?> attr) {

		if (attr == GateAttributes.ATTR_SIZE || attr == StdAttr.FACING) {
			instance.recomputeBounds();
			computePorts(instance);
			computeLabel(instance);
		} else if (attr == GateAttributes.ATTR_INPUTS
				|| attr instanceof NegateAttribute) {
			instance.recomputeBounds();
			computePorts(instance);
		} else if (attr == GateAttributes.ATTR_XOR) {
			instance.fireInvalidated();
		}

	}

	private void computeLabel(Instance instance) {

		GateAttributes attrs = (GateAttributes) instance.getAttributeSet();
		Direction facing = attrs.facing;
		int baseWidth = ((Integer) attrs.size.getValue()).intValue();

		int axis = baseWidth / 2 + (negateOutput ? 10 : 0);
		int perp = 0;
		if (AppPreferences.GATE_SHAPE.get().equals(AppPreferences.SHAPE_RECTANGULAR)) {
			perp += 6;
		}
		Location loc = instance.getLocation();
		int cx;
		int cy;
		if (facing == Direction.NORTH) {
			cx = loc.getX() + perp;
			cy = loc.getY() + axis;
		} else if (facing == Direction.SOUTH) {
			cx = loc.getX() - perp;
			cy = loc.getY() - axis;
		} else if (facing == Direction.WEST) {
			cx = loc.getX() + axis;
			cy = loc.getY() - perp;
		} else {
			cx = loc.getX() - axis;
			cy = loc.getY() + perp;
		}
		instance.setTextField(StdAttr.LABEL, StdAttr.LABEL_FONT, cx, cy,
				TextField.H_CENTER, TextField.V_CENTER);

	}

	void computePorts(Instance instance) {

		GateAttributes attrs = (GateAttributes) instance.getAttributeSet();
		int inputs = attrs.inputs;

		Port[] ports = new Port[inputs + 1];
		ports[0] = new Port(0, 0, Port.OUTPUT, StdAttr.WIDTH);
		for (int i = 0; i < inputs; i++) {
			Location offs = getInputOffset(attrs, i);
			ports[i + 1] = new Port(offs.getX(), offs.getY(), Port.INPUT, StdAttr.WIDTH);
		}
		instance.setPorts(ports);

	}

	@Override
	public void propagate(InstanceState state) {

		GateAttributes attrs = (GateAttributes) state.getAttributeSet();
		int inputCount = attrs.inputs;
		int negated = attrs.negated;
		AttributeSet opts = state.getProject().getOptions().getAttributeSet();
		boolean errorIfUndefined = opts.getValue(Options.ATTR_GATE_UNDEFINED)
									.equals(Options.GATE_UNDEFINED_ERROR);

		Value[] inputs = new Value[inputCount];
		int numInputs = 0;
		boolean error = false;
		for (int i = 1; i <= inputCount; i++) {
			if (state.isPortConnected(i)) {
				int negatedBit = (negated >> (i - 1)) & 1;
				if (negatedBit == 1) {
					inputs[numInputs] = state.getPortValue(i).not();
				} else {
					inputs[numInputs] = state.getPortValue(i);
				}
				numInputs++;
			} else {
				if (errorIfUndefined) {
					error = true;
				}
			}
		}
		Value out = null;
		if (numInputs == 0 || error) {
			out = Value.createError(attrs.width);
		} else {
			out = computeOutput(inputs, numInputs, state);
			out = pullOutput(out, attrs.out);
		}
		state.setPort(0, out, GateAttributes.DELAY);

	}

	static Value pullOutput(Value value, Object outType) {

		if (outType == GateAttributes.OUTPUT_01) {
			return value;
		} else {
			Value[] v = value.getAll();
			if (outType == GateAttributes.OUTPUT_0Z) {
				for (int i = 0; i < v.length; i++) {
					if (v[i] == Value.TRUE) v[i] = Value.UNKNOWN;
				}
			} else if (outType == GateAttributes.OUTPUT_Z1) {
				for (int i = 0; i < v.length; i++) {
					if (v[i] == Value.FALSE) v[i] = Value.UNKNOWN;
				}
			}
			return Value.create(v);
		}

	}

	@Override
	protected Object getInstanceFeature(final Instance instance, Object key) {

		if (key == WireRepair.class) {
			return new WireRepair() {
				public boolean shouldRepairWire(WireRepairData data) {
					return AbstractGate.this.shouldRepairWire(instance, data);
				}
			};
		}
		if (key == ExpressionComputer.class) {
			return new ExpressionComputer() {
				public void computeExpression(Map<Location, Expression> expressionMap) {
					GateAttributes attrs = (GateAttributes) instance.getAttributeSet();
					int inputCount = attrs.inputs;
					int negated = attrs.negated;

					Expression[] inputs = new Expression[inputCount];
					int numInputs = 0;
					for (int i = 1; i <= inputCount; i++) {
						Expression e = expressionMap.get(instance.getPortLocation(i));
						if (e != null) {
							int negatedBit = (negated >> (i - 1)) & 1;
							if (negatedBit == 1) {
								e = Expressions.not(e);
							}
							inputs[numInputs] = e;
							++numInputs;
						}
					}
					if (numInputs > 0) {
						Expression out = AbstractGate.this.computeExpression(inputs, numInputs);
						expressionMap.put(instance.getPortLocation(0), out);
					}
				}
			};
		}
		return super.getInstanceFeature(instance, key);

	}

	Location getInputOffset(GateAttributes attrs, int index) {

		int inputs = attrs.inputs;
		Direction facing = attrs.facing;
		int size = ((Integer) attrs.size.getValue()).intValue();
		int axisLength = size + bonusWidth + (negateOutput ? 10 : 0);
		int negated = attrs.negated;

		int skipStart;
		int skipDist;
		int skipLowerEven = 10;
		if (inputs <= 3) {
			if (size < 40) {
				skipStart = -5;
				skipDist = 10;
				skipLowerEven = 10;
			} else if (size < 60 || inputs <= 2) {
				skipStart = -10;
				skipDist = 20;
				skipLowerEven = 20;
			} else {
				skipStart = -15;
				skipDist = 30;
				skipLowerEven = 30;
			}
		} else if (inputs == 4 && size >= 60) {
			skipStart = -5; 
			skipDist = 20;
			skipLowerEven = 0;
		} else {
			skipStart = -5;
			skipDist = 10;
			skipLowerEven = 10;
		}
		
		int dy;
		if ((inputs & 1) == 1) {
			dy = skipStart * (inputs - 1) + skipDist * index;
		} else {
			dy = skipStart * inputs + skipDist * index;
			if (index >= inputs / 2) dy += skipLowerEven;
		}

		int dx = axisLength;
		int negatedBit = (negated >> index) & 1;
		if (negatedBit == 1) {
			dx += 10;
		}
		
		if (facing == Direction.NORTH) {
			return Location.create(dy, dx);
		} else if (facing == Direction.SOUTH) {
			return Location.create(dy, -dx);
		} else if (facing == Direction.WEST) {
			return Location.create(dx, dy);
		} else {
			return Location.create(-dx, dy);
		}

	}



	@Override
	public String getHDLName(AttributeSet attrs) {
		final var myAttrs = (GateAttributes) attrs;
		final var completeName = new StringBuilder();
		completeName.append(CorrectLabel.getCorrectLabel(this.getName()).toUpperCase());
		final var width = myAttrs.getValue(StdAttr.WIDTH);
		if (width.getWidth() > 1) completeName.append("_BUS");
		final var inputCount = myAttrs.getValue(GateAttributes.ATTR_INPUTS);
		if (inputCount > 2) {
			completeName.append("_").append(inputCount).append("_INPUTS");
		}
		if (myAttrs.containsAttribute(GateAttributes.ATTR_XOR)) {
			if (myAttrs.getValue(GateAttributes.ATTR_XOR).equals(GateAttributes.XOR_ONE)) {
				completeName.append("_ONEHOT");
			}
		}
		return completeName.toString();
	}

	@Override
	public boolean hasThreeStateDrivers(AttributeSet attrs) {
		return (attrs.containsAttribute(GateAttributes.ATTR_OUTPUT))
				? (attrs.getValue(GateAttributes.ATTR_OUTPUT) != GateAttributes.OUTPUT_01)
				: false;
	}

}
