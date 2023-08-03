/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.gates;

import LogisimFX.data.Value;
import LogisimFX.instance.InstancePainter;
import LogisimFX.instance.InstanceState;
import LogisimFX.newgui.AnalyzeFrame.Expression;
import LogisimFX.newgui.AnalyzeFrame.Expressions;
import LogisimFX.std.LC;
import LogisimFX.util.LineBuffer;

class EvenParityGate extends AbstractGate {

	private static class XNorGateHdlGeneratorFactory extends AbstractGateHdlGenerator {
		@Override
		public LineBuffer getLogicFunction(int nrOfInputs, int bitwidth, boolean isOneHot) {
			return LineBuffer.getBuffer().add(getParity(true, nrOfInputs, bitwidth > 1));
		}
	}

	public static EvenParityGate FACTORY = new EvenParityGate();

	private EvenParityGate() {

		super("Even Parity", LC.createStringBinding("evenParityComponent"), new XNorGateHdlGeneratorFactory());
		setRectangularLabel("2k");
		setIconNames("parityEvenGate.gif");

	}

	@Override
	protected void paintShape(InstancePainter painter, int width, int height) {
		paintRectangular(painter, width, height);
	}

	@Override
	protected void paintDinShape(InstancePainter painter, int width, int height, int inputs) {
		paintRectangular(painter, width, height);
	}

	@Override
	protected Value computeOutput(Value[] inputs, int numInputs, InstanceState state) {
		return GateFunctions.computeOddParity(inputs, numInputs).not();
	}

	@Override
	protected Expression computeExpression(Expression[] inputs, int numInputs) {

		Expression ret = inputs[0];
		for (int i = 1; i < numInputs; i++) {
			ret = Expressions.xor(ret, inputs[i]);
		}
		return Expressions.not(ret);

	}

	@Override
	protected Value getIdentity() { return Value.FALSE; }

}
