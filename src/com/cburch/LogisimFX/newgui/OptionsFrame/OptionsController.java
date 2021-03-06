package com.cburch.LogisimFX.newgui.OptionsFrame;

import com.cburch.LogisimFX.Localizer;
import com.cburch.LogisimFX.newgui.AbstractController;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class OptionsController extends AbstractController {

    private Stage stage;

    private Localizer lc = new Localizer("LogisimFX/resources/localization/opts");

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
        stage.titleProperty().bind(lc.createStringBinding("optionsFrameTitle"));
    }

    @Override
    public void onClose() {

    }

}
