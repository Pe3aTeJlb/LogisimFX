/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.newgui.PreferencesFrame;

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
