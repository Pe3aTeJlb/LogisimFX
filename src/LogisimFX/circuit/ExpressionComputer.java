/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.circuit;

import LogisimFX.data.Location;
import LogisimFX.newgui.AnalyzeFrame.Expression;

import java.util.Map;

public interface ExpressionComputer {
	/**
	 * Propagates expression computation through a circuit.
	 * The parameter is a map from <code>Point</code>s to
	 * <code>Expression</code>s. The method will use this to
	 * determine the expressions coming into the component,
	 * and it should place any output expressions into
	 * the component.
	 * 
	 * If, in fact, no valid expression exists for the component,
	 * it throws <code>UnsupportedOperationException</code>.
	 */
	public void computeExpression(Map<Location, Expression> expressionMap);
}
