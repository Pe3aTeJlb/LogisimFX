/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package LogisimFX.proj;

import LogisimFX.draw.tools.AbstractTool;
import LogisimFX.file.*;
import LogisimFX.circuit.*;
import LogisimFX.newgui.MainFrame.Canvas.appearanceCanvas.AppearanceCanvas;
import LogisimFX.newgui.MainFrame.Canvas.layoutCanvas.LayoutCanvas;
import LogisimFX.newgui.MainFrame.MainFrameController;
import LogisimFX.newgui.MainFrame.Canvas.layoutCanvas.Selection;
import LogisimFX.newgui.MainFrame.Canvas.layoutCanvas.SelectionActions;
import LogisimFX.tools.AddTool;
import LogisimFX.tools.Library;
import LogisimFX.tools.Tool;
import LogisimFX.util.EventSourceWeakSupport;

import java.util.HashMap;
import java.util.LinkedList;

public class Project {

	private static final int MAX_UNDO_SIZE = 64;

	private static class ActionData {
		CircuitState circuitState;
		Action action;

		public ActionData(CircuitState circuitState, Action action) {
			this.circuitState = circuitState;
			this.action = action;
		}
	}

	private class MyListener implements Selection.Listener, LibraryListener {
		public void selectionChanged(Selection.Event e) {
			fireEvent(ProjectEvent.ACTION_SELECTION, e.getSource());
		}
		
		public void libraryChanged(LibraryEvent event) {
			int action = event.getAction();
			if (action == LibraryEvent.REMOVE_LIBRARY) {
				Library unloaded = (Library) event.getData();
				if (tool != null && unloaded.containsFromSource(tool)) {
					setTool(null);
				}
			} else if (action == LibraryEvent.REMOVE_TOOL) {
				Object data = event.getData();
				if (data instanceof AddTool) {
					Object factory = ((AddTool) data).getFactory();
					if (factory instanceof SubcircuitFactory) {
						SubcircuitFactory fact = (SubcircuitFactory) factory;
						if (fact.getSubcircuit() == getCurrentCircuit()) {
							setCurrentCircuit(file.getMainCircuit());
						}
					}
				}
			}
		}
	}

	private Simulator simulator = new Simulator();
	private LogisimFile file;
	private CircuitState circuitState;
	private HashMap<Circuit,CircuitState> stateMap = new HashMap<>();
	private MainFrameController frameController;
	private Tool tool = null;
	private AbstractTool abstractTool = null;
	private LinkedList<ActionData> undoLog = new LinkedList<ActionData>();
	private int undoMods = 0;

	private EventSourceWeakSupport<ProjectListener> projectListeners
		= new EventSourceWeakSupport<ProjectListener>();
	private EventSourceWeakSupport<LibraryListener> fileListeners
		= new EventSourceWeakSupport<LibraryListener>();
	private EventSourceWeakSupport<CircuitListener> circuitListeners
		= new EventSourceWeakSupport<CircuitListener>();

	private Dependencies depends;
	private MyListener myListener = new MyListener();

	public Project(LogisimFile file) {
		addLibraryListener(myListener);
		setLogisimFile(file);
	}

	public void setFrameController(MainFrameController controller){
		frameController = controller;
	}

	public MainFrameController getFrameController(){
		return frameController;
	}

	//
	// access methods
	//
	public LogisimFile getLogisimFile() {
		return file;
	}

	public Simulator getSimulator() {
		return simulator;
	}

	public Options getOptions() {
		return file.getOptions();
	}

	public Dependencies getDependencies() {
		return depends;
	}


	public Circuit getCurrentCircuit() {
		return circuitState == null ? null : circuitState.getCircuit();
	}

	public CircuitState getCircuitState() {
		return circuitState;
	}
	
	public CircuitState getCircuitState(Circuit circuit) {
		if (circuitState != null && circuitState.getCircuit() == circuit) {
			return circuitState;
		} else {
			CircuitState ret = stateMap.get(circuit);
			if (ret == null) {
				ret = new CircuitState(this, circuit);
				stateMap.put(circuit, ret);
			}
			return ret;
		}
	}

	public Action getLastAction() {
		if (undoLog.size() == 0) {
			return null;
		} else {
			return undoLog.getLast().action;
		}
	}

	public Tool getTool() {
		return tool;
	}

	public AbstractTool getAbstractTool(){
		return abstractTool;
	}

	public boolean isFileDirty() {
		return undoMods != 0;
	}

	//
	// Listener methods
	//
	public void addProjectListener(ProjectListener what) {
		projectListeners.add(what);
	}

	public void removeProjectListener(ProjectListener what) {
		projectListeners.remove(what);
	}
	
	public void addLibraryListener(LibraryListener value) {
		fileListeners.add(value);
		if (file != null) file.addLibraryListener(value);
	}
	
	public void removeLibraryListener(LibraryListener value) {
		fileListeners.remove(value);
		if (file != null) file.removeLibraryListener(value);
	}
	
	public void addCircuitListener(CircuitListener value) {
		circuitListeners.add(value);
		Circuit current = getCurrentCircuit();
		if (current != null) current.addCircuitListener(value);
	}
	
	public void removeCircuitListener(CircuitListener value) {
		circuitListeners.remove(value);
		Circuit current = getCurrentCircuit();
		if (current != null) current.removeCircuitListener(value);
	}

	private void fireEvent(int action, Object old, Object data) {
		fireEvent(new ProjectEvent(action, this, old, data));
	}

	private void fireEvent(int action, Object data) {
		fireEvent(new ProjectEvent(action, this, data));
	}

	private void fireEvent(ProjectEvent event) {
		for (ProjectListener l : projectListeners) {
			l.projectChanged(event);
		}
	}

	//
	// actions
	//
	public void setLogisimFile(LogisimFile value) {

		LogisimFile old = this.file;
		if (old != null) {
			for (LibraryListener l : fileListeners) {
				old.removeLibraryListener(l);
			}
		}
		file = value;
		stateMap.clear();
		depends = new Dependencies(file);
		undoLog.clear();
		undoMods = 0;
		fireEvent(ProjectEvent.ACTION_SET_FILE, old, file);
		setCurrentCircuit(file.getMainCircuit());
		if (file != null) {
			for (LibraryListener l : fileListeners) {
				file.addLibraryListener(l);
			}
		}
		file.setDirty(true); // toggle it so that everybody hears the file is fresh
		file.setDirty(false);

	}

	public void setCircuitState(CircuitState value) {

		if (value == null || circuitState == value) return;

		CircuitState old = circuitState;
		Circuit oldCircuit = old == null ? null : old.getCircuit();
		Circuit newCircuit = value.getCircuit();
		boolean circuitChanged = old == null || oldCircuit != newCircuit;

		if (circuitChanged) {

			LayoutCanvas canvas = getFrameController() == null ? null : getFrameController().getLayoutCanvas();
			if (canvas != null) {
				if (tool != null) tool.deselect(canvas);
				Selection selection = canvas.getSelection();
				if (selection != null) {
					Action act = SelectionActions.dropAll(selection);
					if (act != null) {
						doAction(act);
					}
				}
				if (tool != null) tool.select(canvas);
			}

			if (oldCircuit != null) {
				for (CircuitListener l : circuitListeners) {
					oldCircuit.removeCircuitListener(l);
				}
			}
		}

		circuitState = value;
		stateMap.put(circuitState.getCircuit(), circuitState);
		simulator.setCircuitState(circuitState);

		if (circuitChanged) {
			fireEvent(ProjectEvent.ACTION_SET_CURRENT, oldCircuit, newCircuit);
			if (newCircuit != null) {
				for (CircuitListener l : circuitListeners) {
					newCircuit.addCircuitListener(l);
				}
			}
		}

		fireEvent(ProjectEvent.ACTION_SET_STATE, old, circuitState);

	}

	public void setCurrentCircuit(Circuit circuit) {

		CircuitState circState = stateMap.get(circuit);
		if (circState == null) circState = new CircuitState(this, circuit);
		setCircuitState(circState);

		getLogisimFile().setCurrent(circuit);

		if(frameController!=null)frameController.computeTitle();

	}

	public void setTool(Tool value) {

		if (tool == value) return;

		frameController.setAttributeTable(value);

		Tool old = tool;

		LayoutCanvas canvas = getFrameController().getLayoutCanvas();
		if (old != null) old.deselect(canvas);
		Selection selection = canvas.getSelection();
		if (selection != null && !selection.isEmpty()) {
			Circuit circuit = canvas.getCircuit();
			CircuitMutation xn = new CircuitMutation(circuit);
			if (value == null) {
				Action act = SelectionActions.dropAll(selection);
				if (act != null) {
					doAction(act);
				}
			} else if (!getOptions().getMouseMappings().containsSelectTool()) {
				Action act = SelectionActions.dropAll(selection);
				if (act != null) {
					doAction(act);
				}
			}
			if (!xn.isEmpty()) doAction(xn.toAction(null));
		}

		tool = value;
		if (tool != null) tool.select(getFrameController().getLayoutCanvas());

		tool = value;
		fireEvent(ProjectEvent.ACTION_SET_TOOL, old, tool);

	}

	public void setAbstractTool(AbstractTool value){

		if (abstractTool == value) return;

		frameController.setAttributeTable(value);

		AbstractTool old = abstractTool;

		AppearanceCanvas canvas = getFrameController().getAppearanceCanvas();
		if (old != null) old.toolDeselected(canvas);
		/*
		LogisimFX.newgui.MainFrame.Canvas.appearanceCanvas.Selection selection
				= canvas.getSelection();
		if (selection != null && !selection.isEmpty()) {
			Circuit circuit = canvas.getCircuit();
			CircuitMutation xn = new CircuitMutation(circuit);
			if (value == null) {
				Action act = SelectionAction.dropAll(selection);
				if (act != null) {
					doAction(act);
				}
			} else if (!getOptions().getMouseMappings().containsSelectTool()) {
				Action act =
						LogisimFX.newgui.MainFrame.Canvas.appearanceCanvas.SelectionAction.dropAll(selection);
				if (act != null) {
					doAction(act);
				}
			}
			if (!xn.isEmpty()) doAction(xn.toAction(null));
		}


		 */


		abstractTool = value;

		fireEvent(ProjectEvent.ACTION_SET_TOOL, old, abstractTool);

	}

	public void doAction(Action act) {

		if (act == null) return;
		Action toAdd = act;
		if (!undoLog.isEmpty() && act.shouldAppendTo(getLastAction())) {
			ActionData firstData = undoLog.removeLast();
			Action first = firstData.action;
			if (first.isModification()) --undoMods;
			toAdd = first.append(act);
			if (toAdd != null) {
				undoLog.add(new ActionData(circuitState, toAdd));
				if (toAdd.isModification()) ++undoMods;
			}
			fireEvent(new ProjectEvent(ProjectEvent.ACTION_START, this, act));
			act.doIt(this);
			file.setDirty(isFileDirty());
			fireEvent(new ProjectEvent(ProjectEvent.ACTION_COMPLETE, this, act));
			fireEvent(new ProjectEvent(ProjectEvent.ACTION_MERGE, this, first, toAdd));
			return;
		}
		undoLog.add(new ActionData(circuitState, toAdd));
		fireEvent(new ProjectEvent(ProjectEvent.ACTION_START, this, act));
		act.doIt(this);
		while (undoLog.size() > MAX_UNDO_SIZE) {
			undoLog.removeFirst();
		}
		if (toAdd.isModification()) ++undoMods;
		file.setDirty(isFileDirty());
		fireEvent(new ProjectEvent(ProjectEvent.ACTION_COMPLETE, this, act));

	}

	public void undoAction() {

		if (undoLog != null && undoLog.size() > 0) {
			ActionData data = undoLog.removeLast();
			setCircuitState(data.circuitState);
			Action action = data.action;
			if (action.isModification()) --undoMods;
			fireEvent(new ProjectEvent(ProjectEvent.UNDO_START, this, action));
			action.undo(this);
			file.setDirty(isFileDirty());
			fireEvent(new ProjectEvent(ProjectEvent.UNDO_COMPLETE, this, action));
		}

	}

	public void setFileAsClean() {
		undoMods = 0;
		file.setDirty(isFileDirty());
	}

}
