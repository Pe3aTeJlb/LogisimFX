/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor.appearanceCanvas;

import LogisimFX.draw.actions.ModelDeleteHandleAction;
import LogisimFX.draw.actions.ModelInsertHandleAction;
import LogisimFX.draw.actions.ModelReorderAction;
import LogisimFX.draw.model.*;
import LogisimFX.draw.util.MatchingSet;
import LogisimFX.draw.util.ZOrder;
import LogisimFX.circuit.Circuit;
import LogisimFX.circuit.appear.AppearanceAnchor;
import LogisimFX.circuit.appear.AppearanceElement;
import LogisimFX.data.Direction;
import LogisimFX.data.Location;
import LogisimFX.newgui.MainFrame.EditorTabs.EditHandler;
import LogisimFX.newgui.MainFrame.LC;
import LogisimFX.proj.Action;
import LogisimFX.proj.Project;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class AppearanceEditHandler extends EditHandler {

	private AppearanceCanvas canvas;

	public boolean selEmpty;
	public boolean canChange;
	public boolean clipExists;
	public boolean selHasRemovable;
	public boolean canRaise;
	public boolean canLower;
	public boolean canAddCtrl;
	public boolean canRemCtrl;

	public AppearanceEditHandler(AppearanceCanvas canvas) {
		this.canvas = canvas;
	}
	
	@Override
	public boolean computeEnabled(String from) {

		Project proj = canvas.getProject();
		Circuit circ = canvas.getCircuit();
		Selection sel = canvas.getSelection();
		selEmpty = sel.isEmpty();
		canChange = proj.getLogisimFile().contains(circ);
		clipExists = !Clipboard.isEmpty();
		selHasRemovable = false;

		for (CanvasObject o : sel.getSelected()) {
			if (!(o instanceof AppearanceElement)) {
				selHasRemovable = true;
			}
		}

		if (!selEmpty && canChange) {
			Map<CanvasObject, Integer> zs = ZOrder.getZIndex(sel.getSelected(),
					canvas.getModel());
			int zmin = Integer.MAX_VALUE;
			int zmax = Integer.MIN_VALUE;
			int count = 0; 
			for (Map.Entry<CanvasObject, Integer> entry : zs.entrySet()) {
				if (!(entry.getKey() instanceof AppearanceElement)) {
					count++;
					int z = entry.getValue().intValue();
					if (z < zmin) zmin = z;
					if (z > zmax) zmax = z;
				}
			}
			int maxPoss = AppearanceCanvas.getMaxIndex(canvas.getModel());
			if (count > 0 && count <= maxPoss) {
				canRaise = zmin <= maxPoss - count;
				canLower = zmax >= count;
			} else {
				canRaise = false;
				canLower = false;
			}
		} else {
			canRaise = false;
			canLower = false;
		}
		canAddCtrl = false;
		canRemCtrl = false;
		Handle handle = sel.getSelectedHandle();
		if (handle != null && canChange) {
			CanvasObject o = handle.getObject();
			canAddCtrl = o.canInsertHandle(handle.getLocation()) != null;
			canRemCtrl = o.canDeleteHandle(handle.getLocation()) != null;
		}

		if(from.equals("CUT")){ return selHasRemovable && canChange;}
		if(from.equals("COPY")){ return !selEmpty;}
		if(from.equals("PASTE")){ return canChange && clipExists;}
		if(from.equals("DELETE")){ return selHasRemovable && canChange;}
		if(from.equals("DUPLICATE")){ return !selEmpty && canChange;}
		if(from.equals("SELECT_ALL")){ return true;}
		if(from.equals("RAISE")){ return canRaise;}
		if(from.equals("LOWER")){ return canLower;}
		if(from.equals("RAISE_TOP")){ return canRaise;}
		if(from.equals("LOWER_BOTTOM")){ return canLower;}
		if(from.equals("ADD_CONTROL")){ return canAddCtrl;}
		if(from.equals("REMOVE_CONTROL")){ return canRemCtrl;}
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

		if (!canvas.getSelection().isEmpty()) {
			canvas.getProject().doAction(ClipboardActions.cut(canvas));
		}

	}
	
	@Override
	public void copy() {

		if (!canvas.getSelection().isEmpty()) {
			canvas.getProject().doAction(ClipboardActions.copy(canvas));
		}

	}
	
	@Override
	public void paste() {

		ClipboardContents clip = Clipboard.get();
		Collection<CanvasObject> contents = clip.getElements();
		List<CanvasObject> add = new ArrayList<CanvasObject>(contents.size());
		for (CanvasObject o : contents) {
			add.add(o.clone());
		}
		if (add.isEmpty()) return;
		
		// find how far we have to translate shapes so that at least one of the
		// pasted shapes doesn't match what's already in the model
		Collection<CanvasObject> raw = canvas.getModel().getObjectsFromBottom(); 
		MatchingSet<CanvasObject> cur = new MatchingSet<CanvasObject>(raw);
		int dx = 0;
		while (true) {
			// if any shapes in "add" aren't in canvas, we are done
			boolean allMatch = true;
			for (CanvasObject o : add) {
				if (!cur.contains(o)) {
					allMatch = false;
					break;
				}
			}
			if (!allMatch) break;
			
			// otherwise translate everything by 10 pixels and repeat test
			for (CanvasObject o : add) {
				o.translate(10, 10);
			}
			dx += 10;
		}
		
		Location anchorLocation = clip.getAnchorLocation();
		if (anchorLocation != null && dx != 0) {
			anchorLocation = anchorLocation.translate(dx, dx);
		}
			
		canvas.getProject().doAction(new SelectionAction(canvas,
				LC.createStringBinding("pasteClipboardAction"), null, add, add,
				anchorLocation, clip.getAnchorFacing()));

	}
	
	@Override
	public void delete() {

		Selection sel = canvas.getSelection();
		int n = sel.getSelected().size();
		List<CanvasObject> select = new ArrayList<CanvasObject>(n);
		List<CanvasObject> remove = new ArrayList<CanvasObject>(n);
		Location anchorLocation = null;
		Direction anchorFacing = null;
		for (CanvasObject o : sel.getSelected()) {
			if (o.canRemove()) {
				remove.add(o);
			} else {
				select.add(o);
				if (o instanceof AppearanceAnchor) {
					AppearanceAnchor anchor = (AppearanceAnchor) o;
					anchorLocation = anchor.getLocation();
					anchorFacing = anchor.getFacing();
				}
			}
		}
		
		if (!remove.isEmpty()) {
			canvas.getProject().doAction(new SelectionAction(canvas,
					LC.createStringBinding("deleteSelectionAction"), remove, null, select,
				anchorLocation, anchorFacing));
		}

	}
	
	@Override
	public void duplicate() {

		Selection sel = canvas.getSelection();
		int n = sel.getSelected().size();
		List<CanvasObject> select = new ArrayList<CanvasObject>(n);
		List<CanvasObject> clones = new ArrayList<CanvasObject>(n);
		for (CanvasObject o : sel.getSelected()) {
			if (o.canRemove()) {
				CanvasObject copy = o.clone();
				copy.translate(10, 10);
				clones.add(copy);
				select.add(copy);
			} else {
				select.add(o);
			}
		}
		
		if (!clones.isEmpty()) {
			canvas.getProject().doAction(new SelectionAction(canvas,
					LC.createStringBinding("duplicateSelectionAction"), null, clones, select,
				null, null));
		}

	}
	
	@Override
	public void selectAll() {

		Selection sel = canvas.getSelection();
		sel.setSelected(canvas.getModel().getObjectsFromBottom(), true);

	}

	public void raise() {

		ModelReorderAction act = ModelReorderAction.createRaise(canvas.getModel(),
				canvas.getSelection().getSelected());
		if (act != null) {
			canvas.doAction(act);
		}

	}

	public void lower() {

		ModelReorderAction act = ModelReorderAction.createLower(canvas.getModel(),
				canvas.getSelection().getSelected());
		if (act != null) {
			canvas.doAction(act);
		}

	}

	public void raiseTop() {

		ModelReorderAction act = ModelReorderAction.createRaiseTop(canvas.getModel(),
				canvas.getSelection().getSelected());
		if (act != null) {
			canvas.doAction(act);
		}

	}

	public void lowerBottom() {

		ModelReorderAction act = ModelReorderAction.createLowerBottom(canvas.getModel(),
				canvas.getSelection().getSelected());
		if (act != null) {
			canvas.doAction(act);
		}

	}

	public void addControlPoint() {

		Selection sel = canvas.getSelection();
		Handle handle = sel.getSelectedHandle();
		canvas.doAction(new ModelInsertHandleAction(canvas.getModel(), handle));

	}

	public void removeControlPoint() {

		Selection sel = canvas.getSelection();
		Handle handle = sel.getSelectedHandle();
		canvas.doAction(new ModelDeleteHandleAction(canvas.getModel(), handle));

	}

}
