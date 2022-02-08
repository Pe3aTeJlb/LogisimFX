/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.newgui.CircLogFrame;


import LogisimFX.circuit.CircuitState;
import LogisimFX.data.Value;
import LogisimFX.util.EventSourceWeakSupport;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Model {

	private EventSourceWeakSupport<ModelListener> listeners;
	private ObservableList<SelectionItem> components;
	private HashMap<SelectionItem, ValueLog> log;
	private boolean fileEnabled = false;
	private File file = null;
	private boolean fileHeader = true;
	private boolean selected = false;
	private LogThread logger = null;
	private CircuitState circuitState;
	private ArrayList<Value[]> values = new ArrayList<>();
	
	public Model(CircuitState circuitState) {
		listeners = new EventSourceWeakSupport<>();
		this.circuitState = circuitState;
		log = new HashMap<>();
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void addModelListener(ModelListener l) { listeners.add(l); }
	public void removeModelListener(ModelListener l) { listeners.remove(l); }
	
	public CircuitState getCircuitState() {
		return circuitState;
	}
	
	public ValueLog getValueLog(SelectionItem item) {
		ValueLog ret = log.get(item);
		if (ret == null && components.contains(item)) {
			ret = new ValueLog();
			log.put(item, ret);
		}
		return ret;
	}

	public ObservableList<SelectionItem> getComponents(){
		return components;
	}

	public ArrayList<Value[]> getValues(){
		return values;
	}

	public void bindComponentsList(ObservableList<SelectionItem> newComps){
		this.components = newComps;
	}
	
	public boolean isFileEnabled() {
		return fileEnabled;
	}
	
	public File getFile() {
		return file;
	}

	public void setFileEnabled(boolean value) {
		if (fileEnabled == value) return;
		fileEnabled = value;
		fireFilePropertyChanged(new ModelEvent());
	}
	
	public void setFile(File value) {
		if (file == null ? value == null : file.equals(value)) return;
		file = value;
		fileEnabled = file != null;
		fireFilePropertyChanged(new ModelEvent());
	}

	public void propagationCompleted() {
		CircuitState circuitState = getCircuitState();
		Value[] vals = new Value[components.size()];
		boolean changed = false;
		for (int i = components.size() - 1; i >= 0; i--) {
			SelectionItem item = components.get(i);
			vals[i] = item.fetchValue(circuitState);
			if (!changed) {
				Value v = getValueLog(item).getLast();
				changed = v == null ? vals[i] != null : !v.equals(vals[i]);
			}
		}
		//if (changed) {
			for (int i = components.size() - 1; i >= 0; i--) {
				SelectionItem item = components.get(i);
				getValueLog(item).append(vals[i]);
			}
			values.add(vals);
			fireEntryAdded(new ModelEvent(), vals);
		//}
	}

	void fireSelectionChanged(ModelEvent e) {
		log.keySet().removeIf(i -> !components.contains(i));

		for (ModelListener l : listeners) {
			l.selectionChanged(e);
		}
	}
	
	private void fireEntryAdded(ModelEvent e, Value[] values) {
		for (ModelListener l : listeners) {
			l.entryAdded(e, values);
		}
	}
	
	private void fireFilePropertyChanged(ModelEvent e) {
		for (ModelListener l : listeners) {
			l.filePropertyChanged(e);
		}
	}
}
