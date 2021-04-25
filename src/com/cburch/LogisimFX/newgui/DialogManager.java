package com.cburch.LogisimFX.newgui;


import com.cburch.LogisimFX.Localizer;
import com.cburch.LogisimFX.file.LogisimFile;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.logisim.tools.Library;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

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

    public static void CreateStackTraceDialog(String title, String header, Exception e){

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();

    }

    public static void CreateScrollError(String title, String body){

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);

        TextArea textArea = new TextArea(body);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();

    }

    public static int CreateConfirmCloseDialog(Project proj){

        Localizer lc = new Localizer("LogisimFX/resources/localization/gui");

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("");
        alert.setHeaderText(lc.get("confirmCloseTitle"));
        alert.setContentText(lc.createComplexString("confirmDiscardMessage",  proj.getLogisimFile().getName()));

        ButtonType buttonTypeSave = new ButtonType( lc.get("saveOption"));
        ButtonType buttonTypeDiscard = new ButtonType(lc.get("discardOption"));
        ButtonType buttonTypeCancel = new ButtonType(lc.get("cancelOption"), ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeSave, buttonTypeDiscard, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeSave){
            return 2;
        } else if (result.get() == buttonTypeDiscard) {
            return 1;
        }else{
            return 0;
        }

    }

    public static int CreateFileReloadDialog(Project proj){

        Localizer lc = new Localizer("LogisimFX/resources/localization/proj");

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("");
        alert.setHeaderText(lc.get("openAlreadyTitle"));
        alert.setContentText(lc.createComplexString("openAlreadyMessage",proj.getLogisimFile().getName()));

        ButtonType buttonTypeLoseChanges = new ButtonType( lc.get("openAlreadyLoseChangesOption"));
        ButtonType buttonTypeNewWindow = new ButtonType(lc.get("openAlreadyNewWindowOption"));
        ButtonType buttonTypeCancel = new ButtonType(lc.get("openAlreadyCancelOption"), ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeLoseChanges, buttonTypeNewWindow, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeLoseChanges){
            return 2;
        } else if (result.get() == buttonTypeNewWindow) {
            return 1;
        }else{
            return 0;
        }

    }

    public static String CreateInputDialog(LogisimFile file){

        Localizer lc = new Localizer("LogisimFX/resources/localization/menu");

        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle(lc.get("circuitNameDialogTitle"));
        inputDialog.setHeaderText(lc.get("circuitNameDialogTitle"));
        inputDialog.setContentText(lc.get("circuitNamePrompt"));

        Optional<String> result = inputDialog.showAndWait();

        if (result.isPresent()){

            String buff = result.get();
            buff.trim();
            System.out.println(buff);

            if (buff.equals("")) {
                CreateErrorDialog("Error",lc.get("circuitNameMissingError"));
                return null;
            } else {

                if (file.getTool(buff) == null) {
                    return buff;
                } else {
                    CreateErrorDialog("Error",lc.get("circuitNameDuplicateError"));
                    return null;
                }

            }

        }else {
            return null;
        }

    }

    public static int CreateConfirmDialog(){

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Look, a Confirmation Dialog");
        alert.setContentText("Are you ok with this?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            return 1;
        } else {
            return 0;
        }

    }


}
