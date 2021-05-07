package com.cburch.LogisimFX.newgui.MainFrame;

import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.circuit.CircuitState;
import com.cburch.LogisimFX.circuit.SubcircuitFactory;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.LogisimFX.tools.Tool;

import javafx.scene.control.*;
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

                    if (!empty) {

                        if(item instanceof CircuitState){

                            SubcircuitFactory buff = ((CircuitState) item).getCircuit().getSubcircuitFactory();

                            setText(buff.getName());
                            setTooltip(new Tooltip(buff.getDefaultToolTip().toString()));
                            setGraphic(buff.getIcon());

                        }
                        else{
                            setText("you fucked up2");
                        }

                    } else {

                        setText(null);
                        setGraphic(null);

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

        TreeItem<Object> root = new TreeItem<>(proj.getCircuitState(proj.getCurrentCircuit()));
        this.setRoot(root);
        root.expandedProperty().set(true);

        getChildren(root);

    }

    public void getChildren(TreeItem root){

        System.out.println(((CircuitState)root.getValue()).getSubstates().size());

        for (CircuitState st: ((CircuitState)root.getValue()).getSubstates()) {

            TreeItem<Object> subRoot = new TreeItem<>(st);
            root.getChildren().add(subRoot);
            getChildren(subRoot);

        }

    }

}
