/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.draw.actions;

import LogisimFX.draw.LC;
import LogisimFX.draw.model.CanvasModel;
import LogisimFX.draw.model.CanvasObject;
import LogisimFX.draw.undo.Action;

import java.util.Collection;
import java.util.Collections;

public abstract class ModelAction extends Action {

	private CanvasModel model;
	
	public ModelAction(CanvasModel model) {
		this.model = model;
	}
	
	public Collection<CanvasObject> getObjects() {
		return Collections.emptySet();
	}

	@Override
	public abstract String getName();
	
	abstract void doSub(CanvasModel model);
	
	abstract void undoSub(CanvasModel model);

	@Override
	public final void doIt() {
		doSub(model);
	}

	@Override
	public final void undo() {
		undoSub(model);
	}
	
	public CanvasModel getModel() {
		return model;
	}
	
	static String getShapesName(Collection<CanvasObject> coll) {

		if (coll.size() != 1) {
			return LC.get("shapeMultiple");
		} else {
			CanvasObject shape = coll.iterator().next();
			return shape.getDisplayName();
		}

	}

}
