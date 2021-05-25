package com.cburch.LogisimFX.newgui.CircLogFrame;

import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.newgui.AbstractController;
import com.cburch.LogisimFX.newgui.PreferencesFrame.LC;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class CircLogController extends AbstractController {

    private Stage stage;

    @FXML
    public void initialize(){
    }

    @Override
    public void postInitialization(Stage s) {
        stage = s;
        stage.setTitle("LogisimFX: Logging");
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
