/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.newgui.MainFrame.Canvas.appearanceCanvas;

import com.cburch.LogisimFX.draw.model.CanvasModel;
import com.cburch.LogisimFX.draw.model.CanvasObject;
import com.cburch.LogisimFX.draw.util.ZOrder;
import com.cburch.LogisimFX.circuit.appear.AppearanceAnchor;
import com.cburch.LogisimFX.data.Direction;
import com.cburch.LogisimFX.data.Location;
import com.cburch.LogisimFX.newgui.MainFrame.LC;
import com.cburch.LogisimFX.proj.Action;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.LogisimFX.util.PropertyChangeWeakSupport;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

class Clipboard {

	private Clipboard() { }
	
	public static final String contentsProperty = "appearance";
	
	private static ClipboardContents current = ClipboardContents.EMPTY;
	private static PropertyChangeWeakSupport propertySupport
		= new PropertyChangeWeakSupport(Clipboard.class);
	
	public static boolean isEmpty() {
		return current == null || current.getElements().isEmpty();
	}
	
	public static ClipboardContents get() {
		return current;
	}
	
	public static void set(ClipboardContents value) {

		ClipboardContents old = current;
		current = value;
		propertySupport.firePropertyChange(contentsProperty, old, current);

	}
	
	//
	// PropertyChangeSource methods
	//
	public static void addPropertyChangeListener(PropertyChangeListener listener) {
		propertySupport.addPropertyChangeListener(listener);
	}

	public static void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertySupport.addPropertyChangeListener(propertyName, listener);
	}

	public static void removePropertyChangeListener(PropertyChangeListener listener) {
		propertySupport.removePropertyChangeListener(listener);
	}

	public static void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertySupport.removePropertyChangeListener(propertyName, listener);
	}

}

class ClipboardActions extends Action {

	public static Action cut(AppearanceCanvas canvas) {
		return new ClipboardActions(true, canvas);
	}

	public static Action copy(AppearanceCanvas canvas) {
		return new ClipboardActions(false, canvas);
	}

	private boolean remove;
	private AppearanceCanvas canvas;
	private CanvasModel canvasModel;
	private ClipboardContents oldClipboard;
	private Map<CanvasObject, Integer> affected;
	private ClipboardContents newClipboard;

	private ClipboardActions(boolean remove, AppearanceCanvas canvas) {

		this.remove = remove;
		this.canvas = canvas;
		this.canvasModel = canvas.getModel();

		ArrayList<CanvasObject> contents = new ArrayList<CanvasObject>();
		Direction anchorFacing = null;
		Location anchorLocation = null;
		ArrayList<CanvasObject> aff = new ArrayList<CanvasObject>();
		for (CanvasObject o : canvas.getSelection().getSelected()) {
			if (o.canRemove()) {
				aff.add(o);
				contents.add(o.clone());
			} else if (o instanceof AppearanceAnchor) {
				AppearanceAnchor anch = (AppearanceAnchor) o;
				anchorFacing = anch.getFacing();
				anchorLocation = anch.getLocation();
			}
		}
		contents.trimToSize();
		affected = ZOrder.getZIndex(aff, canvasModel);
		newClipboard = new ClipboardContents(contents, anchorLocation, anchorFacing);

	}

	@Override
	public String getName() {

		if (remove) {
			return LC.get("cutSelectionAction");
		} else {
			return LC.get("copySelectionAction");
		}

	}

	@Override
	public void doIt(Project proj) {

		oldClipboard = Clipboard.get();
		Clipboard.set(newClipboard);
		if (remove) {
			canvasModel.removeObjects(affected.keySet());
		}

	}

	@Override
	public void undo(Project proj) {

		if (remove) {
			canvasModel.addObjects(affected);
			canvas.getSelection().clearSelected();
			canvas.getSelection().setSelected(affected.keySet(), true);
		}

		Clipboard.set(oldClipboard);

	}

}

class ClipboardContents {

	static final ClipboardContents EMPTY
			= new ClipboardContents(Collections.<CanvasObject>emptySet(), null, null);

	private Collection<CanvasObject> onClipboard;
	private Location anchorLocation;
	private Direction anchorFacing;

	public ClipboardContents(Collection<CanvasObject> onClipboard,
							 Location anchorLocation, Direction anchorFacing) {

		this.onClipboard = Collections.unmodifiableList(new ArrayList<CanvasObject>(onClipboard));
		this.anchorLocation = anchorLocation;
		this.anchorFacing = anchorFacing;

	}

	public Collection<CanvasObject> getElements() {
		return onClipboard;
	}

	public Location getAnchorLocation() {
		return anchorLocation;
	}

	public Direction getAnchorFacing() {
		return anchorFacing;
	}

}


