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
import LogisimFX.util.LineBuffer;

class XnorGate extends AbstractGate {

	public static XnorGate FACTORY = new XnorGate();

	private static class XNorGateHdlGeneratorFactory extends AbstractGateHdlGenerator {
		@Override
		public LineBuffer getLogicFunction(int nrOfInputs, int bitwidth, boolean isOneHot) {
			return LineBuffer.getBuffer()
					.add(
							isOneHot
									? getOneHot(true, nrOfInputs, bitwidth > 1)
									: getParity(true, nrOfInputs, bitwidth > 1));
		}
	}

	private XnorGate() {

		super("XNOR Gate", LC.createStringBinding("xnorGateComponent"), true, new XNorGateHdlGeneratorFactory());
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
	protected void paintDinShape(InstancePainter painter, int width, int height, int inputs) {
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
	protected Value getIdentity() {
		return Value.FALSE;
	}
}
