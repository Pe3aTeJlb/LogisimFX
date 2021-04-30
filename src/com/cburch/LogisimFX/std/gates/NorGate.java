/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.std.gates;

import java.awt.Graphics;

import com.cburch.LogisimFX.analyze.model.Expression;
import com.cburch.LogisimFX.analyze.model.Expressions;
import com.cburch.LogisimFX.data.Value;
import com.cburch.LogisimFX.instance.Instance;
import com.cburch.LogisimFX.instance.InstancePainter;
import com.cburch.LogisimFX.instance.InstanceState;
import com.cburch.LogisimFX.tools.WireRepairData;
import com.cburch.LogisimFX.util.GraphicsUtil;

class NorGate extends AbstractGate {
	public static NorGate FACTORY = new NorGate();

	private NorGate() {
		super("NOR Gate", Strings.getter("norGateComponent"));
		setNegateOutput(true);
		setRectangularLabel(OrGate.FACTORY.getRectangularLabel(null));
		setIconNames("norGate.gif", "norGateRect.gif", "dinNorGate.gif");
		setPaintInputLines(true);
	}

	@Override
	public void paintIconShaped(InstancePainter painter) {
		Graphics g = painter.getGraphics();
		GraphicsUtil.drawCenteredArc(g,   0, -5, 22, -90,  53);
		GraphicsUtil.drawCenteredArc(g,   0, 23, 22,  90, -53);
		GraphicsUtil.drawCenteredArc(g, -12,  9, 16, -30, 60);
		g.drawOval(16, 8, 4, 4);
	}

	@Override
	protected void paintShape(InstancePainter painter, int width, int height) {
		PainterShaped.paintOr(painter, width, height);
	}

	@Override
	protected void paintDinShape(InstancePainter painter, int width, int height,
                                 int inputs) {
		PainterDin.paintOr(painter, width, height, true);
	}

	@Override
	protected Value computeOutput(Value[] inputs, int numInputs,
                                  InstanceState state) {
		return GateFunctions.computeOr(inputs, numInputs).not();
	}

	@Override
	protected boolean shouldRepairWire(Instance instance, WireRepairData data) {
		return !data.getPoint().equals(instance.getLocation());
	}

	@Override
	protected Expression computeExpression(Expression[] inputs, int numInputs) {
		Expression ret = inputs[0];
		for (int i = 1; i < numInputs; i++) {
			ret = Expressions.or(ret, inputs[i]);
		}
		return Expressions.not(ret);
	}

	@Override
	protected Value getIdentity() { return Value.FALSE; }
}
