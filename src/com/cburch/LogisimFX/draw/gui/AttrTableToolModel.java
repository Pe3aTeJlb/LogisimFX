/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.draw.gui;

import com.cburch.LogisimFX.draw.tools.AbstractTool;
import com.cburch.LogisimFX.draw.tools.DrawingAttributeSet;
import com.cburch.LogisimFX.data.Attribute;
import com.cburch.logisim.gui.generic.AttrTableSetException;
import com.cburch.logisim.gui.generic.AttributeSetTableModel;

class AttrTableToolModel extends AttributeSetTableModel {
	private DrawingAttributeSet defaults;
	private AbstractTool currentTool;
	
	public AttrTableToolModel(DrawingAttributeSet defaults, AbstractTool tool) {
		super(defaults.createSubset(tool));
		this.defaults = defaults;
		this.currentTool = tool;
	}
	
	public void setTool(AbstractTool value) {
		currentTool = value;
		setAttributeSet(defaults.createSubset(value));
		fireTitleChanged();
	}
	
	@Override
	public String getTitle() {
		return currentTool.getDescription();
	}

	@Override
	public void setValueRequested(Attribute<Object> attr, Object value)
			throws AttrTableSetException {
		defaults.setValue(attr, value);
	}
}
