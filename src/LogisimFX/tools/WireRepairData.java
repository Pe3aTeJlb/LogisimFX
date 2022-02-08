/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.tools;

import LogisimFX.data.Location;
import LogisimFX.circuit.Wire;

public class WireRepairData {
	private Wire wire;
	private Location point;
	
	public WireRepairData(Wire wire, Location point) {
		this.wire = wire;
		this.point = point;
	}
	
	public Location getPoint() {
		return point;
	}
	
	public Wire getWire() {
		return wire;
	}
}
