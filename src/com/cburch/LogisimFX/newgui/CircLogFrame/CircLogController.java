package com.cburch.LogisimFX.newgui.CircLogFrame;

import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.newgui.AbstractController;
import com.cburch.LogisimFX.proj.Project;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class CircLogController extends AbstractController {

    private Stage stage;


    @FXML
    private ListView<?> selectLst;

    @FXML
    private ListView<?> selectedLst;



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



    @FXML
    private CheckBox timeSelectionChckbx;



    @FXML
    private Label clockLbl;

    @FXML
    private ComboBox<?> clockCmbbx;

    @FXML
    private Label frequencyLbl;

    @FXML
    private TextField frequencyTxtfld;

    @FXML
    private ComboBox<?> hertsCmbbx;


    @FXML
    private Button timelineBtn;

    private Project proj;

    @FXML
    public void initialize(){
    }

    @Override
    public void postInitialization(Stage s, Project proj) {

        this.proj = proj;

        stage = s;
        stage.setWidth(800);
        stage.setHeight(600);
        computeTitle();


        System.out.println("Logging");

        addBtn.textProperty().bind(LC.createStringBinding("selectionAdd"));
        addBtn.disableProperty().bind();
        addBtn.setOnAction(event -> {});

        changRadixBtn.textProperty().bind(LC.createStringBinding("selectionChangeBase"));
        changRadixBtn.disableProperty().bind();
        changRadixBtn.setOnAction(event -> {});

        moveUpBtn.textProperty().bind(LC.createStringBinding("selectionMoveUp"));
        moveUpBtn.disableProperty().bind();
        moveUpBtn.setOnAction(event -> {});

        moveDownBtn.textProperty().bind(LC.createStringBinding("selectionMoveDown"));
        moveDownBtn.disableProperty().bind();
        moveDownBtn.setOnAction(event -> {});

        removeBtn.textProperty().bind(LC.createStringBinding("selectionRemove"));
        removeBtn.disableProperty().bind();
        removeBtn.setOnAction(event -> {});

    }

    private void computeTitle() {
        //String name = data == null ? "???" : data.getCircuitState().getCircuit().getName();
        //return StringUtil.format(Strings.get("logFrameTitle"), name,
          //      proj.getLogisimFile().getDisplayName());
    }

    public void setCircuit(Circuit circ){

        //setText(StringUtil.format(Strings.get("logFrameMenuItem"), title));
        stage.titleProperty().bind(LC.createStringBinding("logFrameTitle"));

    }

    @Override
    public void onClose() {
        System.out.println("Circ log closed");
    }

}
