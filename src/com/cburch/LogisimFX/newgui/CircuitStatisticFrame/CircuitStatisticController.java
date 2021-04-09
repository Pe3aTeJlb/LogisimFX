package com.cburch.LogisimFX.newgui.CircuitStatisticFrame;

import com.cburch.LogisimFX.newgui.AbstractController;
import com.cburch.LogisimFX.proj.Project;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class CircuitStatisticController extends AbstractController {

    @FXML
    public void initialize(){

    }

    @Override
    public void postInitialization(Stage s) {

    }

    @Override
    public void onClose() {
        System.out.println("Circuit statistic closed");
    }
}
