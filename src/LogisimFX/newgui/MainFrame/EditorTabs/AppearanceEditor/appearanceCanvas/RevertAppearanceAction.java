/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor.appearanceCanvas;

import LogisimFX.circuit.Circuit;
import LogisimFX.circuit.appear.CircuitAppearance;
import LogisimFX.draw.model.CanvasObject;
import LogisimFX.localization.LC_gui;
import LogisimFX.proj.Action;
import LogisimFX.proj.Project;

import java.util.ArrayList;

public class RevertAppearanceAction extends Action {
	private Circuit circuit;
	private ArrayList<CanvasObject> old;
	private boolean wasDefault;
	
	public RevertAppearanceAction(Circuit circuit) {
		this.circuit = circuit;
	}
	
	@Override
	public String getName() {
		return LC_gui.getInstance().get("revertAppearanceAction");
	}

	@Override
	public int getActionType() {
		return Action.REVERT_APPEARANCE_ACTION;
	}

	@Override
	public void doIt(Project proj) {
		CircuitAppearance appear = circuit.getAppearance();
		wasDefault = appear.isDefaultAppearance();
		old = new ArrayList<CanvasObject>(appear.getObjectsFromBottom());
		appear.setDefaultAppearance(true);
	}

	@Override
	public void undo(Project proj) {
		CircuitAppearance appear = circuit.getAppearance();
		appear.setObjectsForce(old);
		appear.setDefaultAppearance(wasDefault);
	}
}
