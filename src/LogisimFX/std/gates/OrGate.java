/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.std.gates;

import LogisimFX.data.Value;
import LogisimFX.fpga.hdlgenerator.Hdl;
import LogisimFX.instance.Instance;
import LogisimFX.instance.InstancePainter;
import LogisimFX.instance.InstanceState;
import LogisimFX.newgui.AnalyzeFrame.Expression;
import LogisimFX.newgui.AnalyzeFrame.Expressions;
import LogisimFX.std.LC;
import LogisimFX.tools.WireRepairData;
import LogisimFX.util.LineBuffer;

class OrGate extends AbstractGate {

	public static OrGate FACTORY = new OrGate();

	private static class OrGateHdlGeneratorFactory extends AbstractGateHdlGenerator {
		@Override
		public LineBuffer getLogicFunction(int nrOfInputs, int bitwidth, boolean isOneHot) {
			final var contents = LineBuffer.getHdlBuffer();
			final var oneLine = new StringBuilder();
			oneLine.append(Hdl.assignPreamble()).append("result").append(Hdl.assignOperator());
			final var tabWidth = oneLine.length();
			var first = true;
			for (int i = 0; i < nrOfInputs; i++) {
				if (!first) {
					oneLine.append(Hdl.orOperator());
					contents.add(oneLine.toString());
					oneLine.setLength(0);
					oneLine.append(" ".repeat(tabWidth));
				} else {
					first = false;
				}
				oneLine.append("s_realInput").append(i + 1);
			}
			oneLine.append(";");
			contents.add(oneLine.toString());
			return contents;
		}
	}

	private OrGate() {

		super("OR Gate", LC.createStringBinding("orGateComponent"), new OrGateHdlGeneratorFactory());
		setRectangularLabel("1");
		setIconNames("orGate.gif", "orGateRect.gif", "dinOrGate.gif");
		setPaintInputLines(true);

	}

	@Override
	protected void paintShape(InstancePainter painter, int width, int height) {
		PainterShaped.paintOr(painter, width, height);
	}

	@Override
	protected void paintDinShape(InstancePainter painter, int width, int height, int inputs) {
		PainterDin.paintOr(painter, width, height, false);
	}

	@Override
	protected Value computeOutput(Value[] inputs, int numInputs, InstanceState state) {
		return GateFunctions.computeOr(inputs, numInputs);
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

		return ret;

	}

	@Override
	protected Value getIdentity() { return Value.FALSE; }

}
