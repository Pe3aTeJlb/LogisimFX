/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.comp;

import java.util.List;

import LogisimFX.data.Bounds;
import LogisimFX.data.Location;
import LogisimFX.circuit.CircuitState;
import LogisimFX.newgui.MainFrame.Canvas.Graphics;

public abstract class AbstractComponent implements Component {

	protected AbstractComponent() { }

	//
	// basic information methods
	//
	public abstract ComponentFactory getFactory();

	//
	// location/extent methods
	//
	public abstract Location getLocation();

	public abstract Bounds getBounds();

	public Bounds getBounds(Graphics g) { return getBounds(); }

	public boolean contains(Location pt) {
		Bounds bds = getBounds();
		if (bds == null) return false;
		return bds.contains(pt, 1);
	}

	public boolean contains(Location pt, Graphics g) {
		Bounds bds = getBounds(g);
		if (bds == null) return false;
		return bds.contains(pt, 1);
	}

	//
	// propagation methods
	//
	public abstract List<EndData> getEnds();

	public EndData getEnd(int index) {
		return getEnds().get(index);
	}

	public boolean endsAt(Location pt) {
		for (EndData data : getEnds()) {
			if (data.getLocation().equals(pt)) return true;
		}
		return false;
	}

	public abstract void propagate(CircuitState state);

}
