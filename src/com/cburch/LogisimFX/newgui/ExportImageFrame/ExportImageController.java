package com.cburch.LogisimFX.newgui.ExportImageFrame;

import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.data.Bounds;
import com.cburch.LogisimFX.file.LogisimFile;
import com.cburch.LogisimFX.newgui.AbstractController;
import com.cburch.LogisimFX.newgui.DialogManager;
import com.cburch.LogisimFX.proj.Project;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ExportImageController extends AbstractController {

    private Stage stage;

    @FXML
    private Label CircuitsLbl;

    @FXML
    private ListView<Circuit> CircuitsLstVw;


    @FXML
    private Label FormatLbl;

    @FXML
    private RadioButton PngRb;

    @FXML
    private RadioButton GifRb;

    @FXML
    private RadioButton JpegRb;


    @FXML
    private Label ScaleLbl;

    @FXML
    private Slider ScaleSl;


    @FXML
    private CheckBox PrintViewChkBx;

    @FXML
    private Label PrintViewLbl;


    @FXML
    private Button OkBtn;

    @FXML
    private Button CancelBtn;

    private ObservableList<Circuit> circuits;
    private Project proj;

    @FXML
    public void initialize(){

        CircuitsLbl.textProperty().bind(LC.createStringBinding("labelCircuits"));


        FormatLbl.textProperty().bind(LC.createStringBinding("labelImageFormat"));

        PngRb.setOnAction(event -> {
            GifRb.setSelected(false);
            JpegRb.setSelected(false);

        });

        GifRb.setOnAction(event -> {
            PngRb.setSelected(false);
            JpegRb.setSelected(false);

        });

        JpegRb.setOnAction(event -> {
            PngRb.setSelected(false);
            GifRb.setSelected(false);

        });



        ScaleLbl.textProperty().bind(LC.createStringBinding("labelScale"));

        PrintViewLbl.textProperty().bind(LC.createStringBinding("labelPrinterView"));

        OkBtn.setText("Ok");
        OkBtn.setOnAction(event -> {
           // pageSetup(OkBtn,stage);
        });

        CancelBtn.setText("Cancel");
        CancelBtn.setOnAction(event -> {
            stage.close();
        });

    }

    @Override
    public void postInitialization(Stage s, Project project) {

        stage = s;
        stage.titleProperty().bind(LC.createStringBinding("exportImageSelect"));
        stage.setHeight(325);
        stage.setWidth(375);

        stage.setResizable(false);

        proj = project;

        circuits = FXCollections.observableArrayList();

        boolean includeEmpty = true;

        MultipleSelectionModel<Circuit> langsSelectionModel = CircuitsLstVw.getSelectionModel();
        langsSelectionModel.setSelectionMode(SelectionMode.MULTIPLE);

        LogisimFile file = proj.getLogisimFile();
        Circuit current = proj.getCurrentCircuit();

        boolean currentFound = false;

        for (Circuit circ : file.getCircuits()) {
            if (!includeEmpty || circ.getBounds() != Bounds.EMPTY_BOUNDS) {
                if (circ == current) currentFound = true;
                circuits.add(circ);
            }
        }

        CircuitsLstVw.setItems(circuits);

        if (currentFound) CircuitsLstVw.getSelectionModel().select(current);

    }

    @Override
    public void onClose() {
        System.out.println("Export image closed");
    }
}
