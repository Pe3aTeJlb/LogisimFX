/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.circuit;

public class SimulatorEvent {
	private Simulator source;

	public SimulatorEvent(Simulator source) {
		this.source = source;
	}

	public Simulator getSource() {
		return source;
	}
}
