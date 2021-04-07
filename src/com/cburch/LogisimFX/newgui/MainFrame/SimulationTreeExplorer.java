package com.cburch.LogisimFX.newgui.MainFrame;

import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.circuit.CircuitState;
import com.cburch.LogisimFX.file.LogisimFile;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.tools.Tool;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;

public class SimulationTreeExplorer extends AbstractTreeExplorer {

    private Project proj;

    public SimulationTreeExplorer(Project project){

        super();

        this.proj = project;

        MultipleSelectionModel<TreeItem> selectionModel = this.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        this.setCellFactory(tree -> {

            TreeCell<Object> cell = new TreeCell<Object>() {

                public void updateItem(Object item, boolean empty) {

                    super.updateItem(item, empty) ;

                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {

                        if(item instanceof Library){
                            setText(((Library)item).getName());
                            setGraphic(new ImageView(new Image("com/cburch/LogisimFX/resources/icons/poke.gif")));
                        }
                        else if(item instanceof Tool){
                            setText(((Tool)item).getName());
                            //setGraphic(new ImageView(new Image("com/cburch/LogisimFX/resources/icons/poke.gif")));
                        }
                        else if(item instanceof LogisimFile){
                            setText(proj.getLogisimFile().getName());
                            //setGraphic(new ImageView(new Image("com/cburch/LogisimFX/resources/icons/poke.gif")));
                        }
                        else{
                            setText("???");
                        }

                    }

                }

            };

            cell.setOnMouseClicked(event -> {
                if (!cell.isEmpty()) {

                    TreeItem<Object> treeItem = cell.getTreeItem();

                    if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2 && !event.isConsumed()) {
                        System.out.println("double click");
                        event.consume();

                        if(treeItem.getValue() instanceof Circuit){
                            project.setCurrentCircuit((Circuit) treeItem.getValue());
                        }

                    }else if (event.getButton().equals(MouseButton.PRIMARY)){
                        System.out.println("Left click");

                    }else if(event.getButton().equals(MouseButton.SECONDARY)){
                        System.out.println("Right click");
                        if(treeItem.getValue() instanceof Tool){
                            //project.setCurrentCircuit((Circuit) treeItem.getValue());
                        }

                    }

                }
            });

            cell.setOnMouseEntered(event -> {
                System.out.println("entered");
            });

            return cell ;

        });


        //todo: create update binding to proj tool.size

        updateTree();

    }

    public void updateTree(){

        //this.getChildren().clear();

        //showingProjectTree = false;

        TreeItem<Circuit> root = new TreeItem<>(proj.getCurrentCircuit());
        root.setGraphic(new ImageView(new Image("com/cburch/LogisimFX/resources/icons/subcirc.gif")));
        this.setRoot(root);
        root.expandedProperty().set(true);

        for (CircuitState cState: proj.getCircuitState().getSubstates()) {

            TreeItem<Circuit> c = new TreeItem<>(cState.getCircuit());
            c.setGraphic(new ImageView(new Image("com/cburch/LogisimFX/resources/icons/subcirc.gif")));
            root.getChildren().add(c);

        }

    }

}
