package com.cburch.LogisimFX.newgui.CircuitStatisticFrame;

import com.cburch.LogisimFX.Localizer;
import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.newgui.AbstractController;
import com.cburch.LogisimFX.proj.Project;

import javafx.fxml.FXML;
import javafx.stage.Stage;

public class CircuitStatisticController extends AbstractController {

    private Stage stage;

    private Localizer lc = new Localizer("gui");

    private Project proj;

    @FXML
    public void initialize(){

    }

    @Override
    public void postInitialization(Stage s, Project project) {

        stage = s;

        proj = project;

        stage.setTitle("LogisimFx: circuit statistics");

    }

    public void describeCircuit(Circuit circuit){

        stage.titleProperty().bind(lc.createComplexStringBinding("statsDialogTitle",circuit.getName()));

    }

    @Override
    public void onClose() {
        System.out.println("Circuit statistic closed");
    }
}
