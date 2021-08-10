/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.tools;

import com.cburch.LogisimFX.comp.ComponentUserEvent;
import javafx.beans.binding.StringBinding;

public interface ToolTipMaker {

	StringBinding getToolTip(ComponentUserEvent event);

}
