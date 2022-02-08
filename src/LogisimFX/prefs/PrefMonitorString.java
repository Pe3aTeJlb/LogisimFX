/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.prefs;

import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.Preferences;

class PrefMonitorString extends AbstractPrefMonitor<String> {

	private String dflt;
	private String value;
	
	PrefMonitorString(String name, String dflt) {
		super(name);
		this.dflt = dflt;
		Preferences prefs = AppPreferences.getPrefs();
		this.value = prefs.get(name, dflt);
		prefs.addPreferenceChangeListener(this);
	}
	
	public String get() {
		return value;
	}
	
	public void set(String newValue) {
		String oldValue = value;
		if (!isSame(oldValue, newValue)) {
			value = newValue;
			AppPreferences.getPrefs().put(getIdentifier(), newValue);
		}
	}

	public void preferenceChange(PreferenceChangeEvent event) {
		Preferences prefs = event.getNode();
		String prop = event.getKey();
		String name = getIdentifier();
		if (prop.equals(name)) {
			String oldValue = value;
			String newValue = prefs.get(name, dflt);
			if (!isSame(oldValue, newValue)) {
				value = newValue;
				AppPreferences.firePropertyChange(name, oldValue, newValue);
			}
		}
	}
	
	private static boolean isSame(String a, String b) {
		return a == null ? b == null : a.equals(b);
	}

}
