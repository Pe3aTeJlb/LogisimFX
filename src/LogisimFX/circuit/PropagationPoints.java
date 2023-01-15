/* Copyright (c) 2010, Carl Burch.
 *  Copyright (c) 2022, Pplos Studio
 *  License information is located in the Launch file */

package LogisimFX.circuit;

import LogisimFX.comp.Component;
import LogisimFX.comp.ComponentDrawContext;
import LogisimFX.data.Bounds;
import LogisimFX.data.Location;
import LogisimFX.newgui.MainFrame.EditorTabs.Graphics;

import java.util.HashMap;
import java.util.HashSet;

class PropagationPoints {
	private static class Entry {
		private CircuitState state;
		private Location loc;

		private Entry(CircuitState state, Location loc) {
			this.state = state;
			this.loc = loc;
		}

		@Override
		public boolean equals(Object other) {
			if (!(other instanceof Entry)) return false;
			Entry o = (Entry) other;
			return state.equals(o.state) && loc.equals(o.loc);
		}

		@Override
		public int hashCode() {
			return state.hashCode() * 31 + loc.hashCode();
		}
	}

	private HashSet<Entry> data;

	PropagationPoints() {
		this.data = new HashSet<Entry>();
	}

	void add(CircuitState state, Location loc) {
		data.add(new Entry(state, loc));
	}

	void clear() {
		data.clear();
	}

	boolean isEmpty() {
		return data.isEmpty();
	}

	void draw(ComponentDrawContext context) {

		if (data.isEmpty()) return;

		CircuitState state = context.getCircuitState();
		HashMap<CircuitState, CircuitState> stateMap = new HashMap<CircuitState, CircuitState>();
		for (CircuitState s : state.getSubstates()) {
			addSubstates(stateMap, s, s);
		}

		Graphics g = context.getGraphics();
		g.setLineWidth(2);
		for (Entry e : data) {
			if (e.state == state) {
				Location p = e.loc;
				g.c.strokeOval(p.getX() - 4, p.getY() - 4, 8, 8);
			} else if (stateMap.containsKey(e.state)) {
				CircuitState substate = stateMap.get(e.state);
				Component subcirc = substate.getSubcircuit();
				Bounds b = subcirc.getBounds();
				g.c.strokeRect(b.getX(), b.getY(), b.getWidth(), b.getHeight());
			}
		}

		g.toDefault();

	}

	private void addSubstates(HashMap<CircuitState, CircuitState> map,
                              CircuitState source, CircuitState value) {
		map.put(source, value);
		for (CircuitState s : source.getSubstates()) {
			addSubstates(map, s, value);
		}
	}
}
