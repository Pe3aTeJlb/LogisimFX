/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package LogisimFX.proj;

import LogisimFX.file.LibraryEvent;
import LogisimFX.file.LibraryListener;
import LogisimFX.file.LogisimFile;
import LogisimFX.circuit.Circuit;
import LogisimFX.circuit.CircuitEvent;
import LogisimFX.circuit.CircuitListener;
import LogisimFX.circuit.SubcircuitFactory;
import LogisimFX.comp.Component;
import LogisimFX.comp.ComponentFactory;
import LogisimFX.tools.AddTool;
import LogisimFX.util.Dag;

public class Dependencies {

	private class MyListener
			implements LibraryListener, CircuitListener {

		public void libraryChanged(LibraryEvent e) {
			switch (e.getAction()) {
			case LibraryEvent.ADD_TOOL:
				if (e.getData() instanceof AddTool) {
					ComponentFactory factory = ((AddTool) e.getData()).getFactory();
					if (factory instanceof SubcircuitFactory) {
						SubcircuitFactory circFact = (SubcircuitFactory) factory;
						processCircuit(circFact.getSubcircuit());
					}
				}
				break;
			case LibraryEvent.REMOVE_TOOL:
				if (e.getData() instanceof AddTool) {
					ComponentFactory factory = ((AddTool) e.getData()).getFactory();
					if (factory instanceof SubcircuitFactory) {
						SubcircuitFactory circFact = (SubcircuitFactory) factory;
						Circuit circ = circFact.getSubcircuit();
						depends.removeNode(circ);
						circ.removeCircuitListener(this);
					}
				}
				break;
			}
		}

		public void circuitChanged(CircuitEvent e) {
			Component comp;
			switch (e.getAction()) {
			case CircuitEvent.ACTION_ADD:
				comp = (Component) e.getData();
				if (comp.getFactory() instanceof SubcircuitFactory) {
					SubcircuitFactory factory = (SubcircuitFactory) comp.getFactory();
					depends.addEdge(e.getCircuit(), factory.getSubcircuit());
				}
				break;
			case CircuitEvent.ACTION_REMOVE:
				comp = (Component) e.getData();
				if (comp.getFactory() instanceof SubcircuitFactory) {
					SubcircuitFactory factory = (SubcircuitFactory) comp.getFactory();
					boolean found = false;
					for (Component o : e.getCircuit().getNonWires()) { 
						if (o.getFactory() == factory) {
							found = true;
							break;
						}
					}
					if (!found) depends.removeEdge(e.getCircuit(), factory.getSubcircuit());
				}
				break;
			case CircuitEvent.ACTION_CLEAR:
				depends.removeNode(e.getCircuit());
				break;
			}
		}

	}

	private MyListener myListener = new MyListener();
	private Dag depends = new Dag();

	Dependencies(LogisimFile file) {
		addDependencies(file);
	}

	public boolean canRemove(Circuit circ) {
		return !depends.hasPredecessors(circ);
	}

	public boolean canAdd(Circuit circ, Circuit sub) {
		return depends.canFollow(sub, circ);
	}

	private void addDependencies(LogisimFile file) {
		file.addLibraryListener(myListener);
		for (Circuit circuit : file.getCircuits()) {
			processCircuit(circuit);
		}
	}

	private void processCircuit(Circuit circ) {
		circ.addCircuitListener(myListener);
		for (Component comp : circ.getNonWires()) {
			if (comp.getFactory() instanceof SubcircuitFactory) {
				SubcircuitFactory factory = (SubcircuitFactory) comp.getFactory();
				depends.addEdge(circ, factory.getSubcircuit());
			}
		}
	}

}
