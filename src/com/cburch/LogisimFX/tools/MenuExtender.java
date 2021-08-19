/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.tools;

import com.cburch.LogisimFX.proj.Project;

import javafx.scene.control.ContextMenu;

public interface MenuExtender {

	void configureMenu(ContextMenu menu, Project proj);

}
