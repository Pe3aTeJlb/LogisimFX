package com.cburch.LogisimFX.newgui.MainFrame;

import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.circuit.CircuitState;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.logisim.tools.Tool;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;

import java.util.Set;

public class SimulationTreeExplorer extends AbstractTreeExplorer {

    private Project proj;
    private TreeItem<CircuitState> root;

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

                        if(item instanceof CircuitState){
                            setText(((CircuitState)item).getCircuit().getName());
                            setGraphic(new ImageView(new Image("com/cburch/LogisimFX/resources/icons/poke.gif")));
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
               // System.out.println("entered");
            });

            return cell ;

        });

        //todo: create update binding to proj tool.size

        updateTree();

    }

    public void updateTree(){

        //this.getChildren().clear();

        //showingProjectTree = false;

        TreeItem<CircuitState> root = new TreeItem<>(proj.getSimulator().getCircuitState());
        root.setGraphic(new ImageView(new Image("com/cburch/LogisimFX/resources/icons/poke.gif")));
        this.setRoot(root);
        root.expandedProperty().set(true);

        for (CircuitState cState: proj.getCircuitState().getSubstates()) {

            TreeItem<CircuitState> c = new TreeItem<>(cState);
            c.setGraphic(new ImageView(new Image("com/cburch/LogisimFX/resources/icons/subcirc.gif")));
            root.getChildren().add(c);

        }
/*
        for (CircuitState cState: upd(proj.getCircuitState())) {

            TreeItem<CircuitState> c = new TreeItem<>(cState);
            c.setGraphic(new ImageView(new Image("com/cburch/LogisimFX/resources/icons/subcirc.gif")));
            root.getChildren().add(c);

        }

 */

    }

    public Set<CircuitState> upd(CircuitState state){

        for (CircuitState st: state.getSubstates()) {

            TreeItem<CircuitState> subRoot = new TreeItem<>(st);
            subRoot.setGraphic(new ImageView(new Image("com/cburch/LogisimFX/resources/icons/subcirc.gif")));
            root.getChildren().add(subRoot);

            upd(st);
        }

        return state.getSubstates();
    }

}
