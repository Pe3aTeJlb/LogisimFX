/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.std.gates;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.cburch.LogisimFX.analyze.model.Expression;
import com.cburch.LogisimFX.analyze.model.Expressions;
import com.cburch.LogisimFX.data.Value;
import com.cburch.LogisimFX.instance.InstancePainter;
import com.cburch.LogisimFX.instance.InstanceState;
import com.cburch.LogisimFX.std.LC;
import com.cburch.LogisimFX.util.GraphicsUtil;

class OddParityGate extends AbstractGate {

	public static OddParityGate FACTORY = new OddParityGate();

	private OddParityGate() {

		super("Odd Parity", LC.createStringBinding("oddParityComponent"));
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
