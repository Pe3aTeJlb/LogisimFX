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

public class ModelDeleteHandleAction extends ModelAction {
	private Handle handle;
	private Handle previous;
	
	public ModelDeleteHandleAction(CanvasModel model, Handle handle) {
		super(model);
		this.handle = handle;
	}

	@Override
	public Collection<CanvasObject> getObjects() {
		return Collections.singleton(handle.getObject());
	}

	@Override
	public String getName() {
		return LC.get("actionDeleteHandle");
	}
	
	@Override
	void doSub(CanvasModel model) {
		previous = model.deleteHandle(handle);
	}
	
	@Override
	void undoSub(CanvasModel model) {
		model.insertHandle(handle, previous);
	}
}
