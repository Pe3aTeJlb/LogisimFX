package LogisimFX.newgui.CircLogFrame;

import LogisimFX.FileSelector;
import LogisimFX.IconsManager;
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
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
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


public class CircLogController extends AbstractController {

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
    private Button addBtn;

    @FXML
    private Button changRadixBtn;

    @FXML
    private Button moveUpBtn;

    @FXML
    private Button moveDownBtn;

    @FXML
    private Button removeBtn;



    //TimelineTab

    @FXML
    private Tab timelineTab;

    @FXML
    private Button SimPlayOneStepBtn;

    @FXML
    private Button SimPlayBtn;

    @FXML
    private Button SimStepBtn;

    @FXML
    private Button SimStopBtn;

    @FXML
    private SplitPane splitPane;

    @FXML
    private AnchorPane timelineCanvasAnchor;

    @FXML
    private TreeTableView<TimelineTableModel> timelineTblvw;

    @FXML
    private Canvas timelineCnvs;

    @FXML
    private ScrollBar canvasVScrlBr;

    @FXML
    private ScrollBar canvasHScrlBr;

    @FXML
    private Button exportImageBtn;

    @FXML
    private Button fileOpenBtn;



    //TableTab

    @FXML
    private Tab tableTab;

    @FXML
    private TableView<LogLine> logTblvw;

    @FXML
    private Button loadFileBtn;

    @FXML
    private Button exportFileBtn;



    @FXML
    private Button startTimelineBtn;


    private Project proj;

    private MultipleSelectionModel<TreeItem<Object>> treeSelectionModel;
    private MultipleSelectionModel<TreeItem<SelectionItem>> selectiontrvwSelectionModel;

    private Simulator curSimulator = null;
    private Model curModel;
    private Map<CircuitState, Model> modelMap = new HashMap<>();
    private MyListener myListener = new MyListener();
    private LogThread logger;

    private class MyListener
            implements ProjectListener, LibraryListener,
            SimulatorListener, ModelListener {

        public void projectChanged(ProjectEvent event) {
            int action = event.getAction();
            if (action == ProjectEvent.ACTION_SET_STATE) {
                setSimulator(event.getProject().getSimulator());
            } else if (action == ProjectEvent.ACTION_SET_FILE) {
                computeTitle(curModel);
            }
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
                logTblvw.getItems().add(new LogLine(values));
                logTblvw.refresh();

                timelineTblvw.refresh();
                Platform.runLater(() -> updateTimelineData(values));
            }

        }

        @Override
        public void filePropertyChanged(ModelEvent event) {

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

        initSelectionTab();

        setSimulator(proj.getSimulator());
        curModel.addModelListener(myListener);

        initTimelineTab();
        initTableTab();

        startTimelineBtn.textProperty().bind(LC.createStringBinding("startLogging"));
        startTimelineBtn.setOnAction(event -> {

            if(!selectionTrVw.getRoot().getChildren().isEmpty()) {

                curModel.bindComponentsList(getItemsToLogPlain(null, selectionTrVw.getRoot()));

                //log tab
                itemsToLog = getItemsToLogPlain(null, selectionTrVw.getRoot());
                updateColumns(itemsToLog);

                //timeline tab
                updateTimelineTable(selectionTrVw.getRoot(), itemsToLog);

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

                            textProperty().bind(((Component) item).getFactory().getDisplayName());
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
                                selectionTrVw.getRoot().getChildren().addAll(array);
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
                selectionTrVw.getRoot().getChildren().addAll(array);
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
        removeBtn.setOnAction(event -> selectiontrvwSelectionModel.getSelectedItem().getParent().getChildren().remove(selectiontrvwSelectionModel.getSelectedItem()));

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

        TreeItem<SelectionItem> cur = selectiontrvwSelectionModel.getSelectedItem();
        TreeItem<SelectionItem> subRoot = selectiontrvwSelectionModel.getSelectedItem().getParent();
        int oldIndex =  subRoot.getChildren().indexOf(cur);
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

    private ScrollBar timelineTableViewScrollbar;

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

    private double pixelScale = 4;

    /*
    Поскольку, заснапшотить сплитпейн нормально не представляется возможным, делаем профанацию с canvas
    Будем отрисовывать таблицу с названием элементов в невидимой для юзера зоне.
    Для этого определим ширину столбца и сдвинем canvas в отрицательную зону.
    После взятия снапшота от всего canvas всё будет тип топ.
    отрисовка мини-таблицы будет в основном цикле отрисовке updateTimeline
    На своё время, это лучшее решение
    */
    TreeTableColumn<TimelineTableModel, String> nameColumn;
    double hiddenColumnWidth;
    double hiddenColumnHeight = 22;

    TreeTableColumn<TimelineTableModel, String> valueColumn;

    private ObservableList<TreeItem<TimelineTableModel>> logObjects;

    private void initTimelineTab(){

        timelineTab.textProperty().bind(LC.createStringBinding("timelineTab"));

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



        timelineTblvw.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        timelineTblvw.setRoot(new TreeItem<>(null));
        timelineTblvw.setShowRoot(false);
        timelineTblvw.refresh();


        nameColumn = new TreeTableColumn<>("");
        nameColumn.textProperty().bind(LC.createStringBinding("componentTitle"));
        nameColumn.setSortable(false);
        //nameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("Title"));
        nameColumn.setCellValueFactory(param -> param.getValue().getValue().getTitle());
        nameColumn.setCellFactory(param -> {
            TreeTableCell<TimelineTableModel, String> cell = new TreeTableCell<TimelineTableModel, String>() {

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
                updateTimeline();
            });
            cell.setOnMouseExited(event -> {
                currSelectedRow = -1;
                updateTimeline();
            });
            cell.setOnMousePressed(event -> {
                if(event.getButton() == MouseButton.SECONDARY){
                    cell.setContextMenu(ContextMenuManager.RadixOptionsContextMenu(cell.getTreeTableRow().getItem(), timelineTblvw));
                }
            });

            return cell;

        });

        valueColumn = new TreeTableColumn<>("");
        valueColumn.textProperty().bind(LC.createStringBinding("valueTitle"));
        valueColumn.setSortable(false);
        valueColumn.setCellValueFactory(param -> param.getValue().getValue().getValueAt());
        valueColumn.setCellFactory(param -> {
            TreeTableCell<TimelineTableModel, String> cell = new TreeTableCell<TimelineTableModel, String>() {

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
                updateTimeline();
            });
            cell.setOnMouseExited(event -> {
                currSelectedRow = -1;
                updateTimeline();
            });
            cell.setOnMousePressed(event -> {
                if(event.getButton() == MouseButton.SECONDARY){
                    cell.setContextMenu(ContextMenuManager.RadixOptionsContextMenu(cell.getTreeTableRow().getItem(),timelineTblvw));
                }
            });

            return cell;
        });

        timelineTblvw.getColumns().add(nameColumn);
        timelineTblvw.getColumns().add(valueColumn);



        splitPane.setDividerPositions(0.05);


        timelineCnvs.setCache(true);
        timelineCnvs.setCacheHint(CacheHint.SPEED);

        gc = timelineCnvs.getGraphicsContext2D();
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

        timelineCanvasAnchor.widthProperty().addListener((observable, oldValue, newValue) -> updateTimeline());

        timelineCanvasAnchor.heightProperty().addListener((observable, oldValue, newValue) -> updateTimeline());

        timelineCnvs.setOnMousePressed(event -> {

            dragScreenX = event.getX();
            dragScreenY = event.getY();

            if(event.getButton() == MouseButton.PRIMARY) {
                currCursorPos = (Math.max(0, Math.min(width - 1, inverseTransformX(event.getX()))));

                timelineTblvw.refresh();
                updateTimeline();
            }

        });

        timelineCnvs.setOnMouseDragged(event -> {

            if(event.getButton() == MouseButton.PRIMARY) {
                currCursorPos = (Math.max(0, Math.min(width - 1, inverseTransformX(event.getX()))));

                timelineTblvw.refresh();
                updateTimeline();
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

                if(transform[4] + dx > 0 || transform[4] + dx < -(width-timelineCanvasAnchor.getWidth()-hiddenColumnWidth+vertScrollbarWidth)){
                    dx = 0;
                }

                if(transform[5] + dy > 0 || transform[5] + dy < -(height-timelineCanvasAnchor.getHeight()+horizScrollbarHeight)){
                    dy = 0;
                }

                clearRect40K(transform[4], transform[5]);

                transform[4] += dx;
                transform[5] += dy;

                canvasHScrlBr.setValue(-transform[4]);
                //canvasVScrlBr.setValue(-transform[5]);
                timelineTableViewScrollbar.setValue(-transform[5]/canvasVScrlBr.getMax());

                dragScreenX = event.getX();
                dragScreenY = event.getY();

                updateTimeline();

            }

        });

        timelineCnvs.setOnScroll(event -> {

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

            updateTimeline();

        });

        canvasHScrlBr.valueProperty().addListener(event -> {

            clearRect40K(transform[4], transform[5]);

            transform[4] = -canvasHScrlBr.getValue();

            updateTimeline();

        });

        canvasVScrlBr.valueProperty().addListener(event -> {

            clearRect40K(transform[4], transform[5]);

            transform[5] = -canvasVScrlBr.getValue();

            if(timelineTableViewScrollbar.getValue() != canvasVScrlBr.getValue()) {
                timelineTableViewScrollbar.setValue(canvasVScrlBr.getValue() / canvasVScrlBr.getMax());
            }

            updateTimeline();

        });

        //Lookup func must be executed after tableview is shown
        stage.setOnShown(event -> {

            //Find tableview scrollbar and add listener for rows scroll
            timelineTableViewScrollbar = (ScrollBar) timelineTblvw.lookup(".scroll-bar");

            timelineTableViewScrollbar.visibleProperty().addListener(observable -> recalculateScrollBars());

            timelineTableViewScrollbar.valueProperty().addListener(observable -> {

                clearRect40K(transform[4], transform[5]);

                transform[5] = -timelineTableViewScrollbar.getValue()*(canvasVScrlBr.getMax());

                if(timelineTableViewScrollbar.getValue() != canvasVScrlBr.getValue()) {
                    canvasVScrlBr.setValue(timelineTableViewScrollbar.getValue() * canvasVScrlBr.getMax());
                }

                updateTimeline();

            });

        });



        //Bottom buttons

        fileOpenBtn.textProperty().bind(LC.createStringBinding("openButton"));
        fileOpenBtn.setOnAction(event -> importLog());

        exportImageBtn.textProperty().bind(LC.createStringBinding("exportImage"));
        exportImageBtn.setOnAction(event -> exportImage());

    }

    private static class ToolTip extends Tooltip{

        public ToolTip(String text){
            super();
            textProperty().bind(LogisimFX.newgui.MainFrame.LC.createStringBinding(text));
        }

    }

    public class TimelineTableModel{

        private SelectionItem comp;
        private String title;
        private ArrayList<Value> values = new ArrayList<>();
        private int radix = 2;

        public TimelineTableModel(SelectionItem comp){
            this.comp = comp;
            this.radix = comp.getRadix();
        }

        public TimelineTableModel(String title, ArrayList<Value> values){
            this.title = title;
            this.values = values;
        }

        public void addValue(Value val){
            values.add(val);
        }

        public void setRadix(int radix){
            this.radix = radix;
        }

        public SimpleStringProperty getTitle(){
            return comp == null ? new SimpleStringProperty(title) : new SimpleStringProperty(comp.toShortString());
        }

        public SimpleObjectProperty<String> getValueAt(){

            int index = (int) Math.floor((currCursorPos-hiddenColumnWidth) / spaceX);
            return values.isEmpty() || index >= values.size() ?  new SimpleObjectProperty<>(Value.NIL.toDisplayString(radix)) :
                    new SimpleObjectProperty<>(values.get(index).toDisplayString(radix));

        }

        public ArrayList<Value> getValues(){
            return values;
        }

        public SelectionItem getSelectionItem(){
            return comp;
        }

        public int getRadix(){
            return radix;
        }

    }

    private void updateTimelineTable(TreeItem<SelectionItem> selectedItemsRoot, ObservableList<SelectionItem> logItemsPlain){

        timelineTblvw.getRoot().getChildren().clear();
        double bufflen = 0;

        logObjects = convertToTimelineTableModel(null, selectedItemsRoot, timelineTblvw.getRoot());

        for (SelectionItem item: logItemsPlain) {
            if(fm.computeStringWidth(item.toShortString())>bufflen) bufflen = fm.computeStringWidth(item.toShortString());
        }

        hiddenColumnWidth = bufflen+5;

        updateTimeline();

    }

    private void updateTimelineTable(String[] titles, ArrayList<ArrayList<Value>> values){

        logObjects = FXCollections.observableArrayList();

        timelineTblvw.getRoot().getChildren().clear();
        double bufflen = 0;

        int i =0;
        for (String title: titles) {
            title = title.trim();
            TreeItem<TimelineTableModel> model = new TreeItem<>(new TimelineTableModel(title,values.get(i)));
            timelineTblvw.getRoot().getChildren().add(model);
            if(fm.computeStringWidth(title)>bufflen)bufflen = fm.computeStringWidth(title);
            logObjects.add(model);
            i++;
        }

        hiddenColumnWidth = bufflen+5;
        width = hiddenColumnWidth+values.get(0).size()*spaceX;
        currCursorPos = hiddenColumnWidth;

        updateTimeline();

    }

    private void updateTimelineData(Value[] values){

        int i = 0;
        int size = 0;
        for (TreeItem<TimelineTableModel> model : logObjects){
            size = model.getValue().getValues().size();
            if(size < 1000) {
                model.getValue().addValue(values[i]);
                size = model.getValue().getValues().size();
                i++;
            }
        }

        System.out.println(size);
        if(size < 1000) {

            //ze_ reference :extend extend extend(canvas)
            width = hiddenColumnWidth + size * spaceX;

            currCursorPos = (hiddenColumnWidth + size * spaceX - 1);

            timelineTblvw.refresh();
            updateTimeline();

        }

    }

    private ObservableList<TreeItem<TimelineTableModel>> convertToTimelineTableModel(
            ObservableList<TreeItem<TimelineTableModel>> ret, TreeItem<SelectionItem> root, TreeItem<TimelineTableModel> copyDest){

        if(ret == null) ret = FXCollections.observableArrayList();

        for (TreeItem<SelectionItem> node: root.getChildren()) {
            TreeItem<TimelineTableModel> item = new TreeItem<>(new TimelineTableModel(node.getValue()));
            copyDest.getChildren().add(item);
            ret.add(item);
            if(!node.getChildren().isEmpty()) convertToTimelineTableModel(ret, node, item);
        }

        return ret;

    }

    private void updateTimeline(){

        clearRect40K(transform[4], transform[5]);

        gc.setTransform(transform[0], transform[1], transform[2],
                transform[3], transform[4], transform[5]
        );

        timelineCnvs.setWidth(hiddenColumnWidth+timelineCanvasAnchor.getWidth());
        timelineCnvs.setHeight(timelineCanvasAnchor.getHeight()+hiddenColumnHeight);

        timelineCnvs.setLayoutX(-hiddenColumnWidth);

        if(logObjects != null) {

            //compute canvas height

            int hcnt = 0;
            for (TreeItem<TimelineTableModel> item : logObjects) {
                if (timelineTblvw.getTreeItemLevel(item) == 1 || item.getParent().isExpanded()) {
                    hcnt++;
                }
            }

            height = hcnt * spaceY + hiddenColumnHeight;

            recalculateScrollBars();

            gc.setStroke(LINE);
            gc.setFill(LINE);

            //hidden table
            gc.strokeLine(hiddenColumnWidth, 0, hiddenColumnWidth, height);
            gc.strokeLine(0, 20, hiddenColumnWidth, 20);
            gc.fillText(LC.get("componentTitle"), (hiddenColumnWidth - fm.computeStringWidth(LC.get("componentTitle"))) / 2, 15);


            //time steps
            gc.strokeLine(0, 5, width, 5);
            int counter = 0;
            int skip = 1;
            String prefix = "";
            if (curSimulator.getTickFrequency() > 1000) prefix = "m";

            for (double i = hiddenColumnWidth + 2 * spaceX; i < width; i += 2 * spaceX) {

                String text = formatter.format(1 / curSimulator.getTickFrequency() * counter) + " " + prefix + "s";

                if (fm.computeStringWidth(text) + charlen < 2 * spaceX * skip && computeRender(i,15)) {

                    gc.strokeLine(i, 5, i, 15);
                    gc.fillText(text, i - fm.computeStringWidth(text) - 2, 15);
                    skip = 1;

                } else {

                    skip++;

                }

                counter++;

            }

            //lower line bound of first row
            double currRowY = 70;

            //comp data

            for (TreeItem<TimelineTableModel> item : logObjects) {

                if ( (timelineTblvw.getTreeItemLevel(item) == 1 || item.getParent().isExpanded())) {

                    TimelineTableModel model = item.getValue();
                    ArrayList<Value> vals = model.getValues();
                    int curValueLength = 0;

                    double currX = hiddenColumnWidth;
                    double currY = currRowY;

                    if (logObjects.indexOf(item) == currSelectedRow) {
                        gc.setLineWidth(2);
                    } else {
                        gc.setLineWidth(1);
                    }

                    if (vals != null && !vals.isEmpty()) {

                        for (int i = 0; i < vals.size(); i++) {

                            gc.setStroke(LINE);
                            gc.setFill(LINE);

                            //if start value is true
                            if (i == 0 && vals.get(0).equals(Value.TRUE)) currY -= yAdjust;

                            if (i > 0) {

                                //line up
                                if (vals.get(i - 1).equals(Value.FALSE) && vals.get(i).equals(Value.TRUE)) {

                                    if(computeRender(currX,currY))gc.strokeLine(currX + w, currY, currX + w, currY - yAdjust);
                                    currY -= yAdjust;

                                }

                                //line down
                                if (vals.get(i - 1).equals(Value.TRUE) && vals.get(i).equals(Value.FALSE)) {

                                    if(computeRender(currX,currY))gc.strokeLine(currX + w, currY, currX + w, currY + yAdjust);
                                    currY += yAdjust;

                                }

                            }

                            if (vals.get(i).equals(Value.TRUE) && computeRender(currX,currY)) {
                                //true fill
                                gc.setStroke(LINEFILL);
                                gc.setFill(LINEFILL);

                                gc.fillRect(currX + 2 * w, currY + w, spaceX, yAdjust);

                                gc.setStroke(LINE);
                                gc.setFill(LINE);

                            } else if (vals.get(i).equals(Value.UNKNOWN) && computeRender(currX,currY)) {
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
                                    if(computeRender(currX,currY)) {
                                        gc.strokeLine(currX + w, currRowY - yAdjust / 2, currX + spaceX, currRowY - 0.75 * yAdjust);
                                        gc.strokeLine(currX + w, currRowY - yAdjust / 2, currX + spaceX, currRowY - 0.25 * yAdjust);
                                    }
                                    curValueLength++;

                                } else {

                                    if (i != vals.size() - 1 && vals.get(i + 1).equals(vals.get(i))) {
                                        //finish hexagon
                                        if(computeRender(currX,currY)) {
                                            gc.strokeLine(currX + w, currRowY - 0.75 * yAdjust, currX + spaceX, currRowY - 0.75 * yAdjust);
                                            gc.strokeLine(currX + w, currRowY - 0.25 * yAdjust, currX + spaceX, currRowY - 0.25 * yAdjust);
                                        }
                                        curValueLength++;

                                    } else {
                                        //hexagon body
                                        if(computeRender(currX,currY)) {
                                            gc.strokeLine(currX + w, currRowY - 0.75 * yAdjust, currX + spaceX, currRowY - yAdjust / 2);
                                            gc.strokeLine(currX + w, currRowY - 0.25 * yAdjust, currX + spaceX, currRowY - yAdjust / 2);
                                        }

                                        gc.setStroke(LINE);
                                        gc.setFill(LINE);

                                        //Format value
                                        int radix = item.getValue().getRadix();
                                        String text = vals.get(i).toDisplayString(radix);
                                        double stringlen = fm.computeStringWidth(text);

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

                                        gc.fillText(text, currX - ((curValueLength - 1) * spaceX + fm.computeStringWidth(text)) / 2, currY - ((yAdjust - fm.getAscent()) / 2));

                                        curValueLength = 0;

                                    }

                                }

                                gc.setStroke(LINE);
                                gc.setFill(LINE);

                            }

                            //lower row bound
                            if(computeRender(currX,currY))gc.strokeLine(currX + gc.getLineWidth(), currY, currX + spaceX, currY);
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

        return (currX > inverseTransformX(-spaceX) && currX < inverseTransformX(timelineCnvs.getWidth()+spaceX*2)) &&
                (currY > inverseTransformY(0)-spaceY && currY < inverseTransformY(timelineCnvs.getHeight())+spaceY);

    }

    private void exportImage(){

        File dest;
        FileSelector fileSelector = new FileSelector(stage);
        dest = fileSelector.showSaveDialog(LogisimFX.newgui.ExportImageFrame.LC.get("exportImageFileSelect"));

        if(dest != null) {

            for (TreeItem<TimelineTableModel> item: timelineTblvw.getRoot().getChildren()) {
                item.setExpanded(true);
            }

            double curSpaceX = spaceX;
            spaceX = REFERENCE_SPACE_X;

            width = logObjects.get(0).getValue().getValues().size() * spaceX + hiddenColumnWidth;

            clearRect40K(transform[4], transform[5]);

            transform[4] = 0;
            transform[5] = 0;

            updateTimeline();

            WritableImage writableImage = new WritableImage((int)(width), (int)(height));

            final double tileXStep = timelineCnvs.getWidth();
            final double tileYStep = timelineCnvs.getHeight();

            final int tilesX = (int)Math.ceil(width / tileXStep);
            final int tilesY = (int)Math.ceil(height / tileYStep);

            try {

                for (int col = 0; col < tilesX; col++) {

                    for (int row = 0; row < tilesY; row++) {

                        System.out.println("title "+col+" "+row);

                        int x = col * (int)tileXStep;
                        int tileWidth = (int)tileXStep;
                        if(tileWidth > width - tileXStep * col) tileWidth = (int)(width - tileXStep * col);

                        int y = row * (int)tileYStep;
                        int tileHeight = (int)tileYStep;
                        if(tileHeight > height - tileYStep * row) tileHeight = (int)(height - tileYStep * row);

                        System.out.println("Coords "+x+" "+y + " width "+tileWidth+" "+tileHeight);

                        clearRect40K(transform[4], transform[5]);

                        transform[4] = -x+1;
                        transform[5] = -y;

                        updateTimeline();

                        final SnapshotParameters params = new SnapshotParameters();

                        Image img = new ImageView(timelineCnvs.snapshot(params, null)).getImage();

                        writableImage.getPixelWriter().setPixels(x, y, tileWidth, tileHeight, img.getPixelReader(), 0, 0);

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

                    BufferedImage bImage = SwingFXUtils.fromFXImage(writableImage, null);
                    ImageIO.write(bImage, "PNG", where);
                    bImage = null;
                    writableImage = null;

                } catch (Exception e) {
                    DialogManager.CreateErrorDialog(LogisimFX.newgui.ExportImageFrame.LC.get("couldNotCreateFile"), LogisimFX.newgui.ExportImageFrame.LC.get("couldNotCreateFile"));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            for (TreeItem<TimelineTableModel> item : timelineTblvw.getRoot().getChildren()) {
                item.setExpanded(false);
            }

            spaceX = curSpaceX;
            width = logObjects.get(0).getValue().getValues().size() * spaceX + hiddenColumnWidth;

            clearRect40K(transform[4], transform[5]);

            transform[4] = 0;
            transform[5] = 0;

            updateTimeline();

        }

    }


    private void restartCanvas(){

        currCursorPos = hiddenColumnWidth;
        width = 0;

        timelineCnvs.setWidth(timelineCanvasAnchor.getWidth()+hiddenColumnWidth);
        timelineCnvs.setHeight(timelineCanvasAnchor.getHeight()+hiddenColumnHeight);

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

        if(width - hiddenColumnWidth < timelineCanvasAnchor.getWidth()){
            canvasHScrlBr.setMax(1);
            canvasHScrlBr.setVisible(false);
        }else{
            double vertScrollbarWidth = 0;
            if(canvasVScrlBr.isVisible())vertScrollbarWidth = canvasVScrlBr.getWidth();
            canvasHScrlBr.setMin(0);
            canvasHScrlBr.setMax(width-hiddenColumnWidth-timelineCanvasAnchor.getWidth()+vertScrollbarWidth);
            canvasHScrlBr.setVisible(true);
            canvasHScrlBr.setVisibleAmount(canvasHScrlBr.getMax()*(timelineCanvasAnchor.getWidth()/(width)));
        }

        if(height < timelineCanvasAnchor.getHeight()){
            canvasVScrlBr.setMax(1);
            canvasVScrlBr.setVisible(false);
        }else{
            double horizScrollbarHeight = 0;
            if(canvasHScrlBr.isVisible())horizScrollbarHeight = canvasHScrlBr.getHeight();
            canvasVScrlBr.setMin(0);
            canvasVScrlBr.setMax(height-timelineCanvasAnchor.getHeight()+horizScrollbarHeight);
            canvasVScrlBr.setVisibleAmount(timelineTableViewScrollbar.getVisibleAmount()*canvasVScrlBr.getMax());
            canvasVScrlBr.setVisible(true);
        }

    }

    private void clearRect40K(double prevX, double prevY) {

        gc.setFill(BACKGROUND);
        gc.fillRect(-prevX/transform[0],-prevY/transform[0],timelineCnvs.getWidth()/transform[0],
                timelineCnvs.getHeight()/transform[0]);

    }



    private void initTableTab(){

        tableTab.textProperty().bind(LC.createStringBinding("tableTab"));

        logTblvw.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadFileBtn.textProperty().bind(LC.createStringBinding("openButton"));
        loadFileBtn.setOnAction(event -> importLog());

        exportFileBtn.textProperty().bind(LC.createStringBinding("saveButton"));
        exportFileBtn.setOnAction(event -> exportLog());

    }

    private void updateColumns(ObservableList<SelectionItem> items){

        logTblvw.getItems().clear();
        logTblvw.getColumns().clear();

        int i = 0;
        for (SelectionItem item: items) {

            TableColumn<LogLine, String> column = new TableColumn<>(item.toShortString());
            int finalI = i;
            column.setCellValueFactory(param -> param.getValue().getValue(finalI,item.getRadix()));
            column.setCellFactory(param -> {
                TableCell<LogLine, String> cell = new TableCell<LogLine, String>() {

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

                cell.setOnMousePressed(event -> {
                    if(event.getButton() == MouseButton.SECONDARY){
                        cell.setContextMenu(ContextMenuManager.RadixOptionsContextMenu(item, logTblvw));
                    }
                });

                cell.setAlignment(Pos.CENTER);

                return cell;
            });

            column.setSortable(false);

            logTblvw.getColumns().add(column);
            i++;

        }

    }

    private void updateColumns(String[] titles, ArrayList<LogLine> logLines){

        logTblvw.getItems().clear();
        logTblvw.getColumns().clear();

        int i = 0;
        for (String title: titles) {

            TableColumn<LogLine, Value> column = new TableColumn<>(title.trim());
            int finalI = i;
            column.setCellValueFactory(param -> param.getValue().getValue(finalI));
            column.setCellFactory(param -> {
                TableCell<LogLine, Value> cell = new TableCell<LogLine, Value>() {

                    @Override
                    protected void updateItem(Value item, boolean empty) {

                        super.updateItem(item, empty);

                        if (item == null) {
                            super.setText(null);
                            super.setGraphic(null);
                        } else {
                            super.setText(item.toString());
                            super.setGraphic(null);
                        }

                    }

                };


                cell.setAlignment(Pos.CENTER);

                return cell;
            });
            column.setSortable(false);

            logTblvw.getColumns().add(column);
            i++;
        }

        logTblvw.getItems().addAll(logLines);

    }

    private static class LogLine{

        private Value[] values;

        public LogLine(Value[] values){
            this.values = values;
        }

        public SimpleObjectProperty<Value> getValue(int index){
            return new SimpleObjectProperty<>(values[index]);
        }

        public SimpleStringProperty getValue(int index, int radix){
            return new SimpleStringProperty(values[index].toDisplayString(radix));
        }

        public Value[] getValues(){
            return values;
        }

    }

    private void exportLog(){

        PrintWriter writer;

        FileSelector fileSelector = new FileSelector(stage);

        File file = fileSelector.SaveCirclog();

        if (file != null && file.exists() && (!file.canWrite() || file.isDirectory())) {

            DialogManager.CreateErrorDialog(LC.get("fileCannotWriteTitle"),
                    LC.getFormatted("fileCannotWriteMessage", file.getName()));

            return;
        }

        curModel.setFile(file);

        try {
            writer = new PrintWriter(new FileWriter(file, true));
        } catch (IOException e) {
            curModel.setFile(null);
            return;
        }

        ObservableList<SelectionItem> sel = curModel.getComponents();

        StringBuilder buff = new StringBuilder();
        for (int i = 0; i < sel.size(); i++) {
            if (i > 0) buff.append("\t");
            buff.append(sel.get(i).toShortString().trim());
        }
        writer.println(buff.toString());

        for (Value[] values: curModel.getValues()) {

            StringBuilder buf = new StringBuilder();

            for (int i = 0; i < values.length; i++) {
                if (i > 0) buf.append("\t");
                if (values[i] != null) {
                    buf.append(values[i].toDisplayString(2).replace(" ",""));
                }
            }
            writer.println(buf.toString());

        }

        writer.flush();
        writer.close();

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
            ArrayList<LogLine> logLines = new ArrayList<>();

            ArrayList<ArrayList<Value>> values = new ArrayList<>();
            for (int m = 0; m < lines[0].split("\t").length; m++) {
                values.add(new ArrayList<>());
            }

            for (int j = 1; j < lines.length; j++) {

                String[] buff = lines[j].split("\t");

                Value[] vals = new Value[buff.length];

                for (int h = 0; h < buff.length; h++) {

                    Value val = null;
                    if (buff[h].trim().equals("0")) val = Value.FALSE;
                    else if (buff[h].trim().equals("1")) val = Value.TRUE;
                    else val = Value.createKnown(BitWidth.create(buff[h].length()), Integer.parseInt(buff[h], 2));

                    vals[h] = val;
                    values.get(h).add(val);

                }

                LogLine l = new LogLine(vals);
                logLines.add(l);
            }

            String[] titles = lines[0].split("\t");

            restartCanvas();
            if (logger != null) logger.interrupt();
            recalculateScrollBars();

            updateColumns(titles, logLines);
            updateTimelineTable(titles, values);
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

        System.out.println("Circ log closed");

        if(logger != null)logger.interrupt();

        timelineCnvs = null;
        gc = null;

        modelMap.clear();

        proj.removeProjectListener(myListener);
        proj.removeLibraryListener(myListener);
        curModel.removeModelListener(myListener);

    }

}
