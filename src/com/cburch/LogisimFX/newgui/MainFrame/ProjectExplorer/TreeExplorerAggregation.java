package com.cburch.LogisimFX.newgui.MainFrame.ProjectExplorer;

import com.cburch.LogisimFX.circuit.*;
import com.cburch.LogisimFX.file.LibraryEvent;
import com.cburch.LogisimFX.file.LibraryEventSource;
import com.cburch.LogisimFX.file.LibraryListener;
import com.cburch.LogisimFX.file.LogisimFile;
import com.cburch.LogisimFX.prefs.AppPreferences;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.LogisimFX.proj.ProjectEvent;
import com.cburch.LogisimFX.proj.ProjectListener;
import com.cburch.LogisimFX.tools.AddTool;
import com.cburch.LogisimFX.tools.Library;

import javafx.scene.control.TreeView;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class TreeExplorerAggregation extends TreeView {

    private Project proj;

    private MyListener myListener = new MyListener();
    private SubListener subListener = new SubListener();

    private ProjectTreeExplorer projectTreeExplorer;
    private SimulationTreeExplorer simulationTreeExplorer;
    private AbstractTreeExplorer currTreeExplorer;

    private class SubListener implements LibraryListener {
        public void libraryChanged(LibraryEvent event) {
            updateTree();
        }
    }

    private class MyListener
            implements ProjectListener, LibraryListener, CircuitListener, PropertyChangeListener {

        //
        // project/library file/circuit listener methods
        //
        public void projectChanged(ProjectEvent event) {
            int act = event.getAction();
            if (act == ProjectEvent.ACTION_SET_FILE) {
                setFile(event.getLogisimFile());
            } else if (act == ProjectEvent.ACTION_SET_CURRENT) {
                updateTree();
            }else if (act == ProjectEvent.ACTION_SET_STATE) {
                updateTree();
            }
        }

        public void libraryChanged(LibraryEvent event) {
            int act = event.getAction();
            if (act == LibraryEvent.ADD_TOOL) {
                if (event.getData() instanceof AddTool) {
                    AddTool tool = (AddTool) event.getData();
                    if (tool.getFactory() instanceof SubcircuitFactory) {
                        SubcircuitFactory fact = (SubcircuitFactory) tool.getFactory();
                        fact.getSubcircuit().addCircuitListener(this);
                    }
                }
            } else if (act == LibraryEvent.REMOVE_TOOL) {
                if (event.getData() instanceof AddTool) {
                    AddTool tool = (AddTool) event.getData();
                    if (tool.getFactory() instanceof SubcircuitFactory) {
                        SubcircuitFactory fact = (SubcircuitFactory) tool.getFactory();
                        fact.getSubcircuit().removeCircuitListener(this);
                    }
                }
            } else if (act == LibraryEvent.ADD_LIBRARY) {
                if (event.getData() instanceof LibraryEventSource) {
                    ((LibraryEventSource) event.getData()).addLibraryListener(subListener);
                }
            } else if (act == LibraryEvent.REMOVE_LIBRARY) {
                if (event.getData() instanceof LibraryEventSource) {
                    ((LibraryEventSource) event.getData()).removeLibraryListener(subListener);
                }
            }
            switch (act) {
                case LibraryEvent.DIRTY_STATE:
                case LibraryEvent.SET_NAME:
                    updateTree();
                    break;
                case LibraryEvent.MOVE_TOOL:
                    updateTree();
                    break;
                case LibraryEvent.SET_MAIN:
                    break;
                default:
                    updateTree();
            }
        }

        public void circuitChanged(CircuitEvent event) {
            int act = event.getAction();
            if (act == CircuitEvent.ACTION_SET_NAME) {
                updateTree();
                // The following almost works - but the labels aren't made
                // bigger, so you get "..." behavior with longer names.
                // model.fireNodesChanged(model.findPaths(event.getCircuit()));
            }
        }

        private void setFile(LogisimFile lib) {

            updateTree();

            for (Circuit circ : lib.getCircuits()) {
                circ.addCircuitListener(this);
            }

            subListener = new SubListener(); // create new one so that old listeners die away
            for (Library sublib : lib.getLibraries()) {
                if (sublib instanceof LibraryEventSource) {
                    ((LibraryEventSource) sublib).addLibraryListener(subListener);
                }
            }
        }

        //
        // PropertyChangeListener methods
        //
        public void propertyChange(PropertyChangeEvent event) {
            if (AppPreferences.GATE_SHAPE.isSource(event)) {
                updateTree();
            }
        }

    }

    public TreeExplorerAggregation(Project project){

        super();

        proj = project;

        projectTreeExplorer = new ProjectTreeExplorer(proj);
        simulationTreeExplorer = new SimulationTreeExplorer(proj);

        currTreeExplorer = projectTreeExplorer;

        proj.addProjectListener(myListener);
        proj.addLibraryListener(myListener);
        AppPreferences.GATE_SHAPE.addPropertyChangeListener(myListener);
        myListener.setFile(proj.getLogisimFile());

        setProjectView();

    }

    public void setSimulationView(){

        currTreeExplorer = simulationTreeExplorer;

        this.setCellFactory(simulationTreeExplorer.getCellFactory());
        this.setRoot(simulationTreeExplorer.getRoot());

        updateTree();

    }

    public void setProjectView(){

        currTreeExplorer = projectTreeExplorer;

        this.setCellFactory(projectTreeExplorer.getCellFactory());
        this.setRoot(projectTreeExplorer.getRoot());

        updateTree();

    }

    public void updateTree(){

        currTreeExplorer.updateTree();

        this.setRoot(null);
        this.setCellFactory(null);

        this.setCellFactory(currTreeExplorer.getCellFactory());
        this.setRoot(currTreeExplorer.getRoot());

    }

}
