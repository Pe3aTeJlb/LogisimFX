package LogisimFX.newgui.MainFrame;

import LogisimFX.circuit.CircuitEvent;
import LogisimFX.circuit.CircuitListener;
import LogisimFX.comp.Component;
import LogisimFX.draw.tools.AbstractTool;
import LogisimFX.file.LibraryEvent;
import LogisimFX.file.LibraryListener;
import LogisimFX.newgui.MainFrame.Canvas.EditHandler;
import LogisimFX.newgui.MainFrame.Canvas.appearanceCanvas.AppearanceCanvas;
import LogisimFX.newgui.AbstractController;
import LogisimFX.newgui.MainFrame.Canvas.layoutCanvas.LayoutCanvas;
import LogisimFX.newgui.MainFrame.ProjectExplorer.AdditionalToolBar;
import LogisimFX.newgui.MainFrame.ProjectExplorer.ExplorerToolBar;
import LogisimFX.newgui.MainFrame.ProjectExplorer.TreeExplorerAggregation;
import LogisimFX.proj.Project;
import LogisimFX.circuit.Circuit;
import LogisimFX.proj.ProjectEvent;
import LogisimFX.proj.ProjectListener;
import LogisimFX.tools.Tool;

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

    private Project proj;

    //UI
    private CustomMenuBar menubar;
    private MainToolBar mainToolBar;
    private AdditionalToolBar additionalToolBar;
    private ExplorerToolBar explorerToolBar;
    private TreeExplorerAggregation treeExplorerAggregation;
    private AttributeTable attributeTable;

    private AnchorPane canvasRoot;
    private LayoutCanvas layoutCanvas;
    private AppearanceCanvas appearanceCanvas;

    MyProjectListener myProjectListener = new MyProjectListener();

    class MyProjectListener
            implements ProjectListener, LibraryListener, CircuitListener {

        public void projectChanged(ProjectEvent event) {
            int action = event.getAction();

            if (action == ProjectEvent.ACTION_SET_FILE) {
                computeTitle();
            } else if (action == ProjectEvent.ACTION_SET_CURRENT) {
                computeTitle();
            }
        }

        public void libraryChanged(LibraryEvent e) {
            if (e.getAction() == LibraryEvent.SET_NAME) {
                computeTitle();
            }
        }

        public void circuitChanged(CircuitEvent event) {
            if (event.getAction() == CircuitEvent.ACTION_SET_NAME) {
                computeTitle();
            }
        }

    }

//monolith - strength in unity
    @FXML
    public void initialize(){
        //Nothing to see here lol
    }

    public void postInitialization(Stage s,Project p) {

        stage = s;

        proj = p;
        proj.setFrameController(this);

        proj.addProjectListener(myProjectListener);
        proj.addLibraryListener(myProjectListener);
        proj.addCircuitListener(myProjectListener);
        computeTitle();

        AnchorPane treeRoot = new AnchorPane();
        treeRoot.setMinHeight(0);

        //Canvas
        canvasRoot = new AnchorPane();
        canvasRoot.setMinSize(0,0);

        layoutCanvas = new LayoutCanvas(canvasRoot, proj);
        appearanceCanvas = new AppearanceCanvas(canvasRoot, proj);
        canvasRoot.getChildren().add(appearanceCanvas);


        //Attribute table
        AnchorPane tableRoot = new AnchorPane();
        tableRoot.setMinHeight(0);

        ScrollPane scrollPane = new ScrollPane();
        setAnchor(0,0,0,0, scrollPane);

        attributeTable = new AttributeTable(proj);
        attributeTable.setFocusTraversable(false);

        scrollPane.setContent(attributeTable);
        scrollPane.setFitToWidth(true);


        tableRoot.getChildren().add(scrollPane);


        //TreeExplorer
        treeExplorerAggregation = new TreeExplorerAggregation(proj);
        setAnchor(0,40,0,0, treeExplorerAggregation);
        treeExplorerAggregation.setFocusTraversable(false);

        mainToolBar = new MainToolBar(proj);
        additionalToolBar = new AdditionalToolBar(proj, treeExplorerAggregation);
        explorerToolBar = new ExplorerToolBar(mainToolBar,additionalToolBar, treeExplorerAggregation, this);

        treeRoot.getChildren().addAll(explorerToolBar,additionalToolBar, treeExplorerAggregation);


        SplitPane explorerSplitPane = new SplitPane(treeRoot,tableRoot);
        explorerSplitPane.setOrientation(Orientation.VERTICAL);


        SplitPane mainSplitPane = new SplitPane(explorerSplitPane,canvasRoot);
        mainSplitPane.setOrientation(Orientation.HORIZONTAL);
        setAnchor(0,50,0,0,mainSplitPane);
        mainSplitPane.setDividerPositions(0.25);


        menubar = new CustomMenuBar(explorerToolBar,proj,treeExplorerAggregation);


        Root.getChildren().addAll(menubar,mainToolBar,mainSplitPane);

        setLayoutView();

    }

    public void computeTitle(){

        stage.titleProperty().unbind();

        Circuit circuit = proj.getCurrentCircuit();
        String name = proj.getLogisimFile().getName();

        if (circuit != null) {
            stage.titleProperty().bind(LC.createComplexStringBinding("titleCircFileKnown",circuit.getName(), name));
        } else {
            stage.titleProperty().bind(LC.createComplexStringBinding("titleCircFileKnown", name));
        }

    }



    //Section for static access from proj.getController. Duplicate functional

    public void setAppearanceView(){
        setAppearanceView(proj.getCurrentCircuit());
    }

    public void setAppearanceView(Circuit circ){

        proj.setCurrentCircuit(circ);
        if(!explorerToolBar.EditCircuitAppearance.getValue())
            explorerToolBar.EditAppearance();

        if(!canvasRoot.getChildren().get(0).equals(appearanceCanvas)){

            canvasRoot.getChildren().clear();
            canvasRoot.getChildren().add(appearanceCanvas);

            menubar.setEditHandler(appearanceCanvas.getEditHandler());

            appearanceCanvas.updateResume();
            layoutCanvas.updateStop();

        }

    }

    public void setLayoutView(){
        setLayoutView(proj.getCurrentCircuit());
    }

    public void setLayoutView(Circuit circ){

        proj.setCurrentCircuit(circ);
        if(!explorerToolBar.EditCircuitLayout.getValue())
        explorerToolBar.EditCircuit();

        if(!canvasRoot.getChildren().get(0).equals(layoutCanvas)){

            canvasRoot.getChildren().clear();
            canvasRoot.getChildren().add(layoutCanvas);

            menubar.setEditHandler(layoutCanvas.getEditHandler());

            layoutCanvas.updateResume();
            appearanceCanvas.updateStop();

        }

    }



    public void setAttributeTable(Tool tool){
        attributeTable.setTool(tool);
    }

    public void setAttributeTable(AbstractTool tool){
        attributeTable.setTool(tool);
    }

    public void setAttributeTable(Circuit circ){
        attributeTable.setCircuit(circ);
    }

    public void setAttributeTable(Circuit circ, Component comp){
        attributeTable.setComponent(circ, comp);
    }



    private void setAnchor(double left,double top, double right, double bottom, Node n){
        AnchorPane.setLeftAnchor(n,left);
        AnchorPane.setTopAnchor(n,top);
        AnchorPane.setRightAnchor(n,right);
        AnchorPane.setBottomAnchor(n,bottom);
    }



    //Getter

    public Project getProj(){
        return proj;
    }

    public Stage getStage(){
        return stage;
    }

    public LayoutCanvas getLayoutCanvas(){return layoutCanvas;}

    public AppearanceCanvas getAppearanceCanvas(){return appearanceCanvas;}

    public EditHandler getEditHandler(){

        if(canvasRoot.getChildren().get(0).equals(layoutCanvas)){
            return layoutCanvas.getEditHandler();
        }else if(canvasRoot.getChildren().get(0).equals(appearanceCanvas)){
            return appearanceCanvas.getEditHandler();
        }else {
            return null;
        }

    }



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

        appearanceCanvas.terminateCanvas();
        layoutCanvas.terminateCanvas();

        mainToolBar.terminateListeners();
        treeExplorerAggregation.terminateListeners();
        attributeTable.terminateListener();
        menubar.terminateListeners();

        proj.removeProjectListener(myProjectListener);
        proj.removeLibraryListener(myProjectListener);
        proj.removeCircuitListener(myProjectListener);

        System.out.println("main close. requested by:" + this);

    }

}