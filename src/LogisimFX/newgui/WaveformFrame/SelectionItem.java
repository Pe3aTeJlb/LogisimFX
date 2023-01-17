/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.newgui.WaveformFrame;

import LogisimFX.circuit.*;
import LogisimFX.comp.Component;
import LogisimFX.data.AttributeEvent;
import LogisimFX.data.AttributeListener;
import LogisimFX.data.Value;
import LogisimFX.instance.StdAttr;


public class SelectionItem implements AttributeListener, CircuitListener {

	private Model model;
	private Component[] path;
	private Component comp;
	private Object option;
	private int radix = 2;
	private String shortDescriptor;
	private String longDescriptor;
	
	public SelectionItem(Model model, Component[] path, Component comp, Object option) {
		this.model = model;
		this.path = path;
		this.comp = comp;
		this.option = option;
		computeDescriptors();
		
		if (path != null) {
			model.getCircuitState().getCircuit().addCircuitListener(this);
			for (int i = 0; i < path.length; i++) {
				path[i].getAttributeSet().addAttributeListener(this);
				SubcircuitFactory circFact = (SubcircuitFactory) path[i].getFactory();
				circFact.getSubcircuit().addCircuitListener(this);
			}
		}
		comp.getAttributeSet().addAttributeListener(this);
	}
	
	private boolean computeDescriptors() {
		boolean changed = false;
		
		Loggable log = (Loggable) comp.getFeature(Loggable.class);
		String newShort = log.getLogName(option);
		if (newShort == null || newShort.equals("")) {
			newShort = comp.getFactory().getDisplayName().getValue()
				+ comp.getLocation().toString();
			if (option != null) {
				newShort += "." + option.toString();
			}
		}
		if (!newShort.equals(shortDescriptor)) {
			changed = true;
			shortDescriptor = newShort;
		}

		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < path.length; i++) {
			if (i > 0) buf.append(".");
			String label = path[i].getAttributeSet().getValue(StdAttr.LABEL);
			if (label != null && !label.equals("")) {
				buf.append(label);
			} else {
				buf.append(path[i].getFactory().getDisplayName().getValue());
				buf.append(path[i].getLocation());
			}
			buf.append(".");
		}
		buf.append(shortDescriptor);
		String newLong = buf.toString();
		if (!newLong.equals(longDescriptor)) {
			changed = true;
			longDescriptor = newLong;
		}
		
		return changed;
	}
	
	public Component[] getPath() {
		return path;
	}
	
	public Component getComponent() {
		return comp;
	}
	
	public Object getOption() {
		return option;
	}
	
	public int getRadix() {
		return radix;
	}
	
	public void setRadix(int value) {
		radix = value;
		model.fireSelectionChanged(new ModelEvent());
	}
	
	public String toShortString() {
		return shortDescriptor;
	}
	
	@Override
	public String toString() {
		return longDescriptor;
	}
	
	public Value fetchValue(CircuitState root) {
		CircuitState cur = root;
		for (int i = 0; i < path.length; i++) {
			SubcircuitFactory circFact = (SubcircuitFactory) path[i].getFactory();
			cur = circFact.getSubstate(cur, path[i]);
		}
		Loggable log = (Loggable) comp.getFeature(Loggable.class);
		return log == null ? Value.NIL : log.getLogValue(cur, option);
	}

	public void attributeListChanged(AttributeEvent e) { }

	public void attributeValueChanged(AttributeEvent e) {
		if (computeDescriptors()) {
			model.fireSelectionChanged(new ModelEvent());
		}
	}

	public void circuitChanged(CircuitEvent event) {
		int action = event.getAction();
		if (action == CircuitEvent.ACTION_CLEAR
				|| action == CircuitEvent.ACTION_REMOVE) {
			Circuit circ = event.getCircuit();
			Component circComp = null;
			if (circ == model.getCircuitState().getCircuit()) {
				circComp = path != null && path.length > 0 ? path[0] : comp;
			} else if (path != null) {
				for (int i = 0; i < path.length; i++) {
					SubcircuitFactory circFact = (SubcircuitFactory) path[i].getFactory();
					if (circ == circFact.getSubcircuit()) {
						circComp = i + 1 < path.length ? path[i + 1] : comp;
					}
				}
			}
			if (circComp == null) return;
			
			if (action == CircuitEvent.ACTION_REMOVE
					&& event.getData() != circComp) {
				return;
			}
			
			int index = model.getComponents().indexOf(this);
			if (index < 0) return;
			model.getComponents().remove(index);
		}
	}

}
