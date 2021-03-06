package com.cburch.LogisimFX.newgui;

import javafx.scene.control.Alert;

public class DialogManager {

    public static void CreateWarningDialog(String title, String content){

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();

    }

    public static void CreateInfoDialog(String title, String content){

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();

    }

    public static void CreateErrorDialog(String title, String content){

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();

    }


}
