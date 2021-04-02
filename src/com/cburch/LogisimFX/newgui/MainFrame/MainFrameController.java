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

    @FXML
    private TreeView<?> TreeHierarchy;

    private CustomCanvas cv;
    private AnimationTimer update;

    private Localizer lc = new Localizer("LogisimFX/resources/localization/menu");

    private Project proj;

    //UI
    CustomMenuBar menubar;

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

        menubar = new CustomMenuBar();

        MainToolBar mainToolBar = new MainToolBar();
        AdditionalToolBar additionalToolBar = new AdditionalToolBar();
        ExplorerToolBar controlToolBar = new ExplorerToolBar(mainToolBar,additionalToolBar);

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

    @Override
    public void postInitialization(Stage s) {
        stage = s;
        //computeTitle();

    }

    @Override
    public void linkProjectReference(Project project) {

        proj = project;
        menubar.linkProjectReference(proj);

    }

    public Project getProj(){
        return proj;
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
        stage.titleProperty().bind(lc.createStringBinding(""));
        stage.setTitle(s);

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