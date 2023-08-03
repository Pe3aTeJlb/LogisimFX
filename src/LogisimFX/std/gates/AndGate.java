/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.gates;

import LogisimFX.data.Value;
import LogisimFX.fpga.hdlgenerator.Hdl;
import LogisimFX.instance.InstancePainter;
import LogisimFX.instance.InstanceState;
import LogisimFX.newgui.AnalyzeFrame.Expression;
import LogisimFX.newgui.AnalyzeFrame.Expressions;
import LogisimFX.std.LC;
import LogisimFX.util.LineBuffer;

class AndGate extends AbstractGate {

	public static AndGate FACTORY = new AndGate();

	private static class AndGateHdlGeneratorFactory extends AbstractGateHdlGenerator {
		@Override
		public boolean getFloatingValue(boolean isInverted) {
			return isInverted;
		}

		@Override
		public LineBuffer getLogicFunction(int nrOfInputs, int bitwidth, boolean isOneHot) {
			final var contents = LineBuffer.getHdlBuffer();
			var oneLine = new StringBuilder();
			oneLine.append(Hdl.assignPreamble()).append("result").append(Hdl.assignOperator());
			final var tabWidth = oneLine.length();
			var first = true;
			for (int i = 0; i < nrOfInputs; i++) {
				if (!first) {
					oneLine.append(Hdl.andOperator());
					contents.add(oneLine.toString());
					oneLine.setLength(0);
					oneLine.append(" ".repeat(tabWidth));
				} else {
					first = false;
				}
				oneLine.append(String.format("s_realInput%d", i + 1));
			}
			oneLine.append(";");
			contents.add(oneLine.toString());
			return contents;
		}
	}

	private AndGate() {

		super("AND Gate", LC.createStringBinding("andGateComponent"), new AndGateHdlGeneratorFactory());
		setRectangularLabel("&");
		setIconNames("andGate.gif", "andGateRect.gif", "dinAndGate.gif");

	}

	@Override
	protected void paintShape(InstancePainter painter, int width, int height) {
		PainterShaped.paintAnd(painter, width, height);
	}

	@Override
	protected void paintDinShape(InstancePainter painter, int width, int height, int inputs) {
		PainterDin.paintAnd(painter, width, height, false);
	}

	@Override
	protected Value computeOutput(Value[] inputs, int numInputs, InstanceState state) {
		return GateFunctions.computeAnd(inputs, numInputs);
	}

	@Override
	protected Expression computeExpression(Expression[] inputs, int numInputs) {

		Expression ret = inputs[0];
		for (int i = 1; i < numInputs; i++) {
			ret = Expressions.and(ret, inputs[i]);
		}
		return ret;

	}

	@Override
	protected Value getIdentity() { return Value.TRUE; }

}
