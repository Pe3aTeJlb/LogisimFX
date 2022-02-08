/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.prefs;

import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.Preferences;

class PrefMonitorDouble extends AbstractPrefMonitor<Double> {

	private double dflt;
	private double value;
	
	PrefMonitorDouble(String name, double dflt) {
		super(name);
		this.dflt = dflt;
		this.value = dflt;
		Preferences prefs = AppPreferences.getPrefs();
		set(Double.valueOf(prefs.getDouble(name, dflt)));
		prefs.addPreferenceChangeListener(this);
	}
	
	public Double get() {
		return Double.valueOf(value);
	}
	
	public void set(Double newValue) {
		double newVal = newValue.doubleValue();
		if (value != newVal) {
			AppPreferences.getPrefs().putDouble(getIdentifier(), newVal);
		}
	}

	public void preferenceChange(PreferenceChangeEvent event) {
		Preferences prefs = event.getNode();
		String prop = event.getKey();
		String name = getIdentifier();
		if (prop.equals(name)) {
			double oldValue = value;
			double newValue = prefs.getDouble(name, dflt);
			if (newValue != oldValue) {
				value = newValue;
				AppPreferences.firePropertyChange(name,
						Double.valueOf(oldValue), Double.valueOf(newValue));
			}
		}
	}

}
