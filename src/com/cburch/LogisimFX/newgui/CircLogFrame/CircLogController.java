package com.cburch.LogisimFX.newgui.CircLogFrame;

import com.cburch.LogisimFX.Localizer;
import com.cburch.LogisimFX.newgui.AbstractController;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class CircLogController extends AbstractController {

    private Stage stage;

    private Localizer lc = new Localizer("LogisimFX/resources/localization/log");

    @FXML
    public void initialize(){



    }


    @Override
    public void prepareFrame(Stage s) {
        stage = s;
        setStageTitle();
    }

    @Override
    public void setStageTitle() {
        stage.titleProperty().bind(lc.createStringBinding("logFrameTitle"));
    }

    @Override
    public void onClose() {

    }

}
