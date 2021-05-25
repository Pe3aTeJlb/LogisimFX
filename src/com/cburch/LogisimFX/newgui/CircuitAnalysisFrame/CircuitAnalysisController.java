package com.cburch.LogisimFX.newgui.CircuitAnalysisFrame;

import com.cburch.LogisimFX.newgui.AbstractController;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class CircuitAnalysisController extends AbstractController {

    private Stage stage;

    @FXML
    public void initialize(){
    }

    @Override
    public void postInitialization(Stage s) {

        stage = s;
        //stage.titleProperty().bind();

    }



    @Override
    public void onClose() {
        System.out.println("Circuit analysis closed");
    }
}
