/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.circuit;

import LogisimFX.data.Location;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class WireSet {
	private static final Set<Wire> NULL_WIRES = Collections.emptySet();
	public static final WireSet EMPTY = new WireSet(NULL_WIRES);

	private Set<Wire> wires;
	private Set<Location> points;

	WireSet(Set<Wire> wires) {
		if (wires.isEmpty()) {
			this.wires = NULL_WIRES;
			points = Collections.emptySet();
		} else {
			this.wires = wires;
			points = new HashSet<Location>();
			for (Wire w : wires) {
				points.add(w.e0);
				points.add(w.e1);
			}
		}
	}
	
	public boolean containsWire(Wire w) {
		return wires.contains(w);
	}
	
	public boolean containsLocation(Location loc) {
		return points.contains(loc);
	}
}
