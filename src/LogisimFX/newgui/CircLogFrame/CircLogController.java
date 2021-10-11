package LogisimFX.newgui.CircLogFrame;

import LogisimFX.FileSelector;
import LogisimFX.IconsManager;
import LogisimFX.circuit.*;
import LogisimFX.comp.Component;
import LogisimFX.data.Value;
import LogisimFX.file.LibraryEvent;
import LogisimFX.file.LibraryListener;
import LogisimFX.instance.StdAttr;
import LogisimFX.newgui.AbstractController;
import LogisimFX.newgui.DialogManager;
import LogisimFX.proj.Project;
import LogisimFX.proj.ProjectEvent;
import LogisimFX.proj.ProjectListener;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;

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
    private ListView<SelectionItem> selectedLst;



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
    private AnchorPane canvasAnchor;

    @FXML
    private TableView<TimelineTableModel> timelineTblvw;

    @FXML
    private Canvas timelineCnvs;

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
    private MultipleSelectionModel<SelectionItem> listSelectionModel;

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

            System.out.println("entry added");
            logTblvw.getItems().add(new LogLine(values));
            logTblvw.refresh();

            timelineTblvw.refresh();
            updateTimelineData(values);


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
        setSimulator(proj.getSimulator());
        curModel.addModelListener(myListener);

        initSelectionTab();
        initTimelineTab();
        initTableTab();

        startTimelineBtn.textProperty().bind(LC.createStringBinding("startLogging"));
        startTimelineBtn.setOnAction(event -> {

            if(!selectedLst.getItems().isEmpty()) {
                restartCanvas();
                updateColumnCount(selectedLst.getItems());
                updateTimelineTable(selectedLst.getItems());

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
                            List<SelectionItem> array = getSelectedItems();
                            if(array != null && !array.isEmpty()){
                                selectedLst.getItems().addAll(array);
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

        selectedLst.setCellFactory(list -> new ListCell<SelectionItem>(){

            protected void updateItem(SelectionItem item, boolean empty) {

                super.updateItem(item, empty);
                textProperty().unbind();

                if (empty) {

                    setText(null);
                    setGraphic(null);

                }else{

                    if(item != null){

                        setText(item.toString());
                        setGraphic(new ImageView(item.getComponent().getFactory().getIcon().getImage()));

                    }

                }

            }

        });

        listSelectionModel = selectedLst.getSelectionModel();
        listSelectionModel.setSelectionMode(SelectionMode.SINGLE);
        listSelectionModel.selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> {
                    moveUpBtn.setDisable(listSelectionModel.selectedIndexProperty().getValue()==0);
                    moveDownBtn.setDisable(listSelectionModel.selectedIndexProperty().getValue()==selectedLst.getItems().size()-1);
                }
        );


        addBtn.textProperty().bind(LC.createStringBinding("selectionAdd"));
        addBtn.setOnAction(event -> {
            ArrayList<SelectionItem> array = getSelectedItems();
            if(array != null && !array.isEmpty()){
                 selectedLst.getItems().addAll(array);
            }
        });

        changRadixBtn.textProperty().bind(LC.createStringBinding("selectionChangeBase"));
        changRadixBtn.setOnAction(event -> {
            if(listSelectionModel.getSelectedItem() != null) {
                int radix = listSelectionModel.getSelectedItem().getRadix();
                switch (radix) {
                    case 2:
                        listSelectionModel.getSelectedItem().setRadix(10);
                        break;
                    case 10:
                        listSelectionModel.getSelectedItem().setRadix(16);
                        break;
                    default:
                        listSelectionModel.getSelectedItem().setRadix(2);
                }
                selectedLst.refresh();
            }
        });

        moveUpBtn.textProperty().bind(LC.createStringBinding("selectionMoveUp"));
        moveUpBtn.setOnAction(event -> {
            doMove(-1);
        });

        moveDownBtn.textProperty().bind(LC.createStringBinding("selectionMoveDown"));
        moveDownBtn.setOnAction(event -> {
            doMove(1);
        });

        removeBtn.textProperty().bind(LC.createStringBinding("selectionRemove"));
        removeBtn.setOnAction(event -> selectedLst.getItems().removeAll(listSelectionModel.getSelectedItem()));

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

        int oldIndex = listSelectionModel.getSelectedIndex();
        int newIndex = oldIndex + delta;

        if (oldIndex >= 0 && newIndex >= 0 && newIndex < selectedLst.getItems().size()) {
            SelectionItem buff = selectedLst.getItems().get(newIndex);
            selectedLst.getItems().set(newIndex, listSelectionModel.getSelectedItem());
            selectedLst.getItems().set(oldIndex, buff);
        }

        listSelectionModel.select(newIndex);

    }

    private ArrayList<SelectionItem> getSelectedItems(){

        ArrayList<SelectionItem> ret = new ArrayList<>();

        if(treeSelectionModel.getSelectedItems().isEmpty())return new ArrayList<>();

        for (TreeItem<Object> node: treeSelectionModel.getSelectedItems()) {

            ComponentNode n = null;
            Object opt = null;
            if (node instanceof OptionNode) {
                OptionNode o = (OptionNode) node;
                n = o.parent;
                opt = o.option;
            } else if (node instanceof ComponentNode) {
                n = (ComponentNode) node;
                if (n.opts != null) n = null;
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

                ret.add(new SelectionItem(curModel, nPath, n.comp, opt));

            }

        }

        return ret.size() == 0 ? null : ret;

    }



    private final SimpleDoubleProperty currCursorPos = new SimpleDoubleProperty(0);

    private GraphicsContext gc;
    private double width, height;
    private double[] transform;

    NumberFormat formatter = new DecimalFormat("#.####");
    private static final Font TIMESTEPFONT = Font.font("serif", FontWeight.THIN, FontPosture.REGULAR, 10);
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
    TableColumn<TimelineTableModel, String> nameColumn;
    double hiddenColumnWidth;
    double hiddenColumnHeight = 21;

    TableColumn<TimelineTableModel, Value> valueColumn;

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

        splitPane.setDividerPositions(0.05);

        canvasAnchor.widthProperty().addListener((observable, oldValue, newValue) -> {
            //width = canvasAnchor.getWidth()+hiddenColumnWidth;
            //timelineCnvs.setWidth(width);
            updateTimeline();
        });

        timelineTblvw.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        nameColumn = new TableColumn<>("");
        nameColumn.textProperty().bind(LC.createStringBinding("componentTitle"));
        nameColumn.setSortable(false);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("Title"));
        nameColumn.setCellValueFactory(param -> param.getValue().getTitle());
        nameColumn.setCellFactory(param -> {
            TableCell<TimelineTableModel, String> cell = new TableCell<TimelineTableModel, String>() {

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
                currSelectedRow = cell.getTableRow().getIndex();
                updateTimeline();
            });
            cell.setOnMouseExited(event -> {
                currSelectedRow = -1;
                updateTimeline();
            });

            return cell;

        });

        valueColumn = new TableColumn<>("");
        valueColumn.textProperty().bind(LC.createStringBinding("valueTitle"));
        valueColumn.setSortable(false);
        valueColumn.setCellValueFactory(param -> param.getValue().getValueAt());
        valueColumn.setCellFactory(param -> {
            TableCell<TimelineTableModel, Value> cell = new TableCell<TimelineTableModel, Value>() {

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

            cell.setPrefHeight(spaceY);
            cell.setAlignment(Pos.CENTER);
            cell.setOnMouseEntered(event -> {
                currSelectedRow = cell.getTableRow().getIndex();
                updateTimeline();
            });
            cell.setOnMouseExited(event -> {
                currSelectedRow = -1;
                updateTimeline();
            });

            return cell;
        });

        timelineTblvw.getColumns().add(nameColumn);
        timelineTblvw.getColumns().add(valueColumn);

        restartCanvas();
        gc = timelineCnvs.getGraphicsContext2D();
        gc.setFont(TIMESTEPFONT);
        w = gc.getLineWidth()/2;
        transform = new double[6];
        transform[0] = transform[3] = 1;
        transform[1] = transform[2] = transform[4] = transform[5] = 0;
        gc.setTransform(transform[0], transform[1], transform[2],
                transform[3], transform[4], transform[5]
        );

        timelineCnvs.setOnMousePressed(event -> {

            currCursorPos.set(Math.max(0, Math.min(width-hiddenColumnWidth-1, inverseTransformX(event.getX())-hiddenColumnWidth)));

            timelineTblvw.refresh();
            updateTimeline();

        });

        timelineCnvs.setOnMouseDragged(event -> {

            currCursorPos.set(Math.max(0, Math.min(width-hiddenColumnWidth-1, inverseTransformX(event.getX())-hiddenColumnWidth)));

            timelineTblvw.refresh();
            updateTimeline();

        });

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

    private class TimelineTableModel{

        private SelectionItem comp;
        private String title;
        private ArrayList<Value> values = new ArrayList<>();

        public TimelineTableModel(SelectionItem comp){
            this.comp = comp;
        }

        public TimelineTableModel(String title, ArrayList<Value> values){
            this.title = title;
            this.values = values;
        }

        public SimpleStringProperty getTitle(){
            return comp == null ? new SimpleStringProperty(title):new SimpleStringProperty(comp.toShortString());
        }

        public SimpleObjectProperty<Value> getValueAt(){
            int index = (int) Math.floor(currCursorPos.getValue() /spaceX);
            return values.isEmpty() || index >= values.size() ?  new SimpleObjectProperty<>(Value.NIL) :
                    new SimpleObjectProperty<>(values.get(index));
        }

        public ArrayList<Value> getValues(){
            return values;
        }

        public void addValue(Value val){
            values.add(val);
        }

    }

    private void updateTimelineTable(ObservableList<SelectionItem> items){

        timelineTblvw.getItems().clear();
        double bufflen = 0;

        for (SelectionItem item: items) {
            TimelineTableModel model = new TimelineTableModel(item);
            timelineTblvw.getItems().add(model);
            if(fm.computeStringWidth(item.toShortString())>bufflen)bufflen = fm.computeStringWidth(item.toShortString());
        }

        hiddenColumnWidth = bufflen+5;
        timelineCnvs.setWidth(hiddenColumnWidth);
        height = (timelineTblvw.getItems().size())*spaceY+hiddenColumnHeight;
        timelineCnvs.setHeight(height);
        updateTimeline();

    }

    private void updateTimelineTable(String[] titles, ArrayList<ArrayList<Value>> values){

        timelineTblvw.getItems().clear();
        double bufflen = 0;

        int i =0;
        for (String title: titles) {
            title = title.trim();
            TimelineTableModel model = new TimelineTableModel(title,values.get(i));
            timelineTblvw.getItems().add(model);
            if(fm.computeStringWidth(title)>bufflen)bufflen = fm.computeStringWidth(title);
            i++;
        }

        hiddenColumnWidth = bufflen+5;
        timelineCnvs.setWidth(hiddenColumnWidth+values.get(0).size()*spaceX);
        height = (timelineTblvw.getItems().size())*spaceY+hiddenColumnHeight;
        timelineCnvs.setHeight(height);
        updateTimeline();

    }

    private void updateTimelineData(Value[] values){

        int i = 0;
        int size = 0;
        for (TimelineTableModel model : timelineTblvw.getItems()){
            model.addValue(values[i]);
            size = model.getValues().size();
            i++;
        }

        currCursorPos.setValue(hiddenColumnWidth + size * spaceX - 1);

        timelineTblvw.refresh();
        updateTimeline();

    }

    private void updateTimeline(){

        clearRect40K();

        gc.setTransform(transform[0], transform[1], transform[2],
                transform[3], transform[4], transform[5]
        );

        timelineCnvs.setLayoutX(-hiddenColumnWidth);

        gc.setStroke(LINE);
        gc.setFill(LINE);

        //hidden table
        gc.strokeLine(hiddenColumnWidth, 0, hiddenColumnWidth, height);
        gc.strokeLine(0, 20, hiddenColumnWidth, 20);
        gc.fillText(LC.get("componentTitle"), (hiddenColumnWidth - fm.computeStringWidth(LC.get("componentTitle"))) / 2, 15);


        //time
        gc.strokeLine(0, 5, width, 5);
        int counter = 0;
        String prefix = "";
        if (curSimulator.getTickFrequency() > 1000) prefix = "m";

        for (double i = hiddenColumnWidth + 2 * spaceX; i < width; i += 2 * spaceX) {
            gc.strokeLine(i, 5, i, 15);
            String text = formatter.format(1 / curSimulator.getTickFrequency() * counter) + " " + prefix + "s";
            gc.fillText(text, i - fm.computeStringWidth(text) - 2, 15);
            counter++;
        }

        double currRowY = 70;

        //comp data
        for (TimelineTableModel model : timelineTblvw.getItems()) {

            ArrayList<Value> vals = model.getValues();
            int curValueLength = 0;

            double currX = hiddenColumnWidth;
            double currY = currRowY;

            if(timelineTblvw.getItems().indexOf(model)==currSelectedRow){
                gc.setLineWidth(2);
            }else{
                gc.setLineWidth(1);
            }

            if (vals != null && !vals.isEmpty()) {

                //ze_ reference :extend extend extend(canvas)
                if (timelineCnvs.getWidth() - hiddenColumnWidth - vals.size() * spaceX < spaceX) {
                    width = hiddenColumnWidth + vals.size() * spaceX;
                    timelineCnvs.setWidth(width);
                }

                for (int i = 0; i < vals.size(); i++) {

                    gc.setStroke(LINE);
                    gc.setFill(LINE);

                    //if start value is true
                    if (i == 0 && vals.get(0).equals(Value.TRUE)) currY -= yAdjust;

                    if (i > 0) {

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

                        gc.setStroke(LINEFILL);
                        gc.setFill(LINEFILL);

                        gc.fillRect(currX + 2 * w, currY + w, spaceX, yAdjust);

                        gc.setStroke(LINE);
                        gc.setFill(LINE);

                    } else if (vals.get(i).equals(Value.UNKNOWN)) {
                        //Blue grid

                        gc.setStroke(Value.UNKNOWN_COLOR);
                        gc.setFill(Value.UNKNOWN_COLOR);

                        gc.strokeLine(currX, currRowY-yAdjust,currX + spaceX, currRowY-(yAdjust/2));
                        gc.strokeLine(currX, currRowY-(yAdjust/2),currX + spaceX,currRowY-yAdjust);
                        gc.strokeLine(currX, currRowY-(yAdjust/2),currX + spaceX, currRowY);
                        gc.strokeLine(currX, currRowY,currX + spaceX, currRowY-(yAdjust/2));

                        gc.setStroke(LINE);
                        gc.setFill(LINE);

                    }else if(!vals.get(i).equals(Value.FALSE)){

                        //hexagon
                        gc.setStroke(LONGVALUE);
                        gc.setFill(LONGVALUE);

                        if(curValueLength == 0){

                            gc.strokeLine(currX+w, currRowY-yAdjust/2,currX + spaceX, currRowY-0.75*yAdjust);
                            gc.strokeLine(currX+w, currRowY-yAdjust/2,currX + spaceX, currRowY-0.25*yAdjust);
                            curValueLength++;

                        }else {

                            if (i != vals.size()-1 && vals.get(i+1).equals(vals.get(i))) {

                                gc.strokeLine(currX+w, currRowY - 0.75 * yAdjust, currX + spaceX, currRowY - 0.75 * yAdjust);
                                gc.strokeLine(currX+w, currRowY - 0.25 * yAdjust, currX + spaceX, currRowY - 0.25 * yAdjust);
                                curValueLength++;

                            }else{

                                gc.strokeLine(currX+w, currRowY-0.75*yAdjust,currX + spaceX, currRowY-yAdjust/2);
                                gc.strokeLine(currX+w, currRowY-0.25*yAdjust,currX + spaceX, currRowY-yAdjust/2);

                                gc.setStroke(LINE);
                                gc.setFill(LINE);

                                String text = vals.get(i).toHexString()+"h";
                                gc.fillText(text, currX-((curValueLength-1)*spaceX+fm.computeStringWidth(text))/2, currY-(yAdjust/2));

                                curValueLength = 0;

                            }

                        }

                        gc.setStroke(LINE);
                        gc.setFill(LINE);

                    }

                    gc.strokeLine(currX + gc.getLineWidth(), currY, currX + spaceX, currY);
                    currX += spaceX;

                }

            }

            gc.strokeLine(0, currRowY, hiddenColumnWidth, currRowY);
            gc.fillText(model.getTitle().getValue(), 0, currRowY - spaceY / 2);

            currRowY += spaceY;
        }

        gc.setFill(CURRPOS);
        gc.setStroke(CURRPOS);
        gc.strokeLine(currCursorPos.doubleValue() + hiddenColumnWidth - 1, 0, currCursorPos.doubleValue() + hiddenColumnWidth - 1, height);

        gc.setLineWidth(1);

    }

    // convert screen coordinates to grid coordinates by inverting circuit transform
    private double inverseTransformX(double x) {
        return ((x-transform[4])/transform[0]);
    }

    private double inverseTransformY(double y) {
        return (y-transform[5])/transform[3];
    }

    private void clearRect40K() {

        gc.setFill(BACKGROUND);
        gc.fillRect(-1,-1,width+1,height+1);

    }

    private void restartCanvas(){
        currCursorPos.setValue(0);
        timelineCnvs.setWidth(0);
        timelineCnvs.setHeight(0);
    }

    private void exportImage(){

        WritableImage writableImage = new WritableImage((int)(timelineCnvs.getWidth()*pixelScale),
                (int)(timelineCnvs.getHeight()*pixelScale));
        SnapshotParameters spa = new SnapshotParameters();
        spa.setTransform(Transform.scale(pixelScale, pixelScale));
        ImageView img = new ImageView(timelineCnvs.snapshot(spa, writableImage));

        File dest;
        FileSelector fileSelector = new FileSelector(stage);
        dest = fileSelector.showSaveDialog(LogisimFX.newgui.ExportImageFrame.LC.get("exportImageFileSelect"));

        File where;
        if (dest.isDirectory()) {
            where = new File(dest, proj.getLogisimFile().getName()+".png");
        } else {
            String newName = dest.getName() + ".png";
            where = new File(dest.getParentFile(), newName);
        }

        try {

            BufferedImage bImage = SwingFXUtils.fromFXImage(img.getImage(), null);
            ImageIO.write(bImage, "PNG", where);

        } catch (Exception e) {
            DialogManager.CreateErrorDialog(LogisimFX.newgui.ExportImageFrame.LC.get("couldNotCreateFile"), LogisimFX.newgui.ExportImageFrame.LC.get("couldNotCreateFile"));
        }

    }



    private void initTableTab(){

        tableTab.textProperty().bind(LC.createStringBinding("tableTab"));

        logTblvw.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadFileBtn.textProperty().bind(LC.createStringBinding("openButton"));
        loadFileBtn.setOnAction(event -> importLog());

        exportFileBtn.textProperty().bind(LC.createStringBinding("saveButton"));
        exportFileBtn.setOnAction(event -> exportLog());

    }

    private void updateColumnCount(ObservableList<SelectionItem> items){

        logTblvw.getColumns().clear();

        int i = 0;
        for (SelectionItem item: items) {

            TableColumn<LogLine, Value> column = new TableColumn<>(item.toShortString());
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

    }

    private void updateColumnCount(String[] titles, ArrayList<LogLine> logLines){

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

        public Value[] getValues(){
            return values;
        }

    }

    private void exportLog(){

        PrintWriter writer;

        FileSelector fileSelector = new FileSelector(stage);

        File file = fileSelector.showSaveDialog(LC.get("fileHelp"));

        if (file.exists() && (!file.canWrite() || file.isDirectory())) {

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
            buff.append(sel.get(i).toShortString());
        }
        writer.println(buff.toString());

        for (Value[] values: curModel.getValues()) {

            StringBuilder buf = new StringBuilder();

            for (int i = 0; i < values.length; i++) {
                if (i > 0) buf.append("\t");
                if (values[i] != null) {
                    int radix = sel.get(i).getRadix();
                    buf.append(values[i].toDisplayString(radix));
                }
            }
            writer.println(buf.toString());

        }

        writer.flush();
        writer.close();

    }

    private void importLog(){

        FileSelector fileSelector = new FileSelector(stage);

        File file = fileSelector.showOpenDialog(LC.get("fileHelp"));

        //read
        StringBuilder builder = new StringBuilder();

        try {

            FileReader reader = new FileReader(file);

            char[] buf = new char[1024];
            int numRead;

            while ((numRead=reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                builder.append(readData);
            }

            reader.close();

        }
        catch(IOException e) {
            System.out.println("Cant read File " + e.getMessage());
        }

        //Parse
        String data = builder.toString();

        String[] lines = data.trim().split("\n");
        ArrayList<LogLine> logLines = new ArrayList<>();

        ArrayList<ArrayList<Value>> values = new ArrayList<>();
        for(int m = 0; m < lines[0].split("\t").length; m++){ values.add(new ArrayList<>()); }

        for(int j = 1; j < lines.length; j++){

            String[] buff = lines[j].split("\t");

            Value[] vals = new Value[buff.length];

            for(int h = 0; h < buff.length; h++){

                Value val = null;
                if(buff[h].trim().equals("0"))val = Value.FALSE;
                else if(buff[h].trim().equals("1"))val = Value.TRUE;

                vals[h] = val;
                values.get(h).add(val);

            }

            LogLine l = new LogLine(vals);
            logLines.add(l);
        }

        String[] titles =  lines[0].split("\t");

        restartCanvas();
        updateColumnCount(titles, logLines);
        updateTimelineTable(titles, values);

    }



    public void setCircuit(Circuit circ){

        //setText(StringUtil.format(Strings.get("logFrameMenuItem"), title));
        stage.titleProperty().bind(LC.createStringBinding("logFrameTitle"));

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
        curModel.bindComponentsList(selectedLst.getItems());

        if (curSimulator != null) curSimulator.addSimulatorListener(myListener);
        computeTitle(curModel);

    }



    @Override
    public void onClose() {
        System.out.println("Circ log closed");
    }

}
