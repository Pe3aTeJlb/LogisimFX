/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.circuit;

import LogisimFX.comp.Component;
import LogisimFX.data.Attribute;

public interface CircuitMutator {
	public void clear(Circuit circuit);
	public void add(Circuit circuit, Component comp);
	public void remove(Circuit circuit, Component comp);
	public void replace(Circuit circuit, Component oldComponent, Component newComponent);
	public void replace(Circuit circuit, ReplacementMap replacements);
	public void set(Circuit circuit, Component comp, Attribute<?> attr, Object value);
	public void setForCircuit(Circuit circuit, Attribute<?> attr, Object value);
}
