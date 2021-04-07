package com.cburch.LogisimFX.newgui.MainFrame;

import com.cburch.LogisimFX.newgui.AbstractController;
import com.cburch.LogisimFX.Localizer;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.LogisimFX.circuit.Circuit;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainFrameController extends AbstractController {

    //Check Frame of com.cburch.logisim.gui.main

    private Stage stage;

    @FXML
    private AnchorPane Root;

    private Localizer lc = new Localizer("LogisimFX/resources/localization/gui");

    private Project proj;

    //UI
    CustomMenuBar menubar;
    AdditionalToolBar additionalToolBar;
    MainToolBar mainToolBar;
    TreeExplorerAggregation treeExplorerAggregation;

    private CustomCanvas cv;
    private AnimationTimer update;

//monolith - strength in unity
    @FXML
    public void initialize(){
        //Nothing to see here lol
    }

    public void postInitialization(Stage s,Project p) {

        stage = s;
        proj = p;
        computeTitle();

        AnchorPane canvasRoot = new AnchorPane();
        canvasRoot.setMinWidth(200);

        AnchorPane treeRoot = new AnchorPane();
        treeRoot.setMinHeight(0);

        AnchorPane tableRoot = new AnchorPane();

        treeExplorerAggregation = new TreeExplorerAggregation(proj);
        setAnchor(0,40,0,0, treeExplorerAggregation.getTree());

        mainToolBar = new MainToolBar(proj);
        additionalToolBar = new AdditionalToolBar(proj, treeExplorerAggregation);
        ExplorerToolBar controlToolBar = new ExplorerToolBar(mainToolBar,additionalToolBar, treeExplorerAggregation);

        treeRoot.getChildren().addAll(controlToolBar,additionalToolBar, treeExplorerAggregation.getTree());

        cv = new CustomCanvas(canvasRoot);

        SplitPane toolSplitPane = new SplitPane(treeRoot,tableRoot);
        toolSplitPane.setOrientation(Orientation.VERTICAL);

        SplitPane mainSplitPane = new SplitPane(toolSplitPane,canvasRoot);
        mainSplitPane.setOrientation(Orientation.HORIZONTAL);
        setAnchor(0,50,0,0,mainSplitPane);

        menubar = new CustomMenuBar(this, controlToolBar,proj);

        Root.getChildren().addAll(menubar,mainToolBar,mainSplitPane);

        update = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Update();
            }
        };

        update.start();

    }


    private void Update() {
        cv.draw();
    }

    private void computeTitle(){

        stage.titleProperty().unbind();

        Circuit circuit = proj.getCurrentCircuit();
        String name = proj.getLogisimFile().getName();

        if (circuit != null) {
            stage.titleProperty().bind(lc.createComplexTitleBinding("titleCircFileKnown",circuit.getName(), name));
        } else {
            stage.titleProperty().bind(lc.createComplexTitleBinding("titleCircFileKnown", name));
        }

    }








    public Project getProj(){
        return proj;
    }

    public Stage getStage(){
        return stage;
    }

    private void setAnchor(double left,double top, double right, double bottom, Node n){
        AnchorPane.setLeftAnchor(n,left);
        AnchorPane.setTopAnchor(n,top);
        AnchorPane.setRightAnchor(n,right);
        AnchorPane.setBottomAnchor(n,bottom);
    }

    @Override
    public void onClose() {
    }

}