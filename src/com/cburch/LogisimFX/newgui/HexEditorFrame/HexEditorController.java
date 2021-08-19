package com.cburch.LogisimFX.newgui.HexEditorFrame;

import com.cburch.LogisimFX.newgui.AbstractController;
import com.cburch.LogisimFX.proj.Project;

import com.cburch.LogisimFX.std.memory.MemContents;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public class HexEditorController extends AbstractController {

    private Stage stage;

    @FXML
    private AnchorPane Root;

    @FXML
    private TableView<HexEditorDataModel> hexTableVw;

    @FXML
    private Button loadBtn;

    @FXML
    private Button saveBtn;

    private MemContents memContents;
    private HexModel hexModel;
    private HexEditor editor;

    private int adrLength, dataLength;
    private Formatter fmt;

    private int cols = 8;

    private double COLUMN_SIZE = 80;



    @FXML
    public void initialize(){

    }

    @Override
    public void postInitialization(Stage s, Project project) {

        stage = s;

        stage.setWidth(800);
        stage.setHeight(600);

        stage.titleProperty().bind(LC.createStringBinding("hexFrameTitle"));

        stage.widthProperty().addListener((observable, oldValue, newValue) -> {

            if (stage.getWidth() > 17 * COLUMN_SIZE && cols != 16) {
                cols = 16;
                calculateTableColumns(cols);
            } else if (stage.getWidth() < 17 * COLUMN_SIZE && stage.getWidth() > 9 * COLUMN_SIZE && cols != 8) {
                cols = 8;
                calculateTableColumns(cols);
            } else if (stage.getWidth() < 9 * COLUMN_SIZE && cols != 4) {
                cols = 4;
                calculateTableColumns(cols);
            }

        });

        fmt = new Formatter();
        hexTableVw.setEditable(false);

    }

    public void openHex(MemContents memContents){

        this.memContents = memContents;
        this.hexModel = memContents;

        cols = 8;

        adrLength = memContents.getLogLength()/4;
        dataLength  = memContents.getValueWidth()/4;

        System.out.println(memContents.getWidth());
        System.out.println(memContents.getValueWidth());
        System.out.println(memContents.getLogLength());

        System.out.println(hexModel.getFirstOffset());
        System.out.println(hexModel.getLastOffset());
        System.out.println(hexModel.getValueWidth());
        calculateTableColumns(cols);

    }

    private void calculateTableColumns(int columnCount){

        ArrayList<HexEditorDataModel> d = new ArrayList<>();

        for(int i = 0; i <= hexModel.getLastOffset(); i+=columnCount){

            System.out.println("object");
            d.add(new HexEditorDataModel(i));

        }

        hexTableVw.getColumns().clear();

        TableColumn<HexEditorDataModel, String> adrColumn = new TableColumn<>("Adresss");

        adrColumn.setCellValueFactory(new PropertyValueFactory<>("Address"));
        adrColumn.setEditable(false);
        adrColumn.setSortable(false);

        hexTableVw.getColumns().add(adrColumn);

        for(int i = 0; i < columnCount; i++){

            int j = i;

           // Formatter formatter = new Formatter();
            //formatter.format("%05X",Integer.toHexString(j));
            //System.out.println(formatter.toString());

            TableColumn<HexEditorDataModel, String> column = new TableColumn<>(Integer.toHexString(j));
            column.setCellValueFactory(data -> data.getValue().getMem(j));
            column.setCellFactory(param -> {

                TextFieldTableCell<HexEditorDataModel, String> cell = new TextFieldTableCell<>();
                cell.setAlignment(Pos.CENTER);
                return cell;

            });

            column.setSortable(false);


            hexTableVw.getColumns().add(column);

        }

        hexTableVw.getItems().addAll(d);

        hexTableVw.refresh();

    }


    @Override
    public void onClose() {

    }



    public class HexEditorDataModel{

        private int adr;
        private String address;
        private ArrayList<Integer> mem = new ArrayList<>();

        public HexEditorDataModel(int adr){

            this.adr = adr;
            address = Integer.toHexString(adr);

        }

        public String getAddress() {
            return address;
        }

        public ObservableValue<String> getMem(int i) {
            return new SimpleStringProperty(Integer.toHexString(hexModel.get(adr+i)));
        }

    }

}
