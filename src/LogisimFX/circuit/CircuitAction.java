/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.circuit;

import LogisimFX.proj.Action;
import LogisimFX.proj.Project;

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
