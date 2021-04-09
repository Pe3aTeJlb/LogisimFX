package com.cburch.LogisimFX.newgui.ExportImageFrame;

import com.cburch.LogisimFX.Localizer;
import com.cburch.LogisimFX.newgui.AbstractController;
import com.cburch.LogisimFX.newgui.DialogManager;
import com.cburch.LogisimFX.proj.Project;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ExportImageController extends AbstractController {

    private Stage stage;

    @FXML
    private Label CircuitsLbl;

    @FXML
    private ListView<?> CircuitsLstVw;


    @FXML
    private Label FormatLbl;

    @FXML
    private RadioButton PngRB;

    @FXML
    private RadioButton GifRb;

    @FXML
    private RadioButton JpegRB;


    @FXML
    private Label ScaleLbl;

    @FXML
    private Slider ScaleSl;


    @FXML
    private CheckBox MonocrhomChkBx;


    @FXML
    private Button OkBtn;

    @FXML
    private Button CancelBtn;


    private Localizer lc = new Localizer("LogisimFX/resources/localization/gui");

    private ObservableList<String> circuits = FXCollections.observableArrayList("1","2","1","1","1","1","1","1","1");
    private Project proj;

    @FXML
    public void initialize(){

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
        stage.titleProperty().bind(lc.createStringBinding("printParmsTitle"));
        stage.setHeight(300);
        stage.setWidth(300);

        proj = project;

        //setCircuitList(true);

        if(circuits.size()==0){
            DialogManager.CreateErrorDialog( lc.get("printEmptyCircuitsTitle"), lc.get("printEmptyCircuitsMessage"));
        }

    }

    @Override
    public void onClose() {
        System.out.println("Export image closed");
    }
}
