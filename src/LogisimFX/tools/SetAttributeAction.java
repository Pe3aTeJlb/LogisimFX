/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package LogisimFX.tools;

import java.util.ArrayList;
import java.util.List;

import LogisimFX.comp.Component;
import LogisimFX.data.Attribute;
import LogisimFX.data.AttributeSet;
import LogisimFX.circuit.Circuit;
import LogisimFX.circuit.CircuitMutation;
import LogisimFX.circuit.CircuitTransaction;
import LogisimFX.circuit.CircuitTransactionResult;
import LogisimFX.proj.Action;
import LogisimFX.proj.Project;

import javafx.beans.binding.StringBinding;

public class SetAttributeAction extends Action {

	private StringBinding nameGetter;
	private Circuit circuit;
	private List<Component> comps;
	private List<Attribute<Object>> attrs;
	private List<Object> values;
	private List<Object> oldValues;
	private CircuitTransaction xnReverse;
	
	public SetAttributeAction(Circuit circuit, StringBinding nameGetter) {

		this.nameGetter = nameGetter;
		this.circuit = circuit;
		this.comps = new ArrayList<Component>();
		this.attrs = new ArrayList<Attribute<Object>>();
		this.values = new ArrayList<Object>();
		this.oldValues = new ArrayList<Object>();

	}
	
	public void set(Component comp, Attribute<?> attr, Object value) {

		@SuppressWarnings("unchecked")
        Attribute<Object> a = (Attribute<Object>) attr;
		comps.add(comp);
		attrs.add(a);
		values.add(value);

	}
	
	public boolean isEmpty() {
		return comps.isEmpty();
	}
	
	@Override
	public String getName() {
		return nameGetter.get();
	}
	
	@Override
	public void doIt(Project proj) {

		CircuitMutation xn = new CircuitMutation(circuit);
		int len = values.size();
		oldValues.clear();
		for (int i = 0; i < len; i++) {
			Component comp = comps.get(i);
			Attribute<Object> attr = attrs.get(i);
			Object value = values.get(i);
			if (circuit.contains(comp)) {
				oldValues.add(null);
				xn.set(comp, attr, value);
			} else {
				AttributeSet compAttrs = comp.getAttributeSet();
				oldValues.add(compAttrs.getValue(attr));
				compAttrs.setValue(attr, value);    
			}
		}
		
		if (!xn.isEmpty()) {
			CircuitTransactionResult result = xn.execute();
			xnReverse = result.getReverseTransaction();
		}

	}

	@Override
	public void undo(Project proj) {

		if (xnReverse != null) xnReverse.execute();
		for (int i = oldValues.size() - 1; i >= 0; i--) {
			Component comp = comps.get(i);
			Attribute<Object> attr = attrs.get(i);
			Object value = oldValues.get(i);
			if (value != null) {
				comp.getAttributeSet().setValue(attr, value);
			}
		}

	}

}
