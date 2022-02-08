/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.newgui.MainFrame;

import LogisimFX.data.Attribute;
import LogisimFX.data.AttributeSet;
import LogisimFX.proj.Action;
import LogisimFX.proj.Project;
import LogisimFX.tools.Tool;
import LogisimFX.tools.key.KeyConfigurationEvent;
import LogisimFX.tools.key.KeyConfigurationResult;

import java.util.HashMap;
import java.util.Map;

public class ToolAttributeAction extends Action {

	public static Action create(Tool tool, Attribute<?> attr, Object value) {
		AttributeSet attrs = tool.getAttributeSet();
		KeyConfigurationEvent e = new KeyConfigurationEvent(0, attrs, null, null);
		KeyConfigurationResult r = new KeyConfigurationResult(e, attr, value);
		return new ToolAttributeAction(r);
	}

	public static Action create(KeyConfigurationResult results) {
		return new ToolAttributeAction(results);
	}
	
	private KeyConfigurationResult config;
	private Map<Attribute<?>,Object> oldValues;
	
	private ToolAttributeAction(KeyConfigurationResult config) {
		this.config = config;
		this.oldValues = new HashMap<Attribute<?>,Object>(2);
	}
	
	@Override
	public String getName() {
		return LC.get("changeToolAttrAction");
	}

	@Override
	public void doIt(Project proj) {
		AttributeSet attrs = config.getEvent().getAttributeSet();
		Map<Attribute<?>,Object> newValues = config.getAttributeValues();
		Map<Attribute<?>,Object> oldValues = new HashMap<Attribute<?>,Object>(newValues.size());
		for (Map.Entry<Attribute<?>,Object> entry : newValues.entrySet()) {
			@SuppressWarnings("unchecked")
			Attribute<Object> attr = (Attribute<Object>) entry.getKey();
			oldValues.put(attr, attrs.getValue(attr));
			attrs.setValue(attr, entry.getValue());
		}
		this.oldValues = oldValues;
	}
	
	@Override
	public void undo(Project proj) {
		AttributeSet attrs = config.getEvent().getAttributeSet();
		Map<Attribute<?>,Object> oldValues = this.oldValues;
		for (Map.Entry<Attribute<?>,Object> entry : oldValues.entrySet()) {
			@SuppressWarnings("unchecked")
			Attribute<Object> attr = (Attribute<Object>) entry.getKey();
			attrs.setValue(attr, entry.getValue());
		}
	}

}
