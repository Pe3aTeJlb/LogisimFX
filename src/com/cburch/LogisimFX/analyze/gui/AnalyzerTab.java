/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.analyze.gui;

import javax.swing.*;

abstract class AnalyzerTab extends JPanel {
	abstract void updateTab();
	abstract void localeChanged();
}
