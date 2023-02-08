package LogisimFX.newgui.MainFrame.SystemTabs.SimulationExplorerTab;

import LogisimFX.IconsManager;
import LogisimFX.circuit.*;
import LogisimFX.comp.Component;
import LogisimFX.file.LibraryEvent;
import LogisimFX.file.LibraryEventSource;
import LogisimFX.file.LibraryListener;
import LogisimFX.file.LogisimFile;
import LogisimFX.instance.StdAttr;
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

                        if(item instanceof Component){

                            String label = ((Component) item).getAttributeSet().getValue(StdAttr.LABEL);
                            if (!label.equals("")){
                                setText(label);
                            } else {
                                setText(((Component) item).getFactory().getName() + " " + ((Component) item).getLocation().toString());
                            }

                            if(proj.getCircuitState() == ((SubcircuitFactory)((Component) item).getFactory()).getSubstate(superState, (Component) item)){
                                setGraphic(IconsManager.getIcon("currsubcirc.gif"));
                            }else {
                                setGraphic(IconsManager.getIcon("subcirc.gif"));
                            }

                        } else if (item instanceof CircuitState){
                            setText(((CircuitState) item).getCircuit().getName());
                            if(proj.getCircuitState() == item){
                                setGraphic(IconsManager.getIcon("currsubcirc.gif"));
                            }else {
                                setGraphic(IconsManager.getIcon("subcirc.gif"));
                            }
                        } else{
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

                        if(treeItem.getValue() instanceof Component){
                            //project.setCircuitState((CircuitState) treeItem.getValue());
                            //project.getFrameController().selectCircLayoutEditor(((CircuitState) treeItem.getValue()).getCircuit(), (CircuitState) treeItem.getValue());
                            //project.setCurrentCircuit(((CircuitState) treeItem.getValue()).getCircuit());
                            Circuit oldCirc = project.getCurrentCircuit();
                            project.getFrameController().addCircLayoutEditor(((SubcircuitFactory)((Component) treeItem.getValue()).getFactory()).getSubcircuit());
                            project.setCurrentCircuit(oldCirc);
                            proj.setCircuitState(((SubcircuitFactory)((Component) treeItem.getValue()).getFactory()).getSubstate(superState, (Component) treeItem.getValue()));
                        } else {
                            Circuit oldCirc = project.getCurrentCircuit();
                            project.getFrameController().addCircLayoutEditor(((CircuitState) treeItem.getValue()).getCircuit());
                            project.setCurrentCircuit(oldCirc);
                            proj.setCircuitState((CircuitState) treeItem.getValue());
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

        TreeItem<Object> root = new TreeItem<>(superState);
        this.setRoot(root);
        root.setExpanded(true);

        for (Component comp : superState.getCircuit().getNonWires()){

            if (comp.getFactory() instanceof SubcircuitFactory) {

                TreeItem<Object> subRoot = new TreeItem<>(comp);
                root.getChildren().add(subRoot);
                TreeItem<Object> child = getChildren(subRoot);
                if (child != null) subRoot.getChildren().add(child);
                subRoot.setExpanded(true);

            }

        }

    }

    private TreeItem<Object> getChildren(TreeItem<Object> root){

        TreeItem<Object> subRoot = null;

        for (Component comp: ((SubcircuitFactory)((Component) root.getValue()).getFactory()).getSubcircuit().getNonWires()){

            if (comp.getFactory() instanceof SubcircuitFactory) {

                subRoot = new TreeItem<>(comp);
                root.getChildren().add(subRoot);
                subRoot.getChildren().add(getChildren(subRoot));
                subRoot.setExpanded(true);

            }

        }

        return subRoot;

    }

}
