/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.std.gates;

import com.cburch.LogisimFX.analyze.model.Expression;
import com.cburch.LogisimFX.analyze.model.Expressions;
import com.cburch.LogisimFX.data.Value;
import com.cburch.LogisimFX.instance.InstancePainter;
import com.cburch.LogisimFX.instance.InstanceState;
import com.cburch.LogisimFX.newgui.MainFrame.Graphics;
import com.cburch.LogisimFX.std.LC;
import com.cburch.LogisimFX.util.GraphicsUtil;

import java.awt.*;

class NandGate extends AbstractGate {

	public static NandGate FACTORY = new NandGate();

	private NandGate() {

		super("NAND Gate", LC.createStringBinding("nandGateComponent"));
		setNegateOutput(true);
		setRectangularLabel(AndGate.FACTORY.getRectangularLabel(null));
		setIconNames("nandGate.gif", "nandGateRect.gif", "dinNandGate.gif");

	}

	@Override
	protected void paintShape(InstancePainter painter, int width, int height) {

		PainterShaped.paintAnd(painter, width, height);

	}

	@Override
	protected void paintDinShape(InstancePainter painter, int width, int height,
                                 int inputs) {

		PainterDin.paintAnd(painter, width, height, true);

	}

	@Override
	protected Value computeOutput(Value[] inputs, int numInputs,
                                  InstanceState state) {

		return GateFunctions.computeAnd(inputs, numInputs).not();

	}

	@Override
	protected Expression computeExpression(Expression[] inputs, int numInputs) {

		Expression ret = inputs[0];
		for (int i = 1; i < numInputs; i++) {
			ret = Expressions.and(ret, inputs[i]);
		}
		return Expressions.not(ret);

	}

	@Override
	protected Value getIdentity() { return Value.TRUE; }

}
