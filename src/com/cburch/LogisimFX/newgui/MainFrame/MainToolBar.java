package com.cburch.LogisimFX.newgui.MainFrame;


import com.cburch.LogisimFX.prefs.AppPreferences;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.LogisimFX.file.ToolbarData;
import com.cburch.LogisimFX.tools.Tool;
import com.cburch.LogisimFX.draw.tools.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MainToolBar extends ToolBar {

    private ObservableList<Node> RedactCircuitBtnsList;
    private ObservableList<Node> RedactAppearanceBtnsList;

    private final int prefWidth = 15;
    private final int prefHeight = 15;

    private int toolsCounter = 1;

    private Project proj;

    private String currType = null;

    private MyListener myListener = new MyListener();

    private class MyListener
            implements  PropertyChangeListener {

        //
        // PropertyChangeListener methods
        //
        public void propertyChange(PropertyChangeEvent event) {
            if (AppPreferences.GATE_SHAPE.isSource(event)) {
                ToolsRefresh();
            }
        }

    }

    public MainToolBar(Project project){

        super();

        proj = project;

        AppPreferences.GATE_SHAPE.addPropertyChangeListener(myListener);

        AnchorPane.setLeftAnchor(this,0.0);
        AnchorPane.setTopAnchor(this,25.0);
        AnchorPane.setRightAnchor(this,0.0);

        prefHeight(25);

        initItems();

    }

    private void initItems(){

        RedactCircuitBtnsList = FXCollections.observableArrayList();
        RedactAppearanceBtnsList = FXCollections.observableArrayList();

        SetLayoutTools();
        SetAppearanceTools();

        SetMainToolBarItems("RedactCircuit");
    }

    private void SetLayoutTools(){

        toolsCounter= 1;

        RedactCircuitBtnsList.clear();

        ToolbarData data = proj.getLogisimFile().getOptions().getToolbarData();

        for (Tool tool : data.getContents()) {

            if (tool == null) {
                RedactCircuitBtnsList.add(new Separator());
            } else {
                RedactCircuitBtnsList.add(new ToolButton(tool));
            }

        }

        proj.setTool(data.getFirstTool());

    }

    private void SetAppearanceTools(){

        RedactAppearanceBtnsList.clear();

        DrawingAttributeSet attrs = new DrawingAttributeSet();

        AbstractTool[] tools = {
                new SelectTool(),
                //new TextTool(attrs),
                new LineTool(attrs),
                new CurveTool(attrs),
                new PolyTool(false, attrs),
                new RectangleTool(attrs),
                new RoundRectangleTool(attrs),
                new OvalTool(attrs),
                new PolyTool(true, attrs),
        };

        proj.setAbstractTool(tools[0]);

        for (AbstractTool tool: tools) {
            RedactAppearanceBtnsList.add(new ToolButton(tool));
        }

    }

    public void SetMainToolBarItems(String ToolBarType){

        currType = ToolBarType;

        if(ToolBarType.equals("RedactCircuit")){
            getItems().clear();
            getItems().addAll(RedactCircuitBtnsList);
        }

        if(ToolBarType.equals("RedactAppearance")){
            getItems().clear();
            getItems().addAll(RedactAppearanceBtnsList);
        }

    }

    private void ToolsRefresh(){

        SetLayoutTools();
        SetAppearanceTools();
        SetMainToolBarItems(currType);

    }


    class ToolButton extends Button {

        public ToolButton(Tool tool) {

            super();

            setPrefSize(prefWidth,prefHeight);
            setMinSize(prefWidth,prefHeight);
            setMaxSize(prefWidth,prefHeight);

            ImageView buff = new ImageView(tool.getIcon().getImage());
            graphicProperty().setValue(buff);

            if(toolsCounter <11){

                if(toolsCounter == 10) {toolsCounter = 0;}

                proj.getFrameController().getStage().getScene().getAccelerators().put(
                        new KeyCodeCombination(KeyCode.valueOf("DIGIT"+ toolsCounter), KeyCombination.CONTROL_DOWN),
                        new Runnable() {
                            @FXML
                            public void run() {
                               fire();
                            }
                        }

                );

                toolsCounter++;
            }

            String bindbuff = " ("+"CTRL+"+ (toolsCounter-1)+")";

            Tooltip tip = new Tooltip();
            tip.textProperty().bind(tool.getDescription().concat(bindbuff));
            setTooltip(tip);

            this.setOnAction(event -> proj.setTool(tool));

        }

        public ToolButton(AbstractTool tool) {

            super();

            setPrefSize(prefWidth,prefHeight);
            setMinSize(prefWidth,prefHeight);
            setMaxSize(prefWidth,prefHeight);

            ImageView buff = new ImageView(tool.getIcon().getImage());
            graphicProperty().setValue(buff);

            this.setOnAction(event -> proj.setAbstractTool(tool));

        }


    }

}