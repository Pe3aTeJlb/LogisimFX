/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.draw.actions;

import LogisimFX.draw.LC;
import LogisimFX.draw.model.CanvasModel;
import LogisimFX.draw.model.CanvasObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ModelAddAction extends ModelAction {

	private ArrayList<CanvasObject> added;
	private int addIndex;
	
	public ModelAddAction(CanvasModel model, CanvasObject added) {
		this(model, Collections.singleton(added));
	}

	public ModelAddAction(CanvasModel model, Collection<CanvasObject> added) {

		super(model);
		this.added = new ArrayList<CanvasObject>(added);
		this.addIndex = model.getObjectsFromBottom().size();

	}

	public ModelAddAction(CanvasModel model, Collection<CanvasObject> added, int index) {

		super(model);
		this.added = new ArrayList<CanvasObject>(added);
		this.addIndex = index;

	}
	
	public int getDestinationIndex() {
		return addIndex;
	}
	
	@Override
	public Collection<CanvasObject> getObjects() {
		return Collections.unmodifiableList(added);
	}

	@Override
	public String getName() {
		return LC.getFormatted("actionAdd", getShapesName(added));
	}
	
	@Override
	void doSub(CanvasModel model) {
		model.addObjects(addIndex, added);
	}
	
	@Override
	void undoSub(CanvasModel model) {
		model.removeObjects(added);
	}

}
