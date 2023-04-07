/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.circuit.appear;

import LogisimFX.circuit.ReplacementMap;
import LogisimFX.comp.Component;
import LogisimFX.comp.ComponentEvent;
import LogisimFX.comp.ComponentListener;
import LogisimFX.data.Attribute;
import LogisimFX.data.AttributeEvent;
import LogisimFX.data.AttributeListener;
import LogisimFX.instance.Instance;
import LogisimFX.instance.StdAttr;
import LogisimFX.std.wiring.Pin;

import java.util.*;

public class CircuitPins {
	private class MyComponentListener
			implements ComponentListener, AttributeListener {
		public void endChanged(ComponentEvent e) {
			appearanceManager.updatePorts();
		}
		public void componentInvalidated(ComponentEvent e) { }

		@Override
		public void labelChanged(ComponentEvent e) {

		}

		public void attributeListChanged(AttributeEvent e) { }
		public void attributeValueChanged(AttributeEvent e) {
			Attribute<?> attr = e.getAttribute();
			if (attr == StdAttr.FACING || attr == StdAttr.LABEL
					|| attr == Pin.ATTR_TYPE) {
				appearanceManager.updatePorts();
			}
		}
	}

	private PortManager appearanceManager;
	private MyComponentListener myComponentListener;
	private Set<Instance> pins;

	CircuitPins(PortManager appearanceManager) {
		this.appearanceManager = appearanceManager;
		myComponentListener = new MyComponentListener();
		pins = new HashSet<Instance>();
	}
	
	public void transactionCompleted(ReplacementMap repl) {
		// determine the changes
		Set<Instance> adds = new HashSet<Instance>();
		Set<Instance> removes = new HashSet<Instance>();
		Map<Instance, Instance> replaces = new HashMap<Instance, Instance>(); 
		for (Component comp : repl.getAdditions()) {
			if (comp.getFactory() instanceof Pin) {
				Instance in = Instance.getInstanceFor(comp);
				boolean added = pins.add(in);
				if (added) {
					comp.addComponentListener(myComponentListener);
					in.getAttributeSet().addAttributeListener(myComponentListener);
					adds.add(in);
				}
			}
		}
		for (Component comp : repl.getRemovals()) {
			if (comp.getFactory() instanceof Pin) {
				Instance in = Instance.getInstanceFor(comp);
				boolean removed = pins.remove(in);
				if (removed) {
					comp.removeComponentListener(myComponentListener);
					in.getAttributeSet().removeAttributeListener(myComponentListener);
					Collection<Component> rs = repl.getComponentsReplacing(comp);
					if (rs.isEmpty()) {
						removes.add(in);
					} else {
						Component r = rs.iterator().next();
						Instance rin = Instance.getInstanceFor(r);
						adds.remove(rin);
						replaces.put(in, rin);
					}
				}
			}
		}

		appearanceManager.updatePorts(adds, removes, replaces, getPins());
	}
	
	public Collection<Instance> getPins() {
		return new ArrayList<Instance>(pins);
	}
}
