/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.gates;

import LogisimFX.data.AttributeSet;
import LogisimFX.data.Value;
import LogisimFX.instance.Instance;
import LogisimFX.instance.InstancePainter;
import LogisimFX.instance.InstanceState;
import LogisimFX.newgui.AnalyzeFrame.Expression;
import LogisimFX.newgui.AnalyzeFrame.Expressions;
import LogisimFX.std.LC;
import LogisimFX.tools.WireRepairData;

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
