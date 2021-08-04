package com.cburch.LogisimFX.newgui.MainFrame;

import com.cburch.LogisimFX.comp.Component;
import com.cburch.LogisimFX.localization.LC_gui;
import com.cburch.LogisimFX.newgui.AbstractController;
import com.cburch.LogisimFX.localization.Localizer;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.tools.Tool;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
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
    private AttributeTable attributeTable;

    private LayoutCanvas cv;


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
        treeExplorerAggregation.setFocusTraversable(false);

        mainToolBar = new MainToolBar(proj);
        additionalToolBar = new AdditionalToolBar(proj, treeExplorerAggregation);
        explorerToolBar = new ExplorerToolBar(mainToolBar,additionalToolBar, treeExplorerAggregation, this);

        treeRoot.getChildren().addAll(explorerToolBar,additionalToolBar, treeExplorerAggregation);


        //Attribute table
        AnchorPane tableRoot = new AnchorPane();
        tableRoot.setMinHeight(0);

        ScrollPane scrollPane = new ScrollPane();
        setAnchor(0,0,0,0, scrollPane);

        attributeTable = new AttributeTable();
        attributeTable.setFocusTraversable(false);

        scrollPane.setContent(attributeTable);
        scrollPane.setFitToWidth(true);


        tableRoot.getChildren().add(scrollPane);


        SplitPane explorerSplitPane = new SplitPane(treeRoot,tableRoot);
        explorerSplitPane.setOrientation(Orientation.VERTICAL);


        //Canvas
        AnchorPane canvasRoot = new AnchorPane();
        canvasRoot.setMinSize(0,0);

        cv = new LayoutCanvas(canvasRoot, proj);


        SplitPane mainSplitPane = new SplitPane(explorerSplitPane,canvasRoot);
        mainSplitPane.setOrientation(Orientation.HORIZONTAL);
        setAnchor(0,50,0,0,mainSplitPane);


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

    public void setAppearanceView(){
        setAppearanceView(proj.getCurrentCircuit());
    }

    public void setAppearanceView(Circuit circ){
        proj.setCurrentCircuit(circ);
        explorerToolBar.EditAppearance();
    }

    public void setEditView(){
        setEditView(proj.getCurrentCircuit());
    }

    public void setEditView(Circuit circ){
        proj.setCurrentCircuit(circ);
        explorerToolBar.EditCircuit();
    }



    public void setAttributeTable(Tool tool){

        attributeTable.setTool(tool);

    }

    public void setAttributeTable(Circuit circ, Component comp){

       // attributeTable.setTool(tool);

    }



    private void setAnchor(double left,double top, double right, double bottom, Node n){
        AnchorPane.setLeftAnchor(n,left);
        AnchorPane.setTopAnchor(n,top);
        AnchorPane.setRightAnchor(n,right);
        AnchorPane.setBottomAnchor(n,bottom);
    }

    //Manual UI Update

    public void manual_UI_Update(){

        manual_ToolBar_Update();
        manual_Explorer_Update();

    }

    public void manual_ToolBar_Update(){
        mainToolBar.ToolsRefresh();
    }

    public void manual_Explorer_Update(){
        treeExplorerAggregation.updateTree();
    }


    //Getter

    public Project getProj(){
        return proj;
    }

    public Stage getStage(){
        return stage;
    }

    public LayoutCanvas getCanvas(){return cv;}

    /*
    public void savePreferences() {
        AppPreferences.TICK_FREQUENCY.set(Double.valueOf(proj.getSimulator().getTickFrequency()));
        AppPreferences.LAYOUT_SHOW_GRID.setBoolean(layoutZoomModel.getShowGrid());
        AppPreferences.LAYOUT_ZOOM.set(Double.valueOf(layoutZoomModel.getZoomFactor()));
        if (appearance != null) {
            ZoomModel aZoom = appearance.getZoomModel();
            AppPreferences.APPEARANCE_SHOW_GRID.setBoolean(aZoom.getShowGrid());
            AppPreferences.APPEARANCE_ZOOM.set(Double.valueOf(aZoom.getZoomFactor()));
        }
        int state = getExtendedState() & ~JFrame.ICONIFIED;
        AppPreferences.WINDOW_STATE.set(Integer.valueOf(state));
        Dimension dim = getSize();
        AppPreferences.WINDOW_WIDTH.set(Integer.valueOf(dim.width));
        AppPreferences.WINDOW_HEIGHT.set(Integer.valueOf(dim.height));
        Point loc;
        try {
            loc = getLocationOnScreen();
        } catch (IllegalComponentStateException e) {
            loc = Projects.getLocation(this);
        }
        if (loc != null) {
            AppPreferences.WINDOW_LOCATION.set(loc.x + "," + loc.y);
        }
        AppPreferences.WINDOW_LEFT_SPLIT.set(Double.valueOf(leftRegion.getFraction()));
        AppPreferences.WINDOW_MAIN_SPLIT.set(Double.valueOf(mainRegion.getFraction()));
        AppPreferences.DIALOG_DIRECTORY.set(JFileChoosers.getCurrentDirectory());
    }

     */

    @Override
    public void onClose() {
        System.out.println("main close. requested by:" + this);
    }

}