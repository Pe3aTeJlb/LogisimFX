/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.circuit;

import com.cburch.LogisimFX.proj.Action;
import com.cburch.LogisimFX.proj.Project;

import javafx.beans.binding.StringBinding;

public class CircuitAction extends Action {

	private StringBinding name;
	private CircuitTransaction forward;
	private CircuitTransaction reverse;
	
	CircuitAction(StringBinding name, CircuitMutation forward) {
		this.name = name;
		this.forward = forward;
	}

	@Override
	public String getName() {
		return name.getValue();
	}

	@Override
	public void doIt(Project proj) {
		CircuitTransactionResult result = forward.execute();
		if (result != null) {
			reverse = result.getReverseTransaction();
		}
	}

	@Override
	public void undo(Project proj) {
		if (reverse != null) {
			reverse.execute();
		}
	}

}
