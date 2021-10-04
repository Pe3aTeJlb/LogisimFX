/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package LogisimFX.newgui.CircLogFrame;

import LogisimFX.data.Value;

interface ModelListener {

	void selectionChanged(ModelEvent event);
	void entryAdded(ModelEvent event, Value[] values);
	void filePropertyChanged(ModelEvent event);

}
