package com.cburch.LogisimFX.newgui;


import com.cburch.LogisimFX.IconsManager;
import com.cburch.LogisimFX.Localizer;
import com.cburch.LogisimFX.file.LogisimFile;
import com.cburch.LogisimFX.newgui.MainFrame.ProjectLibraryActions;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.LogisimFX.tools.Library;

import com.cburch.logisim.util.Icons;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Optional;

public class DialogManager {

    private static final Localizer lc = new Localizer(null);

    public static void CreateWarningDialog(String header, String content){

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("LogisimFX");
        alert.setHeaderText(header);
        alert.setContentText(content);

        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(IconsManager.LogisimFX);

        alert.showAndWait();

    }

    public static void CreateInfoDialog(String header, String content){

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("LogisimFX");
        alert.setHeaderText(header);
        alert.setContentText(content);

        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(IconsManager.LogisimFX);

        alert.showAndWait();

    }

    public static void CreateErrorDialog(String header, String content){

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("LogisimFX");
        alert.setHeaderText(header);
        alert.setContentText(content);

        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(IconsManager.LogisimFX);

        alert.showAndWait();

    }

    public static void CreateStackTraceDialog(String title, String header, Exception e){

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("LogisimFX");
        //alert.setTitle(title);
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

        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(IconsManager.LogisimFX);

        alert.showAndWait();

    }

    public static void CreateScrollError(String header, String content){

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("LogisimFX");
        alert.setTitle(header);

        TextArea textArea = new TextArea(content);
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

        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(IconsManager.LogisimFX);

        alert.showAndWait();

    }

    public static int CreateConfirmCloseDialog(Project proj){

        lc.changeBundle("gui");
        //Localizer lc = new Localizer("gui");

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("LogisimFX");
        alert.setHeaderText(lc.get("confirmCloseTitle"));
        alert.setContentText(lc.createComplexString("confirmDiscardMessage",  proj.getLogisimFile().getName()));

        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(IconsManager.LogisimFX);

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

        lc.changeBundle("proj");
        //Localizer lc = new Localizer("proj");

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("LogisimFX");
        alert.setHeaderText(lc.get("openAlreadyTitle"));
        alert.setContentText(lc.createComplexString("openAlreadyMessage",proj.getLogisimFile().getName()));

        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(IconsManager.LogisimFX);

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

        lc.changeBundle("menu");
        //Localizer lc = new Localizer("menu");

        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("LogisimFX");
        inputDialog.setHeaderText(lc.get("circuitNameDialogTitle"));
        inputDialog.setContentText(lc.get("circuitNamePrompt"));

        ((Stage) inputDialog.getDialogPane().getScene().getWindow()).getIcons().add(IconsManager.LogisimFX);

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

    public static String CreateInputDialog(String title, String body){

        //lc.changeBundle("menu");
        //Localizer lc = new Localizer("menu");

        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("LogisimFX");
        inputDialog.setHeaderText(title);
        inputDialog.setContentText(body);

        ((Stage) inputDialog.getDialogPane().getScene().getWindow()).getIcons().add(IconsManager.LogisimFX);

        Optional<String> result = inputDialog.showAndWait();

        if (result.isPresent()){
            return  result.get();
        }else {
            return null;
        }

    }

    public static Library[] CreateLibSelectionDialog(ArrayList<Library> libs){

        lc.changeBundle("menu");

        ListViewDialog<Library> dialog = new ListViewDialog<>(null,libs);

        dialog.setTitle("LogisimFX");
        dialog.setHeaderText(lc.get("unloadLibrariesDialogTitle"));
        dialog.setMultipleSelectionModel();

        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(IconsManager.LogisimFX);

        Optional<Library> result = dialog.showAndWait();

       if(result.isPresent()){

           Library[] out = new Library[dialog.getSelectedItems().size()];

           for(int i = 0; i < dialog.getSelectedItems().size(); i++){

               out[i] = dialog.getSelectedItems().get(i);

           }

           return  out;

       }else {return null;}

    }

}
