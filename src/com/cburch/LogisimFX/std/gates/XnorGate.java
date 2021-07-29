/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.std.gates;

import com.cburch.LogisimFX.analyze.model.Expression;
import com.cburch.LogisimFX.analyze.model.Expressions;
import com.cburch.LogisimFX.data.AttributeSet;
import com.cburch.LogisimFX.data.Value;
import com.cburch.LogisimFX.instance.Instance;
import com.cburch.LogisimFX.instance.InstancePainter;
import com.cburch.LogisimFX.instance.InstanceState;
import com.cburch.LogisimFX.newgui.MainFrame.Graphics;
import com.cburch.LogisimFX.std.LC;
import com.cburch.LogisimFX.tools.WireRepairData;
import com.cburch.LogisimFX.util.GraphicsUtil;

import javafx.scene.canvas.GraphicsContext;

class XnorGate extends AbstractGate {

	public static XnorGate FACTORY = new XnorGate();

	private XnorGate() {

		super("XNOR Gate", LC.createStringBinding("xnorGateComponent"), true);
		setNegateOutput(true);
		setAdditionalWidth(10);
		setIconNames("xnorGate.gif", "xnorGateRect.gif", "dinXnorGate.gif");
		setPaintInputLines(true);

	}

	@Override
	protected String getRectangularLabel(AttributeSet attrs) {
		return XorGate.FACTORY.getRectangularLabel(attrs);
	}

	@Override
	protected void paintShape(InstancePainter painter, int width, int height) {

		PainterShaped.paintXor(painter, width, height);

	}

	@Override
	protected void paintDinShape(InstancePainter painter, int width, int height,
                                 int inputs) {

		PainterDin.paintXnor(painter, width, height, false);

	}

	@Override
	protected Value computeOutput(Value[] inputs, int numInputs, InstanceState state) {

		Object behavior = state.getAttributeValue(GateAttributes.ATTR_XOR);
		if (behavior == GateAttributes.XOR_ODD) {
			return GateFunctions.computeOddParity(inputs, numInputs).not();
		} else {
			return GateFunctions.computeExactlyOne(inputs, numInputs).not();
		}

	}

	@Override
	protected boolean shouldRepairWire(Instance instance, WireRepairData data) {

		return !data.getPoint().equals(instance.getLocation());

	}

	@Override
	protected Expression computeExpression(Expression[] inputs, int numInputs) {

		return Expressions.not(XorGate.xorExpression(inputs, numInputs));

	}

	@Override
	protected Value getIdentity() { return Value.FALSE; }
}
