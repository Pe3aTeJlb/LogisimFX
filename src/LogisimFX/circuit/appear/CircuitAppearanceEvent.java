/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.circuit.appear;

import LogisimFX.circuit.Circuit;

public class CircuitAppearanceEvent {
	public static final int APPEARANCE = 1;
	public static final int BOUNDS = 2;
	public static final int PORTS = 4;
	public static final int ALL_TYPES = 7;
	
	private Circuit circuit;
	private int affects;
	
	CircuitAppearanceEvent(Circuit circuit, int affects) {
		this.circuit = circuit;
		this.affects = affects;
	}
	
	public Circuit getSource() {
		return circuit;
	}
	
	public boolean isConcerning(int type) {
		return (affects & type) != 0;
	}
}
