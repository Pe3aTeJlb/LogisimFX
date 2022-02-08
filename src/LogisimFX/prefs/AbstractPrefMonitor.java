/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.prefs;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

abstract class AbstractPrefMonitor<E> implements PrefMonitor<E> {

	private String name;
	
	AbstractPrefMonitor(String name) {
		this.name = name;
	}
	
	public String getIdentifier() {
		return name;
	}
	
	public boolean isSource(PropertyChangeEvent event) {
		return name.equals(event.getPropertyName());
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		AppPreferences.addPropertyChangeListener(name, listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		AppPreferences.removePropertyChangeListener(name, listener);
	}

	public boolean getBoolean() {
		return ((Boolean) get()).booleanValue();
	}
	
	public void setBoolean(boolean value) {
		@SuppressWarnings("unchecked")
		E valObj = (E) Boolean.valueOf(value); 
		set(valObj);
	}

}
