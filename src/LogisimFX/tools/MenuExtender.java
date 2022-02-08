/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.tools;

import LogisimFX.proj.Project;

import javafx.scene.control.ContextMenu;

public interface MenuExtender {

	void configureMenu(ContextMenu menu, Project proj);

}
