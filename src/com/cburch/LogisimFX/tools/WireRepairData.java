/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.tools;

import com.cburch.LogisimFX.data.Location;
import com.cburch.LogisimFX.circuit.Wire;

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
