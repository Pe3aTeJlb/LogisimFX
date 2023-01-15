package LogisimFX.newgui.MainFrame.SystemTabs;

import LogisimFX.IconsManager;
import LogisimFX.circuit.Circuit;
import LogisimFX.circuit.CircuitEvent;
import LogisimFX.circuit.CircuitListener;
import LogisimFX.circuit.SubcircuitFactory;
import LogisimFX.comp.ComponentFactory;
import LogisimFX.file.LibraryEvent;
import LogisimFX.file.LibraryEventSource;
import LogisimFX.file.LibraryListener;
import LogisimFX.file.LogisimFile;
import LogisimFX.newgui.ContextMenuManager;
import LogisimFX.prefs.AppPreferences;
import LogisimFX.proj.Project;
import LogisimFX.proj.ProjectEvent;
import LogisimFX.proj.ProjectListener;
import LogisimFX.tools.AddTool;
import LogisimFX.tools.Library;
import LogisimFX.tools.Tool;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ProjectExplorerTreeView extends AbstractTreeExplorer {

    private Project proj;

    private Tool prevTool;

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
                Platform.runLater(() -> updateTree());
            }
        }

    }

    public ProjectExplorerTreeView(Project project){

        super();

        this.proj = project;

        proj.addProjectListener(myListener);
        proj.addLibraryListener(myListener);
        AppPreferences.GATE_SHAPE.addPropertyChangeListener(myListener);
        myListener.setFile(proj.getLogisimFile());

        MultipleSelectionModel<TreeItem> selectionModel = this.getSelectionModel();
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

                            setGraphic(((Tool) item).getIcon());

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

                        } else {
                            setText("you fucked up2");
                        }

                    }

                }

            };

            cell.setOnMouseClicked(event -> {

                if (!cell.isEmpty()) {

                    TreeItem<Object> treeItem = cell.getTreeItem();

                    if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2
                            && !event.isConsumed()) {

                        if(treeItem.getValue() instanceof AddTool){

                            ComponentFactory fact = ((AddTool) treeItem.getValue()).getFactory(false);
                            if (fact instanceof SubcircuitFactory) {

                                proj.setCurrentCircuit(
                                        ((SubcircuitFactory) fact).getSubcircuit()
                                );

                                proj.setTool(prevTool);

                            }

                        }

                        event.consume();

                    }else if (event.getButton().equals(MouseButton.PRIMARY)){

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

            TreeItem<Object> l = new TreeItem<>(tool);
            root.getChildren().add(l);

        }

        //Libs and tools
        for (Library lib: proj.getLogisimFile().getLibraries()) {

            TreeItem<Object> l = new TreeItem<>(lib);
            root.getChildren().add(l);

            for (Tool tool: lib.getTools()) {

                TreeItem<Object> t = new TreeItem<>(tool);
                l.getChildren().add(t);

            }

        }

    }
    
}
