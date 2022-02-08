/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.tools;

import LogisimFX.comp.ComponentUserEvent;

public interface Pokable {
	public Caret getPokeCaret(ComponentUserEvent event);
}
