package com.cburch.LogisimFX.newgui.MainFrame;

import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.file.LogisimFile;
import com.cburch.LogisimFX.file.LogisimFileActions;
import com.cburch.LogisimFX.newgui.DialogManager;
import com.cburch.LogisimFX.proj.Project;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;

public class AdditionalToolBar extends ToolBar {

    private ObservableList<Node> CircuitOrderControlBtnsList;
    private ObservableList<Node> CircuitTicksControlBtnsList;

    private int prefWidth = 15;
    private int prefHeight = 15;

    private TreeExplorerAggregation treeExplorerAggregation;

    private Project proj;
    private LogisimFile logisimFile;

    public AdditionalToolBar(Project project, TreeExplorerAggregation explorer){

        super();

        proj = project;
        logisimFile = proj.getLogisimFile();

        treeExplorerAggregation = explorer;
        treeExplorerAggregation = explorer;

        AnchorPane.setLeftAnchor(this,0.0);
        AnchorPane.setTopAnchor(this,20.0);
        AnchorPane.setRightAnchor(this,0.0);

        prefHeight(20);

        CircuitOrderControlBtnsList = FXCollections.observableArrayList();
        CircuitTicksControlBtnsList = FXCollections.observableArrayList();

        SetCircuitOrderControlItems();
        SetCircuitTicksControlItems();

        SetAdditionalToolBarItems("ControlCircuitOrder");

    }

    private void SetCircuitOrderControlItems(){

        CustomButton AddCircuitBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/projadd.gif");
        AddCircuitBtn.setOnAction(event -> {

            String circuitName = DialogManager.CreateInputDialog(proj.getLogisimFile());

            if (circuitName != null) {
                Circuit circuit = new Circuit(circuitName);
                proj.doAction(LogisimFileActions.addCircuit(circuit));
                proj.setCurrentCircuit(circuit);
                treeExplorerAggregation.updateTree();
            }

        });


        CustomButton PullCircuitUpBtn = new  CustomButton(prefWidth,prefHeight,"resources/logisim/icons/projup.gif");
        PullCircuitUpBtn.disableProperty().bind(
                Bindings.or(logisimFile.obsPos.isEqualTo("first"),logisimFile.obsPos.isEqualTo("first&last"))
        );
        PullCircuitUpBtn.setOnAction(event -> {
            //logisimFile.moveCircuit(proj.getTool(),);
            treeExplorerAggregation.updateTree();
        });

        CustomButton PullCircuitDownIBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/projdown.gif");
        PullCircuitDownIBtn.disableProperty().bind(
                Bindings.or(logisimFile.obsPos.isEqualTo("last"),logisimFile.obsPos.isEqualTo("first&last"))
        );
        PullCircuitDownIBtn.setOnAction(event -> {
                //logisimFile.moveCircuit();
                treeExplorerAggregation.updateTree();
        });

        CustomButton DeleteCircuitBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/projdel.gif");
        DeleteCircuitBtn.disableProperty().bind(
                logisimFile.obsPos.isEqualTo("first&last")
        );
        DeleteCircuitBtn.setOnAction(event -> {
                logisimFile.removeCircuit(proj.getCurrentCircuit());
                treeExplorerAggregation.updateTree();
        });

        CircuitOrderControlBtnsList.addAll(
                AddCircuitBtn,
                PullCircuitUpBtn,
                PullCircuitDownIBtn,
                DeleteCircuitBtn
        );

    }

    private void SetCircuitTicksControlItems(){

        CustomButton SimStopBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/simstop.png");
        SimStopBtn.setOnAction(event -> {
        });

        CustomButton SimPlayOneStepBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/simtplay.png");
        SimPlayOneStepBtn.setOnAction(event -> {
        });

        CustomButton SimPlayBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/simplay.png");
        SimPlayBtn.setOnAction(event -> {
        });

        CustomButton SimStepBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/simstep.png");
        SimStepBtn.setOnAction(event -> {
        });

        CircuitTicksControlBtnsList.addAll(
                SimStopBtn,
                SimPlayOneStepBtn,
                SimPlayBtn,
                SimStepBtn
        );

    }

    public void SetAdditionalToolBarItems(String ToolBarType){

        if(ToolBarType.equals("ControlCircuitOrder")){
            getItems().clear();
            getItems().addAll(CircuitOrderControlBtnsList);
        }

        if(ToolBarType.equals("ControlCircuitTicks")){
            getItems().clear();
            getItems().addAll(CircuitTicksControlBtnsList);
        }

    }
}
