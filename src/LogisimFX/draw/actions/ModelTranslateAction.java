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
import java.util.HashSet;

public class ModelTranslateAction extends ModelAction {

	private HashSet<CanvasObject> moved;
	private int dx;
	private int dy;
	
	public ModelTranslateAction(CanvasModel model,
                                Collection<CanvasObject> moved, int dx, int dy) {
		super(model);
		this.moved = new HashSet<CanvasObject>(moved);
		this.dx = dx;
		this.dy = dy;
	}
	
	@Override
	public Collection<CanvasObject> getObjects() {
		return Collections.unmodifiableSet(moved);
	}

	@Override
	public String getName() {
		return LC.getFormatted("actionTranslate", getShapesName(moved));
	}
	
	@Override
	void doSub(CanvasModel model) {
		model.translateObjects(moved, dx, dy);
	}
	
	@Override
	void undoSub(CanvasModel model) {
		model.translateObjects(moved, -dx, -dy);
	}
	
	@Override
	public boolean shouldAppendTo(Action other) {
		if (other instanceof ModelTranslateAction) {
			ModelTranslateAction o = (ModelTranslateAction) other;
			return this.moved.equals(o.moved);
		} else {
			return false;
		}
	}

	@Override
	public Action append(Action other) {
		if (other instanceof ModelTranslateAction) {
			ModelTranslateAction o = (ModelTranslateAction) other;
			if (this.moved.equals(o.moved)) {
				return new ModelTranslateAction(getModel(), moved,
						this.dx + o.dx, this.dy + o.dy);
			}
		}
		return super.append(other);
	}

}
