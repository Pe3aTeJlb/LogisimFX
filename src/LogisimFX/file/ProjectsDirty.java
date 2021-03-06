/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.file;

import LogisimFX.newgui.FrameManager;
import LogisimFX.proj.Project;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;

class ProjectsDirty {
	private ProjectsDirty() { }
	
	private static class DirtyListener implements LibraryListener {
		Project proj;
		
		DirtyListener(Project proj) {
			this.proj = proj;
		}
		
		public void libraryChanged(LibraryEvent event) {
			if (event.getAction() == LibraryEvent.DIRTY_STATE) {
				LogisimFile lib = proj.getLogisimFile();
				File file = lib.getLoader().getMainFile();
				LibraryManager.instance.setDirty(file, lib.isDirty());
			}
		}
	}
	
	private static class ProjectListListener implements PropertyChangeListener {
		public synchronized void propertyChange(PropertyChangeEvent event) {
			for (DirtyListener l : listeners) {
				l.proj.removeLibraryListener(l);
			}
			listeners.clear();
			for (Project proj : FrameManager.getOpenProjects()) {
				DirtyListener l = new DirtyListener(proj);
				proj.addLibraryListener(l);
				listeners.add(l);
				
				LogisimFile lib = proj.getLogisimFile();
				LibraryManager.instance.setDirty(lib.getLoader().getMainFile(), lib.isDirty());
			}
		}
	}
	
	private static ProjectListListener projectListListener = new ProjectListListener();
	private static ArrayList<DirtyListener> listeners = new ArrayList<DirtyListener>();

}
