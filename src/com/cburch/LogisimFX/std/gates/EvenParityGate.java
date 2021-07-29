/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.std.gates;

import com.cburch.LogisimFX.analyze.model.Expression;
import com.cburch.LogisimFX.analyze.model.Expressions;
import com.cburch.LogisimFX.data.Value;
import com.cburch.LogisimFX.instance.InstancePainter;
import com.cburch.LogisimFX.instance.InstanceState;
import com.cburch.LogisimFX.std.LC;

class EvenParityGate extends AbstractGate {

	public static EvenParityGate FACTORY = new EvenParityGate();

	private EvenParityGate() {

		super("Even Parity", LC.createStringBinding("evenParityComponent"));
		setRectangularLabel("2k");
		setIconNames("parityEvenGate.gif");

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
