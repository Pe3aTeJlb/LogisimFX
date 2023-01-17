/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.newgui.WaveformFrame;

import LogisimFX.data.Value;

public interface ModelListener {

	void selectionChanged(ModelEvent event);
	void entryAdded(ModelEvent event, Value[] values);
	void filePropertyChanged(ModelEvent event);

}
