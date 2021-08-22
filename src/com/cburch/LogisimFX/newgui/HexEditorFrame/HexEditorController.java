package com.cburch.LogisimFX.newgui.HexEditorFrame;

import com.cburch.LogisimFX.FileSelector;
import com.cburch.LogisimFX.newgui.AbstractController;
import com.cburch.LogisimFX.newgui.DialogManager;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.LogisimFX.std.memory.MemContents;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class HexEditorController extends AbstractController {

    private Stage stage;

    @FXML
    private TableView<HexEditorDataModel> hexTableVw;

    @FXML
    private Button loadBtn;

    @FXML
    private Button saveBtn;

    private MemContents memContents;
    private HexModel hexModel;

    private int maxLength;
    private int maxAdrLen;
    private int maxCols;

    private int cols = 8;

    private double COLUMN_SIZE = 80;

    private Thread thread;

    private TableColumn<HexEditorDataModel, String> adrColumn;
    private ArrayList<TableColumn<HexEditorDataModel, Long>> dataColumns = new ArrayList<>();

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

    }

    @Override
    public void postInitialization(Stage s, Project project) {

        stage = s;

        stage.setWidth(800);
        stage.setHeight(600);

        stage.titleProperty().bind(LC.createStringBinding("hexFrameTitle"));

        stage.widthProperty().addListener((observable, oldValue, newValue) -> {

            if (stage.getWidth() > 17 * COLUMN_SIZE && cols != 16 && memContents.getWidth() > 3 && maxCols > 8) {
                cols = 16;
                calculateTableColumns(cols);
            } else if (stage.getWidth() < 17 * COLUMN_SIZE && stage.getWidth() > 9 * COLUMN_SIZE && cols != 8 && memContents.getWidth() > 2 && maxCols > 4) {
                cols = 8;
                calculateTableColumns(cols);
            } else if (stage.getWidth() < 9 * COLUMN_SIZE && cols != 4) {
                cols = 4;
                calculateTableColumns(cols);
            }

        });



        adrColumn = new TableColumn<>("Address");
        adrColumn.setCellValueFactory(data -> data.getValue().getAddress());

        adrColumn.setCellFactory(param -> {

            TableCell<HexEditorDataModel, String> cell = new TableCell<HexEditorDataModel, String>() {

                @Override
                protected void updateItem(String item, boolean empty) {

                    if (item == getItem()) return;

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


            cell.setAlignment(Pos.CENTER);

            return cell;

        });


        adrColumn.setEditable(false);
        adrColumn.setSortable(false);
        //adrColumn.setResizable(false);

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
                            super.startEdit();
                            this.requestFocus();
                        }
                    }

                };



                cell.setOnMouseClicked(event -> cell.requestFocus());

                cell.setOnKeyPressed(event -> {

                    if(event.getText().matches("^[0-9A-Fa-f]")) {

                        StringBuilder buff = new StringBuilder(cell.getText().substring(1)+event.getText());

                        int diff = buff.length()*4 - memContents.getValueWidth();

                        if(diff != 0 && buff.toString().toCharArray()[0] != '0'){

                            StringBuilder fByte = new StringBuilder(
                                    Integer.toBinaryString(Integer.parseInt(Character.toString(buff.toString().toCharArray()[0]),16))
                            );

                            System.out.println(fByte.toString()+" " +fByte.toString().length());
                            int l = 4-fByte.toString().length();
                            for(int n = 0; n < l; n++){
                                fByte.insert(0,'0');
                            }
                            System.out.println(fByte.toString());

                            StringBuilder zero = new StringBuilder();
                            for(int n = 0 ; n < diff; n++){
                                zero.append("0");
                            }

                            fByte.replace(0,diff,zero.toString());

                            String newByte = Integer.toHexString(Integer.parseInt(fByte.toString(),2));

                            buff.replace(0,1, newByte);

                        }

                        cell.setText(String.format("%0"+maxLength+"X",Long.parseLong(buff.toString(),16)));
                        memContents.set(
                                hexTableVw.getItems().get(cell.getTableRow().getIndex()).getAdr(j),
                                (int)Long.parseLong(buff.toString(),16));

                        //hexTableVw.refresh();

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


        TableView.TableViewSelectionModel<HexEditorDataModel> selectionModel = hexTableVw.getSelectionModel();
        selectionModel.setCellSelectionEnabled(true);
        selectionModel.setSelectionMode(SelectionMode.MULTIPLE);

        hexTableVw.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        hexTableVw.getFocusModel().focusedCellProperty().addListener((observable, oldValue, newValue) ->
                hexTableVw.edit(hexTableVw.getFocusModel().getFocusedCell().getRow(),
                hexTableVw.getFocusModel().getFocusedCell().getTableColumn())
        );

    }

    public void openHex(MemContents memContents) {

        this.memContents = memContents;
        this.hexModel = memContents;

        if(memContents.getLogLength() < 4){
            maxCols = (int)Math.pow(2,memContents.getLogLength());
        }else{
            maxCols = 16;
        }
        cols = 8;

        System.out.println(memContents.getWidth());
        System.out.println(memContents.getValueWidth());
        System.out.println(memContents.getLogLength());

        calculateTableColumns(cols);

    }

    private void calculateTableColumns(int columnCount) {

        System.out.println("fucked up");

        //todo gc manual update
        if(thread != null && thread.getState() != Thread.State.TERMINATED) {
            thread.interrupt();
            hexTableVw.getItems().clear();
        }

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

        //long maxValue = (long)Math.pow(2, memContents.getWidth())-1;
        //String maxValueHex = Long.toHexString(maxValue);

        //System.out.println("max length: "+maxLength);
        //System.out.println("max val: "+maxValue+ " "+ maxValueHex);

        hexTableVw.getItems().clear();
        hexTableVw.getColumns().clear();
        hexTableVw.refresh();


        hexTableVw.getColumns().add(adrColumn);
        hexTableVw.getColumns().addAll(dataColumns.subList(0,columnCount));

        Runnable task = () -> {

            for (int i = 0; i <= hexModel.getLastOffset(); i += columnCount) {
                hexTableVw.getItems().add(new HexEditorDataModel(i));
            }

            hexTableVw.refresh();

        };

        thread = new Thread(task);
        thread.start();


        hexTableVw.refresh();

    }


    @Override
    public void onClose() {

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

        public SimpleStringProperty getAddress() {
            return new SimpleStringProperty(String.format("%0"+maxAdrLen+"X",adr));
        }

        public SimpleObjectProperty<Long> getMem(int i) {
            return new SimpleObjectProperty<>((long)hexModel.get(adr + i));
        }

    }

}
