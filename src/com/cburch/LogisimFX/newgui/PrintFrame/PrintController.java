package com.cburch.LogisimFX.newgui.PrintFrame;

import com.cburch.LogisimFX.file.LogisimFile;
import com.cburch.LogisimFX.newgui.AbstractController;
import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.data.Bounds;
import com.cburch.LogisimFX.proj.Project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.NodeOrientation;
import javafx.print.PrinterJob;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class PrintController extends AbstractController {

    private Stage stage;

    private Project proj;



    @FXML
    private Label CircuitsLbl;

    @FXML
    private ListView<Circuit> CircuitLstVw;

    @FXML
    private Label HeaderLbl;

    @FXML
    private TextField HeaderTxtFld;

    @FXML
    private CheckBox RotateToFitChkbx;

    @FXML
    private Label RotateToFitLbl;

    @FXML
    private CheckBox PrintViewChkbx;

    @FXML
    private Label PrintViewLbl;

    @FXML
    private Button OkBtn;

    @FXML
    private Button CancleBtn;



    private PrinterJob job;

    private ObservableList<Circuit> circuits;


    @FXML
    public void initialize(){

        CircuitsLbl.textProperty().bind(LC.createStringBinding("labelCircuits"));

        HeaderLbl.textProperty().bind(LC.createStringBinding("labelHeader"));
        HeaderTxtFld.setText("%n (%p of %P)");

        RotateToFitLbl.textProperty().bind(LC.createStringBinding("labelRotateToFit"));

        PrintViewLbl.textProperty().bind(LC.createStringBinding("labelPrinterView"));

        OkBtn.setText("Ok");
        OkBtn.setOnAction(event -> pageSetup(stage));

        CancleBtn.setText("Cancel");
        CancleBtn.setOnAction(event -> stage.close());

    }

    @Override
    public void postInitialization(Stage s, Project project) {

        stage = s;

        stage.titleProperty().bind(LC.createStringBinding("printParmsTitle"));
        stage.setHeight(300);
        stage.setWidth(450);
        stage.setResizable(false);

        proj = project;

        circuits = FXCollections.observableArrayList();

        boolean includeEmpty = true;

        MultipleSelectionModel<Circuit> langsSelectionModel = CircuitLstVw.getSelectionModel();
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

        CircuitLstVw.setItems(circuits);

        if (currentFound) CircuitLstVw.getSelectionModel().select(current);

    }

    private void pageSetup(Stage owner) {

        // Create the PrinterJob
        job = PrinterJob.createPrinterJob();

        if (job == null)
        {
            return;
        }

        // Show the print setup dialog
        boolean proceed = job.showPageSetupDialog(owner);

        if (proceed)
        {
            printSetup(stage);
        }

    }

    private void printSetup(Stage owner){

        if (job == null)
        {
            return;
        }

        // Show the print setup dialog
        boolean proceed = job.showPrintDialog(owner);

        if (proceed)
        {
            print(job);
        }

    }

    private void print(PrinterJob job) {

        MultipleSelectionModel<Circuit> langsSelectionModel = CircuitLstVw.getSelectionModel();

        boolean success = true;

        int pageIndex = 0;

        for (Circuit circ: langsSelectionModel.getSelectedItems()) {

            String header = format(HeaderTxtFld.getText(), pageIndex,
                    langsSelectionModel.getSelectedItems().size(), circ.getName());

            System.out.println("header "+header);

            // Print the node
            success &= job.printPage(proj.getFrameController().getPrintImage(circ));

            pageIndex++;

        }

        if (success) {
            job.endJob();
        }

    }

    private String format(String header, int index, int max,
                                 String circName) {
        int mark = header.indexOf('%');
        if (mark < 0) return header;
        StringBuilder ret = new StringBuilder();
        int start = 0;
        for (; mark >= 0 && mark + 1 < header.length();
             start = mark + 2, mark = header.indexOf('%', start)) {
            ret.append(header.substring(start, mark));
            switch (header.charAt(mark + 1)) {
                case 'n': ret.append(circName); break;
                case 'p': ret.append("" + index); break;
                case 'P': ret.append("" + max); break;
                case '%': ret.append("%"); break;
                default:  ret.append("%" + header.charAt(mark + 1));
            }
        }
        if (start < header.length()) {
            ret.append(header.substring(start));
        }
        return ret.toString();
    }

    @Override
    public void onClose() {
        System.out.println("Print closed");
    }


}
