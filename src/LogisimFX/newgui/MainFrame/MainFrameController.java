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

import docklib.customsplitpane.CustomSplitPane;
import docklib.dock.DockAnchor;
import docklib.dock.DockPane;
import docklib.draggabletabpane.*;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.util.*;

public class MainFrameController extends AbstractController {

    //Check Frame of com.cburch.logisim.gui.main

    private Stage stage;
    private boolean sceneGraphPerformed;

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
    private HashMap<Circuit, DraggableTab> openedCircuitVerilogModelEditors = new HashMap<>();
    private HashMap<Component, DraggableTab> openedComponentVerilogModelEditors = new HashMap<>();


    private DockPane lastUsedDockPane;
    private HashMap<Window, DraggableTab> lastSelectedTabInWindow = new HashMap<>();


    private DockPane mainWinRootDockPane;
    private DoubleSidedTabPane systemTabPaneLeft, systemTabPaneRight, systemTabPaneBottom;
    private DockPane mainWinWorkspace;

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
                        if (openedCircuitVerilogModelEditors.containsKey(circ)){
                            openedCircuitVerilogModelEditors.get(circ).close();
                            ((EditorBase) openedCircuitVerilogModelEditors.get(circ).getContent()).terminateListeners();
                            openedCircuitVerilogModelEditors.remove(circ);
                        }

                        for (Component comp : openedComponentVerilogModelEditors.keySet()){
                            CodeEditor editor = (CodeEditor) openedComponentVerilogModelEditors.get(comp).getContent();
                            if (editor.getCirc() == circ){
                                openedComponentVerilogModelEditors.get(comp).close();
                                editor.terminateListeners();
                                openedComponentVerilogModelEditors.remove(comp);
                            }
                        }

                        if (openedComponentVerilogModelEditors.containsKey(circ)){
                            openedCircuitVerilogModelEditors.get(circ).close();
                            ((EditorBase) openedCircuitVerilogModelEditors.get(circ).getContent()).terminateListeners();
                            openedCircuitVerilogModelEditors.remove(circ);
                        }
                    }
                }
            }

            if(e.getAction() == LibraryEvent.ADD_TOOL){
                Object t = e.getData();
                if (t instanceof AddTool) {
                    t = ((AddTool) t).getFactory();
                    if (t instanceof SubcircuitFactory) {
                        addCircLayoutEditor(((SubcircuitFactory) t).getSubcircuit());
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

    public void postInitialization(Stage s, Project p) {

        stage = s;

        stage.iconifiedProperty().addListener((observable, oldValue, newValue) ->{
            if(observable.getValue().booleanValue()){
                stopCanvasRender();
            }else{
                resumeCanvasRender();
            }
        });

        stage.focusedProperty().addListener(change ->{
                if(stage.isFocused() && lastSelectedTabInWindow.containsKey(stage)) {
                    lastSelectedTabInWindow.get(stage).getTabPane().getSelectionModel().select(lastSelectedTabInWindow.get(stage));
                }
        });

        stage.setOnShowing(event -> sceneGraphPerformed = true);

        proj = p;
        proj.setFrameController(this);

        proj.addProjectListener(myProjectListener);
        proj.addLibraryListener(myProjectListener);
        proj.addCircuitListener(myProjectListener);

        mainWinRootDockPane = new DockPane(false);
        VBox.setVgrow(mainWinRootDockPane, Priority.ALWAYS);

        systemTabPaneLeft = new DoubleSidedTabPane(stage, proj);
        systemTabPaneLeft.setSide(Side.LEFT);

        systemTabPaneRight= new DoubleSidedTabPane(stage, proj);
        systemTabPaneRight.setSide(Side.RIGHT);

        systemTabPaneBottom = new DoubleSidedTabPane(stage, proj);
        systemTabPaneBottom.setSide(Side.BOTTOM);

        mainWinWorkspace = new DockPane(false);
        mainWinWorkspace.setUseDockPaneBoundaryForSideDock(true);

        mainWinRootDockPane.dock(mainWinWorkspace, DockAnchor.TOP);
        mainWinRootDockPane.dock(systemTabPaneLeft, DockAnchor.LEFT);
        mainWinRootDockPane.dock(systemTabPaneRight, DockAnchor.RIGHT);
        mainWinRootDockPane.dock(systemTabPaneBottom, DockAnchor.BOTTOM);

        menubar = new CustomMenuBar(stage, proj);

        Root.getChildren().addAll(menubar, mainWinRootDockPane);

        //This will init all necessary listeners for all new created work tabpanes
        workpaneProperty().addListener((v, o, n) -> {
            n.getSelectionModel().selectedIndexProperty().addListener(ch -> {
                if (n.getScene() != null && n.getScene() == stage.getScene()) {
                    computeTitle((DraggableTab) n.getSelectionModel().getSelectedItem());
                }
            });
            n.getSelectionModel().selectedItemProperty().addListener(ch -> {
                if (n.getScene() != null && n.getScene() == stage.getScene()) {
                    computeTitle((DraggableTab) n.getSelectionModel().getSelectedItem());
                }
            });
            n.setOnUndock(event -> {
                DraggableTabPane tabPane = getNearestWorkPane();
                if (tabPane != null) tabPane.getSelectionModel().select(tabPane.getSelectionModel().getSelectedItem());
            });
            if (n.getScene() != null && n.getScene() == stage.getScene()) {
                computeTitle((DraggableTab) n.getSelectionModel().getSelectedItem());
            }
        });

        restoreLayout(proj.getLogisimFile().getOptions().getMainFrameLayout());

        computeTitle();

    }

    private void computeTitle(){

        stage.titleProperty().unbind();

        if (!getNearestWorkPaneOrCreate().getTabs().isEmpty()){
            stage.titleProperty().bind(((DraggableTab) getNearestWorkPaneOrCreate().getSelectionModel().getSelectedItem()).getStageTitle());
        } else {
            stage.setTitle("LogisimFX");
        }

    }

    private void computeTitle(DraggableTab tab){

        stage.titleProperty().unbind();

        if (tab != null){
            stage.titleProperty().bind(tab.getStageTitle());
        } else {
            stage.setTitle("LogisimFX");
        }

    }



    /* Layout */

    public void restoreLayout(FrameLayout layout){

        layout.registerProject(proj);

        if (layout.isLayoutDefault()) {
            createDefaultLayout();
        } else {

            FrameLayout.MainWindowDescriptor mainWindowDescriptor = layout.getMainWindowDescriptor();
            stage.setWidth(mainWindowDescriptor.width);
            stage.setHeight(mainWindowDescriptor.height);
            stage.setX(mainWindowDescriptor.x);
            stage.setY(mainWindowDescriptor.y);
            stage.setFullScreen(mainWindowDescriptor.isFullScreen);

           for (FrameLayout.SideBarDescriptor sideBarDescriptor: mainWindowDescriptor.sideBarDescriptors){

               DoubleSidedTabPane buffPane;
               switch (sideBarDescriptor.name){
                   case "sysLeft":     buffPane = systemTabPaneLeft; break;
                   case "sysRight":    buffPane = systemTabPaneRight; break;
                   case "sysBottom":   buffPane = systemTabPaneBottom; break;
                   default:            buffPane = systemTabPaneLeft; break;
               }

               if (!sideBarDescriptor.leftCollapsed){
                   buffPane.setLeftCollapseOnInit(false);
               }
               if (!sideBarDescriptor.rightCollapsed){
                   buffPane.setRightCollapseOnInit(false);
               }
               buffPane.setPrefExpandedSize(sideBarDescriptor.size);

               for(FrameLayout.SystemTabDescriptor systemTabDescriptor: sideBarDescriptor.systemTabDescriptors){

                   DraggableTab tab = null;
                   switch (systemTabDescriptor.type){
                       case "tools":   tab = createToolsTab(); break;
                       case "simtree": tab = createSimulationTab(); break;
                       case "attrs":   tab = createAttributesTab(); break;
                       case "wave":    tab = createWaveformTab(); break;
                   }

                   if (systemTabDescriptor.side.equals("left")){
                       buffPane.addLeft(tab);
                   } else {
                       buffPane.addRight(tab);
                   }

               }

           }

            //Restore workspace tabs

            restoreTabPaneLayout(mainWindowDescriptor.tabPaneLayoutDescriptors, mainWinWorkspace, true);

            //SubWindows
            DockPane dockPane;
            for(FrameLayout.SubWindowDescriptor subWindowDescriptor: layout.getSubWindowDescriptors()){

                dockPane = new DockPane();
                DraggableTab lastTab = null;

                restoreTabPaneLayout(subWindowDescriptor.tabpanes, dockPane, false);

                final Stage newFloatStage = new Stage();
                newFloatStage.getIcons().add(IconsManager.LogisimFX);
                newFloatStage.titleProperty().bind(lastTab.getStageTitle());
                stage.getScene().getWindow().addEventHandler(WindowEvent.WINDOW_HIDING, windowEvent -> newFloatStage.close());

                newFloatStage.setScene(new Scene(dockPane));
                newFloatStage.initStyle(StageStyle.DECORATED);
                newFloatStage.setWidth(subWindowDescriptor.width);
                newFloatStage.setHeight(subWindowDescriptor.height);
                newFloatStage.setX(subWindowDescriptor.x);
                newFloatStage.setY(subWindowDescriptor.y);
                newFloatStage.show();

                DockPane finalDockPane = dockPane;
                finalDockPane.getChildren().addListener((ListChangeListener<Node>) change -> {

                    if(finalDockPane.getChildren().isEmpty()){
                        newFloatStage.close();
                        newFloatStage.setScene(null);
                        lastSelectedTabInWindow.remove(newFloatStage);
                    }

                });

                lastSelectedTabInWindow.put(newFloatStage, lastTab);

                newFloatStage.show();
                newFloatStage.toFront();

            }

        }

        editorProperty().addListener((observableValue, handler, t1) -> {
            Platform.runLater(() -> setLastUsedDockPane(t1));
        });

        proj.setCurrentCircuit(proj.getLogisimFile().getMainCircuit());

    }

    private void restoreTabPaneLayout(ArrayList<FrameLayout.TabPaneLayoutDescriptor> descriptors, DockPane dockPane, boolean setWorkpane){

        Object selectedTab;
        String selectedTabType;
        DraggableTabPane prevTabPane = null;

        for (FrameLayout.TabPaneLayoutDescriptor tabPaneLayoutDescriptor: descriptors){

            DraggableTabPane tabPane = new DraggableTabPane(stage, TabGroup.WorkSpace);
            tabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
            tabPane.setProject(proj);

            DraggableTab tab = null;
            selectedTab = null;
            selectedTabType = null;

            for (FrameLayout.EditorTabDescriptor editorTabDescriptor : tabPaneLayoutDescriptor.tabs){

                Circuit circ = proj.getLogisimFile().getCircuit(editorTabDescriptor.circ);
                if (editorTabDescriptor.isSelected) {
                    selectedTab = editorTabDescriptor.isCompEditor ? editorTabDescriptor.comp : circ;
                    selectedTabType = editorTabDescriptor.type;
                }

                switch (editorTabDescriptor.type){
                    case "app":     tab = createCircAppearanceEditor(circ); break;
                    case "layo":    tab = createCircLayoutEditor(circ); break;
                    case "hdl": {
                        tab = editorTabDescriptor.isCompEditor
                        ? createVerilogModelEditor(circ, circ.getExclusive(editorTabDescriptor.loc))
                        : createVerilogModelEditor(circ);
                        break;
                    }
                }

                tabPane.addTab(tab);

            }

            if (selectedTab != null) {
                switch (selectedTabType) {
                    case "app":
                        if (selectedTab instanceof Circuit) {
                            selectCircAppearanceEditor((Circuit) selectedTab);
                        }
                        break;
                    case "layo":
                        if (selectedTab instanceof Circuit) {
                            selectCircLayoutEditor((Circuit) selectedTab);
                        }
                        break;
                    case "hdl": {
                        if (selectedTab instanceof Circuit) {
                            selectVerilogModelEditor((Circuit)selectedTab);
                        }
                        if (selectedTab instanceof Component) {
                            selectVerilogModelEditor((Component) selectedTab);
                        }
                        break;
                    }
                }
            }

            DockAnchor anchor;
            if (tabPaneLayoutDescriptor.anchor.equals("bottom")){
                anchor = DockAnchor.BOTTOM;
            } else {
                anchor = DockAnchor.RIGHT;
            }

            if (tabPaneLayoutDescriptor.append && prevTabPane != null){
                dockPane.dock(tabPane, anchor, prevTabPane);
            } else {
                dockPane.dock(tabPane, anchor);
            }

            prevTabPane = tabPane;

        }

        if (setWorkpane){
            setWorkPane(prevTabPane);
        }

    }

    private void createDefaultLayout(){

        systemTabPaneLeft.setCollapseOnInit(false);
        systemTabPaneLeft.setPrefExpandedSize(200);

        systemTabPaneBottom.setPrefExpandedSize(300);

        createWorkPane(mainWinWorkspace);

        addToolsTab();
        addSimulationTab();
        addAttributesTab();
        addWaveformTab();

        addCircAppearanceEditor(proj.getCurrentCircuit());
        addCircLayoutEditor(proj.getCurrentCircuit());
        addVerilogModelEditor(proj.getCurrentCircuit());

        selectCircLayoutEditor(proj.getCurrentCircuit());

    }

    public void toDefaultSystemTabs(){

        ArrayList<DraggableTab> tabs = new ArrayList<>(openedSystemTabs.values());
        for (DraggableTab tab: tabs) {
            tab.close();
        }
        openedSystemTabs.clear();

        tabs = new ArrayList<>(openedWaveforms.keySet());
        for (DraggableTab tab: tabs){
            openedWaveforms.get(tab).onClose();
            tab.close();
        }
        openedWaveforms.clear();

        systemTabPaneLeft.setCollapseOnInit(false);
        systemTabPaneLeft.setPrefExpandedSize(200);

        systemTabPaneBottom.setPrefExpandedSize(300);

        addToolsTab();
        addSimulationTab();
        addAttributesTab();
        addWaveformTab();

    }

    public void toDefaultWorkspace(){

        ArrayList<DraggableTab> tabs = new ArrayList<>(openedLayoutEditors.values());
        for (DraggableTab tab: tabs){
            ((LayoutEditor) tab.getContent()).terminateListeners();
            if (tab.getTabPane() != null)tab.close();
        }
        openedLayoutEditors.clear();


        tabs = new ArrayList<>(openedAppearanceEditors.values());
        for (DraggableTab tab: tabs){
            ((AppearanceEditor) tab.getContent()).terminateListeners();
            if (tab.getTabPane() != null)tab.close();
        }
        openedAppearanceEditors.clear();

        tabs = new ArrayList<>(openedCircuitVerilogModelEditors.values());
        for (DraggableTab tab: tabs){
            ((CodeEditor) tab.getContent()).terminateListeners();
            if (tab.getTabPane() != null)tab.close();
        }
        openedCircuitVerilogModelEditors.clear();

        tabs = new ArrayList<>(openedComponentVerilogModelEditors.values());
        for (DraggableTab tab: tabs){
            ((CodeEditor) tab.getContent()).terminateListeners();
            if (tab.getTabPane() != null)tab.close();
        }
        openedComponentVerilogModelEditors.clear();

        createWorkPane(mainWinWorkspace);

        addCircAppearanceEditor(proj.getCurrentCircuit());

        addCircLayoutEditor(proj.getCurrentCircuit());
        addVerilogModelEditor(proj.getCurrentCircuit());

        selectCircLayoutEditor(proj.getCurrentCircuit());


    }

    private void drawTree(Node parent, String dash){

        System.out.println(dash + parent);
        ObservableList<Node> children;
        if (parent instanceof SplitPane) {
            SplitPane split = (SplitPane) parent;
            children = split.getItems();
            for (Node n: children){
                drawTree(n, dash+"    ");
            }
        }

    }

    public void toDefaultLayout(){

        toDefaultSystemTabs();
        toDefaultWorkspace();

    }

    public Element getLayout(Document doc){

        Element layout = doc.createElement("layout");

        Element mainframe = doc.createElement("mainwindow");

        mainframe.setAttribute("width", Double.toString(stage.getWidth()));
        mainframe.setAttribute("height", Double.toString(stage.getHeight()));
        mainframe.setAttribute("x", Double.toString(stage.getX()));
        mainframe.setAttribute("y", Double.toString(stage.getY()));
        mainframe.setAttribute("fullscreen", Boolean.toString(stage.isFullScreen()));

        //SystemSideBars

        Element sysLeft = getSystemSideBar(systemTabPaneLeft, doc);
        sysLeft.setAttribute("pane", "sysLeft");

        Element sysRight = getSystemSideBar(systemTabPaneRight, doc);
        sysRight.setAttribute("pane", "sysRight");

        Element sysBottom = getSystemSideBar(systemTabPaneBottom, doc);
        sysBottom.setAttribute("pane", "sysBottom");

        //Workspace

        Element workSpace = doc.createElement("workpane");
        for (Element tabpane: getSubFrameLayout(mainWinWorkspace, doc, null)){
            workSpace.appendChild(tabpane);
        }

        mainframe.appendChild(sysLeft);
        mainframe.appendChild(sysRight);
        mainframe.appendChild(sysBottom);
        mainframe.appendChild(workSpace);

        layout.appendChild(mainframe);

        //SubWindows

        for(Window win: lastSelectedTabInWindow.keySet()){
            Element frame = doc.createElement("window");
            frame.setAttribute("width", Double.toString(win.getWidth()));
            frame.setAttribute("height", Double.toString(win.getHeight()));
            frame.setAttribute("x", Double.toString(win.getX()));
            frame.setAttribute("y", Double.toString(win.getY()));
            for (Element tabpane: getSubFrameLayout(win.getScene().getRoot(), doc, null)){
                frame.appendChild(tabpane);
            }
            layout.appendChild(frame);
        }

        return layout;

    }

    private Element getSystemSideBar(DoubleSidedTabPane tabPane, Document doc){

        Element sysTabPane = doc.createElement("sidebar");
        sysTabPane.setAttribute("left", Boolean.toString(tabPane.getLeftTabPane().isCollapsed()));
        sysTabPane.setAttribute("right", Boolean.toString(tabPane.getRightTabPane().isCollapsed()));
        sysTabPane.setAttribute("size", Double.toString(tabPane.getPrefExpandedSize()));

        for (Tab tab: tabPane.getLeftTabPane().getTabs()){
            Element t = doc.createElement("tab");
            t.setAttribute("type", ((DraggableTab)tab).getType());
            t.setAttribute("side", "left");
            sysTabPane.appendChild(t);
        }
        for (Tab tab: tabPane.getRightTabPane().getTabs()){
            Element t = doc.createElement("tab");
            t.setAttribute("type", ((DraggableTab)tab).getType());
            t.setAttribute("side", "right");
            sysTabPane.appendChild(t);
        }

        return sysTabPane;

    }

    private ArrayList<Element> getSubFrameLayout(Parent parent, Document doc, String prevDockAnchor){

        ArrayList<Element> tabpanes = new ArrayList<>();

        ObservableList<Node> children = parent.getChildrenUnmodifiable();

        boolean dash = prevDockAnchor != null;

        String dockAnchor = null;
        if (parent instanceof SplitPane) {

            SplitPane split = (SplitPane) parent;
            children = split.getItems();

            if (split.getOrientation() == Orientation.HORIZONTAL) {
                dockAnchor = "right";
            } else {
                dockAnchor = "bottom";
            }

        }

        for (int i = 0; i < children.size(); i++) {

            if (children.get(i) instanceof DraggableTabPane) {

                Element tabpane = doc.createElement("tabpane");
                tabpanes.add(tabpane);

                if (dash) {
                    tabpane.setAttribute("anchor", prevDockAnchor);
                    tabpane.setAttribute("append", "false");
                    dash = false;
                } else {
                    tabpane.setAttribute("anchor", dockAnchor);
                    //if it is first seen tabpane append should be false
                    if (i > 0 && !(children.get(i-1) instanceof DraggableTabPane)) {
                        tabpane.setAttribute("append", "false");
                    } else {
                        tabpane.setAttribute("append", "true");
                    }

                }

                for (Tab tab: ((DraggableTabPane)children.get(i)).getTabs()){

                    DraggableTab t = (DraggableTab) tab;
                    EditorBase editor = (EditorBase)t.getContent();
                    Element elm = doc.createElement("tab");
                    elm.setAttribute("circ", editor.getCirc().getName());

                    if (t.getType().equals("hdl") && ((CodeEditor) editor).getComp() != null){
                        Component comp = ((CodeEditor) editor).getComp();
                        elm.setAttribute("comp", comp.getFactory().getName());
                        elm.setAttribute("loc", comp.getLocation().toString());
                    }
                    elm.setAttribute("type", t.getType());

                    if (t.isSelected()) {
                        elm.setAttribute("selected", "true");
                    }

                    tabpane.appendChild(elm);

                }

            } else if (children.get(i) instanceof Parent) {
                dash = false;
                tabpanes.addAll(getSubFrameLayout((Parent) children.get(i), doc, dockAnchor));
            }

        }

        return tabpanes;

    }



    //SideBar Tabs

    public void addToolsTab(){
        DraggableTab tab = createToolsTab();
        if (tab != null) systemTabPaneLeft.addLeft(tab);
    }

    private DraggableTab createToolsTab(){

        if (openedSystemTabs.containsKey("ToolsTab")){
            openedSystemTabs.get("ToolsTab").getTabPane().getSelectionModel().select(openedSystemTabs.get("ToolsTab"));
            ((DraggableTabPane) openedSystemTabs.get("ToolsTab").getTabPane()).expand();
            return null;
        }

        ProjectExplorerTreeView projectExplorerTreeView = new ProjectExplorerTreeView(proj);
        ProjectTreeToolBar projectTreeToolBar = new ProjectTreeToolBar(proj, projectExplorerTreeView);
        VBox vBox1 = new VBox(projectTreeToolBar, projectExplorerTreeView);
        VBox.setVgrow(projectExplorerTreeView, Priority.ALWAYS);

        DraggableTab projectExplorerTab = new DraggableTab(LC.createStringBinding("toolsTab"), IconsManager.getImage("projtool.gif"), vBox1);
        projectExplorerTab.setTooltip(new ToolTip("projectViewToolboxTip"));
        projectExplorerTab.setOnClosed(event -> openedSystemTabs.remove("ToolsTab"));
        projectExplorerTab.setType("tools");

        openedSystemTabs.put("ToolsTab", projectExplorerTab);

        return projectExplorerTab;

    }

    public void addSimulationTab(){
        DraggableTab tab = createSimulationTab();
        if (tab != null) systemTabPaneLeft.addLeft(tab);
    }

    private DraggableTab createSimulationTab(){

        if (openedSystemTabs.containsKey("SimulationTab")){
            openedSystemTabs.get("SimulationTab").getTabPane().getSelectionModel().select(openedSystemTabs.get("SimulationTab"));
            ((DraggableTabPane) openedSystemTabs.get("SimulationTab").getTabPane()).expand();
            return null;
        }

        SimulationExplorerTreeView simulationExplorerTreeView = new SimulationExplorerTreeView(proj);
        SimulationTreeToolBar simulationTreeToolBar = new SimulationTreeToolBar(proj);
        VBox vBox2 = new VBox(simulationTreeToolBar, simulationExplorerTreeView);
        VBox.setVgrow(simulationExplorerTreeView, Priority.ALWAYS);

        DraggableTab simulationExplorerTab = new DraggableTab(LC.createStringBinding("simTab"), IconsManager.getImage("projsim.gif"), vBox2);
        simulationExplorerTab.setTooltip(new ToolTip("projectViewSimulationTip"));
        simulationExplorerTab.setOnClosed(event -> openedSystemTabs.remove("SimulationTab"));
        simulationExplorerTab.setType("simtree");

        openedSystemTabs.put("SimulationTab", simulationExplorerTab);

        return simulationExplorerTab;

    }

    public void addAttributesTab(){
        DraggableTab tab = createAttributesTab();
        if (tab != null) systemTabPaneLeft.addRight(tab);
    }

    private DraggableTab createAttributesTab(){

        if (openedSystemTabs.containsKey("AttributesTab")){
            openedSystemTabs.get("AttributesTab").getTabPane().getSelectionModel().select(openedSystemTabs.get("AttributesTab"));
            ((DraggableTabPane) openedSystemTabs.get("AttributesTab").getTabPane()).expand();
            return null;
        }

        ScrollPane scrollPane = new ScrollPane();

        attributeTable = new AttributeTable(proj);
        attributeTable.setFocusTraversable(false);

        scrollPane.setContent(attributeTable);
        scrollPane.setFitToWidth(true);

        DraggableTab attributeTableTab = new DraggableTab(LC.createStringBinding("attrTab"), IconsManager.getImage("circattr.gif"), scrollPane);
        attributeTableTab.setOnClosed(event -> openedSystemTabs.remove("AttributesTab"));
        attributeTableTab.setType("attrs");

        openedSystemTabs.put("AttributesTab", attributeTableTab);

        return attributeTableTab;

    }

    public void addWaveformTab(){
        DraggableTab tab = createWaveformTab();
        if (tab != null) systemTabPaneBottom.addLeft(tab);
    }

    private DraggableTab createWaveformTab(){

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
        waveformTab.setType("wave");

        waveformTab.setOnCloseRequest(event -> openedWaveforms.remove(waveformTab));

        openedWaveforms.put(waveformTab, (WaveformController)c);

        if (stage.isShowing()) {
            Platform.runLater(((WaveformController) c)::findScrollBar);
        }

        return waveformTab;

    }



    //WorkSpace Tabs

    public void addCircLayoutEditor(Circuit circ){
        DraggableTab tab = createCircLayoutEditor(circ);
        if (tab != null) {
            getNearestWorkPaneOrCreate().addTab(tab);
            selectCircLayoutEditor(circ);
        }
    }

    private DraggableTab createCircLayoutEditor(Circuit circ){

        proj.setCurrentCircuit(circ);

        if (openedLayoutEditors.containsKey(circ)){
            if (openedLayoutEditors.get(circ).getTabPane() == null){
                getNearestWorkPaneOrCreate().addTab(openedLayoutEditors.get(circ));
            }
            selectCircLayoutEditor(circ);
            return null;
        }

        LayoutEditor layoutEditor = new LayoutEditor(proj, circ);
        setEditor(layoutEditor);
        currLayoutCanvas = layoutEditor.getLayoutCanvas();
        layoutEditor.getLayoutCanvas().getSelection().addListener(attributeTable);

        DraggableTab tab = new DraggableTab(circ.getName()+".layo", IconsManager.getImage("projlayo.gif"), layoutEditor);
        tab.setStageTitle(LC.createComplexStringBinding("titleLayoutEditor", circ.getName(), proj.getLogisimFile().getName()));
        tab.setType("layo");



        tab.getContent().setOnMousePressed(event -> {
            setEditor(layoutEditor);
            setWorkPane((DraggableTabPane) tab.getTabPane());
            currLayoutCanvas = layoutEditor.getLayoutCanvas();
            proj.setCurrentCircuit(circ);
        });

        tab.setOnSelectionChanged(event -> {
            if (tab.isSelected()) {
                setEditor(layoutEditor);
                if (tab.getTabPane() != null) {
                    setWorkPane((DraggableTabPane) tab.getTabPane());
                } else {
                    Platform.runLater(() -> setWorkPane((DraggableTabPane) tab.getTabPane()));
                }
                currLayoutCanvas = layoutEditor.getLayoutCanvas();
                layoutEditor.getLayoutCanvas().updateResume();
                proj.setCurrentCircuit(circ);
            } else {
                layoutEditor.getLayoutCanvas().updateStop();
            }
        });

        tab.setOnIntoSeparatedWindow(event -> {
            layoutEditor.copyAccelerators();
            Window win = tab.getTabPane().getScene().getWindow();
            lastSelectedTabInWindow.put(win, tab);
            win.addEventHandler(WindowEvent.WINDOW_HIDING, windowEvent -> lastSelectedTabInWindow.remove(win));
            win.focusedProperty().addListener(change -> {
            	if (tab.getTabPane() == null) return;
                Window tabWin = tab.getTabPane().getScene().getWindow();
                if (tabWin.isFocused() && lastSelectedTabInWindow.get(tabWin).getTabPane() != null) {
                    lastSelectedTabInWindow.get(tabWin).getTabPane().getSelectionModel().select(lastSelectedTabInWindow.get(tabWin));
                    setEditor((EditorBase) tab.getContent());
                }
            });
        });

        tab.setOnCloseRequest(event -> currLayoutCanvas.updateStop());

        openedLayoutEditors.put(circ, tab);

        currLayoutCanvas.updateResume();

        return tab;

    }


    public void addCircAppearanceEditor(Circuit circ){
        DraggableTab tab = createCircAppearanceEditor(circ);
        if (tab != null) {
            getNearestWorkPaneOrCreate().addTab(tab);
            selectCircAppearanceEditor(circ);
        }
    }

    private DraggableTab createCircAppearanceEditor(Circuit circ){

        proj.setCurrentCircuit(circ);

        if (openedAppearanceEditors.containsKey(circ)){
            if (openedAppearanceEditors.get(circ).getTabPane() == null){
                getNearestWorkPaneOrCreate().addTab(openedAppearanceEditors.get(circ));
            }
            selectCircAppearanceEditor(circ);
            return null;
        }

        AppearanceEditor appearanceEditor = new AppearanceEditor(proj, circ);
        setEditor(appearanceEditor);
        currAppearanceCanvas = appearanceEditor.getAppearanceCanvas();
        appearanceEditor.getAppearanceCanvas().getSelection().addSelectionListener(attributeTable);

        DraggableTab tab = new DraggableTab(circ.getName()+".app", IconsManager.getImage("projapp.gif"), appearanceEditor);
        tab.setStageTitle(LC.createComplexStringBinding("titleAppearanceEditor", circ.getName(), proj.getLogisimFile().getName()));
        tab.setType("app");



        tab.getContent().setOnMousePressed(event -> {
            setEditor(appearanceEditor);
            setWorkPane((DraggableTabPane) tab.getTabPane());
            currAppearanceCanvas = appearanceEditor.getAppearanceCanvas();
            proj.setCurrentCircuit(circ);
        });

        tab.setOnSelectionChanged(event -> {
            if (tab.isSelected()) {
                setEditor(appearanceEditor);
                if (tab.getTabPane() != null) {
                    setWorkPane((DraggableTabPane) tab.getTabPane());
                } else {
                    Platform.runLater(() -> setWorkPane((DraggableTabPane) tab.getTabPane()));
                }
                currAppearanceCanvas = appearanceEditor.getAppearanceCanvas();
                appearanceEditor.getAppearanceCanvas().updateResume();
                proj.setCurrentCircuit(circ);
            } else {
                appearanceEditor.getAppearanceCanvas().updateStop();
            }
        });

        tab.setOnIntoSeparatedWindow(event -> {
            appearanceEditor.copyAccelerators();
			Window win = tab.getTabPane().getScene().getWindow();
            lastSelectedTabInWindow.put(win, tab);
			win.addEventHandler(WindowEvent.WINDOW_HIDING, windowEvent -> lastSelectedTabInWindow.remove(win));
			win.focusedProperty().addListener(change -> {
				if (tab.getTabPane() == null) return;
                Window tabWin = tab.getTabPane().getScene().getWindow();
                if (tabWin.isFocused() && lastSelectedTabInWindow.get(tabWin).getTabPane() != null) {
                    lastSelectedTabInWindow.get(tabWin).getTabPane().getSelectionModel().select(lastSelectedTabInWindow.get(tabWin));
                    setEditor((EditorBase) tab.getContent());
                }
            });
        });

        tab.setOnCloseRequest(event -> currAppearanceCanvas.updateStop());

        openedAppearanceEditors.put(circ, tab);

        currAppearanceCanvas.updateResume();

        return tab;

    }


    public void addVerilogModelEditor(Circuit circ){
        DraggableTab tab = createVerilogModelEditor(circ);
        if (tab != null) {
            getNearestWorkPaneOrCreate().addTab(tab);
            selectVerilogModelEditor(circ);
        }
    }

    private DraggableTab createVerilogModelEditor(Circuit circ){

        proj.setCurrentCircuit(circ);

        if (openedCircuitVerilogModelEditors.containsKey(circ)){
            if (openedCircuitVerilogModelEditors.get(circ).getTabPane() == null){
                getNearestWorkPaneOrCreate().addTab(openedCircuitVerilogModelEditors.get(circ));
            }
            selectVerilogModelEditor(circ);
            return null;
        }

        CodeEditor codeEditor = new CodeEditor(proj, circ);
        setEditor(codeEditor);

        DraggableTab tab = new DraggableTab(circ.getName()+".v", IconsManager.getImage("code.gif"), codeEditor);
        tab.setStageTitle(LC.createComplexStringBinding("titleVerilogCodeEditor", circ.getName(), proj.getLogisimFile().getName()));
        tab.setType("hdl");



        tab.getContent().setOnMousePressed(event -> {
            setEditor(codeEditor);
            setWorkPane((DraggableTabPane) tab.getTabPane());
            proj.setCurrentCircuit(circ);
        });

        tab.setOnSelectionChanged(event -> {
            if (tab.isSelected()) {
                setEditor(codeEditor);
                if (tab.getTabPane() != null) {
                    setWorkPane((DraggableTabPane) tab.getTabPane());
                } else {
                    Platform.runLater(() -> setWorkPane((DraggableTabPane) tab.getTabPane()));
                }
                proj.setCurrentCircuit(circ);
            }
        });

        tab.setOnIntoSeparatedWindow(event -> {
            codeEditor.copyAccelerators();
			Window win = tab.getTabPane().getScene().getWindow();
            lastSelectedTabInWindow.put(win, tab);
            win.addEventHandler(WindowEvent.WINDOW_HIDING, windowEvent -> lastSelectedTabInWindow.remove(win));
            win.focusedProperty().addListener(change -> {
				if (tab.getTabPane() == null) return;
                Window tabWin = tab.getTabPane().getScene().getWindow();
                if (tabWin.isFocused() && lastSelectedTabInWindow.get(tabWin).getTabPane() != null) {
                    lastSelectedTabInWindow.get(tabWin).getTabPane().getSelectionModel().select(lastSelectedTabInWindow.get(tabWin));
                    setEditor((EditorBase) tab.getContent());
                }
            });
        });

        openedCircuitVerilogModelEditors.put(circ, tab);

        return tab;

    }


    public void addVerilogModelEditor(Circuit circ, Component comp){
        DraggableTab tab = createVerilogModelEditor(circ, comp);
        if (tab != null) {
            getNearestWorkPaneOrCreate().addTab(tab);
            selectVerilogModelEditor(comp);
        }
    }

    private DraggableTab createVerilogModelEditor(Circuit circ, Component comp){

        String hdlName = comp.getFactory().getHDLName(comp.getAttributeSet());

        proj.setCurrentCircuit(circ);

        if (openedComponentVerilogModelEditors.containsKey(comp)){
            if (openedComponentVerilogModelEditors.get(comp).getTabPane() == null){
                getNearestWorkPaneOrCreate().addTab(openedComponentVerilogModelEditors.get(comp));
            }
            selectVerilogModelEditor(comp);
            return null;
        }

        CodeEditor codeEditor = new CodeEditor(proj, circ, comp);
        setEditor(codeEditor);

        DraggableTab tab = new DraggableTab(hdlName+comp.getLocation().toString()+".v", IconsManager.getImage("code.gif"), codeEditor);
        tab.setStageTitle(LC.createComplexStringBinding("titleVerilogCodeEditor", hdlName, proj.getLogisimFile().getName()));
        tab.setType("hdl");



        tab.getContent().setOnMousePressed(event -> {
            setEditor(codeEditor);
            setWorkPane((DraggableTabPane) tab.getTabPane());
            proj.setCurrentCircuit(circ);
        });

        tab.setOnSelectionChanged(event -> {
            if (tab.isSelected()) {
                setEditor(codeEditor);
                if (tab.getTabPane() != null) {
                    setWorkPane((DraggableTabPane) tab.getTabPane());
                } else {
                    Platform.runLater(() -> setWorkPane((DraggableTabPane) tab.getTabPane()));
                }
                proj.setCurrentCircuit(circ);
            }
        });

        tab.setOnIntoSeparatedWindow(event -> {
            codeEditor.copyAccelerators();
			Window win = tab.getTabPane().getScene().getWindow();
            lastSelectedTabInWindow.put(win, tab);
            win.addEventHandler(WindowEvent.WINDOW_HIDING, windowEvent -> lastSelectedTabInWindow.remove(win));
            win.focusedProperty().addListener(change -> {
				if (tab.getTabPane() == null) return;
                Window tabWin = tab.getTabPane().getScene().getWindow();
                if (tabWin.isFocused() && lastSelectedTabInWindow.get(tabWin).getTabPane() != null) {
                    lastSelectedTabInWindow.get(tabWin).getTabPane().getSelectionModel().select(lastSelectedTabInWindow.get(tabWin));
                    setEditor((EditorBase) tab.getContent());
                }
            });
        });

        openedComponentVerilogModelEditors.put(comp, tab);

        return tab;

    }



    public void selectCircLayoutEditor(Circuit circ){
        openedLayoutEditors.get(circ).getTabPane().getSelectionModel().select(openedLayoutEditors.get(circ));
    }

    public void selectCircAppearanceEditor(Circuit circ){
        openedAppearanceEditors.get(circ).getTabPane().getSelectionModel().select(openedAppearanceEditors.get(circ));
    }

    public void selectVerilogModelEditor(Circuit circ){
        openedCircuitVerilogModelEditors.get(circ).getTabPane().getSelectionModel().select(
                openedCircuitVerilogModelEditors.get(circ));
    }

    public void selectVerilogModelEditor(Component comp){
        openedComponentVerilogModelEditors.get(comp).getTabPane().getSelectionModel().select(
                openedComponentVerilogModelEditors.get(comp));
    }


    private ObjectProperty<DraggableTabPane> currWorkspaceTabPane;

    private void createWorkPane(DockPane parent){

        DraggableTabPane workspaceTabPane = new DraggableTabPane(stage, TabGroup.WorkSpace);
        workspaceTabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
        workspaceTabPane.setSide(Side.TOP);
        workspaceTabPane.setRotateGraphic(true);
        //workspaceTabPane.setUnDockable(false);
        workspaceTabPane.setProject(proj);

        parent.dock(workspaceTabPane, DockAnchor.RIGHT);

        setWorkPane(workspaceTabPane);

    }

    private void setLastUsedDockPane(Node node){
        Node n = node;
        while(n.getParent() != null && !(n.getParent() instanceof DockPane)){
            n = n.getParent();
        }
        lastUsedDockPane = (DockPane) n.getParent();
    }

    private void setWorkPane(DraggableTabPane value) {
        if (value != null && workpaneProperty().get() != value) {
            workpaneProperty().set(value);
        }
    }

    /*Use when u'r sure it is present in scene graph*/
    private DraggableTabPane getWorkPane(){
        return workpaneProperty().get();
    }

    /*Use when u'r not sure it is present in scene graph. return last selected/first in window/first in main window*/
    public DraggableTabPane getNearestWorkPaneOrCreate() {

        if (sceneGraphPerformed && getWorkPane().getParent() == null){

            if (lastUsedDockPane.getParent() != null){

                if(lastUsedDockPane.getChildren().isEmpty()){
                    createWorkPane(lastUsedDockPane);
                    return getWorkPane();
                } else {
                    DraggableTabPane tabPane = (DraggableTabPane)((CustomSplitPane)lastUsedDockPane.getChildren().get(0)).getItems().get(0);
                    setWorkPane(tabPane);
                    return tabPane;

                }

            } else {

                if(mainWinWorkspace.getChildren().isEmpty()){
                    createWorkPane(mainWinWorkspace);
                    return getWorkPane();
                } else {
                    DraggableTabPane tabPane = (DraggableTabPane)((CustomSplitPane) mainWinWorkspace.getChildren().get(0)).getItems().get(0);
                    setWorkPane(tabPane);
                    return tabPane;
                }

            }

        } else {
            return getWorkPane();
        }

    }

    public DraggableTabPane getNearestWorkPane() {

        if (sceneGraphPerformed && getWorkPane().getParent() == null){

            if (lastUsedDockPane.getParent() != null){

                if(!lastUsedDockPane.getChildren().isEmpty()){
                    DraggableTabPane tabPane = (DraggableTabPane)((CustomSplitPane)lastUsedDockPane.getChildren().get(0)).getItems().get(0);
                    setWorkPane(tabPane);
                    return tabPane;
                }

            } else {

                if(!mainWinWorkspace.getChildren().isEmpty()){
                    DraggableTabPane tabPane = (DraggableTabPane)((CustomSplitPane) mainWinWorkspace.getChildren().get(0)).getItems().get(0);
                    setWorkPane(tabPane);
                    return tabPane;
                }

            }

        } else {
            return getWorkPane();
        }

        return null;

    }

    public ObjectProperty<DraggableTabPane> workpaneProperty() {
        if (currWorkspaceTabPane == null) {
            currWorkspaceTabPane = new ObjectPropertyBase<>(null) {
                @Override protected void invalidated() {
                }

                @Override
                public Object getBean() {
                    return MainFrameController.this;
                }

                @Override
                public String getName() {
                    return "workspace";
                }
            };
        }
        return currWorkspaceTabPane;
    }



    //Editor property

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

    private void resumeCanvasRender(){
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

    private void stopCanvasRender(){

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


    @Override
    public void onClose() {

        for (Tab tab: openedLayoutEditors.values()){
            ((LayoutEditor) tab.getContent()).terminateListeners();
        }

        for (Tab tab: openedAppearanceEditors.values()){
            ((AppearanceEditor) tab.getContent()).terminateListeners();
        }

        for (Tab tab: openedCircuitVerilogModelEditors.values()){
            ((CodeEditor) tab.getContent()).terminateListeners();
        }

        for (Tab tab: openedComponentVerilogModelEditors.values()){
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

    }

}