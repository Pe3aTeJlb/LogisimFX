package com.cburch.LogisimFX.newgui.MainFrame;

import com.cburch.LogisimFX.proj.Project;
import com.cburch.LogisimFX.file.ToolbarData;
import com.cburch.LogisimFX.tools.Tool;

import com.cburch.LogisimFX.draw.tools.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class MainToolBar extends ToolBar {

    private ObservableList<Node> RedactCircuitBtnsList;
    private ObservableList<Node> RedactAppearanceBtnsList;

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
        RedactAppearanceBtnsList = FXCollections.observableArrayList();

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

        DrawingAttributeSet attrs = new DrawingAttributeSet();

        AbstractTool[] tools = {
                new SelectTool(),
                new TextTool(attrs),
                new LineTool(attrs),
                new CurveTool(attrs),
                new PolyTool(false, attrs),
                new RectangleTool(attrs),
                new RoundRectangleTool(attrs),
                new OvalTool(attrs),
                new PolyTool(true, attrs),
        };

        for (AbstractTool tool: tools) {
            RedactAppearanceBtnsList.add(new ToolButton(tool));
        }

    }

    public void SetMainToolBarItems(String ToolBarType){

        if(ToolBarType.equals("RedactCircuit")){
            getItems().clear();
            getItems().addAll(RedactCircuitBtnsList);
        }

        if(ToolBarType.equals("RedactAppearance")){
            getItems().clear();
            getItems().addAll(RedactAppearanceBtnsList);
        }

    }


    class ToolButton extends Button {

        public ToolButton(Tool tool) {

            super();

            setPrefSize(prefWidth,prefHeight);
            setMinSize(prefWidth,prefHeight);
            setMaxSize(prefWidth,prefHeight);

            ImageView buff = new ImageView(tool.getIcon().getImage());
            graphicProperty().setValue(buff);

            setActions(tool);

        }

        public ToolButton(AbstractTool tool) {

            super();

            setPrefSize(prefWidth,prefHeight);
            setMinSize(prefWidth,prefHeight);
            setMaxSize(prefWidth,prefHeight);

            ImageView buff = new ImageView(tool.getIcon().getImage());
            graphicProperty().setValue(buff);

            setActions(tool);

        }

        public void setActions(Tool tool){

            this.setOnAction(event -> {

            });

        }

        public void setActions(AbstractTool tool){

            this.setOnAction(event -> {

            });

        }

    }

}