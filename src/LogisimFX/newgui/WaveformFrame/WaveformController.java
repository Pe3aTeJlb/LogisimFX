/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.WaveformFrame;

import LogisimFX.FileSelector;
import LogisimFX.IconsManager;
import LogisimFX.OldFontmetrics;
import LogisimFX.circuit.*;
import LogisimFX.comp.Component;
import LogisimFX.data.BitWidth;
import LogisimFX.data.Value;
import LogisimFX.file.LibraryEvent;
import LogisimFX.file.LibraryListener;
import LogisimFX.instance.StdAttr;
import LogisimFX.newgui.AbstractController;
import LogisimFX.newgui.ContextMenuManager;
import LogisimFX.newgui.DialogManager;
import LogisimFX.proj.Project;
import LogisimFX.proj.ProjectEvent;
import LogisimFX.proj.ProjectListener;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import java.awt.image.*;

import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;


public class WaveformController extends AbstractController {

    private Stage stage;

    @FXML
    private TabPane TabPane;


    //SelectionTab
    @FXML
    private Tab selectionTab;



    @FXML
    private TreeView<Object> circTrvw;

    @FXML
    private TreeView<SelectionItem> selectionTrVw;

    @FXML
    private Label logItemsCountLbl;




    @FXML
    private Button addBtn;

    @FXML
    private Button changRadixBtn;

    @FXML
    private Button moveUpBtn;

    @FXML
    private Button moveDownBtn;

    @FXML
    private Button removeBtn;



    //WaveformTab

    @FXML
    private Tab waveformTab;

    @FXML
    private Button SimPlayOneStepBtn;

    @FXML
    private Button SimPlayBtn;

    @FXML
    private Button SimStepBtn;

    @FXML
    private Button SimStopBtn;

    @FXML
    private Button ResetSimulationBtn;

    @FXML
    private Button ClearWaveformBtn;

    @FXML
    private SplitPane splitPane;

    @FXML
    private AnchorPane waveformCanvasAnchor;

    @FXML
    private TreeTableView<WaveformTableModel> waveformTblvw;

    @FXML
    private Canvas waveformCnvs;

    @FXML
    private ScrollBar canvasVScrlBr;

    @FXML
    private ScrollBar canvasHScrlBr;

    @FXML
    private Button exportImageBtn;

    @FXML
    private Button exportFileBtn;

    @FXML
    private Button fileOpenBtn;



    @FXML
    private Button startWaveformBtn;


    private Project proj;

    private MultipleSelectionModel<TreeItem<Object>> treeSelectionModel;
    private MultipleSelectionModel<TreeItem<SelectionItem>> selectiontrvwSelectionModel;

    private Simulator curSimulator = null;
    private Model curModel;
    private Map<CircuitState, Model> modelMap = new HashMap<>();
    private final MyListener myListener = new MyListener();
    private LogThread logger;

    private final int LOG_ITEMS_LIMIT = 257;
    private SimpleIntegerProperty currLogItemsCount = new SimpleIntegerProperty(0);

    private class MyListener
            implements ProjectListener, LibraryListener,
            SimulatorListener, ModelListener, CircuitListener {

        public void projectChanged(ProjectEvent event) {
            int action = event.getAction();
            if (action == ProjectEvent.ACTION_SET_STATE) {
                setSimulator(event.getProject().getSimulator());
            } else if (action == ProjectEvent.ACTION_SET_FILE) {
                computeTitle(curModel);
            }
            circTrvw.getRoot().getChildren().clear();
            updateTree((CircuitNode)circTrvw.getRoot(), proj.getCircuitState());
        }

        public void libraryChanged(LibraryEvent event) {
            int action = event.getAction();
            if (action == LibraryEvent.SET_NAME) {
                computeTitle(curModel);
            }
        }

        public void propagationCompleted(SimulatorEvent e) {
            curModel.propagationCompleted();
        }

        public void tickCompleted(SimulatorEvent e) { }

        public void simulatorStateChanged(SimulatorEvent e) { }

        @Override
        public void selectionChanged(ModelEvent event) {

        }

        @Override
        public void entryAdded(ModelEvent event, Value[] values) {

            if(logObjects != null) {

                waveformTblvw.refresh();
                Platform.runLater(() -> updateWaveformData(values));

            }

        }

        @Override
        public void filePropertyChanged(ModelEvent event) {

        }

        @Override
        public void circuitChanged(CircuitEvent event) {

            circTrvw.getRoot().getChildren().clear();
            updateTree((CircuitNode)circTrvw.getRoot(), proj.getCircuitState());

        }

    }



    @FXML
    public void initialize(){
    }

    @Override
    public void postInitialization(Stage s, Project proj) {

        this.proj = proj;

        stage = s;
        stage.setWidth(800);
        stage.setHeight(600);

        proj.addProjectListener(myListener);
        proj.addLibraryListener(myListener);
        proj.getCircuitState().getCircuit().addCircuitListener(myListener);

        initSelectionTab();

        setSimulator(proj.getSimulator());
        curModel.addModelListener(myListener);


        initWaveformTab();
        //initTableTab();

        startWaveformBtn.textProperty().bind(LC.createStringBinding("startLogging"));
        startWaveformBtn.setOnAction(event -> {

            if(!selectionTrVw.getRoot().getChildren().isEmpty()) {

                curModel.bindComponentsList(getItemsToLogPlain(null, selectionTrVw.getRoot()));

                //log tab
                itemsToLog = getItemsToLogPlain(null, selectionTrVw.getRoot());

                //Waveform tab
                updateWaveformTable(selectionTrVw.getRoot(), itemsToLog);

                restartCanvas();

                logger = new LogThread(curModel);
                logger.start();

                TabPane.getSelectionModel().select(1);

            }

        });

    }

    private void computeTitle(Model data) {
        String name = data == null ? "???" : data.getCircuitState().getCircuit().getName();
        stage.titleProperty().bind(LC.createComplexStringBinding("logFrameTitle",
                name, proj.getLogisimFile().getDisplayName().getValue()));
    }





    private ObservableList<SelectionItem> itemsToLog;

    private void initSelectionTab(){

        selectionTab.textProperty().bind(LC.createStringBinding("selectionTab"));

        //TreeView

        treeSelectionModel = circTrvw.getSelectionModel();
        treeSelectionModel.setSelectionMode(SelectionMode.MULTIPLE);

        circTrvw.setCellFactory(tree -> {

            TreeCell<Object> cell = new TreeCell<Object>() {

                @Override
                public void updateItem(Object item, boolean empty) {

                    super.updateItem(item, empty);

                    textProperty().unbind();

                    if (empty || item == null) {

                        setText(null);
                        setGraphic(null);

                    } else {

                        if(item instanceof  Component){

                            if (((Component) item).getFactory() instanceof SubcircuitFactory){

                                String label = ((Component) item).getAttributeSet().getValue(StdAttr.LABEL);
                                if (!label.equals("")){
                                    setText(label);
                                } else {
                                    setText(((Component) item).getFactory().getName() + " " + ((Component) item).getLocation().toString());
                                }

                            } else {
                                setText(((Component) item).getFactory().getDisplayName().get() + " " + ((Component) item).getLocation().toString());
                            }
                            setGraphic(new ImageView(((Component) item).getFactory().getIcon().getImage()));

                        }else {

                            setText(item.toString());
                            setGraphic(IconsManager.getIcon("optState.gif"));

                        }

                    }

                }

            };

            cell.setOnMouseClicked(event -> {

                if (!cell.isEmpty()) {

                    if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2
                            && !event.isConsumed()) {

                        event.consume();

                        if (cell.getTreeItem() != null) {

                            List<TreeItem<SelectionItem>> array;
                            if(event.isControlDown()){
                                treeSelectionModel.clearSelection();
                                treeSelectionModel.select(cell.getTreeItem());
                                array = getSelectedNodes(true);
                            }else{
                                array = getSelectedNodes(false);
                            }

                            if(array != null && !array.isEmpty()){
                                addLogItem(array);
                                // selectionTrVw.getRoot().getChildren().addAll(array);
                            }

                        }

                    }

                }

            });

            return cell;
        });

        CircuitNode treeRoot = new CircuitNode(null, null, null);
        circTrvw.setRoot(treeRoot);
        circTrvw.setShowRoot(false);
        updateTree(treeRoot, proj.getCircuitState());

        logItemsCountLbl.textProperty().bind(Bindings.concat(currLogItemsCount.asString(),"\\"+LOG_ITEMS_LIMIT));



        //selectedLst

        selectionTrVw.setCellFactory(tree -> {

            TreeCell<SelectionItem> cell = new TreeCell<SelectionItem>() {

                @Override
                public void updateItem(SelectionItem item, boolean empty) {

                    super.updateItem(item, empty);

                    textProperty().unbind();

                    if (empty || item == null) {

                        setText(null);
                        setGraphic(null);

                    } else {

                        setText(item.toString()+ " - " + item.getRadix());
                        setGraphic(new ImageView(item.getComponent().getFactory().getIcon().getImage()));

                    }

                }

            };

            return cell;
        });

        selectiontrvwSelectionModel = selectionTrVw.getSelectionModel();
        selectiontrvwSelectionModel.setSelectionMode(SelectionMode.SINGLE);
        selectiontrvwSelectionModel.selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if(selectiontrvwSelectionModel.getSelectedItem() != null) {
                        int localIndex = selectiontrvwSelectionModel.getSelectedItem().getParent().getChildren().indexOf(selectiontrvwSelectionModel.getSelectedItem());
                        moveUpBtn.setDisable(localIndex == 0);
                        moveDownBtn.setDisable(
                                localIndex == selectiontrvwSelectionModel.getSelectedItem().getParent().getChildren().size() - 1);
                        removeBtn.setDisable(currLogItemsCount.intValue() == 0);
                    }
                }
        );

        TreeItem<SelectionItem> selectedRoot = new TreeItem<>(null);
        selectionTrVw.setRoot(selectedRoot);
        selectionTrVw.setShowRoot(false);


        addBtn.textProperty().bind(LC.createStringBinding("selectionAdd"));
        addBtn.setOnAction(event -> {
            List<TreeItem<SelectionItem>> array = getSelectedNodes(false);
            if(array != null && !array.isEmpty()){
                addLogItem(array);
                //selectionTrVw.getRoot().getChildren().addAll(array);
                //selectedLst.getItems().addAll(array);
            }
        });

        changRadixBtn.textProperty().bind(LC.createStringBinding("selectionChangeBase"));
        changRadixBtn.setOnAction(event -> {
            if(selectiontrvwSelectionModel.getSelectedItem() != null) {
                int radix = selectiontrvwSelectionModel.getSelectedItem().getValue().getRadix();
                switch (radix) {
                    case 2:
                        selectiontrvwSelectionModel.getSelectedItem().getValue().setRadix(10);
                        break;
                    case 10:
                        selectiontrvwSelectionModel.getSelectedItem().getValue().setRadix(16);
                        break;
                    default:
                        selectiontrvwSelectionModel.getSelectedItem().getValue().setRadix(2);
                }
                selectionTrVw.refresh();
            }
        });

        moveUpBtn.textProperty().bind(LC.createStringBinding("selectionMoveUp"));
        moveUpBtn.setOnAction(event -> doMove(-1));

        moveDownBtn.textProperty().bind(LC.createStringBinding("selectionMoveDown"));
        moveDownBtn.setOnAction(event -> doMove(1));

        removeBtn.textProperty().bind(LC.createStringBinding("selectionRemove"));
        removeBtn.setOnAction(event -> {

            if(!selectiontrvwSelectionModel.getSelectedItems().isEmpty()) {

                selectiontrvwSelectionModel.getSelectedItem().getParent().getChildren().remove(selectiontrvwSelectionModel.getSelectedItem());

                int c = 0;
                for (TreeItem<SelectionItem> item : selectionTrVw.getRoot().getChildren()) {
                    c++;
                    if (!item.getChildren().isEmpty()) c += item.getChildren().size();
                }

                currLogItemsCount.set(c);
                removeBtn.setDisable(currLogItemsCount.intValue() == 0);

            }

        });

    }

    private static class CircuitNode extends TreeItem<Object>{

        private CircuitNode parent;
        private CircuitState circuitState;
        private Component subcircComp;

        public CircuitNode(CircuitNode parent, CircuitState circuitState, Component subcircComp) {

            super(subcircComp);

            this.parent = parent;
            this.circuitState = circuitState;
            this.subcircComp = subcircComp;

        }

        @Override
        public String toString() {
            if (subcircComp != null) {
                String label = subcircComp.getAttributeSet().getValue(StdAttr.LABEL);
                if (label != null && !label.equals("")) {
                    return label;
                }
            }
            String ret = circuitState.getCircuit().getName();
            if (subcircComp != null) {
                ret += subcircComp.getLocation();
            }
            return ret;
        }

    }

    private static class ComponentNode extends TreeItem<Object>{

        private CircuitNode parent;
        private Component comp;
        private OptionNode[] opts;

        public ComponentNode(CircuitNode parent, Component comp) {

            super(comp);

            this.parent = parent;
            this.comp = comp;
            this.opts = null;

            Loggable log = (Loggable) comp.getFeature(Loggable.class);
            if (log != null) {
                Object[] opts = log.getLogOptions(parent.circuitState);
                if (opts != null && opts.length > 0) {
                    this.opts = new OptionNode[opts.length];
                    for (int i = 0; i < opts.length; i++) {
                        this.opts[i] = new OptionNode(this, opts[i]);
                        this.getChildren().add(this.opts[i]);
                    }
                }
            }

        }

        @Override
        public String toString() {
            Loggable log = (Loggable) comp.getFeature(Loggable.class);
            if (log != null) {
                String ret = log.getLogName(null);
                if (ret != null && !ret.equals("")) return ret;
            }
            return comp.getFactory().getDisplayName().getValue() + " " + comp.getLocation();
        }

    }

    private static class OptionNode extends TreeItem<Object>{

        private ComponentNode parent;
        private Object option;

        public OptionNode(ComponentNode parent, Object option) {

            super(option);

            this.parent = parent;
            this.option = option;

        }

        public String toString() {
            return option.toString();
        }

    }

    private void updateTree(CircuitNode root, CircuitState state){

        ArrayList<Component> subcircs = new ArrayList<>();

        for (Component comp : state.getCircuit().getNonWires()) {

            if (comp.getFactory() instanceof SubcircuitFactory) {
                subcircs.add(comp);
            } else {
                Object o = comp.getFeature(Loggable.class);
                if (o != null) {
                    ComponentNode compNode = new ComponentNode(root, comp);
                    root.getChildren().add(compNode);
                }

            }

        }

        for (Component comp: subcircs) {

            SubcircuitFactory factory = (SubcircuitFactory) comp.getFactory();
            CircuitState s = factory.getSubstate(state, comp);

            CircuitNode circNode = new CircuitNode(root, s, comp);

            root.getChildren().add(circNode);
            updateTree(circNode, s);

        }

    }

    private void doMove(int delta) {

        if(!selectiontrvwSelectionModel.getSelectedItems().isEmpty()) {
            TreeItem<SelectionItem> cur = selectiontrvwSelectionModel.getSelectedItem();
            TreeItem<SelectionItem> subRoot = selectiontrvwSelectionModel.getSelectedItem().getParent();
            int oldIndex = subRoot.getChildren().indexOf(cur);
            int newIndex = oldIndex + delta;

            if (oldIndex >= 0 && newIndex >= 0 &&
                    newIndex < subRoot.getChildren().size()) {
                TreeItem<SelectionItem> buff = subRoot.getChildren().get(newIndex);
                subRoot.getChildren().set(newIndex, cur);
                subRoot.getChildren().set(oldIndex, buff);
            }

            selectiontrvwSelectionModel.select(cur);
            //selectiontrvwSelectionModel.select(selectiontrvwSelectionModel.getSelectedIndex()+delta);
            // selectionTrVw.refresh();
        }

    }

    private void addLogItem(List<TreeItem<SelectionItem>> array){

        if(currLogItemsCount.intValue() >= LOG_ITEMS_LIMIT) {
            return;
        }

        int c = 0;
        for (TreeItem<SelectionItem> item : array) {

            c++;
            if (!item.getChildren().isEmpty()) c += item.getChildren().size();

            if (currLogItemsCount.intValue() + c < LOG_ITEMS_LIMIT) {

                selectionTrVw.getRoot().getChildren().add(item);
                currLogItemsCount.set(currLogItemsCount.intValue()+c);

            } else {

                int cutoff = LOG_ITEMS_LIMIT - currLogItemsCount.intValue();
                if(cutoff > item.getChildren().size()) cutoff = item.getChildren().size();

                TreeItem<SelectionItem> itm = new TreeItem<>(item.getValue());
                List<TreeItem<SelectionItem>> buff = item.getChildren().subList(0, cutoff);
                itm.getChildren().setAll(buff);

                selectionTrVw.getRoot().getChildren().add(itm);
                currLogItemsCount.set(257);
                break;

            }

            c = 0;

        }

    }

    private ArrayList<TreeItem<SelectionItem>> getSelectedNodes(boolean ctrlDown){

        ArrayList<TreeItem<SelectionItem>> ret = new ArrayList<>();

        if(treeSelectionModel.getSelectedItems().isEmpty()) return new ArrayList<>();

        for (TreeItem<Object> node: treeSelectionModel.getSelectedItems()) {

            boolean fromOptionNode = false;

            ComponentNode n = null;
            Object opt = null;
            if (node instanceof OptionNode) {
                fromOptionNode = true;
                OptionNode o = (OptionNode) node;
                n = o.parent;
                opt = o.option;
                if(treeSelectionModel.getSelectedItems().contains(node.getParent())) n = null;
            } else if (node instanceof ComponentNode) {
                n = (ComponentNode) node;
                if(n.opts != null && n.opts.length>0) opt = n;
                //if (n.opts != null) n = null;
            }

            if (n != null) {

                int count = 0;
                for (CircuitNode cur = n.parent; cur != null; cur = cur.parent) {
                    count++;
                }
                Component[] nPath = new Component[count - 1];
                CircuitNode cur = n.parent;
                for (int j = nPath.length - 1; j >= 0; j--) {
                    nPath[j] = cur.subcircComp;
                    cur = cur.parent;
                }

                TreeItem<SelectionItem> newItem = new TreeItem<>(new SelectionItem(curModel, nPath, n.comp, opt));
                ret.add(newItem);

                if(n.opts != null && n.opts.length > 0 && !fromOptionNode && !ctrlDown){

                    int index = circTrvw.getRoot().getChildren().indexOf(node);
                    for (TreeItem<Object> subnode: circTrvw.getRoot().getChildren().get(index).getChildren()) {

                        if(subnode instanceof OptionNode && treeSelectionModel.getSelectedItems().contains(subnode) ||
                                !treeSelectionModel.getSelectedItems().containsAll(node.getChildren())) {

                            OptionNode o = (OptionNode) subnode;
                            n = o.parent;
                            opt = o.option;

                            count = 0;
                            for (cur = n.parent; cur != null; cur = cur.parent) {
                                count++;
                            }
                            nPath = new Component[count - 1];
                            cur = n.parent;
                            for (int j = nPath.length - 1; j >= 0; j--) {
                                nPath[j] = cur.subcircComp;
                                cur = cur.parent;
                            }


                            newItem.getChildren().add(new TreeItem<>(new SelectionItem(curModel, nPath, n.comp, opt)));

                        }

                    }

                }

            }

        }

        return ret.size() == 0 ? null : ret;

    }

    private ObservableList<SelectionItem> getItemsToLogPlain(ObservableList<SelectionItem> ret, TreeItem<SelectionItem> root){

        if(ret == null) ret = FXCollections.observableArrayList();

        for (TreeItem<SelectionItem> node: root.getChildren()) {
            ret.add(node.getValue());
            if(!node.getChildren().isEmpty()) getItemsToLogPlain(ret, node);
        }

        return ret;

    }





    private double currCursorPos = 0;

    private GraphicsContext gc;
    private double width, height;
    private double[] transform;
    private double dragScreenX, dragScreenY;

    private ScrollBar waveformTableViewScrollbar;

    private static final double MIN_ZOOM = 12.5;
    private static final double MAX_ZOOM = 250;

    NumberFormat formatter = new DecimalFormat("#.####");
    private static final Font TIMESTEPFONT = Font.font("serif", FontWeight.THIN, FontPosture.REGULAR, 10);
    private final double charlen = 5;
    private static final FontMetrics fm = Toolkit.getToolkit().getFontLoader().getFontMetrics(TIMESTEPFONT);

    private final Color BACKGROUND = Color.WHITE;
    private final Color LINE = Color.BLACK;
    private final Color LINEFILL = Color.LIGHTGRAY;
    private final Color CURRPOS = Color.RED;
    private final Color LONGVALUE = Color.PURPLE;

    private int currSelectedRow = 0;

    double yAdjust = 40;
    double spaceY = 50;
    double spaceX = 25;
    double REFERENCE_SPACE_X = 25;
    double w;

    double currTickFreq;

    /*
    Поскольку, заснапшотить сплитпейн нормально не представляется возможным, делаем профанацию с canvas
    Будем отрисовывать таблицу с названием элементов в невидимой для юзера зоне.
    Для этого определим ширину столбца и сдвинем canvas в отрицательную зону.
    После взятия снапшота от всего canvas всё будет тип топ.
    отрисовка мини-таблицы будет в основном цикле отрисовке updateWaveform
    На своё время, это лучшее решение
    */
    TreeTableColumn<WaveformTableModel, String> nameColumn;
    double hiddenColumnWidth;
    double hiddenColumnHeight = 22;

    TreeTableColumn<WaveformTableModel, String> valueColumn;

    private ObservableList<TreeItem<WaveformTableModel>> logObjects;


    private void initWaveformTab(){

        waveformTab.textProperty().bind(LC.createStringBinding("waveformTab"));

        SimStopBtn.setGraphic(IconsManager.getIcon("simstop.png"));
        //SimStopBtn.setAccelerator(KeyCombination.keyCombination("Ctrl+E"));
        SimStopBtn.setTooltip(new ToolTip("simulateEnableStepsTip"));
        SimStopBtn.setOnAction(event -> {
            if (curSimulator != null) {
                curSimulator.setIsRunning(!curSimulator.isRunning().getValue());
            }
        });

        SimPlayOneStepBtn.setGraphic(IconsManager.getIcon("simtplay.png"));
        //SimPlayOneStepBtn.setAccelerator(KeyCombination.keyCombination("Ctrl+I"));
        SimPlayOneStepBtn.setTooltip(new ToolTip("simulateStepTip"));
        SimPlayOneStepBtn.disableProperty().bind(curSimulator.isRunning());
        SimPlayOneStepBtn.setOnAction(event -> {
            if (curSimulator != null) curSimulator.step();
        });

        SimPlayBtn.setGraphic(IconsManager.getIcon("simplay.png"));
        //SimPlayBtn.setAccelerator(KeyCombination.keyCombination("Ctrl+K"));
        SimPlayBtn.setTooltip(new ToolTip("simulateEnableTicksTip"));
        SimPlayBtn.setOnAction(event -> {
            if (curSimulator != null) curSimulator.setIsTicking(!curSimulator.isTicking());
        });

        SimStepBtn.setGraphic(IconsManager.getIcon("simstep.png"));
        //SimStepBtn.setAccelerator(KeyCombination.keyCombination("Ctrl+T"));
        SimStepBtn.setTooltip(new ToolTip("simulateTickTip"));
        SimStepBtn.setOnAction(event -> {
            if (curSimulator != null) curSimulator.tick();
        });

        ResetSimulationBtn.setGraphic(IconsManager.getIcon("simreset.png"));
        ResetSimulationBtn.setTooltip(new ToolTip("simulateResetItem"));
        ResetSimulationBtn.setOnAction(event -> {
            if (curSimulator != null) curSimulator.requestReset();
        });

        ClearWaveformBtn.setGraphic(IconsManager.getIcon("clear.png"));
        ClearWaveformBtn.setTooltip(new ToolTip("waveformClear"));
        ClearWaveformBtn.setOnAction(event -> {

            if(logObjects != null){
                for (TreeItem<WaveformTableModel> item : logObjects) {
                    item.getValue().clear();
                }
            }

            restartCanvas();

            updateWaveform();

        });


        waveformTblvw.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        waveformTblvw.setRoot(new TreeItem<>(null));
        waveformTblvw.setShowRoot(false);
        waveformTblvw.refresh();


        nameColumn = new TreeTableColumn<>("");
        nameColumn.textProperty().bind(LC.createStringBinding("componentTitle"));
        nameColumn.setSortable(false);
        //nameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("Title"));
        nameColumn.setCellValueFactory(param -> param.getValue().getValue().getTitle());
        nameColumn.setCellFactory(param -> {
            TreeTableCell<WaveformTableModel, String> cell = new TreeTableCell<WaveformTableModel, String>() {

                @Override
                protected void updateItem(String item, boolean empty) {

                    super.updateItem(item, empty);

                    if (item == null) {
                        super.setText(null);
                        super.setGraphic(null);
                    } else {
                        super.setText(item);
                        super.setGraphic(null);
                    }

                }

            };

            cell.setPrefHeight(spaceY);
            cell.setAlignment(Pos.CENTER_LEFT);
            cell.setOnMouseEntered(event -> {
                if(cell.isVisible()) {
                    currSelectedRow = cell.getTreeTableRow().getIndex();
                }else{currSelectedRow = -1;}
                updateWaveform();
            });
            cell.setOnMouseExited(event -> {
                currSelectedRow = -1;
                updateWaveform();
            });
            cell.setOnMousePressed(event -> {
                if(event.getButton() == MouseButton.SECONDARY){
                    cell.setContextMenu(ContextMenuManager.RadixOptionsContextMenu(cell.getTreeTableRow().getItem(), waveformTblvw));
                }
            });

            return cell;

        });

        valueColumn = new TreeTableColumn<>("");
        valueColumn.textProperty().bind(LC.createStringBinding("valueTitle"));
        valueColumn.setSortable(false);
        valueColumn.setCellValueFactory(param -> param.getValue().getValue().getValueAt());
        valueColumn.setCellFactory(param -> {
            TreeTableCell<WaveformTableModel, String> cell = new TreeTableCell<WaveformTableModel, String>() {

                @Override
                protected void updateItem(String item, boolean empty) {

                    super.updateItem(item, empty);

                    if (item == null) {
                        super.setText(null);
                        super.setGraphic(null);
                    } else {
                        super.setText(item);
                        super.setGraphic(null);
                    }

                }

            };

            cell.setPrefHeight(spaceY);
            cell.setAlignment(Pos.CENTER);
            cell.setOnMouseEntered(event -> {
                if(cell.isVisible()) {
                    currSelectedRow = cell.getTreeTableRow().getIndex();
                }else{currSelectedRow = -1;}
                updateWaveform();
            });
            cell.setOnMouseExited(event -> {
                currSelectedRow = -1;
                updateWaveform();
            });
            cell.setOnMousePressed(event -> {
                if(event.getButton() == MouseButton.SECONDARY){
                    cell.setContextMenu(ContextMenuManager.RadixOptionsContextMenu(cell.getTreeTableRow().getItem(), waveformTblvw));
                }
            });

            return cell;
        });

        waveformTblvw.getColumns().add(nameColumn);
        waveformTblvw.getColumns().add(valueColumn);



        splitPane.setDividerPositions(0.05);


        waveformCnvs.setCache(true);
        waveformCnvs.setCacheHint(CacheHint.SPEED);

        gc = waveformCnvs.getGraphicsContext2D();
        gc.setFont(TIMESTEPFONT);
        w = gc.getLineWidth()/2;
        transform = new double[6];
        transform[0] = transform[3] = 1;
        transform[1] = transform[2] = transform[4] = transform[5] = 0;
        gc.setTransform(transform[0], transform[1], transform[2],
                transform[3], transform[4], transform[5]
        );

        restartCanvas();

        canvasHScrlBr.setVisible(false);
        canvasVScrlBr.setVisible(false);

        //Events

        waveformCanvasAnchor.widthProperty().addListener((observable, oldValue, newValue) -> updateWaveform());

        waveformCanvasAnchor.heightProperty().addListener((observable, oldValue, newValue) -> updateWaveform());

        waveformCnvs.setOnMousePressed(event -> {

            dragScreenX = event.getX();
            dragScreenY = event.getY();

            if(event.getButton() == MouseButton.PRIMARY) {
                currCursorPos = (Math.max(0, Math.min(width - 1, inverseTransformX(event.getX()))));

                waveformTblvw.refresh();
                updateWaveform();
            }

        });

        waveformCnvs.setOnMouseDragged(event -> {

            if(event.getButton() == MouseButton.PRIMARY) {
                currCursorPos = (Math.max(0, Math.min(width - 1, inverseTransformX(event.getX()))));

                waveformTblvw.refresh();
                updateWaveform();
            }

            if(event.getButton() == MouseButton.SECONDARY || event.getButton() == MouseButton.MIDDLE){

                //if scrollbars are visible we must adjust transform[] limits to theirs width/height
                double horizScrollbarHeight = 0;
                double vertScrollbarWidth = 0;

                if(canvasHScrlBr.isVisible())horizScrollbarHeight = canvasHScrlBr.getHeight();
                if(canvasVScrlBr.isVisible())vertScrollbarWidth = canvasVScrlBr.getWidth();

                double dx = event.getX() - dragScreenX;
                double dy = event.getY() - dragScreenY;
                if (dx == 0 && dy == 0) {
                    return;
                }

                if(transform[4] + dx > 0 || transform[4] + dx < -(width- waveformCanvasAnchor.getWidth()-hiddenColumnWidth+vertScrollbarWidth)){
                    dx = 0;
                }

                if(transform[5] + dy > 0 || transform[5] + dy < -(height- waveformCanvasAnchor.getHeight()+horizScrollbarHeight)){
                    dy = 0;
                }

                clearRect40K(transform[4], transform[5]);

                transform[4] += dx;
                transform[5] += dy;

                canvasHScrlBr.setValue(-transform[4]);
                //canvasVScrlBr.setValue(-transform[5]);
                waveformTableViewScrollbar.setValue(-transform[5]/canvasVScrlBr.getMax());

                dragScreenX = event.getX();
                dragScreenY = event.getY();

                updateWaveform();

            }

        });

        waveformCnvs.setOnScroll(event -> {

            clearRect40K(transform[4], transform[5]);

            double oldSpaceX = spaceX;
            double cursorPos = currCursorPos-hiddenColumnWidth;
            double oldWidth = width;

            spaceX += event.getDeltaY()*.05;
            spaceX = Math.max(spaceX, MIN_ZOOM);
            spaceX = Math.min(spaceX, MAX_ZOOM);

            width = ((width-hiddenColumnWidth)/oldSpaceX)*spaceX+hiddenColumnWidth;
            if(width < oldWidth){
                if(transform[4] + (oldWidth - width) < 0){
                    transform[4] += oldWidth - width;
                } else {
                    transform[4] = 0;
                }
            }

            currCursorPos = ((spaceX/oldSpaceX)*cursorPos+hiddenColumnWidth);

            updateWaveform();

        });

        canvasHScrlBr.valueProperty().addListener(event -> {

            clearRect40K(transform[4], transform[5]);

            transform[4] = -canvasHScrlBr.getValue();

            updateWaveform();

        });

        canvasVScrlBr.valueProperty().addListener(event -> {

            clearRect40K(transform[4], transform[5]);

            transform[5] = -canvasVScrlBr.getValue();

            if(waveformTableViewScrollbar.getValue() != canvasVScrlBr.getValue()) {
                waveformTableViewScrollbar.setValue(canvasVScrlBr.getValue() / canvasVScrlBr.getMax());
            }

            updateWaveform();

        });

        //Lookup func must be executed after tableview is shown
        stage.setOnShown(event -> {

            //Find tableview scrollbar and add listener for rows scroll
            waveformTableViewScrollbar = (ScrollBar) waveformTblvw.lookup(".scroll-bar");

            waveformTableViewScrollbar.visibleProperty().addListener(observable -> recalculateScrollBars());

            waveformTableViewScrollbar.valueProperty().addListener(observable -> {

                clearRect40K(transform[4], transform[5]);

                transform[5] = -waveformTableViewScrollbar.getValue()*(canvasVScrlBr.getMax());

                if(waveformTableViewScrollbar.getValue() != canvasVScrlBr.getValue()) {
                    canvasVScrlBr.setValue(waveformTableViewScrollbar.getValue() * canvasVScrlBr.getMax());
                }

                updateWaveform();

            });

        });



        //Bottom buttons

        exportFileBtn.textProperty().bind(LC.createStringBinding("saveButton"));
        exportFileBtn.setOnAction(event -> exportLog());

        exportImageBtn.textProperty().bind(LC.createStringBinding("exportImage"));
        exportImageBtn.setOnAction(event -> exportImage());

        fileOpenBtn.textProperty().bind(LC.createStringBinding("openButton"));
        fileOpenBtn.setOnAction(event -> importLog());

    }

    private static class ToolTip extends Tooltip{

        public ToolTip(String text){
            super();
            textProperty().bind(LC.createStringBinding(text));
        }

    }

    public class WaveformTableModel {

        private SelectionItem comp;
        private String title;
        private ArrayList<Value> values = new ArrayList<>();
        private int radix = 2;

        public WaveformTableModel(SelectionItem cmp){
            comp = cmp;
            radix = cmp.getRadix();
        }

        public WaveformTableModel(String title, ArrayList<Value> values){
            this.title = title;
            this.values = values;
        }

        public void addValue(Value val){
            values.add(val);
        }

        public void setRadix(int rdx){
            radix = rdx;
        }

        public SimpleStringProperty getTitle(){
            return comp == null ? new SimpleStringProperty(title) : new SimpleStringProperty(comp.toShortString());
        }

        public SimpleObjectProperty<String> getValueAt(){

            int index = (int) Math.floor((currCursorPos-hiddenColumnWidth) / spaceX);
            if(index < 0) index = 0;
            return values.isEmpty() || index >= values.size() ?  new SimpleObjectProperty<>(Value.NIL.toDisplayString(radix)) :
                    new SimpleObjectProperty<>(values.get(index).toDisplayString(radix));

        }

        public ArrayList<Value> getValues(){
            return values;
        }

        public SelectionItem getSelectionItem(){
            return comp;
        }

        public void clear(){
            values.clear();
        }

        public int getRadix(){
            return radix;
        }

    }

    private void updateWaveformTable(TreeItem<SelectionItem> selectedItemsRoot, ObservableList<SelectionItem> logItemsPlain){

        waveformTblvw.getRoot().getChildren().clear();
        double bufflen = 0;

        logObjects = convertToWaveformTableModel(null, selectedItemsRoot, waveformTblvw.getRoot());

        for (SelectionItem item: logItemsPlain) {
            if(OldFontmetrics.computeStringWidth(fm,item.toShortString())>bufflen) bufflen = OldFontmetrics.computeStringWidth(fm,item.toShortString());
        }

        hiddenColumnWidth = bufflen+5;

        currTickFreq = curSimulator.getTickFrequency();

        updateWaveform();

    }

    private void updateWaveformTable(String[] titles, ArrayList<ArrayList<Value>> values){

        logObjects = FXCollections.observableArrayList();

        double bufflen = 0;

        int i =0;
        for (String title: titles) {
            title = title.trim();
            TreeItem<WaveformTableModel> model = new TreeItem<>(new WaveformTableModel(title,values.get(i)));
            waveformTblvw.getRoot().getChildren().add(model);
            if(OldFontmetrics.computeStringWidth(fm,title)>bufflen)bufflen = OldFontmetrics.computeStringWidth(fm,title);
            logObjects.add(model);
            i++;
        }

        hiddenColumnWidth = bufflen+5;
        width = hiddenColumnWidth+values.get(0).size()*spaceX;
        currCursorPos = hiddenColumnWidth;

        updateWaveform();

    }

    private void updateWaveformData(Value[] values){

        //because of platform runlater, if u enable ticks on high KHz and spam tick btn and then close
        //window, waveform will go mad with log and fx threads
        if(logObjects == null)return;

        currTickFreq = curSimulator.getTickFrequency();

        int i = 0;
        int size = 0;
        for (TreeItem<WaveformTableModel> model : logObjects){
            size = model.getValue().getValues().size();
            if(size < 1000) {
                model.getValue().addValue(values[i]);
                size = model.getValue().getValues().size();
                i++;
            }
        }

        // System.out.println(size);
        if(size < 1000) {

            //ze_ reference :extend extend extend(canvas)
            width = hiddenColumnWidth + size * spaceX;

            currCursorPos = (hiddenColumnWidth + size * spaceX - 1);

            waveformTblvw.refresh();
            updateWaveform();

        }

    }

    private ObservableList<TreeItem<WaveformTableModel>> convertToWaveformTableModel(
            ObservableList<TreeItem<WaveformTableModel>> ret, TreeItem<SelectionItem> root, TreeItem<WaveformTableModel> copyDest){

        if(ret == null) ret = FXCollections.observableArrayList();

        for (TreeItem<SelectionItem> node: root.getChildren()) {
            TreeItem<WaveformTableModel> item = new TreeItem<>(new WaveformTableModel(node.getValue()));
            copyDest.getChildren().add(item);
            ret.add(item);
            if(!node.getChildren().isEmpty()) convertToWaveformTableModel(ret, node, item);
        }

        return ret;

    }

    private void updateWaveform(){

        clearRect40K(transform[4], transform[5]);

        gc.setTransform(transform[0], transform[1], transform[2],
                transform[3], transform[4], transform[5]
        );

        waveformCnvs.setWidth(hiddenColumnWidth+ waveformCanvasAnchor.getWidth());
        waveformCnvs.setHeight(waveformCanvasAnchor.getHeight()+hiddenColumnHeight);

        waveformCnvs.setLayoutX(-hiddenColumnWidth);

        if(logObjects != null) {

            //compute canvas height
            int visibleRows = 0;
            for (TreeItem<WaveformTableModel> item : logObjects) {
                if (waveformTblvw.getTreeItemLevel(item) == 1 || item.getParent().isExpanded()) {
                    visibleRows++;
                }
            }
            height = visibleRows * spaceY + hiddenColumnHeight;

            recalculateScrollBars();

            gc.setStroke(LINE);
            gc.setFill(LINE);

            //hidden table
            gc.strokeLine(hiddenColumnWidth, 0, hiddenColumnWidth, height);
            gc.strokeLine(0, 20, hiddenColumnWidth, 20);
            gc.fillText(LC.get("componentTitle"), (hiddenColumnWidth - OldFontmetrics.computeStringWidth(fm,LC.get("componentTitle"))) / 2, 15);


            //time steps
            gc.strokeLine(0, 5, width, 5);
            int counter = 0;
            int skip = 1;
            String prefix = "";
            if (currTickFreq > 1000) prefix = "m";

            for (double i = hiddenColumnWidth + 2 * spaceX; i < width; i += 2 * spaceX) {

                String text = formatter.format(1 / curSimulator.getTickFrequency() * counter) + " " + prefix + "s";

                if (OldFontmetrics.computeStringWidth(fm,text) + charlen < 2 * spaceX * skip && computeRender(i,15)) {

                    gc.strokeLine(i, 5, i, 15);
                    gc.fillText(text, i - OldFontmetrics.computeStringWidth(fm,text) - 2, 15);
                    skip = 1;

                } else {
                    skip++;
                }

                counter++;

            }

            //lower line bound of first row
            double currRowY = 70;

            //comp data
            int minVisibleValue = (int)Math.floor(inverseTransformX(0)/spaceX)-5;
            if(minVisibleValue < 0) minVisibleValue = 0;

            int maxVisibleValue = (int)Math.ceil(inverseTransformX(waveformCnvs.getWidth())/spaceX)+5;
            if(maxVisibleValue > logObjects.get(0).getValue().getValues().size()) maxVisibleValue = logObjects.get(0).getValue().getValues().size();

            for (TreeItem<WaveformTableModel> item : logObjects) {

                if ((waveformTblvw.getTreeItemLevel(item) == 1 || item.getParent().isExpanded())) {

                    WaveformTableModel model = item.getValue();
                    ArrayList<Value> vals = model.getValues();
                    int curValueLength = 0;

                    if(minVisibleValue - 1 > 0 && vals.get(minVisibleValue).equals(vals.get(minVisibleValue-1)))
                        curValueLength = 1;

                    double currX = hiddenColumnWidth + minVisibleValue * spaceX;
                    double currY = currRowY;


                    if (logObjects.indexOf(item) == currSelectedRow) {
                        gc.setLineWidth(2);
                    } else {
                        gc.setLineWidth(1);
                    }

                    if (vals != null && !vals.isEmpty()) {

                        //for (int i = 0; i < vals.size(); i++) {
                        for (int i = minVisibleValue; i < maxVisibleValue; i++) {

                            gc.setStroke(LINE);
                            gc.setFill(LINE);

                            //if start value is true
                            if (i == minVisibleValue && vals.get(minVisibleValue).equals(Value.TRUE)) currY -= yAdjust;

                            if (i > minVisibleValue) {

                                //line up
                                if (vals.get(i - 1).equals(Value.FALSE) && vals.get(i).equals(Value.TRUE)) {

                                    gc.strokeLine(currX + w, currY, currX + w, currY - yAdjust);
                                    currY -= yAdjust;

                                }

                                //line down
                                if (vals.get(i - 1).equals(Value.TRUE) && vals.get(i).equals(Value.FALSE)) {

                                    gc.strokeLine(currX + w, currY, currX + w, currY + yAdjust);
                                    currY += yAdjust;

                                }

                            }

                            if (vals.get(i).equals(Value.TRUE)) {

                                //true fill
                                gc.setStroke(LINEFILL);
                                gc.setFill(LINEFILL);

                                gc.fillRect(currX + 2 * w, currY + w, spaceX, yAdjust);

                                gc.setStroke(LINE);
                                gc.setFill(LINE);

                            } else if (vals.get(i).equals(Value.UNKNOWN)) {

                                //Blue grid of unknown value
                                gc.setStroke(Value.UNKNOWN_COLOR);
                                gc.setFill(Value.UNKNOWN_COLOR);

                                gc.strokeLine(currX, currRowY - yAdjust, currX + spaceX, currRowY - (yAdjust / 2));
                                gc.strokeLine(currX, currRowY - (yAdjust / 2), currX + spaceX, currRowY - yAdjust);
                                gc.strokeLine(currX, currRowY - (yAdjust / 2), currX + spaceX, currRowY);
                                gc.strokeLine(currX, currRowY, currX + spaceX, currRowY - (yAdjust / 2));

                                gc.setStroke(LINE);
                                gc.setFill(LINE);

                            } else if (!vals.get(i).equals(Value.FALSE)) {

                                //hexagon
                                gc.setStroke(LONGVALUE);
                                gc.setFill(LONGVALUE);

                                if (curValueLength == 0) {
                                    //draw first 3 dots of hexagon
                                    gc.strokeLine(currX + w, currRowY - yAdjust / 2, currX + spaceX, currRowY - 0.75 * yAdjust);
                                    gc.strokeLine(currX + w, currRowY - yAdjust / 2, currX + spaceX, currRowY - 0.25 * yAdjust);

                                    curValueLength++;

                                } else {

                                    if (i != vals.size() - 1 && vals.get(i + 1).equals(vals.get(i))) {

                                        //finish hexagon
                                        gc.strokeLine(currX + w, currRowY - 0.75 * yAdjust, currX + spaceX, currRowY - 0.75 * yAdjust);
                                        gc.strokeLine(currX + w, currRowY - 0.25 * yAdjust, currX + spaceX, currRowY - 0.25 * yAdjust);

                                        curValueLength++;

                                    } else {

                                        //hexagon body

                                        gc.strokeLine(currX + w, currRowY - 0.75 * yAdjust, currX + spaceX, currRowY - yAdjust / 2);
                                        gc.strokeLine(currX + w, currRowY - 0.25 * yAdjust, currX + spaceX, currRowY - yAdjust / 2);

                                        gc.setStroke(LINE);
                                        gc.setFill(LINE);

                                        //Format value
                                        int radix = item.getValue().getRadix();
                                        String text = vals.get(i).toDisplayString(radix);
                                        double stringlen = OldFontmetrics.computeStringWidth(fm,text);

                                        //(curValueLength - 0.5) because it takes around half of spaceX at start and end
                                        // of hexagon, that are bad to display text
                                        if (stringlen > (curValueLength - 0.5) * spaceX) {

                                            int len = (int) (((curValueLength - 0.5) * spaceX) / charlen);
                                            text = text.substring(0, len).trim();

                                            if (text.length() <= 1) {
                                                text = "...";
                                            } else {
                                                text += "...";
                                            }

                                        }
                                        if (radix == 2) {
                                            text += "b";
                                        } else if (radix == 10) {
                                            text += "d";
                                        } else if (radix == 16) {
                                            text += "h";
                                        }

                                        gc.fillText(text, currX - ((curValueLength - 1) * spaceX + OldFontmetrics.computeStringWidth(fm,text)) / 2, currY - ((yAdjust - fm.getAscent()) / 2));


                                        curValueLength = 0;

                                    }

                                }

                                gc.setStroke(LINE);
                                gc.setFill(LINE);

                            }

                            //lower row bound
                            gc.strokeLine(currX + gc.getLineWidth(), currY, currX + spaceX, currY);
                            currX += spaceX;

                        }

                    }

                    //fill hidden table data
                    if(computeRender(0, currRowY - spaceY / 2)) {
                        gc.strokeLine(0, currRowY, hiddenColumnWidth, currRowY);
                        gc.fillText(model.getTitle().getValue(), 0, currRowY - spaceY / 2);
                    }

                    currRowY += spaceY;

                }

            }

            gc.setLineWidth(1);

            //red line
            gc.setFill(CURRPOS);
            gc.setStroke(CURRPOS);
            gc.strokeLine(currCursorPos - 1, 0, currCursorPos - 1, height);

        }

    }

    public boolean computeRender(double currX, double currY){

        return (currX > inverseTransformX(-spaceX) && currX < inverseTransformX(waveformCnvs.getWidth()+spaceX*2)) &&
                (currY > inverseTransformY(0)-spaceY && currY < inverseTransformY(waveformCnvs.getHeight())+spaceY);

    }

    private void restartCanvas(){

        currCursorPos = hiddenColumnWidth;
        width = 0;

        waveformCnvs.setWidth(waveformCanvasAnchor.getWidth()+hiddenColumnWidth);
        waveformCnvs.setHeight(waveformCanvasAnchor.getHeight()+hiddenColumnHeight);

        gc.setTransform(transform[0], transform[1], transform[2],
                transform[3], transform[4], transform[5]
        );

        clearRect40K(transform[4], transform[5]);

    }

    // convert screen coordinates to grid coordinates by inverting circuit transform
    private double inverseTransformX(double x) {
        return ((x-transform[4])/transform[0]);
    }

    private double inverseTransformY(double y) {
        return (y-transform[5])/transform[3];
    }

    private void recalculateScrollBars(){

        if(width - hiddenColumnWidth < waveformCanvasAnchor.getWidth()){
            canvasHScrlBr.setMax(1);
            canvasHScrlBr.setVisible(false);
        }else{
            double vertScrollbarWidth = 0;
            if(canvasVScrlBr.isVisible())vertScrollbarWidth = canvasVScrlBr.getWidth();
            canvasHScrlBr.setMin(0);
            canvasHScrlBr.setMax(width-hiddenColumnWidth- waveformCanvasAnchor.getWidth()+vertScrollbarWidth);
            canvasHScrlBr.setVisible(true);
            canvasHScrlBr.setVisibleAmount(canvasHScrlBr.getMax()*(waveformCanvasAnchor.getWidth()/(width)));
        }

        if(height < waveformCanvasAnchor.getHeight()){
            canvasVScrlBr.setMax(1);
            canvasVScrlBr.setVisible(false);
        }else{
            double horizScrollbarHeight = 0;
            if(canvasHScrlBr.isVisible())horizScrollbarHeight = canvasHScrlBr.getHeight();
            canvasVScrlBr.setMin(0);
            canvasVScrlBr.setMax(height- waveformCanvasAnchor.getHeight()+horizScrollbarHeight);
            canvasVScrlBr.setVisibleAmount(waveformTableViewScrollbar.getVisibleAmount()*canvasVScrlBr.getMax());
            canvasVScrlBr.setVisible(true);
        }

    }

    private void clearRect40K(double prevX, double prevY) {

        gc.setFill(BACKGROUND);
        gc.fillRect(-prevX/transform[0],-prevY/transform[0], waveformCnvs.getWidth()/transform[0],
                waveformCnvs.getHeight()/transform[0]);

    }



    private void exportImage(){

        double curSpaceX = spaceX;
        double finalCurSpaceX = curSpaceX;

        File dest;
        FileSelector fileSelector = new FileSelector(stage);
        dest = fileSelector.showSaveDialog(LogisimFX.newgui.ExportImageFrame.LC.get("exportImageFileSelect"));

        ExportImageTask exportPngTask = new ExportImageTask(dest);
        Thread exportThread = new Thread(exportPngTask);
        exportPngTask.setOnSucceeded(workerStateEvent ->  {

            exportThread.interrupt();

            for (TreeItem<WaveformTableModel> item : waveformTblvw.getRoot().getChildren()) {
                item.setExpanded(false);
            }

            spaceX = finalCurSpaceX;
            width = logObjects.get(0).getValue().getValues().size() * spaceX + hiddenColumnWidth;

            clearRect40K(transform[4], transform[5]);

            transform[4] = 0;
            transform[5] = 0;

            updateWaveform();

            System.gc();

        });

        DialogManager.createProgressDialog(exportPngTask);

        if(dest != null) {

            for (TreeItem<WaveformTableModel> item: waveformTblvw.getRoot().getChildren()) {
                item.setExpanded(true);
            }

            curSpaceX = spaceX;
            spaceX = REFERENCE_SPACE_X;

            width = logObjects.get(0).getValue().getValues().size() * spaceX + hiddenColumnWidth;

            clearRect40K(transform[4], transform[5]);

            transform[4] = 0;
            transform[5] = 0;

            updateWaveform();

            exportThread.start();

        }

    }

    public class ExportImageTask extends Task<Void> {

        private CountDownLatch latch = new CountDownLatch(1);
        ImageView img;
        File dest;

        public ExportImageTask(File dest){
            this.dest = dest;
        }

        @Override
        protected Void call() {

            if(dest != null) {

                BufferedImage bufferedImage = new BufferedImage((int)width, (int)height, BufferedImage.TYPE_INT_ARGB_PRE);
                WritableRaster raster = bufferedImage.getRaster();

                final double tileXStep = waveformCnvs.getWidth();
                final double tileYStep = waveformCnvs.getHeight();

                final int tilesX = (int)Math.ceil(width / tileXStep);
                final int tilesY = (int)Math.ceil(height / tileYStep);
                int currTile = 0;
                int totalTiles = tilesX * tilesY;

                final SnapshotParameters params = new SnapshotParameters();

                //System.out.println("tiles count "+tilesX + " "+ tilesY);

                try {

                    for (int row = 0; row < tilesY; row++) {

                        for (int col = 0; col < tilesX; col++) {

                            currTile++;
                            this.updateMessage(LC.getFormatted("exportProgressHeader",  Integer.toString(currTile), Integer.toString(totalTiles)));

                            //System.out.println("title "+col+" "+row);

                            int x = col * (int)tileXStep;
                            int y = row * (int)tileYStep;

                            //System.out.println("Coords "+x+" "+y + " width "+tileWidth+" "+tileHeight);

                            Platform.runLater(() ->{

                                clearRect40K(transform[4], transform[5]);

                                transform[4] = -x+1;
                                transform[5] = -y;

                                updateWaveform();

                                img = new ImageView(waveformCnvs.snapshot(params, null));

                                latch.countDown();

                            });

                            latch.await();

                            BufferedImage bImg = SwingFXUtils.fromFXImage(img.getImage(),null);
                            raster.setRect(x,y,bImg.getRaster());

                            latch = new CountDownLatch(1);
                        }

                    }

                    File where;
                    if (dest.isDirectory()) {
                        where = new File(dest, proj.getLogisimFile().getName() + ".png");
                    } else {
                        String newName = dest.getName() + ".png";
                        where = new File(dest.getParentFile(), newName);
                    }

                    try {

                        ImageIO.write(bufferedImage, "PNG", where);

                    } catch (Exception e) {
                        DialogManager.createErrorDialog(LogisimFX.newgui.ExportImageFrame.LC.get("couldNotCreateFile"), LogisimFX.newgui.ExportImageFrame.LC.get("couldNotCreateFile"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;

        }

    }

    private void exportLog(){

        PrintWriter writer;

        FileSelector fileSelector = new FileSelector(stage);

        File file = fileSelector.SaveCirclog();

        if (file != null && file.exists() && (!file.canWrite() || file.isDirectory())) {

            DialogManager.createErrorDialog(LC.get("fileCannotWriteTitle"),
                    LC.getFormatted("fileCannotWriteMessage", file.getName()));

            return;
        }

        if(file != null) {
            curModel.setFile(file);

            try {
                writer = new PrintWriter(new FileWriter(file, true));
            } catch (IOException e) {
                curModel.setFile(null);
                return;
            }

            //write curr freq
            writer.println(currTickFreq);

            //write components names
            ObservableList<SelectionItem> sel = curModel.getComponents();

            StringBuilder buff = new StringBuilder();
            for (int i = 0; i < sel.size(); i++) {
                if (i > 0) buff.append("\t");
                buff.append(sel.get(i).toShortString().trim().replace(" ", ""));
            }
            writer.println(buff.toString());

            //write data
            for (Value[] values : curModel.getValues()) {

                StringBuilder buf = new StringBuilder();

                for (int i = 0; i < values.length; i++) {
                    if (i > 0) buf.append("\t");
                    if (values[i] != null) {
                        buf.append(values[i].toDisplayString(2).replace(" ", ""));
                    }
                }
                writer.println(buf.toString());

            }

            writer.flush();
            writer.close();
        }

    }

    private void importLog(){

        FileSelector fileSelector = new FileSelector(stage);

        File file = fileSelector.OpenCirclog();

        if(file != null) {
            //read
            StringBuilder builder = new StringBuilder();

            try {


                FileReader reader = new FileReader(file);

                char[] buf = new char[1024];
                int numRead;

                while ((numRead = reader.read(buf)) != -1) {
                    String readData = String.valueOf(buf, 0, numRead);
                    builder.append(readData);
                }

                reader.close();

            } catch (IOException e) {
                System.out.println("Cant read File " + e.getMessage());
            }

            //Parse
            String data = builder.toString();

            String[] lines = data.trim().split("\n");

            currTickFreq = Double.parseDouble(lines[0]);

            ArrayList<ArrayList<Value>> values = new ArrayList<>();
            for (int m = 0; m < lines[1].split("\t").length; m++) {
                values.add(new ArrayList<>());
            }

            for (int j = 2; j < lines.length; j++) {

                String line = lines[j];
                //catch carriage  return
                if(line.charAt(line.length()-1) == 13){
                    line = line.substring(0, line.length()-1);
                }

                String[] buff = line.split("\t");
/*
                ArrayList<Integer> bytes = new ArrayList<>();

                for(int k = 0; k < line.length(); k++){
                    bytes.add((int)line.charAt(k));
                }
                System.out.println(bytes);

 */

                for (int h = 0; h < buff.length; h++) {

                    Value val;
                    if (buff[h].trim().equals("0")) val = Value.FALSE;
                    else if (buff[h].trim().equals("1")) val = Value.TRUE;
                    else if (buff[h].trim().equals("x")) val = Value.UNKNOWN;
                    else if (buff[h].trim().equals("-")) val = Value.NIL;
                    else {
                        val = Value.createKnown(BitWidth.create(buff[h].length()), Integer.parseInt(buff[h], 2));
                    }

                    values.get(h).add(val);

                }


            }

            String[] titles = lines[1].split("\t");

            restartCanvas();
            if (logger != null) logger.interrupt();
            recalculateScrollBars();

            updateWaveformTable(titles, values);
        }

    }



    private void setSimulator(Simulator value) {

        if ((value == null) == (curModel == null)) {
            if (value == null || value.getCircuitState() == curModel.getCircuitState()) return;
        }

        if (curSimulator != null) curSimulator.removeSimulatorListener(myListener);

        Model data = null;
        if (value != null) {
            data = modelMap.get(value.getCircuitState());
            if (data == null) {
                data = new Model(value.getCircuitState());
                modelMap.put(data.getCircuitState(), data);
            }
        }
        curSimulator = value;
        curModel = data;
        curModel.bindComponentsList(getItemsToLogPlain(null, selectionTrVw.getRoot()));

        if (curSimulator != null) curSimulator.addSimulatorListener(myListener);
        computeTitle(curModel);

    }



    @Override
    public void onClose() {

        if(logObjects != null) {
            logObjects.clear();
            logObjects = null;
        }

        if(itemsToLog != null) {
            itemsToLog.clear();
            itemsToLog = null;
        }

        if(logger != null)logger.interrupt();

        waveformCnvs = null;
        gc = null;

        modelMap.clear();
        modelMap = null;

        proj.removeProjectListener(myListener);
        proj.removeLibraryListener(myListener);
        curModel.removeModelListener(myListener);

        System.gc();

    }

}
