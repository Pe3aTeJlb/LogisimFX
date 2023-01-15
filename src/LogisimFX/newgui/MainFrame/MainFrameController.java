/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.MainFrame;

import LogisimFX.IconsManager;
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
import LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor.AppearanceEditor;
import LogisimFX.newgui.MainFrame.EditorTabs.EditorBase;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.LayoutEditor;
import LogisimFX.newgui.MainFrame.SystemTabs.ProjectExplorerTreeView;
import LogisimFX.newgui.MainFrame.SystemTabs.ProjectTreeToolBar;
import LogisimFX.newgui.MainFrame.SystemTabs.SimulationExplorerTreeView;
import LogisimFX.newgui.MainFrame.SystemTabs.SimulationTreeToolBar;
import LogisimFX.proj.Project;
import LogisimFX.circuit.Circuit;
import LogisimFX.proj.ProjectEvent;
import LogisimFX.proj.ProjectListener;
import LogisimFX.tools.Tool;

import docklib.dock.DockAnchor;
import docklib.dock.DockPane;
import docklib.draggabletabpane.DoubleSidedTabPane;
import docklib.draggabletabpane.DraggableTab;
import docklib.draggabletabpane.DraggableTabPane;
import docklib.draggabletabpane.TabGroup;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.HashMap;

public class MainFrameController extends AbstractController {

    //Check Frame of com.cburch.logisim.gui.main

    private Stage stage;

    private Project proj;

    @FXML
    private VBox Root;

    //UI

    private CustomMenuBar menubar;
    private AttributeTable attributeTable;

    private LayoutCanvas currLayoutCanvas;
    private AppearanceCanvas currAppearanceCanvas;


    private HashMap<String, DraggableTab> openedTabs = new HashMap<>();
    private HashMap<Pair<Circuit, String>, DraggableTab> openedWorkspaceTabs = new HashMap<>();

    private DockPane dockPane;
    private DoubleSidedTabPane systemTabPaneLeft, systemTabPaneRight, systemTabPaneBottom;
    private DraggableTabPane workspaceTabPane;

    MyProjectListener myProjectListener = new MyProjectListener();

    class MyProjectListener
            implements ProjectListener, LibraryListener, CircuitListener {

        public void projectChanged(ProjectEvent event) {
            int action = event.getAction();

            if (action == ProjectEvent.ACTION_SET_FILE) {
                computeTitle();
            } else if (action == ProjectEvent.ACTION_SET_CURRENT) {
                computeTitle();
                createCircLayoutEditor(proj.getCurrentCircuit());
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

    private static class ToolTip extends Tooltip {

        public ToolTip(String text){
            super();
            textProperty().bind(LC.createStringBinding(text));
        }

    }


//monolith - strength in unity
    @FXML
    public void initialize(){
        //Nothing to see here lol
        docklib.utils.IconsManager.setStageIcon(IconsManager.LogisimFX);
    }

    public void postInitialization(Stage s,Project p) {

        stage = s;

        stage.iconifiedProperty().addListener((observable, oldValue, newValue) ->{
            if(observable.getValue().booleanValue()){
                stopCanvasRender();
            }else{
                resumeCanvasRender();
            }
        });

        proj = p;
        proj.setFrameController(this);

        proj.addProjectListener(myProjectListener);
        proj.addLibraryListener(myProjectListener);
        proj.addCircuitListener(myProjectListener);
        computeTitle();


        DockPane dockPane = new DockPane(false);
        VBox.setVgrow(dockPane, Priority.ALWAYS);

        systemTabPaneLeft = new DoubleSidedTabPane(proj);
        systemTabPaneLeft.setSide(Side.LEFT);

        systemTabPaneRight= new DoubleSidedTabPane(proj);
        systemTabPaneRight.setSide(Side.RIGHT);

        systemTabPaneBottom = new DoubleSidedTabPane(proj);
        systemTabPaneBottom.setSide(Side.BOTTOM);

        workspaceTabPane = new DraggableTabPane(TabGroup.WorkSpace);
        workspaceTabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
        workspaceTabPane.setSide(Side.TOP);
        workspaceTabPane.setRotateGraphic(true);
        workspaceTabPane.setUnDockable(false);
        workspaceTabPane.setProject(proj);

        dockPane.dock(workspaceTabPane, DockAnchor.TOP);
        dockPane.dock(systemTabPaneLeft, DockAnchor.LEFT);
        dockPane.dock(systemTabPaneRight, DockAnchor.RIGHT);
        dockPane.dock(systemTabPaneBottom, DockAnchor.BOTTOM);

        menubar = new CustomMenuBar(proj);

        Root.getChildren().addAll(menubar, dockPane);

        createDefaultLayout();

        /*

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

         */

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

    public void createDefaultLayout(){

        createToolsTab();
        createSimulationTab();
        createAttributesTab();

        createCircAppearanceEditor(proj.getCurrentCircuit());
        createCircLayoutEditor(proj.getCurrentCircuit());

    }

    //SideBar Tabs

    public void createToolsTab(){

        if (openedTabs.containsKey("ToolsTab")){
            openedTabs.get("ToolsTab").getTabPane().getSelectionModel().select(openedTabs.get("ToolsTab"));
            ((DraggableTabPane)openedTabs.get("ToolsTab").getTabPane()).expand();
            return;
        }

        ProjectExplorerTreeView projectExplorerTreeView = new ProjectExplorerTreeView(proj);
        ProjectTreeToolBar projectTreeToolBar = new ProjectTreeToolBar(proj, projectExplorerTreeView);
        VBox vBox1 = new VBox(projectTreeToolBar, projectExplorerTreeView);
        VBox.setVgrow(projectExplorerTreeView, Priority.ALWAYS);

        DraggableTab projectExplorerTab = new DraggableTab(LC.createStringBinding("toolsTab"), IconsManager.getImage("projtool.gif"), vBox1);
        projectExplorerTab.setTooltip(new ToolTip("projectViewToolboxTip"));
        projectExplorerTab.setOnClosed(event -> openedTabs.remove("ToolsTab"));

        openedTabs.put("ToolsTab", projectExplorerTab);

        systemTabPaneLeft.addLeft(projectExplorerTab);

    }

    public void createSimulationTab(){

        if (openedTabs.containsKey("SimulationTab")){
            openedTabs.get("SimulationTab").getTabPane().getSelectionModel().select(openedTabs.get("SimulationTab"));
            ((DraggableTabPane)openedTabs.get("SimulationTab").getTabPane()).expand();
            return;
        }

        SimulationExplorerTreeView simulationExplorerTreeView = new SimulationExplorerTreeView(proj);
        SimulationTreeToolBar simulationTreeToolBar = new SimulationTreeToolBar(proj);
        VBox vBox2 = new VBox(simulationTreeToolBar, simulationExplorerTreeView);
        VBox.setVgrow(simulationExplorerTreeView, Priority.ALWAYS);

        DraggableTab simulationExplorerTab = new DraggableTab(LC.createStringBinding("simTab"), IconsManager.getImage("projsim.gif"), vBox2);
        simulationExplorerTab.setTooltip(new ToolTip("projectViewSimulationTip"));
        simulationExplorerTab.setOnClosed(event -> openedTabs.remove("SimulationTab"));

        openedTabs.put("SimulationTab", simulationExplorerTab);

        systemTabPaneLeft.addLeft(simulationExplorerTab);

    }

    public void createAttributesTab(){

        if (openedTabs.containsKey("AttributesTab")){
            openedTabs.get("AttributesTab").getTabPane().getSelectionModel().select(openedTabs.get("AttributesTab"));
            ((DraggableTabPane)openedTabs.get("AttributesTab").getTabPane()).expand();
            return;
        }

        attributeTable = new AttributeTable(proj);
        attributeTable.setFocusTraversable(false);

        DraggableTab attributeTableTab = new DraggableTab(LC.createStringBinding("attrTab"), IconsManager.getImage("circattr.gif"), attributeTable);
        attributeTableTab.setOnClosed(event -> openedTabs.remove("AttributesTab"));

        openedTabs.put("AttributesTab", attributeTableTab);

        systemTabPaneLeft.addRight(attributeTableTab);

    }

    public void createTimelineTab(){

    }

    //WorkSpace Tabs

    public void createCircLayoutEditor(Circuit circ){

        proj.setCurrentCircuit(circ);

        Pair<Circuit, String> bufLayoutPair = new Pair<>(circ, "layout");
        if (openedWorkspaceTabs.containsKey(bufLayoutPair)){
            openedWorkspaceTabs.get(bufLayoutPair).getTabPane().getSelectionModel().select(openedWorkspaceTabs.get(bufLayoutPair));
            return;
        }

        LayoutEditor layoutEditor = new LayoutEditor(proj);
        setEditor(layoutEditor);
        currLayoutCanvas = layoutEditor.getLayoutCanvas();

        DraggableTab tab = new DraggableTab(circ.getName(), IconsManager.getImage("projlayo.gif"), layoutEditor);
        tab.setStageTitle(LC.createComplexStringBinding("titleLayoutEditor", circ.getName(), proj.getLogisimFile().getName()));

        tab.getContent().setOnMouseClicked(event -> {
            setEditor(layoutEditor);
            currLayoutCanvas = layoutEditor.getLayoutCanvas();
        });

        tab.setOnSelectionChanged(event -> {
            if (tab.isSelected()) {
                setEditor(layoutEditor);
                currLayoutCanvas = layoutEditor.getLayoutCanvas();
            }
        });

        openedWorkspaceTabs.put(new Pair<>(circ, "layout"), tab);

        currLayoutCanvas.updateResume();

        workspaceTabPane.addTab(tab);
        workspaceTabPane.getSelectionModel().select(tab);

    }

    public void createCircAppearanceEditor(Circuit circ){

        proj.setCurrentCircuit(circ);

        Pair<Circuit, String> bufAppearancePair = new Pair<>(circ, "appearance");
        if (openedWorkspaceTabs.containsKey(bufAppearancePair)){
            openedWorkspaceTabs.get(bufAppearancePair).getTabPane().getSelectionModel().select(openedWorkspaceTabs.get(bufAppearancePair));
            return;
        }

        AppearanceEditor appearanceEditor = new AppearanceEditor(proj);
        setEditor(appearanceEditor);
        currAppearanceCanvas = appearanceEditor.getAppearanceCanvas();
        System.out.println("app " + editor.get());

        DraggableTab tab = new DraggableTab(circ.getName(), IconsManager.getImage("projapp.gif"), appearanceEditor);
        tab.setStageTitle(LC.createComplexStringBinding("titleAppearanceEditor", circ.getName(), proj.getLogisimFile().getName()));

        tab.getContent().setOnMouseClicked(event -> {
            setEditor(appearanceEditor);
            currAppearanceCanvas = appearanceEditor.getAppearanceCanvas();
        });

        tab.setOnSelectionChanged(event -> {
            if (tab.isSelected()) {
                setEditor(appearanceEditor);
                currAppearanceCanvas = appearanceEditor.getAppearanceCanvas();
            }
        });

        openedWorkspaceTabs.put(new Pair<>(circ, "appearance"), tab);

        currAppearanceCanvas.updateResume();

        workspaceTabPane.addTab(tab);
        workspaceTabPane.getSelectionModel().select(tab);

    }

    public void createCodeEditor(Circuit circ){

        DraggableTab tab = new DraggableTab(circ.getName(), IconsManager.getImage("projapp.gif"), null);
        tab.setStageTitle(LC.createComplexStringBinding("titleVhdlCodeEditor", circ.getName(), proj.getLogisimFile().getName()));
        tab.setStageTitle(LC.createComplexStringBinding("titleVerilogCodeEditor", circ.getName(), proj.getLogisimFile().getName()));


    }





    private ObjectProperty<EditorBase> editor;

    private void setEditor(EditorBase value) {
        if (editorProperty().get() != value) {
            editorProperty().set(value);
        }
    }

    public EditorBase getEditor() {
        return editorProperty().get();
    }

    public ObjectProperty<EditorBase> editorProperty() {
        if (editor == null) {
            editor = new ObjectPropertyBase<>(null) {
                @Override protected void invalidated() {
                }

                @Override
                public Object getBean() {
                    return MainFrameController.this;
                }

                @Override
                public String getName() {
                    return "editor";
                }
            };
        }
        return editor;
    }








    //Section for static access from proj.getController. Duplicate functional

    public void resumeCanvasRender(){
/*
        if(canvasRoot.getChildren().get(0).equals(currLayoutCanvas)){

            currLayoutCanvas.updateResume();
            currAppearanceCanvas.updateStop();

        }else{

            currLayoutCanvas.updateStop();
            currAppearanceCanvas.updateResume();

        }
*/
    }

    public void stopCanvasRender(){

        currLayoutCanvas.updateStop();
        currAppearanceCanvas.updateStop();

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



    //Getter

    public Project getProj(){
        return proj;
    }

    public Stage getStage(){
        return stage;
    }

    public LayoutCanvas getLayoutCanvas(){
        return currLayoutCanvas;
    }

    public AppearanceCanvas getAppearanceCanvas(){
        return currAppearanceCanvas;
    }

    public EditHandler getEditHandler(){
/*
        if(canvasRoot.getChildren().get(0).equals(layoutCanvas)){
            return layoutCanvas.getEditHandler();
        }else if(canvasRoot.getChildren().get(0).equals(appearanceCanvas)){
            return appearanceCanvas.getEditHandler();
        }else {
            return null;
        }
*/
        return null;

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

        currAppearanceCanvas.terminateCanvas();
        currLayoutCanvas.terminateCanvas();

        attributeTable.terminateListener();
        menubar.terminateListeners();

        proj.removeProjectListener(myProjectListener);
        proj.removeLibraryListener(myProjectListener);
        proj.removeCircuitListener(myProjectListener);

        System.out.println("main close. requested by:" + this);

    }

}