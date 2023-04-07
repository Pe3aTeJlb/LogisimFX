/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.comp;

public interface ComponentListener {

	void endChanged(ComponentEvent e);
	void componentInvalidated(ComponentEvent e);
	void labelChanged(ComponentEvent e);

}
