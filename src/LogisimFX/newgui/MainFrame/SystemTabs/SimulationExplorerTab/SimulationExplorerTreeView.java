package LogisimFX.newgui.MainFrame.SystemTabs.SimulationExplorerTab;

import LogisimFX.IconsManager;
import LogisimFX.circuit.*;
import LogisimFX.comp.Component;
import LogisimFX.file.LibraryEvent;
import LogisimFX.file.LibraryEventSource;
import LogisimFX.file.LibraryListener;
import LogisimFX.file.LogisimFile;
import LogisimFX.newgui.MainFrame.SystemTabs.AbstractTreeExplorer;
import LogisimFX.prefs.AppPreferences;
import LogisimFX.proj.Project;
import LogisimFX.proj.ProjectEvent;
import LogisimFX.proj.ProjectListener;
import LogisimFX.tools.AddTool;
import LogisimFX.tools.Library;
import javafx.application.Platform;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseButton;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class SimulationExplorerTreeView extends AbstractTreeExplorer<Object> {

    private Project proj;
    private CircuitState superState;

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

    public SimulationExplorerTreeView(Project project){

        super();

        this.proj = project;

        proj.addProjectListener(myListener);
        proj.addLibraryListener(myListener);
        myListener.setFile(proj.getLogisimFile());

        MultipleSelectionModel<TreeItem<Object>> selectionModel = this.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        this.setCellFactory(tree -> {

            TreeCell<Object> cell = new TreeCell<Object>() {

                @Override
                public void updateItem(Object item, boolean empty) {

                    super.updateItem(item, empty) ;

                    if(empty || item == null) {

                        setText(null);
                        setGraphic(null);

                    }else{

                        if(item instanceof CircuitState){

                            SubcircuitFactory buff = ((CircuitState) item).getCircuit().getSubcircuitFactory();

                            setText(buff.getName());

                            if(proj.getCircuitState() == item){
                                setGraphic(IconsManager.getIcon("currsubcirc.gif"));
                            }else {
                                setGraphic(IconsManager.getIcon("subcirc.gif"));
                            }

                        }
                        else{
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

                        event.consume();

                        if(treeItem.getValue() instanceof CircuitState){
                            //project.setCircuitState((CircuitState) treeItem.getValue());
                            //project.getFrameController().selectCircLayoutEditor(((CircuitState) treeItem.getValue()).getCircuit(), (CircuitState) treeItem.getValue());
                            project.setCurrentCircuit(((CircuitState) treeItem.getValue()).getCircuit());
                        }

                    }
                }

            });

            return cell;

        });

        updateTree();

    }

    public void updateTree(){

        superState = proj.getCircuitState();
        while (superState.getParentState() != null){
            superState = superState.getParentState();
        }
        //superState = proj.getCircuitState(proj.getCurrentCircuit());

        TreeItem<Object> root = new TreeItem<>(superState);
        this.setRoot(root);
        root.setExpanded(true);

        getChildren(root);

    }

    private void getChildren(TreeItem root){

        for (Component comp : ((CircuitState)root.getValue()).getCircuit().getNonWires()) {

            if (comp.getFactory() instanceof SubcircuitFactory) {

                SubcircuitFactory factory = (SubcircuitFactory) comp.getFactory();
                CircuitState state = factory.getSubstate(superState, comp);

                TreeItem<Object> subRoot = new TreeItem<>(state);
                root.getChildren().add(subRoot);
                root.setExpanded(true);

                getChildren(subRoot);

            }

        }

    }

}
