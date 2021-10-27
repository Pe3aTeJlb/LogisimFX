package LogisimFX.newgui.MainFrame.ProjectExplorer;

import LogisimFX.circuit.SubcircuitFactory;
import LogisimFX.file.LogisimFile;
import LogisimFX.newgui.ContextMenuManager;
import LogisimFX.proj.Project;
import LogisimFX.tools.AddTool;
import LogisimFX.tools.Library;
import LogisimFX.tools.Tool;
import LogisimFX.circuit.Circuit;
import LogisimFX.comp.ComponentFactory;

import javafx.scene.control.*;
import javafx.scene.input.MouseButton;

public class ProjectTreeExplorer extends AbstractTreeExplorer {

    private Project proj;

    public ProjectTreeExplorer(Project project){

        super();

        this.proj = project;

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

                        event.consume();

                        if(treeItem.getValue() instanceof AddTool){

                            ComponentFactory fact = ((AddTool) treeItem.getValue()).getFactory(false);
                            if (fact instanceof SubcircuitFactory) {

                                proj.setCurrentCircuit(
                                        ((SubcircuitFactory) fact).getSubcircuit()
                                );

                                proj.setTool(null);

                               //cell.setStyle(highlight);

                            }

                        }


                    }else if (event.getButton().equals(MouseButton.PRIMARY)){

                        if(treeItem.getValue() instanceof AddTool){
                            proj.setTool((Tool)treeItem.getValue());
                        }

                        if(treeItem.getValue() instanceof Tool){
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