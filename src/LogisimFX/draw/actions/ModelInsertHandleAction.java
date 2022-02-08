/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.draw.actions;

import LogisimFX.draw.LC;
import LogisimFX.draw.model.CanvasModel;
import LogisimFX.draw.model.CanvasObject;
import LogisimFX.draw.model.Handle;

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
