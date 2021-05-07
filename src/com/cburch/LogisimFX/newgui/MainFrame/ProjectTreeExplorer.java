package com.cburch.LogisimFX.newgui.MainFrame;

import com.cburch.LogisimFX.circuit.SubcircuitFactory;
import com.cburch.LogisimFX.file.LogisimFile;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.LogisimFX.tools.AddTool;
import com.cburch.LogisimFX.tools.Library;
import com.cburch.LogisimFX.tools.Tool;

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

                public void updateItem(Object item, boolean empty) {

                    super.updateItem(item, empty) ;

                    if (!empty) {


                        if(item instanceof LogisimFile){

                            setText(proj.getLogisimFile().getName());
                            setGraphic(null);

                        }
                        else if(item instanceof Library){

                            setText(((Library)item).getName());
                            setGraphic(null);

                        }
                        else if(item instanceof Tool){

                            setText(((Tool)item).getName());
                            setTooltip(new Tooltip(((Tool)item).getDescription()));
                            setGraphic(((Tool) item).getIcon());

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

                        if(treeItem.getValue() instanceof SubcircuitFactory){
                            //(SubcircuitFactory)treeItem.getValue().
                        }
                        /*
                        if(treeItem.getValue() instanceof Circuit){
                            project.setCurrentCircuit((Circuit) treeItem.getValue());
                        }

                         */

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

            return cell ;

        });


        //todo: create update binding to proj tool.size

        updateTree();

    }

    //not best solution, but yes
    public void updateCurrentSections(){

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