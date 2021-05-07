package com.cburch.LogisimFX.newgui.OptionsFrame;

import com.cburch.LogisimFX.newgui.AbstractController;
import com.cburch.LogisimFX.newgui.PreferencesFrame.LC;
import com.cburch.LogisimFX.proj.Project;

import javafx.fxml.FXML;
import javafx.stage.Stage;

public class OptionsController extends AbstractController {

    private Stage stage;

    private Project proj;

    @FXML
    public void initialize(){
    }

    @Override
    public void postInitialization(Stage s, Project project) {

        stage = s;
        proj = project;

        String name = proj.getLogisimFile() == null ? "???" : proj.getLogisimFile().getDisplayName();

        stage.titleProperty().bind(LC.createComplexStringBinding("optionsFrameTitle",name));


    }
    
    @Override
    public void onClose() {
        System.out.println("test options");
    }

}
