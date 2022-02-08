/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.tools.key;

import LogisimFX.data.Attribute;

import java.util.HashMap;
import java.util.Map;

public class KeyConfigurationResult {
	private KeyConfigurationEvent event;
	private Map<Attribute<?>,Object> attrValueMap;

	public KeyConfigurationResult(KeyConfigurationEvent event, Attribute<?> attr,
                                  Object value) {
		this.event = event;
		Map<Attribute<?>,Object> singleMap = new HashMap<Attribute<?>,Object>(1);
		singleMap.put(attr, value);
		this.attrValueMap = singleMap;
	}

	public KeyConfigurationResult(KeyConfigurationEvent event,
                                  Map<Attribute<?>,Object> values) {
		this.event = event;
		this.attrValueMap = values;
	}
	
	public KeyConfigurationEvent getEvent() {
		return event;
	}
	
	public Map<Attribute<?>,Object> getAttributeValues() {
		return attrValueMap;
	}
}
