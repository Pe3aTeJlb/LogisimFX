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
import LogisimFX.newgui.HexEditorFrame.HexEditorController;
import LogisimFX.proj.Project;
import LogisimFX.proj.ProjectEvent;
import LogisimFX.proj.ProjectListener;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;
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
    private Button exportImageBtn;


    //TableTab

    @FXML
    private Tab tableTab;

    @FXML
    private TableView<LogLine> logTrvw;

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

            logTrvw.getItems().add(new LogLine(values));
            logTrvw.refresh();
            System.out.println("entry added");
            System.out.println(Arrays.toString(values));

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

            logTrvw.getItems().clear();
            updateColumnCount();

            //if(logger != null)logger.interrupt();
            logger = new LogThread(curModel);
            logger.start();

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

        int oldIndex = selectedLst.getEditingIndex();
        int newIndex = oldIndex + delta;

        if (oldIndex >= 0 && newIndex >= 0 && newIndex < selectedLst.getItems().size()) {
            SelectionItem buff = selectedLst.getItems().get(newIndex);
            selectedLst.getItems().set(newIndex, listSelectionModel.getSelectedItem());
            selectedLst.getItems().set(oldIndex, buff);
        }

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



    private void initTimelineTab(){

        timelineTab.textProperty().bind(LC.createStringBinding("timelineTab"));

    }






    private void initTableTab(){

        tableTab.textProperty().bind(LC.createStringBinding("tableTab"));

        logTrvw.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadFileBtn.textProperty().bind(LC.createStringBinding("openButton"));
        loadFileBtn.setOnAction(event -> importLog());

        exportFileBtn.textProperty().bind(LC.createStringBinding("saveButton"));
        exportFileBtn.setOnAction(event -> exportLog());

    }

    private void updateColumnCount(){

        logTrvw.getColumns().clear();

        int i = 0;
        for (SelectionItem item: selectedLst.getItems()) {

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

            logTrvw.getColumns().add(column);
            i++;
        }

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

        String data = builder.toString();

        String[] lines = data.trim().split("\n");
        ArrayList<LogLine> logLines = new ArrayList<>();

        for(int j = 1; j < lines.length; j++){

            System.out.println(lines[j]);

            String[] buff = lines[j].split("\t");
            System.out.println(Arrays.toString(buff));

            Value[] vals = new Value[buff.length];

            for(int h = 0; h < buff.length; h++){

                if(buff[h].trim().equals("0"))vals[h] = Value.FALSE;
                else if(buff[h].trim().equals("1"))vals[h] = Value.TRUE;

            }
            LogLine l = new LogLine(vals);
            logLines.add(l);
        }

        logTrvw.getColumns().clear();

        int i = 0;
        for (String s: lines[0].split("\t")) {

            TableColumn<LogLine, Value> column = new TableColumn<>(s.trim());
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

            logTrvw.getColumns().add(column);
            i++;

        }

        logTrvw.getItems().addAll(logLines);

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
