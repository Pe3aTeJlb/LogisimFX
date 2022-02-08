/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.draw.actions;

import LogisimFX.draw.LC;
import LogisimFX.draw.model.CanvasModel;
import LogisimFX.draw.model.CanvasObject;
import LogisimFX.draw.util.ZOrder;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class ModelRemoveAction extends ModelAction {
	private Map<CanvasObject, Integer> removed;

	public ModelRemoveAction(CanvasModel model, CanvasObject removed) {
		this(model, Collections.singleton(removed));
	}	
	
	public ModelRemoveAction(CanvasModel model, Collection<CanvasObject> removed) {
		super(model);
		this.removed = ZOrder.getZIndex(removed, model);
	}
	
	@Override
	public Collection<CanvasObject> getObjects() {
		return Collections.unmodifiableSet(removed.keySet());
	}

	@Override
	public String getName() {
		return LC.getFormatted("actionRemove", getShapesName(removed.keySet()));
	}
	
	@Override
	void doSub(CanvasModel model) {
		model.removeObjects(removed.keySet());
	}
	
	@Override
	void undoSub(CanvasModel model) {
		model.addObjects(removed);
	}
}
