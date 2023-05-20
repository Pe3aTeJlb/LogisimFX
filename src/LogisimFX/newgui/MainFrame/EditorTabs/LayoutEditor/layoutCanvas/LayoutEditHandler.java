/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas;

import LogisimFX.circuit.Circuit;
import LogisimFX.newgui.MainFrame.EditorTabs.EditHandler;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.LayoutEditor;
import LogisimFX.proj.Action;
import LogisimFX.proj.Project;
import LogisimFX.std.base.Base;
import LogisimFX.tools.Library;
import LogisimFX.tools.Tool;

public class LayoutEditHandler extends EditHandler {

	private LayoutCanvas canvas;

	public LayoutEditHandler(LayoutEditor editor) {
		this.canvas = editor.getLayoutCanvas();
	}

	@Override
	public boolean computeEnabled(String from) {

		Project proj = canvas.getProject();
		Selection sel = proj == null ? null : canvas.getSelection();
		boolean selEmpty = (sel == null ? true : sel.isEmpty());
		boolean canChange = proj != null && proj.getLogisimFile().contains(proj.getCurrentCircuit());
		
		boolean selectAvailable = false;
		for (Library lib : proj.getLogisimFile().getLibraries()) {
			if (lib instanceof Base) selectAvailable = true;
		}

		if(from.equals("CUT")){ return !selEmpty && selectAvailable && canChange;}
		if(from.equals("COPY")){ return !selEmpty && selectAvailable;}
		if(from.equals("PASTE")){ return selectAvailable && canChange && !Clipboard.isEmpty();}
		if(from.equals("DELETE")){ return !selEmpty && selectAvailable && canChange;}
		if(from.equals("DUPLICATE")){ return !selEmpty && selectAvailable && canChange;}
		if(from.equals("SELECT_ALL")){ return selectAvailable;}
		if(from.equals("RAISE")){ return false;}
		if(from.equals("LOWER")){ return false;}
		if(from.equals("RAISE_TOP")){ return false;}
		if(from.equals("LOWER_BOTTOM")){ return false;}
		if(from.equals("ADD_CONTROL")){ return false;}
		if(from.equals("REMOVE_CONTROL")){ return false;}
		return false;

	}

	@Override
	public void zoomIn() {
		canvas.zoomIn();
	}

	@Override
	public void zoomOut() {
		canvas.zoomOut();
	}

	@Override
	public void toDefaultZoom() {
		canvas.toDefaultZoom();
	}


	@Override
	public void undo() {
		canvas.getProject().undoAction();
	}

	@Override
	public void redo() {
		canvas.getProject().redoAction();
	}


	@Override
	public void cut() {

		Project proj = canvas.getProject();
		Selection sel = canvas.getSelection();
		proj.doAction(SelectionActions.cut(sel));

	}
	
	@Override
	public void copy() {

		Project proj = canvas.getProject();
		Selection sel = canvas.getSelection();
		proj.doAction(SelectionActions.copy(sel));

	}
	
	@Override
	public void paste() {

		Project proj = canvas.getProject();
		Selection sel = canvas.getSelection();
		selectSelectTool(proj);
		Action action = SelectionActions.pasteMaybe(proj, sel);
		if (action != null) {
			proj.doAction(action);
		}

	}
	
	@Override
	public void delete() {

		Project proj = canvas.getProject();
		Selection sel = canvas.getSelection();
		proj.doAction(SelectionActions.clear(sel));

	}
	
	@Override
	public void duplicate() {

		Project proj = canvas.getProject();
		Selection sel = canvas.getSelection();
		proj.doAction(SelectionActions.duplicate(sel));

	}

	@Override
	public void selectAll() {

		Project proj = canvas.getProject();
		Selection sel = canvas.getSelection();
		selectSelectTool(proj);
		Circuit circ = proj.getCurrentCircuit();
		sel.addAll(circ.getWires());
		sel.addAll(circ.getNonWires());

	}
	
	private void selectSelectTool(Project proj) {
		for (Library sub : proj.getLogisimFile().getLibraries()) {
			if (sub instanceof Base) {
				Base base = (Base) sub;
				Tool tool = base.getTool("Edit Tool");
				if (tool != null) proj.setTool(tool);
			}
		}
	}

}
