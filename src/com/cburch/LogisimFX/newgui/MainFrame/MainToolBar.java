package com.cburch.LogisimFX.newgui.MainFrame;

import com.cburch.LogisimFX.IconsManager;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.LogisimFX.file.ToolbarData;
import com.cburch.LogisimFX.tools.Tool;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;

public class MainToolBar extends ToolBar {

    private ObservableList<Node> RedactCircuitBtnsList;
    private ObservableList<Node> RedactBlackBoxBtnsList;

    private int prefWidth = 15;
    private int prefHeight = 15;

    private Project proj;

    public MainToolBar(Project project){

        super();

        proj = project;

        AnchorPane.setLeftAnchor(this,0.0);
        AnchorPane.setTopAnchor(this,25.0);
        AnchorPane.setRightAnchor(this,0.0);

        prefHeight(25);

        initItems();

    }

    private void initItems(){

        RedactCircuitBtnsList = FXCollections.observableArrayList();
        RedactBlackBoxBtnsList = FXCollections.observableArrayList();

        SetRedactCircuitItems();
        SetRedactBlackBoxItems();

        SetMainToolBarItems("RedactCircuit");
    }

    private void SetRedactCircuitItems(){

        ToolbarData data = proj.getLogisimFile().getOptions().getToolbarData();

        for (Tool tool : data.getContents()) {

            if (tool == null) {
                RedactCircuitBtnsList.add(new Separator());
            } else {
                RedactCircuitBtnsList.add(new ToolButton(tool));
            }
        }

    }

    private void SetRedactBlackBoxItems(){

        CustomButton DragSelectionBtn = new CustomButton(prefWidth,prefHeight,"select.gif");
        DragSelectionBtn.setOnAction(event -> {
        });

        CustomButton RedactTextFieldBtn = new CustomButton(prefWidth,prefHeight,"text.gif");
        RedactTextFieldBtn.setOnAction(event -> {
        });

        CustomButton DrawStraightLineBtn = new CustomButton(prefWidth,prefHeight,"drawline.gif");
        DrawStraightLineBtn.setOnAction(event -> {
        });

        CustomButton DrawCurveBtn = new CustomButton(prefWidth,prefHeight,"drawcurv.gif");
        DrawCurveBtn.setOnAction(event -> {
        });

        CustomButton DrawBrokenLineBtn = new CustomButton(prefWidth,prefHeight,"drawplin.gif");
        DrawBrokenLineBtn.setOnAction(event -> {
        });

        CustomButton DrawSquareBtn = new CustomButton(prefWidth,prefHeight,"drawrect.gif");
        DrawSquareBtn.setOnAction(event -> {
        });

        CustomButton DrawRoundedSquareBtn = new CustomButton(prefWidth,prefHeight,"drawrrct.gif");
        DrawRoundedSquareBtn.setOnAction(event -> {
        });

        CustomButton DrawOvalBtn = new CustomButton(prefWidth,prefHeight,"drawoval.gif");
        DrawOvalBtn.setOnAction(event -> {
        });

        CustomButton DrawPolygonBtn = new CustomButton(prefWidth,prefHeight,"drawpoly.gif");
        DrawPolygonBtn.setOnAction(event -> {
        });

        RedactBlackBoxBtnsList.addAll(
                DragSelectionBtn,
                RedactTextFieldBtn,
                DrawStraightLineBtn,
                DrawCurveBtn,
                DrawBrokenLineBtn,
                DrawSquareBtn,
                DrawRoundedSquareBtn,
                DrawOvalBtn,
                DrawPolygonBtn
        );
    }

    public void SetMainToolBarItems(String ToolBarType){

        if(ToolBarType.equals("RedactCircuit")){
            getItems().clear();
            getItems().addAll(RedactCircuitBtnsList);
        }

        if(ToolBarType.equals("RedactBlackBox")){
            getItems().clear();
            getItems().addAll(RedactBlackBoxBtnsList);
        }

    }


    class ToolButton extends Button {

        public ToolButton(Tool tool) {

            super();
            setPrefSize(prefWidth,prefHeight);
            setMinSize(prefWidth,prefHeight);
            setMaxSize(prefWidth,prefHeight);
            graphicProperty().setValue(tool.getIcon());
            setActions(tool);

        }

        public void setActions(Tool tool){

            this.setOnAction(event -> {

            });

        }

    }

}

