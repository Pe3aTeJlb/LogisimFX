/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.circuit;

import LogisimFX.comp.Component;
import LogisimFX.data.Attribute;
import LogisimFX.proj.Action;
import javafx.beans.binding.StringBinding;

import java.util.*;

public final class CircuitMutation extends CircuitTransaction {
	private Circuit primary;
	private List<CircuitChange> changes;

	public CircuitMutation(Circuit circuit) {
		this.primary = circuit;
		this.changes = new ArrayList<CircuitChange>();
	}

	CircuitMutation() {
		this(null);
	}

	public boolean isEmpty() {
		return changes.isEmpty();
	}

	public void clear() {
		changes.add(CircuitChange.clear(primary, null));
	}

	public void add(Component comp) {
		changes.add(CircuitChange.add(primary, comp));
	}

	public void addAll(Collection<? extends Component> comps) {
		changes.add(CircuitChange.addAll(primary, comps));
	}

	public void remove(Component comp) {
		changes.add(CircuitChange.remove(primary, comp));
	}

	public void removeAll(Collection<? extends Component> comps) {
		changes.add(CircuitChange.removeAll(primary, comps));
	}

	public void replace(Component oldComp, Component newComp) {
		ReplacementMap repl = new ReplacementMap(oldComp, newComp);
		changes.add(CircuitChange.replace(primary, repl));
	}

	public void replace(ReplacementMap replacements) {
		if (!replacements.isEmpty()) {
			replacements.freeze();
			changes.add(CircuitChange.replace(primary, replacements));
		}
	}

	public void set(Component comp, Attribute<?> attr, Object value) {
		changes.add(CircuitChange.set(primary, comp, attr, value));
	}

	public void setForCircuit(Attribute<?> attr, Object value) {
		changes.add(CircuitChange.setForCircuit(primary, attr, value));
	}

	void change(CircuitChange change) {
		changes.add(change);
	}

	public Action toAction(StringBinding name) {
		if (name == null) name = LC.createStringBinding("unknownChangeAction");
		return new CircuitAction(name, this);
	}

	@Override
	protected Map<Circuit,Integer> getAccessedCircuits() {
		HashMap<Circuit,Integer> accessMap = new HashMap<Circuit,Integer>();
		HashSet<Circuit> supercircsDone = new HashSet<Circuit>();
		for (CircuitChange change : changes) {
			Circuit circ = change.getCircuit();
			accessMap.put(circ, READ_WRITE);

			if (change.concernsSupercircuit()) {
				boolean isFirstForCirc = supercircsDone.add(circ);
				if (isFirstForCirc) {
					for (Circuit supercirc : circ.getCircuitsUsingThis()) {
						accessMap.put(supercirc, READ_WRITE);
					}
				}
			}
		}
		return accessMap;
	}

	@Override
	protected void run(CircuitMutator mutator) {
		Circuit curCircuit = null;
		ReplacementMap curReplacements = null;
		for (CircuitChange change : changes) {
			Circuit circ = change.getCircuit();
			if (circ != curCircuit) {
				if (curCircuit != null) {
					mutator.replace(curCircuit, curReplacements);
				}
				curCircuit = circ;
				curReplacements = new ReplacementMap();
			}
			change.execute(mutator, curReplacements);
		}
		if (curCircuit != null) {
			mutator.replace(curCircuit, curReplacements);
		}
	}
}