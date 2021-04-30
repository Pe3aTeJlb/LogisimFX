/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.tools;

import javax.swing.JPopupMenu;

import com.cburch.LogisimFX.proj.Project;

public interface MenuExtender {
	public void configureMenu(JPopupMenu menu, Project proj);
}
