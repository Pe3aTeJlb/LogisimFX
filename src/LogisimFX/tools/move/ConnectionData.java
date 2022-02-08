/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.tools.move;

import java.util.List;

import LogisimFX.data.Direction;
import LogisimFX.data.Location;
import LogisimFX.circuit.Wire;

class ConnectionData {
	private Location loc;
	
	private Direction dir;
	
	/** The list of wires leading up to this point - we may well want to
	 * truncate this path somewhat. */
	private List<Wire> wirePath;
	
	private Location wirePathStart;
	
	public ConnectionData(Location loc, Direction dir, List<Wire> wirePath,
                          Location wirePathStart) {
		this.loc = loc;
		this.dir = dir;
		this.wirePath = wirePath;
		this.wirePathStart = wirePathStart;
	}
	
	public Location getLocation() {
		return loc;
	}
	
	public Direction getDirection() {
		return dir;
	}
	
	public List<Wire> getWirePath() {
		return wirePath;
	}
	
	public Location getWirePathStart() {
		return wirePathStart;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof ConnectionData) {
			ConnectionData o = (ConnectionData) other;
			return this.loc.equals(o.loc) && this.dir.equals(o.dir);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return loc.hashCode() * 31 + (dir == null ? 0 : dir.hashCode());
	}
}
