package com.cburch.LogisimFX.newgui.MainFrame;

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

    public AdditionalToolBar() {
        super();

        AnchorPane.setLeftAnchor(this,0.0);
        AnchorPane.setTopAnchor(this,20.0);
        AnchorPane.setRightAnchor(this,0.0);

        prefHeight(20);

        initItems();

        SetCircuitOrderControlItems();
        SetCircuitTicksControlItems();

        SetAdditionalToolBarItems("ControlCircuitOrder");
    }

    private void initItems(){

        CircuitOrderControlBtnsList = FXCollections.observableArrayList();
        CircuitTicksControlBtnsList = FXCollections.observableArrayList();

        SetAdditionalToolBarItems("ControlCircuitOrder");
    }

    private void SetCircuitOrderControlItems(){

        CustomButton AddCircuitBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/projadd.gif");
        AddCircuitBtn.setOnAction(event -> {
        });

        CustomButton PullCircuitUpBtn = new  CustomButton(prefWidth,prefHeight,"resources/logisim/icons/projup.gif");
        PullCircuitUpBtn.setOnAction(event -> {
        });

        CustomButton PullCircuitDownIBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/projdown.gif");
        PullCircuitDownIBtn.setOnAction(event -> {
        });

        CustomButton DeleteCircuitBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/projdel.gif");
        DeleteCircuitBtn.setOnAction(event -> {
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
