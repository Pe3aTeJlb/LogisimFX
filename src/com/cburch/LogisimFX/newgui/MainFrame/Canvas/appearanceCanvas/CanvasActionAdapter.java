/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.newgui.MainFrame.Canvas.appearanceCanvas;

import com.cburch.LogisimFX.draw.actions.ModelAction;
import com.cburch.LogisimFX.draw.model.CanvasObject;
import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.circuit.CircuitMutator;
import com.cburch.LogisimFX.circuit.CircuitTransaction;
import com.cburch.LogisimFX.circuit.appear.AppearanceElement;
import com.cburch.LogisimFX.draw.undo.Action;
import com.cburch.LogisimFX.proj.Project;

import java.util.HashMap;
import java.util.Map;

public class CanvasActionAdapter extends com.cburch.LogisimFX.proj.Action {

	private Circuit circuit;
	private Action canvasAction;
	private boolean wasDefault;
	
	public CanvasActionAdapter(Circuit circuit, Action action) {
		this.circuit = circuit;
		this.canvasAction = action;
	}

	@Override
	public String getName() {
		return canvasAction.getName();
	}

	@Override
	public void doIt(Project proj) {

		wasDefault = circuit.getAppearance().isDefaultAppearance();
		if (affectsPorts()) {
			ActionTransaction xn = new ActionTransaction(true);
			xn.execute();
		} else {
			canvasAction.doIt();
		}

	}

	@Override
	public void undo(Project proj) {

		if (affectsPorts()) {
			ActionTransaction xn = new ActionTransaction(false);
			xn.execute();
		} else {
			canvasAction.undo();
		}
		circuit.getAppearance().setDefaultAppearance(wasDefault);

	}
	
	private boolean affectsPorts() {

		if (canvasAction instanceof ModelAction) {
			for (CanvasObject o : ((ModelAction) canvasAction).getObjects()) {
				if (o instanceof AppearanceElement) {
					return true;
				}
			}
		}

		return false;

	}
	
	private class ActionTransaction extends CircuitTransaction {

		private boolean forward;
		
		ActionTransaction(boolean forward) {
			this.forward = forward;
		}
		
		@Override
		protected Map<Circuit, Integer> getAccessedCircuits() {
			Map<Circuit, Integer> accessMap = new HashMap<Circuit, Integer>();
			for (Circuit supercirc : circuit.getCircuitsUsingThis()) {
				accessMap.put(supercirc, READ_WRITE);
			}
			return accessMap;
		}

		@Override
		protected void run(CircuitMutator mutator) {
			if (forward) {
				canvasAction.doIt();
			} else {
				canvasAction.undo();
			}
		}
		
	}

}
