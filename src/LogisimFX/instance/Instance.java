/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.instance;


import java.util.List;

import LogisimFX.comp.Component;
import LogisimFX.data.Attribute;
import LogisimFX.data.AttributeSet;
import LogisimFX.data.Bounds;
import LogisimFX.data.Location;
import LogisimFX.circuit.CircuitState;
import javafx.scene.text.Font;

public class Instance {

	public static Instance getInstanceFor(Component comp) {

		if (comp instanceof InstanceComponent) {
			return ((InstanceComponent) comp).getInstance();
		} else {
			return null;
		}

	}

	public static Component getComponentFor(Instance instance) {
		return instance.comp;
	}

	private InstanceComponent comp;

	Instance(InstanceComponent comp) {
		this.comp = comp;
	}

	InstanceComponent getComponent() {
		return comp;
	}

	public InstanceFactory getFactory() {
		return (InstanceFactory) comp.getFactory();
	}

	public Location getLocation() {
		return comp.getLocation();
	}

	public Bounds getBounds() {
		return comp.getBounds();
	}

	public void setAttributeReadOnly(Attribute<?> attr, boolean value) {
		comp.getAttributeSet().setReadOnly(attr, value);
	}

	public <E> E getAttributeValue(Attribute<E> attr) {
		return comp.getAttributeSet().getValue(attr);
	}

	public void addAttributeListener() {
		comp.addAttributeListener(this);
	}

	public AttributeSet getAttributeSet() {
		return comp.getAttributeSet();
	}

	public List<Port> getPorts() {
		return comp.getPorts();
	}

	public Location getPortLocation(int index) {
		return comp.getEnd(index).getLocation();
	}

	public void setPorts(Port[] ports) {
		comp.setPorts(ports);
	}

	public void recomputeBounds() {
		comp.recomputeBounds();
	}

	public void setTextField(Attribute<String> labelAttr, Attribute<Font> fontAttr,
                             int x, int y, int halign, int valign) {
		comp.setTextField(labelAttr, fontAttr, x, y, halign, valign);
	}

	public InstanceData getData(CircuitState state) {
		return (InstanceData) state.getData(comp);
	}
	
	public void setData(CircuitState state, InstanceData data) {
		state.setData(comp, data);
	}
	
	public void fireInvalidated() {
		comp.fireInvalidated();
	}
}
