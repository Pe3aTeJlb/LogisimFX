/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.MainFrame;

import LogisimFX.IconsManager;
import LogisimFX.circuit.*;
import LogisimFX.comp.Component;
import LogisimFX.data.Location;
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
import LogisimFX.newgui.MainFrame.SystemTabs.TerminalTab.Terminal;
import LogisimFX.newgui.MainFrame.SystemTabs.WaveformTab.WaveformController;
import LogisimFX.proj.Project;
import LogisimFX.proj.ProjectEvent;
import LogisimFX.proj.ProjectListener;
import LogisimFX.tools.AddTool;
import LogisimFX.tools.Tool;

import docklib.dock.DockAnchor;
import docklib.dock.DockPane;
import docklib.draggabletabpane.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
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

    private Terminal terminal;

    private HashMap<String, DraggableTab> openedSystemTabs = new HashMap<>();
    private HashMap<DraggableTab, WaveformController> openedWaveforms = new HashMap<>();
    private HashMap<Circuit, DraggableTab> openedLayoutEditors = new HashMap<>();
    private HashMap<Circuit, DraggableTab> openedAppearanceEditors = new HashMap<>();
    private HashMap<File, DraggableTab> openedCodeEditors = new HashMap<>();
    private HashMap<Component, DraggableTab> openedComponentVerilogModelViewers = new HashMap<>();


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
                        if (openedCodeEditors.containsKey(circ)){
                            openedCodeEditors.get(circ).close();
                            ((EditorBase) openedCodeEditors.get(circ).getContent()).terminateListeners();
                            openedCodeEditors.remove(circ);
                        }

                        for (Component comp : openedComponentVerilogModelViewers.keySet()){
                            CodeEditor editor = (CodeEditor) openedComponentVerilogModelViewers.get(comp).getContent();
                            if (editor.getCirc() == circ){
                                openedComponentVerilogModelViewers.get(comp).close();
                                editor.terminateListeners();
                                openedComponentVerilogModelViewers.remove(comp);
                            }
                        }

                        if (openedComponentVerilogModelViewers.containsKey(circ)){
                            openedCodeEditors.get(circ).close();
                            ((EditorBase) openedCodeEditors.get(circ).getContent()).terminateListeners();
                            openedCodeEditors.remove(circ);
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

        terminal = new Terminal(proj);
        proj.setTerminal(terminal);

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

        mainWinWorkspace = new DockPane(IconsManager.getLogisimFXPrompt(), false);
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
                        case "terminal":    tab = createTerminalTab(); break;
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

        DraggableTab selectedTab;
        DraggableTabPane prevTabPane = null;

        for (FrameLayout.TabPaneLayoutDescriptor tabPaneLayoutDescriptor: descriptors){

            DraggableTabPane tabPane = new DraggableTabPane(stage, TabGroup.WorkSpace);
            tabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
            tabPane.setProject(proj);

            DraggableTab tab = null;
            selectedTab = null;

            for (FrameLayout.EditorTabDescriptor editorTabDescriptor : tabPaneLayoutDescriptor.tabs){

                tab = readEditorDescriptor(editorTabDescriptor.type, editorTabDescriptor.desk);

                tabPane.addTab(tab);

                if (editorTabDescriptor.isSelected) {
                    selectedTab = tab;
                }

            }

            if (selectedTab != null) {
                selectedTab.getTabPane().getSelectionModel().select(selectedTab);
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
        systemTabPaneLeft.setMinHeight(0);

        systemTabPaneRight.setMinHeight(0);

        systemTabPaneBottom.setPrefExpandedSize(300);

        createWorkPane(mainWinWorkspace);

        addToolsTab();
        addSimulationTab();
        addAttributesTab();
        addWaveformTab();
        addTerminalTab();

        addCircAppearanceEditor(proj.getCurrentCircuit());
        addCircLayoutEditor(proj.getCurrentCircuit());
        addCodeEditor(proj.getCurrentCircuit(), proj.getCurrentCircuit().getVerilogModel(proj));

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
        addTerminalTab();

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

        tabs = new ArrayList<>(openedCodeEditors.values());
        for (DraggableTab tab: tabs){
            ((CodeEditor) tab.getContent()).terminateListeners();
            if (tab.getTabPane() != null)tab.close();
        }
        openedCodeEditors.clear();

        tabs = new ArrayList<>(openedComponentVerilogModelViewers.values());
        for (DraggableTab tab: tabs){
            ((CodeEditor) tab.getContent()).terminateListeners();
            if (tab.getTabPane() != null)tab.close();
        }
        openedComponentVerilogModelViewers.clear();

        createWorkPane(mainWinWorkspace);

        addCircAppearanceEditor(proj.getCurrentCircuit());

        addCircLayoutEditor(proj.getCurrentCircuit());
        addCodeEditor(proj.getCurrentCircuit(), proj.getCurrentCircuit().getVerilogModel(proj));

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
                    Element tabElm = doc.createElement("tab");

                    tabElm.setAttribute("type", t.getType());
                    tabElm.setAttribute("desc", editor.getEditorDescriptor());

                    if (t.isSelected()) {
                        tabElm.setAttribute("selected", "true");
                    }

                    tabpane.appendChild(tabElm);

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
        projectExplorerTab.setStageTitle(
                (StringBinding) Bindings.concat(
                        LC.createStringBinding("logisimFXTitle"),
                        LC.createStringBinding("toolsTab")
                )
        );

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
        simulationExplorerTab.setStageTitle(
                (StringBinding) Bindings.concat(
                        LC.createStringBinding("logisimFXTitle"),
                        LC.createStringBinding("simTab")
                )
        );

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
        attributeTableTab.setStageTitle(
                (StringBinding) Bindings.concat(
                        LC.createStringBinding("logisimFXTitle"),
                        LC.createStringBinding("attrTab"),
                        ": ",
                        attributeTable.getSelectedTool()
                )
        );

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
        waveformTab.setStageTitle(
                (StringBinding) Bindings.concat(
                        LC.createStringBinding("logisimFXTitle"),
                        LC.createStringBinding("waveformTab")
                )
        );

        waveformTab.setOnCloseRequest(event -> openedWaveforms.remove(waveformTab));

        openedWaveforms.put(waveformTab, (WaveformController)c);

        if (stage.isShowing()) {
            Platform.runLater(((WaveformController) c)::findScrollBar);
        }

        return waveformTab;

    }

    public void addTerminalTab(){

        DraggableTab tab = createTerminalTab();
        if (tab != null) systemTabPaneBottom.addRight(tab);

    }

    private DraggableTab createTerminalTab(){

        if (openedSystemTabs.containsKey("TerminalTab")){
            openedSystemTabs.get("TerminalTab").getTabPane().getSelectionModel().select(openedSystemTabs.get("TerminalTab"));
            ((DraggableTabPane) openedSystemTabs.get("TerminalTab").getTabPane()).expand();
            return null;
        }

        DraggableTab tab = new DraggableTab(LC.createStringBinding("terminalTab"), IconsManager.getImage("tty.gif"), terminal);
        tab.setOnClosed(event -> openedSystemTabs.remove("TerminalTab"));
        tab.setType("terminal");
        tab.setStageTitle(
                (StringBinding) Bindings.concat(
                        LC.createStringBinding("logisimFXTitle"),
                        LC.createStringBinding("terminalTab")
                )
        );

        openedSystemTabs.put("TerminalTab", tab);

        return tab;

    }



    //WorkSpace Tabs

    private DraggableTab readEditorDescriptor(String type, String desk){

        if (type.equals("app")){
            return createCircAppearanceEditor(proj.getLogisimFile().getCircuit(desk));
        } else if (type.equals("sch")){
            return createCircLayoutEditor(proj.getLogisimFile().getCircuit(desk));
        } else if (type.equals("code")){

            String[] params = desk.split(" ");

            //component
            if (params.length > 1){
                Circuit circ = proj.getLogisimFile().getCircuit(params[0]);
                Component comp = circ.getExclusive(Location.parse(params[2]));
                return createCodeEditor(circ, comp);
            } else {
                String path = params[0].replace("/", File.separator).replace("\\", File.separator);
                if (path.contains("circuit")){
                    Circuit circ = proj.getLogisimFile().getCircuit(Paths.get(path).getParent().getFileName().toString());
                    return createCodeEditor(circ, Paths.get(proj.getLogisimFile().getProjectDir() + File.separator + path).toFile());
                } else if (path.contains("other")){
                    return createCodeEditor(Paths.get(proj.getLogisimFile().getProjectDir() + File.separator + path).toFile());
                }
            }

        } else {
            return null;
        }

        return null;

    }



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

        StringBinding tabName = (StringBinding) circ.nameProperty().concat(".sch");

        LayoutEditor layoutEditor = new LayoutEditor(proj, circ);
        setEditor(layoutEditor);
        currLayoutCanvas = layoutEditor.getLayoutCanvas();
        layoutEditor.getLayoutCanvas().getSelection().addListener(attributeTable);

        DraggableTab tab = new DraggableTab(tabName, IconsManager.getImage("projlayo.gif"), layoutEditor);
        tab.setStageTitle(
                (StringBinding) Bindings.concat(
                        LC.createStringBinding("logisimFXTitle"),
                        LC.createStringBinding("titleLayoutEditor"),
                        ": ",
                        circ.nameProperty(),
                        " ",
                        LC.createStringBinding("titleOf"),
                        " ",
                        proj.getLogisimFile().nameProperty())
        );
        tab.setType("sch");



        tab.getContent().addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
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

        StringBinding tabName = (StringBinding) circ.nameProperty().concat(".app");

        AppearanceEditor appearanceEditor = new AppearanceEditor(proj, circ);
        setEditor(appearanceEditor);
        currAppearanceCanvas = appearanceEditor.getAppearanceCanvas();
        appearanceEditor.getAppearanceCanvas().getSelection().addSelectionListener(attributeTable);

        DraggableTab tab = new DraggableTab(tabName, IconsManager.getImage("projapp.gif"), appearanceEditor);
        tab.setStageTitle(
                (StringBinding) Bindings.concat(
                        LC.createStringBinding("logisimFXTitle"),
                        LC.createStringBinding("titleAppearanceEditor"),
                        ": ",
                        circ.nameProperty(),
                        " ",
                        LC.createStringBinding("titleOf"),
                        " ",
                        proj.getLogisimFile().nameProperty())
        );
        tab.setType("app");



        tab.getContent().addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
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


    //Generated component model
    public void addCodeEditor(Circuit circ, Component comp){
        DraggableTab tab = createCodeEditor(circ, comp);
        if (tab != null) {
            getNearestWorkPaneOrCreate().addTab(tab);
            selectCodeEditor(comp);
        }
    }

    private DraggableTab createCodeEditor(Circuit circ, Component comp){

        String hdlName = comp.getFactory().getHDLName(comp.getAttributeSet());

        proj.setCurrentCircuit(circ);

        if (openedComponentVerilogModelViewers.containsKey(comp)){
            if (openedComponentVerilogModelViewers.get(comp).getTabPane() == null){
                getNearestWorkPaneOrCreate().addTab(openedComponentVerilogModelViewers.get(comp));
            }
            selectCodeEditor(comp);
            return null;
        }

        CodeEditor codeEditor = new CodeEditor(proj, circ, comp);
        setEditor(codeEditor);

        DraggableTab tab = new DraggableTab(hdlName+comp.getLocation().toString()+".v", IconsManager.getImage("file.gif"), codeEditor);
        tab.setStageTitle((StringBinding) Bindings.concat(LC.createComplexStringBinding("titleVerilogCodeEditor", hdlName), proj.getLogisimFile().nameProperty()));
        tab.setType("code");
        tab.setStageTitle(
                (StringBinding) Bindings.concat(
                        LC.createStringBinding("logisimFXTitle"),
                        LC.createStringBinding("titleCodeEditor"),
                        ": ",
                        hdlName+comp.getLocation().toString()+".v"
                )
        );



        tab.getContent().addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
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

        openedComponentVerilogModelViewers.put(comp, tab);

        return tab;

    }


    //Verilog model/topshell/HLS
    public void addCodeEditor(Circuit circ, File file){
        DraggableTab tab = createCodeEditor(circ, file);
        if (tab != null) {
            getNearestWorkPaneOrCreate().addTab(tab);
            selectCodeEditor(file);
        }
    }

    private DraggableTab createCodeEditor(Circuit circ, File file){

        proj.setCurrentCircuit(circ);

        StringBinding hdlName = (StringBinding) circ.nameProperty().concat("_" + file.getName());

        if (openedCodeEditors.containsKey(file)){
            if (openedCodeEditors.get(file).getTabPane() == null){
                getNearestWorkPaneOrCreate().addTab(openedCodeEditors.get(file));
            }
            selectCodeEditor(file);
            return null;
        }

        CodeEditor codeEditor = new CodeEditor(proj, circ, file);
        setEditor(codeEditor);

        DraggableTab tab = new DraggableTab(hdlName, IconsManager.getImage("file.gif"), codeEditor);
        tab.setStageTitle(
                (StringBinding) Bindings.concat(
                        LC.createStringBinding("logisimFXTitle"),
                        LC.createStringBinding("titleVerilogCodeEditor"),
                        ": ",
                        circ.nameProperty(),
                        " ",
                        LC.createStringBinding("titleOf"),
                        " ",
                        proj.getLogisimFile().nameProperty())
        );
        tab.setType("code");



        tab.getContent().addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
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

        openedCodeEditors.put(file, tab);

        return tab;

    }


    //Other files
    public void addCodeEditor(File file){
        DraggableTab tab = createCodeEditor(file);
        if (tab != null) {
            getNearestWorkPaneOrCreate().addTab(tab);
            selectCodeEditor(file);
        }
    }

    private DraggableTab createCodeEditor(File file){

        String fileName = file.getName();

        if (openedCodeEditors.containsKey(file)){
            if (openedCodeEditors.get(file).getTabPane() == null){
                getNearestWorkPaneOrCreate().addTab(openedCodeEditors.get(file));
            }
            selectCodeEditor(file);
            return null;
        }

        CodeEditor codeEditor = new CodeEditor(proj, file);
        setEditor(codeEditor);

        DraggableTab tab = new DraggableTab(fileName, IconsManager.getImage("file.gif"), codeEditor);
        tab.setStageTitle((StringBinding) Bindings.concat(LC.createComplexStringBinding("titleCodeEditor", fileName), proj.getLogisimFile().nameProperty()));
        tab.setType("code");
        tab.setStageTitle(
                (StringBinding) Bindings.concat(
                        LC.createStringBinding("logisimFXTitle"),
                        LC.createStringBinding("titleCodeEditor"),
                        ": ",
                        fileName
                )
        );



        tab.getContent().addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            setEditor(codeEditor);
            setWorkPane((DraggableTabPane) tab.getTabPane());
        });

        tab.setOnSelectionChanged(event -> {
            if (tab.isSelected()) {
                setEditor(codeEditor);
                if (tab.getTabPane() != null) {
                    setWorkPane((DraggableTabPane) tab.getTabPane());
                } else {
                    Platform.runLater(() -> setWorkPane((DraggableTabPane) tab.getTabPane()));
                }
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

        openedCodeEditors.put(file, tab);

        return tab;

    }



    public void selectCircLayoutEditor(Circuit circ){
        openedLayoutEditors.get(circ).getTabPane().getSelectionModel().select(openedLayoutEditors.get(circ));
        setEditor((EditorBase) openedLayoutEditors.get(circ).getContent());
    }

    public void selectCircAppearanceEditor(Circuit circ){
        openedAppearanceEditors.get(circ).getTabPane().getSelectionModel().select(openedAppearanceEditors.get(circ));
        setEditor((EditorBase) openedAppearanceEditors.get(circ).getContent());
    }

    public void selectCodeEditor(Component comp){
        openedComponentVerilogModelViewers.get(comp).getTabPane().getSelectionModel().select(
                openedComponentVerilogModelViewers.get(comp)
        );
        setEditor((EditorBase) openedComponentVerilogModelViewers.get(comp).getContent());
    }

    public void selectCodeEditor(File file){
        openedCodeEditors.get(file).getTabPane().getSelectionModel().select(
                openedCodeEditors.get(file)
        );
        setEditor((EditorBase) openedCodeEditors.get(file).getContent());
    }


    public void updateFilepath(File oldFile, File newFile){

        if (openedCodeEditors.containsKey(oldFile)) {
            DraggableTab tab = openedCodeEditors.get(oldFile);
            openedCodeEditors.remove(oldFile);
            openedCodeEditors.put(newFile, tab);
        }

    }

    public void reloadFile(File file){
        if (openedCodeEditors.containsKey(file)) {
            DraggableTab tab = openedCodeEditors.get(file);
            ((CodeEditor) tab.getContent()).reloadFile();
        }
    }

    public void doSaveCodeEditors(){
        for (DraggableTab tab: openedCodeEditors.values()){
            ((CodeEditor) tab.getContent()).doSave();
        }
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
    private DraggableTabPane getNearestWorkPaneOrCreate() {

        if (sceneGraphPerformed && getWorkPane().getParent() == null){

            if (lastUsedDockPane != null && lastUsedDockPane.getParent() != null){

                if(lastUsedDockPane.getRoot() == null){
                    createWorkPane(lastUsedDockPane);
                    return getWorkPane();
                } else {
                    DraggableTabPane tabPane = (DraggableTabPane) lastUsedDockPane.getRoot().getItems().get(0);
                    setWorkPane(tabPane);
                    return tabPane;

                }

            } else {

                if(mainWinWorkspace.getRoot() == null){
                    createWorkPane(mainWinWorkspace);
                    return getWorkPane();
                } else {
                    DraggableTabPane tabPane = (DraggableTabPane)(mainWinWorkspace.getRoot()).getItems().get(0);
                    setWorkPane(tabPane);
                    return tabPane;
                }

            }

        } else {
            return getWorkPane();
        }

    }

    private DraggableTabPane getNearestWorkPane() {

        if (sceneGraphPerformed && getWorkPane().getParent() == null){

            if (lastUsedDockPane != null && lastUsedDockPane.getParent() != null){

                if(lastUsedDockPane.getRoot() != null){
                    DraggableTabPane tabPane = (DraggableTabPane) lastUsedDockPane.getRoot().getItems().get(0);
                    setWorkPane(tabPane);
                    return tabPane;
                }

            } else {

                if(mainWinWorkspace.getRoot() != null){
                    DraggableTabPane tabPane = (DraggableTabPane)(mainWinWorkspace.getRoot()).getItems().get(0);
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

        if(getEditor() instanceof LayoutEditor){
            ((LayoutEditor)getEditor()).getLayoutCanvas().updateResume();
        }
        if (getEditor() instanceof AppearanceEditor){
            ((AppearanceEditor)getEditor()).getAppearanceCanvas().updateResume();
        }

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


    public AppearanceEditor getAppearanceEditorFor(Circuit cir){
        if (openedAppearanceEditors.containsKey(cir)) {
            return (AppearanceEditor) openedAppearanceEditors.get(cir).getContent();
        } else {
            return null;
        }
    }

    public LayoutEditor getLayoutEditorFor(Circuit cir){
        if (openedLayoutEditors.containsKey(cir)) {
            return (LayoutEditor) openedLayoutEditors.get(cir).getContent();
        } else {
            return null;
        }
    }

    public CodeEditor getCodeEditorFor(File file){
        if (openedCodeEditors.containsKey(file)) {
            return (CodeEditor) openedCodeEditors.get(file).getContent();
        } else {
            return null;
        }
    }


    @Override
    public void onClose() {

        terminal.terminateListeners();

        for (Tab tab: openedLayoutEditors.values()){
            ((LayoutEditor) tab.getContent()).terminateListeners();
        }

        for (Tab tab: openedAppearanceEditors.values()){
            ((AppearanceEditor) tab.getContent()).terminateListeners();
        }

        for (Tab tab: openedCodeEditors.values()){
            ((CodeEditor) tab.getContent()).terminateListeners();
        }

        for (Tab tab: openedComponentVerilogModelViewers.values()){
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