package LogisimFX.newgui.HexEditorFrame;

import LogisimFX.FileSelector;
import LogisimFX.newgui.AbstractController;
import LogisimFX.newgui.DialogManager;
import LogisimFX.proj.Project;
import LogisimFX.std.memory.MemContents;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;


public class HexEditorController extends AbstractController {

    private Stage stage;

    @FXML
    private TableView<HexEditorDataModel> hexTableVw;

    @FXML
    private Button loadBtn;

    @FXML
    private Button saveBtn;

    @FXML
    private TextField scrollToTxtFld;

    private MemContents memContents;
    private HexModel hexModel;
    private Listener listener;

    private int maxLength;

    private int maxAdrValue;
    private int maxAdrLen;

    private int maxCols;
    private int columnCount = 8;

    private final double COLUMN_SIZE = 80;

    private ScrollBar scrollBar;
    private float scrollValue = -1;


    private int ADJUST = 100;
    private int realUpAdjust, realBottomAdjust;
    private int currAdr = 0;
    private TableView.TableViewSelectionModel<HexEditorDataModel> selectionModel;
    private TableColumn<HexEditorDataModel, Integer> adrColumn;
    private ArrayList<TableColumn<HexEditorDataModel, Long>> dataColumns = new ArrayList<>();
    
    private ObservableList<Long> copyBuffer;

    @FXML
    public void initialize() {

        loadBtn.textProperty().bind(LC.createStringBinding("openButton"));
        loadBtn.setOnAction(event -> {

            FileSelector fileSelector = new FileSelector(stage);

            File f = fileSelector.showOpenDialog(LC.get("hexOpenErrorTitle"));

            try {
                HexFile.open(hexModel,f);
            }catch(IOException e) {
                DialogManager.CreateErrorDialog(LC.get("hexOpenErrorTitle"),e.getMessage());
            }

        });

        saveBtn.textProperty().bind(LC.createStringBinding("saveButton"));
        saveBtn.setOnAction(event -> {

            FileSelector fileSelector = new FileSelector(stage);

            File f = fileSelector.showSaveDialog(LC.get("hexOpenErrorTitle"));

            try {
                HexFile.save(f, hexModel);
            }catch(IOException e) {
                DialogManager.CreateErrorDialog(LC.get("hexSaveErrorTitle"),e.getMessage());
            }

        });

        //scrollToTxtFld.promptTextProperty().bind(LC.createStringBinding("scrollTo"));

        final Pattern pattern = Pattern.compile("^[0-9A-Fa-f]*");
        TextFormatter<?> formatter = new TextFormatter<>(event -> {
            if (pattern.matcher(event.getControlNewText()).matches()) {
                return event; // allow this change to happen
            } else {
                return null; // prevent change
            }
        });

        scrollToTxtFld.setTextFormatter(formatter);

        scrollToTxtFld.textProperty().addListener((observable, oldValue, newValue) -> {

            if(!scrollToTxtFld.getText().equals("") && newValue.length() > 1 && oldValue.length() > 1 && oldValue.length() < newValue.length()+1) {
                StringBuilder buff = new StringBuilder(newValue.substring(1));
                scrollToTxtFld.setText(String.format("%0" + maxAdrLen + "X", Integer.parseInt(newValue.substring(1), 16)));
                return;
            }else{
                scrollToTxtFld.setText(String.format("%0" + maxAdrLen + "X", Integer.parseInt(newValue, 16)));
            }
            scrollToTxtFld.positionCaret(scrollToTxtFld.getText().length());
        });

        scrollToTxtFld.addEventFilter(KeyEvent.ANY, keyEvent -> scrollToTxtFld.positionCaret(scrollToTxtFld.getText().length()));

        scrollToTxtFld.setOnAction(event -> scrollTo());

    }

    @Override
    public void postInitialization(Stage s, Project project) {

        stage = s;

        stage.setWidth(800);
        stage.setHeight(600);

        stage.titleProperty().bind(LC.createStringBinding("hexFrameTitle"));

        //Define columncount model based on window width
        stage.widthProperty().addListener((observable, oldValue, newValue) -> {

            if (stage.getWidth() > 17 * COLUMN_SIZE && columnCount != 16 && memContents.getWidth() > 3 && maxCols > 8) {
                columnCount = 16;
                calculateTableColumns();
            } else if (stage.getWidth() < 17 * COLUMN_SIZE && stage.getWidth() > 9 * COLUMN_SIZE && columnCount != 8 && memContents.getWidth() > 2 && maxCols > 4) {
                columnCount = 8;
                calculateTableColumns();
            } else if (stage.getWidth() < 9 * COLUMN_SIZE && columnCount != 4) {
                columnCount = 4;
                calculateTableColumns();
            }

        });

        listener = new Listener();

        selectionModel = hexTableVw.getSelectionModel();
        selectionModel.setCellSelectionEnabled(true);
        selectionModel.setSelectionMode(SelectionMode.MULTIPLE);

        createColumns();

        setEvents();

    }

    private void createColumns(){

        hexTableVw.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        //Define adr column
        adrColumn = new TableColumn<>("Address");
        adrColumn.setCellValueFactory(new PropertyValueFactory<>("Address"));

        adrColumn.setCellFactory(param -> {

            TableCell<HexEditorDataModel, Integer> cell = new TableCell<HexEditorDataModel, Integer>() {

                @Override
                protected void updateItem(Integer item, boolean empty) {

                    if (item == getItem()) return;

                    super.updateItem(item, empty);

                    if (item == null) {
                        super.setText(null);
                        super.setGraphic(null);
                    } else {
                        super.setText(String.format("%0"+maxAdrLen+"X",item));
                        super.setGraphic(null);
                    }

                }

            };


            cell.setAlignment(Pos.CENTER);

            return cell;

        });

        adrColumn.setEditable(false);
        adrColumn.setSortable(false);

        //define data columns
        for (int i = 0; i < 16; i++) {

            int j = i;

            TableColumn<HexEditorDataModel, Long> column = new TableColumn<>(String.format("%X",j));
            column.setCellValueFactory(data -> data.getValue().getMem(j));
            column.setCellFactory(param -> {

                TableCell<HexEditorDataModel, Long> cell = new TableCell<HexEditorDataModel, Long>() {

                    @Override
                    protected void updateItem(Long item, boolean empty) {

                        if (item == getItem()) return;

                        super.updateItem(item, empty);

                        if (item == null) {
                            super.setText(null);
                            super.setGraphic(null);
                        } else {
                            super.setText(String.format("%0"+maxLength+"X", item));
                            super.setGraphic(null);
                        }

                    }

                    @Override
                    public void startEdit() {
                        if (!isEmpty()) {
                            //abuse focus system
                            //see focusmodel listener
                            super.startEdit();
                            this.requestFocus();
                        }
                    }

                };


                //abuse focus system
                cell.setOnMouseClicked(event -> cell.requestFocus());

                cell.setOnKeyPressed(event -> {

                    if(event.getText().matches("^[0-9A-Fa-f]") && !event.isControlDown()) {

                        StringBuilder buff = new StringBuilder(cell.getText().substring(1)+event.getText());

                        //calculate different between normal data width and attr data width. We will loose accuracy in highest tetrad
                        int diff = buff.length()*4 - memContents.getValueWidth();

                        if(diff != 0 && buff.toString().toCharArray()[0] != '0'){

                            //get first hex digit from cell
                            StringBuilder fByte = new StringBuilder(
                                    Integer.toBinaryString(Integer.parseInt(Character.toString(buff.toString().toCharArray()[0]),16))
                            );

                            //calculate diff between tetrad and java binary representation (cause java 1 dec = 1 bin instead 0001)
                            System.out.println(fByte.toString()+" " +fByte.toString().length());
                            int l = 4-fByte.toString().length();
                            //add fucking zero's
                            for(int n = 0; n < l; n++){
                                fByte.insert(0,'0');
                            }

                            StringBuilder zero = new StringBuilder();
                            for(int n = 0 ; n < diff; n++){
                                zero.append("0");
                            }

                            //loose accuracy
                            fByte.replace(0,diff,zero.toString());

                            String newByte = Integer.toHexString(Integer.parseInt(fByte.toString(),2));

                            buff.replace(0,1, newByte);

                        }

                        //update data in hexmodel
                        cell.setText(String.format("%0"+maxLength+"X",Long.parseLong(buff.toString(),16)));
                        memContents.set(
                                hexTableVw.getItems().get(cell.getTableRow().getIndex()).getAdr(j),
                                (int)Long.parseLong(buff.toString(),16));

                        event.consume();

                    }

                });

                cell.setAlignment(Pos.CENTER);

                return cell;

            });

            //column.setResizable(false);
            column.setSortable(false);


            dataColumns.add(column);

        }

        hexTableVw.getColumns().add(adrColumn);
        hexTableVw.getColumns().addAll(dataColumns);

    }

    private void setEvents(){

        hexTableVw.setOnKeyPressed(event -> {

            if(event.getCode() == KeyCode.DELETE){

                for(int i = 0; i< selectionModel.getSelectedCells().size(); i++){

                    TablePosition pos = selectionModel.getSelectedCells().get(i);

                    if(pos.getColumn() != 0) {
                        memContents.set(
                                hexTableVw.getItems().get(selectionModel.getSelectedIndex()).getAdr(pos.getColumn() - 1),
                                0
                        );
                    }

                }

                hexTableVw.refresh();

            }

            if(event.getCode() == KeyCode.C && event.isControlDown()){

                copyBuffer = FXCollections.observableArrayList();

                for (TablePosition pos: selectionModel.getSelectedCells()) {

                    TableColumn tableColumn = hexTableVw.getColumns().get(pos.getColumn());
                    SimpleObjectProperty<Long> cellItem = (SimpleObjectProperty<Long>)tableColumn.getCellObservableValue(pos.getRow());
                    if(pos.getColumn() != 0) {copyBuffer.add(cellItem.getValue());}

                }

            }

            if(event.getCode() == KeyCode.V && event.isControlDown()){

                int n = 0;

                for(int i = 0; i< selectionModel.getSelectedCells().size(); i++){

                    TablePosition pos = selectionModel.getSelectedCells().get(i);

                    if(pos.getColumn() != 0 && !copyBuffer.isEmpty()) {
                        memContents.set(
                                hexTableVw.getItems().get(selectionModel.getSelectedIndex()).
                                        getAdr(pos.getColumn() - 1),
                                copyBuffer.get(n).intValue()
                        );

                        n++;
                        if (n == copyBuffer.size()) n = 0;//cycle paste
                    }

                }
                hexTableVw.refresh();

            }

            if(event.getCode() == KeyCode.X && event.isControlDown()){

                copyBuffer = FXCollections.observableArrayList();

                for(int i = 0; i< selectionModel.getSelectedCells().size(); i++){

                    TablePosition pos = selectionModel.getSelectedCells().get(i);
                    TableColumn tableColumn = hexTableVw.getColumns().get(pos.getColumn());
                    SimpleObjectProperty<Long> cellItem = (SimpleObjectProperty<Long>)tableColumn.getCellObservableValue(pos.getRow());

                    if(pos.getColumn() != 0) {
                        copyBuffer.add(cellItem.getValue());
                        memContents.set(
                                hexTableVw.getItems().get(selectionModel.getSelectedIndex()).getAdr(pos.getColumn()-1),
                                0
                        );
                    }

                }

                hexTableVw.refresh();

            }

        });

        //Define focusmodel listener for custom cell edit and selection
        //Request focus abuse by startedit override
        hexTableVw.getFocusModel().focusedCellProperty().addListener((observable, oldValue, newValue) ->
                hexTableVw.edit(hexTableVw.getFocusModel().getFocusedCell().getRow(),
                        hexTableVw.getFocusModel().getFocusedCell().getTableColumn())
        );

    }

    public void openHex(MemContents memContents) {

        this.memContents = memContents;
        this.hexModel = memContents;

        if (hexModel != null) hexModel.addHexModelListener(listener);

        //Define max column size depend on address bus width
        if(memContents.getLogLength() < 4){
            maxCols = (int)Math.pow(2,memContents.getLogLength());
        }else{
            maxCols = 16;
        }

        //set default columncount model
        columnCount = 8;

        scrollToTxtFld.promptTextProperty().bind(LC.createStringBinding("scrollTo"));

        //Define first data row for correct tableviewext class work
        hexTableVw.getItems().add(new HexEditorDataModel(0));

        calculateTableColumns();

        //Find tableview scrollbar and add listener for rows scroll
        scrollBar = (ScrollBar) hexTableVw.lookup(".scroll-bar");

        if(scrollBar!=null){

            scrollBar.valueProperty().addListener(observable -> {

                if (scrollBar.getValue() == scrollBar.getMax() && hexTableVw.getItems().size() < maxAdrValue / columnCount) {

                    calculateChunk(1);
                    float adjust = (float)realBottomAdjust;
                    float size = (float)hexTableVw.getItems().size();

                    scrollValue = (size - adjust) / size;
                    scrollBar.setValue(scrollValue);
                    hexTableVw.scrollTo(hexTableVw.getItems().size()-realBottomAdjust);
                }

                if (scrollBar.getValue() == scrollBar.getMin() && currAdr != 0 && !hexTableVw.getItems().isEmpty()) {
                    calculateChunk(-1);
                    float adjust = (float)realUpAdjust;
                    float size = (float)hexTableVw.getItems().size();
                    scrollValue = adjust / size;
                    hexTableVw.scrollTo(realUpAdjust);
                    scrollBar.setValue(scrollValue);
                }

            });

        }

    }

    private void calculateTableColumns() {

        if (memContents.getLogLength() % 4 == 0) {
            maxAdrLen = memContents.getLogLength() / 4;
        } else {
            maxAdrLen = memContents.getLogLength() / 4 + 1;
        }

        if (memContents.getWidth() % 4 == 0) {
            maxLength = memContents.getWidth() / 4;
        } else {
            maxLength = memContents.getWidth() / 4 + 1;
        }

        maxAdrValue = (int)Math.pow(2, memContents.getLogLength())-1;

        for(int n = 0; n < 16; n++){
            hexTableVw.getColumns().get(n+1).setVisible(n < columnCount);
        }

        hexTableVw.getItems().clear();

        calculateChunk(1);

    }

    private void calculateChunk(int direction){

        //note that first and last visible index differs from the real ones value by -1
        //so, if first visible index is 1, the real one is 2
        //idk not my script


        //Пошло всё нахуй я хайлоад программист

        ObservableList<HexEditorDataModel> list = FXCollections.observableArrayList();

        list.addAll(hexTableVw.getItems());

        int count = 0;

        if(direction == 1){

            if(!hexTableVw.getItems().isEmpty() && hexTableVw.getItems().size() != 1)
                currAdr = hexTableVw.getItems().get(hexTableVw.getItems().size()-1).getAddress();
            int adr = currAdr;

            while (count < ADJUST) {

                if (adr + columnCount * count < maxAdrValue)list.add(new HexEditorDataModel(adr + columnCount * count));
                else { break;}

                count++;

            }

            realBottomAdjust = count;

            if(list.size()>1100)list = FXCollections.observableArrayList(list.subList(ADJUST,list.size()));

        }else{

            if(!hexTableVw.getItems().isEmpty() && hexTableVw.getItems().size() != 1)
                currAdr = hexTableVw.getItems().get(0).getAddress();
            int adr = currAdr;

            while (count < ADJUST) {

                if (adr - columnCount * count >= 0) list.add(0,
                        new HexEditorDataModel(adr - columnCount * count));
                else break;

                count++;

            }

            realUpAdjust = count;

            if(list.size()>1100)list = FXCollections.observableArrayList(list.subList(0,list.size()-ADJUST));

        }

        hexTableVw.setItems(list);
        //hexTableVw.refresh();
        System.out.println(hexTableVw.getItems().size());

    }

    private void scrollTo(){

        double adr = Integer.parseInt(scrollToTxtFld.getText(),16);
        double buff = Math.floor(adr / columnCount);
        buff = buff * columnCount;

        hexTableVw.getItems().clear();
        hexTableVw.refresh();

        ObservableList<HexEditorDataModel> list = FXCollections.observableArrayList();
        int count = 0;
        while (count < ADJUST) {

            if (adr + columnCount * count < maxAdrValue)list.add(new HexEditorDataModel((int)buff + columnCount * count));
            else { break;}

            count++;

        }

        currAdr = (int)buff;
        System.out.println(String.format("%0"+maxAdrLen+"X",currAdr));
        hexTableVw.getItems().clear();
        hexTableVw.setItems(list);
        hexTableVw.scrollTo(0);
        hexTableVw.getSelectionModel().select(0,dataColumns.get((int)(adr-buff)));

        scrollBar.setValue(0.0001);

        hexTableVw.refresh();



    }


    @Override
    public void onClose() {

        hexTableVw.setRowFactory(null);
        for (TableColumn column : this.hexTableVw.getColumns()) {
            column.setCellFactory(null);
            column.setCellValueFactory(null);
        }
        hexTableVw.getFocusModel().focus(null);
        hexTableVw.setOnMouseClicked(null);
        hexTableVw.setSelectionModel(null);
        hexTableVw.getColumns().clear();
        hexTableVw.setItems(FXCollections.observableArrayList());
        hexTableVw = null;

        hexModel.removeHexModelListener(listener);

        stage = null;

    }

    private class Listener implements HexModelListener {

        public void metainfoChanged(HexModel source) {
            if(hexTableVw != null)hexTableVw.refresh();
        }
        public void bytesChanged(HexModel source, long start, long numBytes, int[] oldValues) {
            if(!stage.isFocused())hexTableVw.refresh();
        }

    }

///////////////////////////////////

    public class HexEditorDataModel {

        private final int adr;

        public HexEditorDataModel(int adr) {
            this.adr = adr;
        }

        public long getAdr(int i){
            return adr+i;
        }

        public int getAddress() {
            return adr;
        }

        public String toString() {
            return String.format("%X",adr);
        }

        public SimpleObjectProperty<Long> getMem(int i) {
            return new SimpleObjectProperty<>((long)hexModel.get(adr + i));
        }

    }

}
