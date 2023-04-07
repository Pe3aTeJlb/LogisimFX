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

class NandGate extends AbstractGate {

	public static NandGate FACTORY = new NandGate();

	private static class NandGateHdlGeneratorFactory extends AbstractGateHdlGenerator {
		@Override
		public boolean getFloatingValue(boolean isInverted) {
			return isInverted;
		}

		@Override
		public LineBuffer getLogicFunction(int nrOfInputs, int bitwidth, boolean isOneHot) {
			final var contents = LineBuffer.getHdlBuffer();
			final var oneLine = new StringBuilder();
			oneLine
					.append(Hdl.assignPreamble())
					.append("result")
					.append(Hdl.assignOperator())
					.append(Hdl.notOperator())
					.append("(");
			final var tabWidth = oneLine.length();
			var first = true;
			for (var i = 0; i < nrOfInputs; i++) {
				if (!first) {
					oneLine.append(Hdl.andOperator());
					contents.add(oneLine.toString());
					oneLine.setLength(0);
					oneLine.append(" ".repeat(tabWidth));
				} else {
					first = false;
				}
				oneLine.append("s_realInput").append(i + 1);
			}
			oneLine.append(");");
			contents.add(oneLine.toString());
			return contents;
		}
	}

	private NandGate() {

		super("NAND Gate", LC.createStringBinding("nandGateComponent"), new NandGateHdlGeneratorFactory());
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
