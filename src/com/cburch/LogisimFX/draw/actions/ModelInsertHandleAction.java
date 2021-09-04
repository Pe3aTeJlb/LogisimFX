/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.draw.actions;

import com.cburch.LogisimFX.draw.LC;
import com.cburch.LogisimFX.draw.model.CanvasModel;
import com.cburch.LogisimFX.draw.model.CanvasObject;
import com.cburch.LogisimFX.draw.model.Handle;

import java.util.Collection;
import java.util.Collections;

public class ModelInsertHandleAction extends ModelAction {
	private Handle desired;
	
	public ModelInsertHandleAction(CanvasModel model, Handle desired) {
		super(model);
		this.desired = desired;
	}

	@Override
	public Collection<CanvasObject> getObjects() {
		return Collections.singleton(desired.getObject());
	}

	@Override
	public String getName() {
		return LC.get("actionInsertHandle");
	}
	
	@Override
	void doSub(CanvasModel model) {
		model.insertHandle(desired, null);
	}
	
	@Override
	void undoSub(CanvasModel model) {
		model.deleteHandle(desired);
	}
}
