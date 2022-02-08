/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.newgui.PreferencesFrame;

import javafx.beans.binding.StringBinding;

class PrefOption {

	private Object value;
	private StringBinding binding;
	
	PrefOption(String value, StringBinding binding) {
		this.value = value;
		this.binding = binding;
	}


	public StringBinding getBinding() {
		return binding;
	}
	
	public Object getValue() {
		return value;
	}

}
