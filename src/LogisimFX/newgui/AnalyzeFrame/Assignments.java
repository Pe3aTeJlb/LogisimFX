/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.newgui.AnalyzeFrame;

import java.util.HashMap;
import java.util.Map;

class Assignments {

	private Map<String,Boolean> map = new HashMap<String,Boolean>();
	
	public Assignments() { }
	
	public boolean get(String variable) {
		Boolean value = map.get(variable);
		return value != null ? value.booleanValue() : false;
	}
	
	public void put(String variable, boolean value) {
		map.put(variable, Boolean.valueOf(value));
	}

}
