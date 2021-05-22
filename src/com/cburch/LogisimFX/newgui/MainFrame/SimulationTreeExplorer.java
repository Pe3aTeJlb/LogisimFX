package com.cburch.LogisimFX.newgui.MainFrame;

import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.circuit.CircuitState;
import com.cburch.LogisimFX.circuit.SubcircuitFactory;
import com.cburch.LogisimFX.comp.Component;
import com.cburch.LogisimFX.proj.Project;

import javafx.scene.control.*;
import javafx.scene.input.MouseButton;

public class SimulationTreeExplorer extends AbstractTreeExplorer {

    private Project proj;
    private CircuitState superState;

    public SimulationTreeExplorer(Project project){

        super();

        this.proj = project;

        MultipleSelectionModel<TreeItem> selectionModel = this.getSelectionModel();
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
                            setGraphic(buff.getIcon());

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

                        if(treeItem.getValue() instanceof Circuit){
                            project.setCurrentCircuit((Circuit) treeItem.getValue());
                            //Todo: appearance view
                        }

                    }
                }

            });

            return cell;

        });

        updateTree();

    }

    public void updateTree(){

        superState = proj.getCircuitState(proj.getCurrentCircuit());

        TreeItem<Object> root = new TreeItem<>(superState);
        this.setRoot(root);
        root.setExpanded(true);

        getChildren(root);

    }

    public void getChildren(TreeItem root){

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

        System.out.println(((CircuitState)root.getValue()).getCircuit().getNonWires().size());

    }

}
