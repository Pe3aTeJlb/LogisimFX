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

class XorGate extends AbstractGate {

	public static XorGate FACTORY = new XorGate();

	private XorGate() {

		super("XOR Gate", LC.createStringBinding("xorGateComponent"), true);
		setAdditionalWidth(10);
		setIconNames("xorGate.gif", "xorGateRect.gif", "dinXorGate.gif");
		setPaintInputLines(true);

	}

	@Override
	public String getRectangularLabel(AttributeSet attrs) {

		if (attrs == null) return "";
		boolean isOdd = false;
		Object behavior = attrs.getValue(GateAttributes.ATTR_XOR);
		if (behavior == GateAttributes.XOR_ODD) {
			Object inputs = attrs.getValue(GateAttributes.ATTR_INPUTS);
			if (inputs == null || ((Integer) inputs).intValue() != 2) {
				isOdd = true;
			}
		}
		return isOdd ? "2k+1" : "=1";

	}

	@Override
	protected void paintShape(InstancePainter painter, int width, int height) {

		PainterShaped.paintXor(painter, width, height);

	}

	@Override
	protected void paintDinShape(InstancePainter painter, int width, int height,
                                 int inputs) {

		PainterDin.paintXor(painter, width, height, false);

	}

	@Override
	protected Value computeOutput(Value[] inputs, int numInputs,
                                  InstanceState state) {

		Object behavior = state.getAttributeValue(GateAttributes.ATTR_XOR);
		if (behavior == GateAttributes.XOR_ODD) {
			return GateFunctions.computeOddParity(inputs, numInputs);
		} else {
			return GateFunctions.computeExactlyOne(inputs, numInputs);
		}

	}

	@Override
	protected boolean shouldRepairWire(Instance instance, WireRepairData data) {

		return !data.getPoint().equals(instance.getLocation());

	}

	@Override
	protected Expression computeExpression(Expression[] inputs, int numInputs) {
		return xorExpression(inputs, numInputs);
	}

	@Override
	protected Value getIdentity() { return Value.FALSE; }
	
	protected static Expression xorExpression(Expression[] inputs, int numInputs) {

		if (numInputs > 2) {
			throw new UnsupportedOperationException("XorGate");
		}
		Expression ret = inputs[0];
		for (int i = 1; i < numInputs; i++) {
			ret = Expressions.xor(ret, inputs[i]);
		}
		return ret;

	}

}
