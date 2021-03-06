package com.cburch.LogisimFX.newgui.LoadingFrame;

/*This frame shows loading progress
U cant call it during work with LogisimFx so, it won't implements AbstractController, so do MainFrame
 */


import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ProgressBar;

public class LoadingScreen {

    @FXML
    private Canvas cv;

    @FXML
    private ProgressBar ProgressBar;

    //private static double progress = 0;
    private static final SimpleDoubleProperty progress = new SimpleDoubleProperty();

    @FXML
    public void initialize(){

        progress.set(0f);
        //ProgressBar.setProgress(progress);
        ProgressBar.progressProperty().bind(progress);

    }

    public static void nextStep(){
        double buff = progress.get();
        buff += 0.143;
        progress.set(buff);
    }

    public static void Close(){



    }

}
