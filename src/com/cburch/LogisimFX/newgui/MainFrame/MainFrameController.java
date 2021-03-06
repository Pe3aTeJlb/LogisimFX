package com.cburch.LogisimFX.newgui.MainFrame;

import com.cburch.LogisimFX.newgui.AbstractController;
import com.cburch.LogisimFX.Localizer;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainFrameController extends AbstractController {

    private Stage Stage;

    @FXML
    private AnchorPane Root;

    @FXML
    private TreeView<?> TreeHierarchy;

    private CustomCanvas cv;
    private AnimationTimer update;

    private Localizer lc = new Localizer("LogisimFX/resources/localization/menu");

//monolith - strength in unity
    @FXML
    public void initialize(){

        AnchorPane canvasRoot = new AnchorPane();
        canvasRoot.setMinWidth(200);

        AnchorPane treeRoot = new AnchorPane();
        treeRoot.setMinHeight(0);

        AnchorPane tableRoot = new AnchorPane();

        TreeView<CustomButton> t = new TreeView<>();
        setAnchor(0,40,0,0,t);

        CustomMenuBar menubar = new CustomMenuBar();

        MainToolBar mainToolBar = new MainToolBar();
        AdditionalToolBar additionalToolBar = new AdditionalToolBar();
        ControlToolBar controlToolBar = new ControlToolBar(mainToolBar,additionalToolBar);

        treeRoot.getChildren().addAll(controlToolBar,additionalToolBar,t);

        cv = new CustomCanvas(canvasRoot);

        SplitPane toolSplitPane = new SplitPane(treeRoot,tableRoot);
        toolSplitPane.setOrientation(Orientation.VERTICAL);

        SplitPane mainSplitPane = new SplitPane(toolSplitPane,canvasRoot);
        mainSplitPane.setOrientation(Orientation.HORIZONTAL);
        setAnchor(0,50,0,0,mainSplitPane);

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

    private void setAnchor(double left,double top, double right, double bottom, Node n){
        AnchorPane.setLeftAnchor(n,left);
        AnchorPane.setTopAnchor(n,top);
        AnchorPane.setRightAnchor(n,right);
        AnchorPane.setBottomAnchor(n,bottom);
    }


    @Override
    public void prepareFrame(Stage s) {
        Stage = s;
        setStageTitle();
    }

    public void setStageTitle() {
        Stage.setTitle(lc.get("frameTitle"));
    }

    @Override
    public void onClose() {

    }
}