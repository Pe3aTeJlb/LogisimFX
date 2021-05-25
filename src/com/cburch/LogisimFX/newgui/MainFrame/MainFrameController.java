package com.cburch.LogisimFX.newgui.MainFrame;

import com.cburch.LogisimFX.localization.LC_gui;
import com.cburch.LogisimFX.newgui.AbstractController;
import com.cburch.LogisimFX.localization.Localizer;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.LogisimFX.circuit.Circuit;

import javafx.geometry.NodeOrientation;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainFrameController extends AbstractController {

    //Check Frame of com.cburch.logisim.gui.main

    private Stage stage;

    @FXML
    private AnchorPane Root;

    private static Localizer lc = LC_gui.getInstance();

    private Project proj;

    //UI
    private CustomMenuBar menubar;
    private MainToolBar mainToolBar;
    private AdditionalToolBar additionalToolBar;
    private ExplorerToolBar explorerToolBar;
    private TreeExplorerAggregation treeExplorerAggregation;

    private CustomCanvas cv;


//monolith - strength in unity
    @FXML
    public void initialize(){
        //Nothing to see here lol
    }

    public void postInitialization(Stage s,Project p) {

        stage = s;

        proj = p;
        proj.setFrameController(this);

        computeTitle();

        AnchorPane treeRoot = new AnchorPane();
        treeRoot.setMinHeight(0);


        //TreeExplorer
        treeExplorerAggregation = new TreeExplorerAggregation(proj);
        setAnchor(0,40,0,0, treeExplorerAggregation);

        additionalToolBar = new AdditionalToolBar(proj, treeExplorerAggregation);
        explorerToolBar = new ExplorerToolBar(mainToolBar,additionalToolBar, treeExplorerAggregation);

        treeRoot.getChildren().addAll(explorerToolBar,additionalToolBar, treeExplorerAggregation);


        //Attribute table
        AnchorPane tableRoot = new AnchorPane();
        tableRoot.setMinHeight(0);

        AttributeTable attributeTable = new AttributeTable();
        setAnchor(0,0,0,0, attributeTable);

        tableRoot.getChildren().add(attributeTable);


        SplitPane explorerSplitPane = new SplitPane(treeRoot,tableRoot);
        explorerSplitPane.setOrientation(Orientation.VERTICAL);


        //Canvas
        AnchorPane canvasRoot = new AnchorPane();
        canvasRoot.setMinSize(0,0);

        cv = new CustomCanvas(canvasRoot);



        SplitPane mainSplitPane = new SplitPane(explorerSplitPane,canvasRoot);
        mainSplitPane.setOrientation(Orientation.HORIZONTAL);
        setAnchor(0,50,0,0,mainSplitPane);



        mainToolBar = new MainToolBar(proj);

        menubar = new CustomMenuBar(explorerToolBar,proj,treeExplorerAggregation);



        Root.getChildren().addAll(menubar,mainToolBar,mainSplitPane);

    }

    private void computeTitle(){

        stage.titleProperty().unbind();

        Circuit circuit = proj.getCurrentCircuit();
        String name = proj.getLogisimFile().getName();

        if (circuit != null) {
            stage.titleProperty().bind(lc.createComplexStringBinding("titleCircFileKnown",circuit.getName(), name));
        } else {
            stage.titleProperty().bind(lc.createComplexStringBinding("titleCircFileKnown", name));
        }

    }



    //Section for static access from proj.getController. Duplicate functional
    public void setAppearanceView(Circuit cir){
        explorerToolBar.EditAppearance();
    }

    public void setEditView(Circuit circ){
        explorerToolBar.EditCircuit();
    }

    public void manual_UI_Update(){

        treeExplorerAggregation.updateTree();

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
        System.out.println("main close. requested by:" + this);
    }

}