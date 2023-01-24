/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.MainFrame;

import LogisimFX.IconsManager;
import LogisimFX.circuit.*;
import LogisimFX.comp.Component;
import LogisimFX.draw.tools.AbstractTool;
import LogisimFX.file.LibraryEvent;
import LogisimFX.file.LibraryListener;
import LogisimFX.newgui.MainFrame.EditorTabs.CodeEditor.CodeEditor;
import LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor.appearanceCanvas.AppearanceCanvas;
import LogisimFX.newgui.AbstractController;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.LayoutCanvas;
import LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor.AppearanceEditor;
import LogisimFX.newgui.MainFrame.EditorTabs.EditorBase;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.LayoutEditor;
import LogisimFX.newgui.MainFrame.SystemTabs.AttributesTab.AttributeTable;
import LogisimFX.newgui.MainFrame.SystemTabs.ProjectExplorerTab.ProjectExplorerTreeView;
import LogisimFX.newgui.MainFrame.SystemTabs.ProjectExplorerTab.ProjectTreeToolBar;
import LogisimFX.newgui.MainFrame.SystemTabs.SimulationExplorerTab.SimulationExplorerTreeView;
import LogisimFX.newgui.MainFrame.SystemTabs.SimulationExplorerTab.SimulationTreeToolBar;
import LogisimFX.newgui.MainFrame.SystemTabs.WaveformTab.WaveformController;
import LogisimFX.proj.Project;
import LogisimFX.proj.ProjectEvent;
import LogisimFX.proj.ProjectListener;
import LogisimFX.tools.AddTool;
import LogisimFX.tools.Tool;

import docklib.dock.DockAnchor;
import docklib.dock.DockPane;
import docklib.draggabletabpane.DoubleSidedTabPane;
import docklib.draggabletabpane.DraggableTab;
import docklib.draggabletabpane.DraggableTabPane;
import docklib.draggabletabpane.TabGroup;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

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


    private HashMap<String, DraggableTab> openedSystemTabs = new HashMap<>();
    private HashMap<DraggableTab, WaveformController> openedWaveforms = new HashMap<>();
    private HashMap<Circuit, DraggableTab> openedLayoutEditors = new HashMap<>();
    private HashMap<Circuit, DraggableTab> openedAppearanceEditors = new HashMap<>();
    private HashMap<Circuit, DraggableTab> openedVHDLModelEditors = new HashMap<>();
    private HashMap<Circuit, DraggableTab> openedVerilogModelEditors = new HashMap<>();


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
            }

        }

        public void libraryChanged(LibraryEvent e) {
            if (e.getAction() == LibraryEvent.SET_NAME) {
                computeTitle();
            }
            if (e.getAction() == LibraryEvent.REMOVE_TOOL) {
                Object t = e.getData();
                Circuit circ = null;
                if (t instanceof AddTool) {
                    t = ((AddTool) t).getFactory();
                    if (t instanceof SubcircuitFactory) {
                        circ = ((SubcircuitFactory) t).getSubcircuit();
                        if (openedLayoutEditors.containsKey(circ)){
                            openedLayoutEditors.get(circ).close();
                            ((EditorBase)openedLayoutEditors.get(circ).getContent()).terminateListeners();
                            openedLayoutEditors.remove(circ);
                        }
                        if (openedAppearanceEditors.containsKey(circ)){
                            openedAppearanceEditors.get(circ).close();
                            ((EditorBase)openedAppearanceEditors.get(circ).getContent()).terminateListeners();
                            openedAppearanceEditors.remove(circ);
                        }
                        if (openedVHDLModelEditors.containsKey(circ)){
                            openedVHDLModelEditors.get(circ).close();
                            ((EditorBase)openedVHDLModelEditors.get(circ).getContent()).terminateListeners();
                            openedVHDLModelEditors.remove(circ);
                        }
                        if (openedVerilogModelEditors.containsKey(circ)){
                            openedVerilogModelEditors.get(circ).close();
                            ((EditorBase)openedVerilogModelEditors.get(circ).getContent()).terminateListeners();
                            openedVerilogModelEditors.remove(circ);
                        }
                    }
                }
            }

            if(e.getAction() == LibraryEvent.ADD_TOOL){
                Object t = e.getData();
                if (t instanceof AddTool) {
                    t = ((AddTool) t).getFactory();
                    if (t instanceof SubcircuitFactory) {
                        createCircLayoutEditor(((SubcircuitFactory) t).getSubcircuit());
                    }
                }
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


    @FXML
    public void initialize(){ }

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

        DockPane dockPane = new DockPane(false);
        VBox.setVgrow(dockPane, Priority.ALWAYS);

        systemTabPaneLeft = new DoubleSidedTabPane(stage, proj);
        systemTabPaneLeft.setSide(Side.LEFT);

        systemTabPaneRight= new DoubleSidedTabPane(stage, proj);
        systemTabPaneRight.setSide(Side.RIGHT);

        systemTabPaneBottom = new DoubleSidedTabPane(stage, proj);
        systemTabPaneBottom.setSide(Side.BOTTOM);

        workspaceTabPane = new DraggableTabPane(stage, TabGroup.WorkSpace);
        workspaceTabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
        workspaceTabPane.setSide(Side.TOP);
        workspaceTabPane.setRotateGraphic(true);
        workspaceTabPane.setUnDockable(false);
        workspaceTabPane.setProject(proj);

        dockPane.dock(workspaceTabPane, DockAnchor.TOP);
        dockPane.dock(systemTabPaneLeft, DockAnchor.LEFT);
        dockPane.dock(systemTabPaneRight, DockAnchor.RIGHT);
        dockPane.dock(systemTabPaneBottom, DockAnchor.BOTTOM);

        menubar = new CustomMenuBar(stage, proj);

        Root.getChildren().addAll(menubar, dockPane);

        createDefaultLayout();

        workspaceTabPane.getSelectionModel().selectedItemProperty().addListener((v, o, n) -> computeTitle((DraggableTab) n));
        computeTitle();

    }

    public void computeTitle(){

        stage.titleProperty().unbind();

        if (!workspaceTabPane.getTabs().isEmpty()){
            stage.titleProperty().bind(((DraggableTab)workspaceTabPane.getSelectionModel().getSelectedItem()).getStageTitle());
        }

    }

    public void computeTitle(DraggableTab tab){

        stage.titleProperty().unbind();

        if (!workspaceTabPane.getTabs().isEmpty()){
            stage.titleProperty().bind(tab.getStageTitle());
        }

    }

    public void createDefaultLayout(){

        systemTabPaneBottom.setPrefExpandedSize(300);

        createToolsTab();
        createSimulationTab();
        createAttributesTab();
        createWaveformTab();

        createCircAppearanceEditor(proj.getCurrentCircuit());
        createCircLayoutEditor(proj.getCurrentCircuit());
        createVerilogModelEditor(proj.getCurrentCircuit());
        createVHDLModelEditor(proj.getCurrentCircuit());

        workspaceTabPane.getSelectionModel().select(1);

    }

    //SideBar Tabs

    public void createToolsTab(){

        if (openedSystemTabs.containsKey("ToolsTab")){
            openedSystemTabs.get("ToolsTab").getTabPane().getSelectionModel().select(openedSystemTabs.get("ToolsTab"));
            ((DraggableTabPane) openedSystemTabs.get("ToolsTab").getTabPane()).expand();
            return;
        }

        ProjectExplorerTreeView projectExplorerTreeView = new ProjectExplorerTreeView(proj);
        ProjectTreeToolBar projectTreeToolBar = new ProjectTreeToolBar(proj, projectExplorerTreeView);
        VBox vBox1 = new VBox(projectTreeToolBar, projectExplorerTreeView);
        VBox.setVgrow(projectExplorerTreeView, Priority.ALWAYS);

        DraggableTab projectExplorerTab = new DraggableTab(LC.createStringBinding("toolsTab"), IconsManager.getImage("projtool.gif"), vBox1);
        projectExplorerTab.setTooltip(new ToolTip("projectViewToolboxTip"));
        projectExplorerTab.setOnClosed(event -> openedSystemTabs.remove("ToolsTab"));

        openedSystemTabs.put("ToolsTab", projectExplorerTab);

        systemTabPaneLeft.addLeft(projectExplorerTab);

    }

    public void createSimulationTab(){

        if (openedSystemTabs.containsKey("SimulationTab")){
            openedSystemTabs.get("SimulationTab").getTabPane().getSelectionModel().select(openedSystemTabs.get("SimulationTab"));
            ((DraggableTabPane) openedSystemTabs.get("SimulationTab").getTabPane()).expand();
            return;
        }

        SimulationExplorerTreeView simulationExplorerTreeView = new SimulationExplorerTreeView(proj);
        SimulationTreeToolBar simulationTreeToolBar = new SimulationTreeToolBar(proj);
        VBox vBox2 = new VBox(simulationTreeToolBar, simulationExplorerTreeView);
        VBox.setVgrow(simulationExplorerTreeView, Priority.ALWAYS);

        DraggableTab simulationExplorerTab = new DraggableTab(LC.createStringBinding("simTab"), IconsManager.getImage("projsim.gif"), vBox2);
        simulationExplorerTab.setTooltip(new ToolTip("projectViewSimulationTip"));
        simulationExplorerTab.setOnClosed(event -> openedSystemTabs.remove("SimulationTab"));

        openedSystemTabs.put("SimulationTab", simulationExplorerTab);

        systemTabPaneLeft.addLeft(simulationExplorerTab);

    }

    public void createAttributesTab(){

        if (openedSystemTabs.containsKey("AttributesTab")){
            openedSystemTabs.get("AttributesTab").getTabPane().getSelectionModel().select(openedSystemTabs.get("AttributesTab"));
            ((DraggableTabPane) openedSystemTabs.get("AttributesTab").getTabPane()).expand();
            return;
        }

        ScrollPane scrollPane = new ScrollPane();

        attributeTable = new AttributeTable(proj);
        attributeTable.setFocusTraversable(false);

        scrollPane.setContent(attributeTable);
        scrollPane.setFitToWidth(true);

        DraggableTab attributeTableTab = new DraggableTab(LC.createStringBinding("attrTab"), IconsManager.getImage("circattr.gif"), scrollPane);
        attributeTableTab.setOnClosed(event -> openedSystemTabs.remove("AttributesTab"));

        openedSystemTabs.put("AttributesTab", attributeTableTab);

        systemTabPaneLeft.addRight(attributeTableTab);

    }

    public void createWaveformTab(){

        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource(
                "LogisimFX/newgui/MainFrame/SystemTabs/WaveformTab/WaveformTab.fxml"));
        Parent root = null;

        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AbstractController c = loader.getController();
        c.postInitialization(stage, proj);

        DraggableTab waveformTab = new DraggableTab(LC.createStringBinding("waveformTab"), IconsManager.getImage("waveform.gif"), root);

        waveformTab.setOnCloseRequest(event -> openedWaveforms.remove(waveformTab));

        openedWaveforms.put(waveformTab, (WaveformController)c);

        systemTabPaneBottom.addLeft(waveformTab);

        if (stage.isShowing()) {
            Platform.runLater(((WaveformController) c)::findScrollBar);
        }
    }

    //WorkSpace Tabs

    public void createCircLayoutEditor(Circuit circ){

        proj.setCurrentCircuit(circ);

        if (openedLayoutEditors.containsKey(circ)){
            if (openedLayoutEditors.get(circ).getTabPane() == null){
                workspaceTabPane.addTab(openedLayoutEditors.get(circ));
            }
            selectCircLayoutEditor(circ);
            return;
        }

        LayoutEditor layoutEditor = new LayoutEditor(proj, circ);
        setEditor(layoutEditor);
        currLayoutCanvas = layoutEditor.getLayoutCanvas();
        layoutEditor.getLayoutCanvas().getSelection().addListener(attributeTable);

        DraggableTab tab = new DraggableTab(circ.getName()+".layo", IconsManager.getImage("projlayo.gif"), layoutEditor);
        tab.setStageTitle(LC.createComplexStringBinding("titleLayoutEditor", circ.getName(), proj.getLogisimFile().getName()));

        tab.getContent().setOnMouseClicked(event -> {
            setEditor(layoutEditor);
            currLayoutCanvas = layoutEditor.getLayoutCanvas();
            proj.setCurrentCircuit(circ);
        });

        tab.setOnSelectionChanged(event -> {
            if (tab.isSelected()) {
                setEditor(layoutEditor);
                currLayoutCanvas = layoutEditor.getLayoutCanvas();
                layoutEditor.getLayoutCanvas().updateResume();
                proj.setCurrentCircuit(circ);
            } else {
                layoutEditor.getLayoutCanvas().updateStop();
            }
        });

        tab.setOnIntoSeparatedWindow(event -> layoutEditor.copyAccelerators());

        tab.setOnCloseRequest(event -> currLayoutCanvas.updateStop());

        openedLayoutEditors.put(circ, tab);

        currLayoutCanvas.updateResume();

        workspaceTabPane.addTab(tab);
        workspaceTabPane.getSelectionModel().select(tab);

    }

    public void createCircAppearanceEditor(Circuit circ){

        proj.setCurrentCircuit(circ);

        if (openedAppearanceEditors.containsKey(circ)){
            if (openedAppearanceEditors.get(circ).getTabPane() == null){
                workspaceTabPane.addTab(openedAppearanceEditors.get(circ));
            }
            selectCircAppearanceEditor(circ);
            return;
        }

        AppearanceEditor appearanceEditor = new AppearanceEditor(proj, circ);
        setEditor(appearanceEditor);
        currAppearanceCanvas = appearanceEditor.getAppearanceCanvas();
        appearanceEditor.getAppearanceCanvas().getSelection().addSelectionListener(attributeTable);

        DraggableTab tab = new DraggableTab(circ.getName()+".app", IconsManager.getImage("projapp.gif"), appearanceEditor);
        tab.setStageTitle(LC.createComplexStringBinding("titleAppearanceEditor", circ.getName(), proj.getLogisimFile().getName()));

        tab.getContent().setOnMouseClicked(event -> {
            setEditor(appearanceEditor);
            currAppearanceCanvas = appearanceEditor.getAppearanceCanvas();
            proj.setCurrentCircuit(circ);
        });

        tab.setOnSelectionChanged(event -> {
            if (tab.isSelected()) {
                setEditor(appearanceEditor);
                currAppearanceCanvas = appearanceEditor.getAppearanceCanvas();
                appearanceEditor.getAppearanceCanvas().updateResume();
                proj.setCurrentCircuit(circ);
            } else {
                appearanceEditor.getAppearanceCanvas().updateStop();
            }
        });

        tab.setOnIntoSeparatedWindow(event -> appearanceEditor.copyAccelerators());

        tab.setOnCloseRequest(event -> currAppearanceCanvas.updateStop());

        openedAppearanceEditors.put(circ, tab);

        currAppearanceCanvas.updateResume();

        workspaceTabPane.addTab(tab);
        workspaceTabPane.getSelectionModel().select(tab);

    }

    public void createVerilogModelEditor(Circuit circ){

        proj.setCurrentCircuit(circ);

        if (openedVerilogModelEditors.containsKey(circ)){
            if (openedVerilogModelEditors.get(circ).getTabPane() == null){
                workspaceTabPane.addTab(openedVerilogModelEditors.get(circ));
            }
            selectVerilogModelEditor(circ);
            return;
        }

        CodeEditor codeEditor = new CodeEditor(proj, circ, "verilog");
        setEditor(codeEditor);

        DraggableTab tab = new DraggableTab(circ.getName()+".v", IconsManager.getImage("code.gif"), codeEditor);
        tab.setStageTitle(LC.createComplexStringBinding("titleVerilogCodeEditor", circ.getName(), proj.getLogisimFile().getName()));

        tab.getContent().setOnMouseClicked(event -> {
            setEditor(codeEditor);
            proj.setCurrentCircuit(circ);
        });

        tab.setOnSelectionChanged(event -> {
            if (tab.isSelected()) {
                setEditor(codeEditor);
                proj.setCurrentCircuit(circ);
            }
        });

        tab.setOnIntoSeparatedWindow(event -> codeEditor.copyAccelerators());

        openedVerilogModelEditors.put(circ, tab);

        workspaceTabPane.addTab(tab);
        workspaceTabPane.getSelectionModel().select(tab);

    }

    public void createVHDLModelEditor(Circuit circ){

        proj.setCurrentCircuit(circ);

        if (openedVHDLModelEditors.containsKey(circ)){
            if (openedVHDLModelEditors.get(circ).getTabPane() == null){
                workspaceTabPane.addTab(openedVHDLModelEditors.get(circ));
            }
            selectVHDLModelEditor(circ);
            return;
        }

        CodeEditor codeEditor = new CodeEditor(proj, circ, "vhdl");
        setEditor(codeEditor);

        DraggableTab tab = new DraggableTab(circ.getName()+".vhdl", IconsManager.getImage("code.gif"), codeEditor);
        tab.setStageTitle(LC.createComplexStringBinding("titleVhdlCodeEditor", circ.getName(), proj.getLogisimFile().getName()));

        tab.getContent().setOnMouseClicked(event -> {
            setEditor(codeEditor);
            proj.setCurrentCircuit(circ);
        });

        tab.setOnSelectionChanged(event -> {
            if (tab.isSelected()) {
                setEditor(codeEditor);
                proj.setCurrentCircuit(circ);
            }
        });

        tab.setOnIntoSeparatedWindow(event -> codeEditor.copyAccelerators());

        openedVHDLModelEditors.put(circ, tab);

        workspaceTabPane.addTab(tab);
        workspaceTabPane.getSelectionModel().select(tab);

    }


    public void selectCircLayoutEditor(Circuit circ){
        openedLayoutEditors.get(circ).getTabPane().getSelectionModel().select(openedLayoutEditors.get(circ));
    }

    public void selectCircAppearanceEditor(Circuit circ){
        openedAppearanceEditors.get(circ).getTabPane().getSelectionModel().select(openedAppearanceEditors.get(circ));
    }

    public void selectVerilogModelEditor(Circuit circ){
        openedVerilogModelEditors.get(circ).getTabPane().getSelectionModel().select(openedVerilogModelEditors.get(circ));
    }

    public void selectVHDLModelEditor(Circuit circ){
        openedVHDLModelEditors.get(circ).getTabPane().getSelectionModel().select(openedVHDLModelEditors.get(circ));
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


    public AttributeTable getAttributeTable(){
        return attributeTable;
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

        for (Tab tab: openedLayoutEditors.values()){
            ((LayoutEditor) tab.getContent()).terminateListeners();
        }

        for (Tab tab: openedAppearanceEditors.values()){
            ((AppearanceEditor) tab.getContent()).terminateListeners();
        }

        for (Tab tab: openedVHDLModelEditors.values()){
            ((CodeEditor) tab.getContent()).terminateListeners();
        }

        for (Tab tab: openedVerilogModelEditors.values()){
            ((CodeEditor) tab.getContent()).terminateListeners();
        }

        List<DraggableTab> keys = new ArrayList<>(openedWaveforms.keySet());
        for (DraggableTab tab: keys){
            openedWaveforms.get(tab).onClose();
            tab.close();
            openedWaveforms.remove(tab);
        }

        proj.removeProjectListener(myProjectListener);
        proj.removeLibraryListener(myProjectListener);
        proj.removeCircuitListener(myProjectListener);

        System.out.println("main close. requested by:" + this);

    }

}