package com.cburch.LogisimFX.newgui.LoadingFrame;

import com.cburch.LogisimFX.newgui.AbstractController;
import com.cburch.LogisimFX.proj.Project;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoadingScreen extends AbstractController {

    private static Stage stage;

    @FXML
    private Canvas cv;

    @FXML
    private ProgressBar ProgressBar;

    private static final SimpleDoubleProperty progress = new SimpleDoubleProperty();

    @FXML
    public void initialize(){

        progress.set(0f);
        ProgressBar.progressProperty().bind(progress);

    }

    @Override
    public void postInitialization(Stage s) {

        stage = s;

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);

    }

    public static void nextStep(){
        double buff = progress.get();
        buff += 0.143;
        progress.set(buff);
    }

    public static void Close(){
        progress.set(1);
        stage.close();
    }

    @Override
    public void onClose() {
    }


}
