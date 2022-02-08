/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.draw.actions;

import LogisimFX.draw.LC;
import LogisimFX.draw.model.CanvasModel;
import LogisimFX.draw.model.CanvasObject;
import LogisimFX.draw.shapes.Text;

import java.util.Collection;
import java.util.Collections;

public class ModelEditTextAction extends ModelAction {
	private Text text;
	private String oldValue;
	private String newValue;
	
	public ModelEditTextAction(CanvasModel model, Text text, String newValue) {
		super(model);
		this.text = text;
		this.oldValue = text.getText();
		this.newValue = newValue;
	}
	
	@Override
	public Collection<CanvasObject> getObjects() {
		return Collections.singleton((CanvasObject) text);
	}

	@Override
	public String getName() {
		return LC.get("actionEditText");
	}
	
	@Override
	void doSub(CanvasModel model) {
		model.setText(text, newValue);
	}
	
	@Override
	void undoSub(CanvasModel model) {
		model.setText(text, oldValue);
	}
}
