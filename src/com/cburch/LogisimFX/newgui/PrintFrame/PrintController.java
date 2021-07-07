package com.cburch.LogisimFX.newgui.PrintFrame;

import com.cburch.LogisimFX.file.LogisimFile;
import com.cburch.LogisimFX.newgui.AbstractController;
import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.data.Bounds;
import com.cburch.LogisimFX.proj.Project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.print.PrinterJob;
import javafx.scene.Node;
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

/*
        addGb(new JLabel(Strings.get("labelRotateToFit") + " "));
        addGb(new JLabel(Strings.get("labelPrinterView") + " "));
 */

        OkBtn.setText("Ok");
        OkBtn.setOnAction(event -> pageSetup(proj.getFrameController().getCanvas(),stage));

        CancleBtn.setText("Cancel");
        CancleBtn.setOnAction(event -> stage.close());

    }

    @Override
    public void postInitialization(Stage s, Project project) {

        stage = s;

        stage.titleProperty().bind(LC.createStringBinding("printParmsTitle"));
        stage.setHeight(300);
        stage.setWidth(300);

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

    private void pageSetup(Node node, Stage owner) {

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
            printSetup(node,stage);
        }

    }

    private void printSetup(Node node, Stage owner){

        if (job == null)
        {
            return;
        }

        // Show the print setup dialog
        boolean proceed = job.showPrintDialog(owner);

        if (proceed)
        {
            print(job, node);
        }

    }

    private void print(PrinterJob job, Node node) {

        // Print the node
        boolean printed = job.printPage(node);

        if (printed)
        {
            job.endJob();
        }

    }

    @Override
    public void onClose() {
        System.out.println("Print closed");
    }


}
