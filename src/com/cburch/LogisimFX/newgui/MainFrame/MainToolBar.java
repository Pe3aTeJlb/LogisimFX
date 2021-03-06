package com.cburch.LogisimFX.newgui.MainFrame;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;

public class MainToolBar extends ToolBar {

    private ObservableList<Node> RedactCircuitBtnsList;
    private ObservableList<Node> RedactBlackBoxBtnsList;
    private int prefWidth = 15;
    private int prefHeight = 15;

    public MainToolBar(){

        super();

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

        CustomButton CircuitInteracionBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/poke.gif");
        CircuitInteracionBtn.setOnAction(event -> {
        });

        CustomButton DragSelectionBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/select.gif");
        DragSelectionBtn.setOnAction(event -> {
        });

        CustomButton RedactTextFieldBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/text.gif");
        RedactTextFieldBtn.setOnAction(event -> {
        });

        Separator separator = new Separator();

        CustomButton AddInputBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/pinInput.gif");
        AddInputBtn.setOnAction(event -> {
        });

        CustomButton AddOutputBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/pinOutput.gif");
        AddOutputBtn.setOnAction(event -> {
        });

        CustomButton AddNotElmBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/notGate.gif");
        AddNotElmBtn.setOnAction(event -> {
        });

        CustomButton AddAndElmBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/andGate.gif");
        AddAndElmBtn.setOnAction(event -> {
        });

        CustomButton AddOrElmBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/orGate.gif");
        AddOrElmBtn.setOnAction(event -> {
        });

        RedactCircuitBtnsList.addAll(
                CircuitInteracionBtn,
                DragSelectionBtn,
                RedactTextFieldBtn,
                separator,
                AddInputBtn,
                AddOutputBtn,
                AddNotElmBtn,
                AddAndElmBtn,
                AddOrElmBtn
        );

    }

    private void SetRedactBlackBoxItems(){

        CustomButton DragSelectionBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/select.gif");
        DragSelectionBtn.setOnAction(event -> {
        });

        CustomButton RedactTextFieldBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/text.gif");
        RedactTextFieldBtn.setOnAction(event -> {
        });

        CustomButton DrawStraightLineBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/drawline.gif");
        DrawStraightLineBtn.setOnAction(event -> {
        });

        CustomButton DrawCurveBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/drawcurv.gif");
        DrawCurveBtn.setOnAction(event -> {
        });

        CustomButton DrawBrokenLineBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/drawplin.gif");
        DrawBrokenLineBtn.setOnAction(event -> {
        });

        CustomButton DrawSquareBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/drawrect.gif");
        DrawSquareBtn.setOnAction(event -> {
        });

        CustomButton DrawRoundedSquareBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/drawrrct.gif");
        DrawRoundedSquareBtn.setOnAction(event -> {
        });

        CustomButton DrawOvalBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/drawoval.gif");
        DrawOvalBtn.setOnAction(event -> {
        });

        CustomButton DrawPolygonBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/drawpoly.gif");
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
}
