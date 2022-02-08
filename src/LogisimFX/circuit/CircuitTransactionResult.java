/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.circuit;

import java.util.Collection;

public class CircuitTransactionResult {
	private CircuitMutatorImpl mutator;
	
	CircuitTransactionResult(CircuitMutatorImpl mutator) {
		this.mutator = mutator;
	}
	
	public CircuitTransaction getReverseTransaction() {
		return mutator.getReverseTransaction();
	}
	
	public ReplacementMap getReplacementMap(Circuit circuit) {
		ReplacementMap ret = mutator.getReplacementMap(circuit);
		return ret == null ? new ReplacementMap() : ret;
	}
	
	public Collection<Circuit> getModifiedCircuits() {
		return mutator.getModifiedCircuits();
	}
}
