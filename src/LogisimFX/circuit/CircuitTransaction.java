/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.circuit;

import LogisimFX.circuit.appear.CircuitPins;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.locks.Lock;

public abstract class CircuitTransaction {
	public static final Integer READ_ONLY = Integer.valueOf(1);
	public static final Integer READ_WRITE = Integer.valueOf(2);
	
	protected abstract Map<Circuit,Integer> getAccessedCircuits();

	protected abstract void run(CircuitMutator mutator);

	public final CircuitTransactionResult execute() {
		CircuitMutatorImpl mutator = new CircuitMutatorImpl();
		Map<Circuit,Lock> locks = CircuitLocker.acquireLocks(this, mutator);
		CircuitTransactionResult result;
		try {
			this.run(mutator);

			// Let the port locations of each subcircuit's appearance be
			// updated to reflect the changes - this needs to happen before
			// wires are repaired because it could lead to some wires being
			// split
			Collection<Circuit> modified = mutator.getModifiedCircuits();
			for (Circuit circuit : modified) {
				CircuitMutatorImpl circMutator = circuit.getLocker().getMutator();
				if (circMutator == mutator) {
					CircuitPins pins = circuit.getAppearance().getCircuitPins();
					ReplacementMap repl = mutator.getReplacementMap(circuit);
					if (repl != null) {
						pins.transactionCompleted(repl);
					}
				}
			}

			// Now go through each affected circuit and repair its wires
			for (Circuit circuit : modified) {
				CircuitMutatorImpl circMutator = circuit.getLocker().getMutator();
				if (circMutator == mutator) {
					WireRepair repair = new WireRepair(circuit);
					repair.run(mutator);
				} else {
					// this is a transaction executed within a transaction -
					// wait to repair wires until overall transaction is done
					circMutator.markModified(circuit);
				}
			}

			result = new CircuitTransactionResult(mutator);
			for (Circuit circuit : result.getModifiedCircuits()) {
				circuit.fireEvent(CircuitEvent.TRANSACTION_DONE, result);
			}
		} finally {
			CircuitLocker.releaseLocks(locks);
		}
		return result;
	}

}
