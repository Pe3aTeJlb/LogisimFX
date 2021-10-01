/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

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
