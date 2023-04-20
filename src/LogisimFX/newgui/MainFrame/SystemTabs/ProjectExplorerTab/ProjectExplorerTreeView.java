/*
 * This file is part of LogisimFX. Copyright (c) 2023, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.MainFrame.SystemTabs.ProjectExplorerTab;

import LogisimFX.IconsManager;
import LogisimFX.circuit.*;
import LogisimFX.comp.ComponentFactory;
import LogisimFX.file.LibraryEvent;
import LogisimFX.file.LibraryEventSource;
import LogisimFX.file.LibraryListener;
import LogisimFX.file.LogisimFile;
import LogisimFX.newgui.ContextMenuManager;
import LogisimFX.newgui.MainFrame.SystemTabs.AbstractTreeExplorer;
import LogisimFX.prefs.AppPreferences;
import LogisimFX.proj.Project;
import LogisimFX.proj.ProjectEvent;
import LogisimFX.proj.ProjectListener;
import LogisimFX.tools.AddTool;
import LogisimFX.tools.Library;
import LogisimFX.tools.Tool;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Collections;

import static LogisimFX.file.LibraryEvent.REMOVE_TOOL;

public class ProjectExplorerTreeView extends AbstractTreeExplorer<Object> {

    private Project proj;

    private Tool prevTool;
    private TreeView<Object> treeView;

    private MyListener myListener = new MyListener();
    private SubListener subListener = new SubListener();

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
                treeView.refresh();
            }else if (act == ProjectEvent.ACTION_SET_STATE) {
                treeView.refresh();
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
                        TreeItem<Object> treeItem = new TreeItem<>(tool);
                        if (proj.getLogisimFile().getMainCircuit() == fact.getSubcircuit()){
                            treeItem.getChildren().addAll(new TreeItem<>(fact.getSubcircuit().getTopLevelShell(proj)));
                        }
                        treeItem.getChildren().addAll(
                                new TreeItem<>(fact.getSubcircuit().getVerilogModel(proj)),
                                new TreeItem<>(fact.getSubcircuit().getHLS(proj))
                        );
                        treeView.getRoot().getChildren().add(proj.getLogisimFile().getTools().size()-1, treeItem);
                    }
                }
            } else if (act == REMOVE_TOOL) {
                if (event.getData() instanceof AddTool) {
                    AddTool tool = (AddTool) event.getData();
                    if (tool.getFactory() instanceof SubcircuitFactory) {
                        SubcircuitFactory fact = (SubcircuitFactory) tool.getFactory();
                        fact.getSubcircuit().removeCircuitListener(this);
                        TreeItem<Object> item = treeView.getRoot().getChildren().stream().filter(c -> c.getValue().equals(tool)).findFirst().get();
                        treeView.getRoot().getChildren().remove(item);
                    }
                }
            } else if (act == LibraryEvent.ADD_LIBRARY) {
                if (event.getData() instanceof LibraryEventSource) {
                    ((LibraryEventSource) event.getData()).addLibraryListener(subListener);
                }
                updateTree();
            } else if (act == LibraryEvent.REMOVE_LIBRARY) {
                if (event.getData() instanceof LibraryEventSource) {
                    ((LibraryEventSource) event.getData()).removeLibraryListener(subListener);
                }
                updateTree();
            } else if (act == LibraryEvent.DIRTY_STATE || act == LibraryEvent.SET_NAME){
                treeView.refresh();
            } else if (act == LibraryEvent.MOVE_TOOL){
                TreeItem<Object> item = treeView.getRoot().getChildren().stream().filter(c -> c.getValue().equals(event.getData())).findFirst().get();
                Collections.swap(treeView.getRoot().getChildren(), treeView.getRoot().getChildren().indexOf(item), proj.getLogisimFile().getTools().indexOf(item.getValue()));
                treeView.getSelectionModel().select(item);
                refresh();
                //updateTree();
            } else if (act == LibraryEvent.SET_MAIN){
                for(TreeItem<Object> child: treeView.getRoot().getChildren()){
                    if (child.getValue() instanceof AddTool){
                        SubcircuitFactory fact = (SubcircuitFactory) ((AddTool)child.getValue()).getFactory();
                        if (fact.getSubcircuit() == proj.getLogisimFile().getMainCircuit()){
                            child.getChildren().add(0, new TreeItem<>(fact.getSubcircuit().getTopLevelShell(proj)));
                        } else {
                            if (child.getChildren().size() == 3){
                                child.getChildren().remove(0);
                            }
                        }
                    }
                }
                refresh();
            } else {
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
                Platform.runLater(() -> updateTree());
            }
        }

    }

    public ProjectExplorerTreeView(Project project){

        super();

        this.proj = project;
        treeView = this;

        proj.addProjectListener(myListener);
        proj.addLibraryListener(myListener);
        AppPreferences.GATE_SHAPE.addPropertyChangeListener(myListener);
        myListener.setFile(proj.getLogisimFile());

        MultipleSelectionModel<TreeItem<Object>> selectionModel = this.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        this.setCellFactory(tree -> {

            TreeCell<Object> cell = new TreeCell<Object>() {

                @Override
                public void updateItem(Object item, boolean empty) {

                    super.updateItem(item, empty);

                    textProperty().unbind();

                    //getStylesheets().add("LogisimFX/resources/css/treeview.css");

                    if (empty || item == null) {

                        setText(null);
                        setGraphic(null);
                        setTooltip(null);
                        setContextMenu(null);

                    } else {

                        if (item instanceof LogisimFile) {

                            setText(proj.getLogisimFile().getName());
                            setGraphic(null);
                            setTooltip(null);
                            setContextMenu(ContextMenuManager.ProjectContextMenu(proj));

                        } else if (item instanceof Library) {

                            textProperty().bind(((Library) item).getDisplayName());
                            setGraphic(null);
                            setTooltip(null);
                            setContextMenu(ContextMenuManager.LibraryContextMenu(proj, (Library) item));

                        } else if (item instanceof Tool) {

                            textProperty().bind(((Tool) item).getDisplayName());

                            Tooltip tip = new Tooltip();
                            tip.textProperty().bind(((Tool) item).getDescription());
                            setTooltip(tip);

                            setGraphic(new ImageView(((Tool) item).getIcon().getImage()));
                            //setGraphic(((Tool) item).getIcon());


                            if (item instanceof AddTool) {

                                ComponentFactory fact = ((AddTool) item).getFactory(false);

                                if (fact instanceof SubcircuitFactory) {

                                    Circuit circ = ((SubcircuitFactory) fact).getSubcircuit();

                                    if(proj.getCurrentCircuit() == circ){
                                        setGraphic(IconsManager.getIcon("currsubcirc.gif"));
                                    }

                                    setContextMenu(ContextMenuManager.CircuitContextMenu(proj, circ));

                                }

                            } else {
                                setContextMenu(null);
                            }

                        } else if (item instanceof File){

                            setText(((File)item).getName());
                            setGraphic(IconsManager.getIcon("code.gif"));

                        } else {
                            setText("you fucked up");
                        }

                    }

                }

            };

            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, (MouseEvent event) -> {

                if (!cell.isEmpty()) {

                    TreeItem<Object> treeItem = cell.getTreeItem();

                    if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2
                            && !event.isConsumed()) {

                        if(treeItem.getValue() instanceof AddTool){

                            ComponentFactory fact = ((AddTool) treeItem.getValue()).getFactory(false);
                            if (fact instanceof SubcircuitFactory) {

                                proj.setTool(prevTool);
                                proj.getFrameController().addCircLayoutEditor(((SubcircuitFactory) fact).getSubcircuit());
                                proj.setCircuitState(new CircuitState(proj, ((SubcircuitFactory) fact).getSubcircuit()));
                                event.consume();
                            }

                        } else if (treeItem.getValue() instanceof File){

                            ComponentFactory fact = ((AddTool) treeItem.getParent().getValue()).getFactory(false);

                            proj.getFrameController().addCodeEditor(
                                    ((SubcircuitFactory) fact).getSubcircuit(),
                                    (File)treeItem.getValue()
                            );

                        }

                    } else if (event.getButton().equals(MouseButton.PRIMARY)){

                        prevTool = proj.getTool();

                        if(treeItem.getValue() instanceof AddTool || treeItem.getValue() instanceof Tool){
                            proj.setTool((Tool)treeItem.getValue());
                        }

                    }

                }

            });

            return cell ;

        });

        updateTree();

    }

    public void updateTree(){

        TreeItem<Object> root = new TreeItem<>(proj.getLogisimFile());
        this.setRoot(root);
        root.expandedProperty().set(true);

        //Circuits
        for (AddTool tool: proj.getLogisimFile().getTools()) {
            root.getChildren().add(addCircNode(tool));
        }

        //Libs and tools
        for (Library lib: proj.getLogisimFile().getLibraries()) {
           root.getChildren().add(addLibraryNode(lib));
        }

    }

    private TreeItem<Object> addCircNode(AddTool tool){

        TreeItem<Object> circ = new TreeItem<>(tool);

        SubcircuitFactory fact = (SubcircuitFactory) tool.getFactory();

        if (fact.getSubcircuit() == proj.getLogisimFile().getMainCircuit()){
            circ.getChildren().add(
                    new TreeItem<>(((SubcircuitFactory)tool.getFactory()).getSubcircuit().getTopLevelShell(proj))
            );
        }

        circ.getChildren().addAll(
                new TreeItem<>(((SubcircuitFactory)tool.getFactory()).getSubcircuit().getVerilogModel(proj)),
                new TreeItem<>(((SubcircuitFactory)tool.getFactory()).getSubcircuit().getHLS(proj))
        );

        return circ;

    }

    private TreeItem<Object> addLibraryNode(Library lib){

        TreeItem<Object> l = new TreeItem<>(lib);

        for (Tool tool: lib.getTools()) {

            TreeItem<Object> t = null;
            
            if (tool instanceof AddTool) {
                ComponentFactory fact = ((AddTool) tool).getFactory(false);
                if (fact instanceof SubcircuitFactory) {
                    t = addCircNode((AddTool)tool);
                }
            } else {
                t = new TreeItem<>(tool);
            }

            l.getChildren().add(t);

        }

        for (Library sublib: lib.getLibraries()) {
            l.getChildren().add(addLibraryNode(sublib));
        }

        return l;

    }
    
}
