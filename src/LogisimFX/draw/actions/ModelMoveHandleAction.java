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
import LogisimFX.draw.model.HandleGesture;

import java.util.Collection;
import java.util.Collections;

public class ModelMoveHandleAction extends ModelAction {

	private HandleGesture gesture;
	private Handle newHandle;
	
	public ModelMoveHandleAction(CanvasModel model, HandleGesture gesture) {
		super(model);
		this.gesture = gesture;
	}
	
	public Handle getNewHandle() {
		return newHandle;
	}

	@Override
	public Collection<CanvasObject> getObjects() {
		return Collections.singleton(gesture.getHandle().getObject());
	}

	@Override
	public String getName() {
		return LC.get("actionMoveHandle");
	}
	
	@Override
	void doSub(CanvasModel model) {
		newHandle = model.moveHandle(gesture);
	}
	
	@Override
	void undoSub(CanvasModel model) {
		Handle oldHandle = gesture.getHandle();
		int dx = oldHandle.getX() - newHandle.getX();
		int dy = oldHandle.getY() - newHandle.getY();
		HandleGesture reverse = new HandleGesture(newHandle, dx, dy, null);
		model.moveHandle(reverse);
	}

}
