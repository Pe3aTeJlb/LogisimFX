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

class OddParityGate extends AbstractGate {

	public static OddParityGate FACTORY = new OddParityGate();

	private static class XorGateHdlGeneratorFactory extends AbstractGateHdlGenerator {
		@Override
		public LineBuffer getLogicFunction(int nrOfInputs, int bitwidth, boolean isOneHot) {
			return LineBuffer.getBuffer().add(getParity(false, nrOfInputs, bitwidth > 1));
		}
	}

	private OddParityGate() {

		super("Odd Parity", LC.createStringBinding("oddParityComponent"), new XorGateHdlGeneratorFactory());
		setRectangularLabel("2k+1");
		setIconNames("parityOddGate.gif");

	}

	@Override
	protected void paintShape(InstancePainter painter, int width, int height) {
		paintRectangular(painter, width, height);
	}

	@Override
	protected void paintDinShape(InstancePainter painter, int width, int height,
                                 int inputs) {

		paintRectangular(painter, width, height);

	}

	@Override
	protected Value computeOutput(Value[] inputs, int numInputs,
                                  InstanceState state) {

		return GateFunctions.computeOddParity(inputs, numInputs);

	}

	@Override
	protected Expression computeExpression(Expression[] inputs, int numInputs) {

		Expression ret = inputs[0];
		for (int i = 1; i < numInputs; i++) {
			ret = Expressions.xor(ret, inputs[i]);
		}

		return ret;

	}

	@Override
	protected Value getIdentity() { return Value.FALSE; }

}
