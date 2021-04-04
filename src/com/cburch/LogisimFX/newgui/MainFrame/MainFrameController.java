package com.cburch.LogisimFX.newgui.MainFrame;

import com.cburch.LogisimFX.newgui.AbstractController;
import com.cburch.LogisimFX.Localizer;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.logisim.util.StringUtil;
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
    ProjectTreeExplorer projectTreeExplorer;
    AdditionalToolBar additionalToolBar;
    MainToolBar mainToolBar;

    private CustomCanvas cv;
    private AnimationTimer update;

//monolith - strength in unity
    @FXML
    public void initialize(){
        //Nothing to see here lol//
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

        projectTreeExplorer = new ProjectTreeExplorer(proj);
        setAnchor(0,40,0,0,projectTreeExplorer);

        mainToolBar = new MainToolBar(proj);
        additionalToolBar = new AdditionalToolBar(proj, projectTreeExplorer);
        ExplorerToolBar controlToolBar = new ExplorerToolBar(mainToolBar,additionalToolBar,projectTreeExplorer);

        treeRoot.getChildren().addAll(controlToolBar,additionalToolBar,projectTreeExplorer);

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

        String s;
        Circuit circuit = proj.getCurrentCircuit();
        String name = proj.getLogisimFile().getName();

        if (circuit != null) {
            s = StringUtil.format(lc.get("titleCircFileKnown"),
                    circuit.getName(), name);
        } else {
            s = StringUtil.format(lc.get("titleFileKnown"), name);
        }
        //stage.titleProperty().bind(lc.createStringBinding(""));
        stage.setTitle(s);

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