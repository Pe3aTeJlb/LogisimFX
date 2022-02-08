/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.circuit;

import LogisimFX.util.SmallSet;

class WireThread {
	private WireThread parent;
	private SmallSet<CircuitWires.ThreadBundle> bundles
		= new SmallSet<CircuitWires.ThreadBundle>();

	WireThread() {
		parent = this;
	}

	SmallSet<CircuitWires.ThreadBundle> getBundles() {
		return bundles;
	}

	void unite(WireThread other) {
		WireThread group = this.find();
		WireThread group2 = other.find();
		if (group != group2) group.parent = group2;
	}

	WireThread find() {
		WireThread ret = this;
		if (ret.parent != ret) {
			do ret = ret.parent; while (ret.parent != ret);
			this.parent = ret;
		}
		return ret;
	}
}
