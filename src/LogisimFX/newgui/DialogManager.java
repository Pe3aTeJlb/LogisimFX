/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui;

import LogisimFX.IconsManager;
import LogisimFX.localization.LC_null;
import LogisimFX.localization.Localizer;
import LogisimFX.file.LogisimFile;
import LogisimFX.proj.Project;
import LogisimFX.tools.Library;

import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.dialog.FontSelectorDialog;
import org.controlsfx.dialog.ProgressDialog;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Pattern;

public class DialogManager {

    private static final Localizer lc = LC_null.getInstance();


    public static void createWarningDialog(String header, String content){

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("LogisimFX");
        alert.setHeaderText(header);
        alert.setContentText(content);

        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(IconsManager.LogisimFX);

        alert.showAndWait();

    }

    public static int createConfirmWarningDialog(String header, String content){

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("LogisimFX");
        alert.setHeaderText(header);
        alert.setContentText(content);

        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(IconsManager.LogisimFX);

        ButtonType buttonTypeOk = new ButtonType("Ok");
        ButtonType buttonTypeLeave = new ButtonType("Leave");

        alert.getButtonTypes().setAll(buttonTypeOk, buttonTypeLeave);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOk){
            return 1;
        } else {
            return 0;
        }

    }

    public static void createInfoDialog(String header, String content){

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("LogisimFX");
        alert.setHeaderText(header);
        alert.setContentText(content);

        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(IconsManager.LogisimFX);

        alert.showAndWait();

    }

    public static void createErrorDialog(String header, String content){

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("LogisimFX");
        alert.setHeaderText(header);
        alert.setContentText(content);

        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(IconsManager.LogisimFX);

        alert.showAndWait();

    }

    public static void createStackTraceDialog(String title, String header, Exception e){

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

    public static void createScrollWarning(String header, String content){

        Alert alert = new Alert(Alert.AlertType.WARNING);
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

    public static void createScrollError(String header, String content){

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


    public static boolean createConfirmDialog(String header, String content){

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("LogisimFX");
        alert.setHeaderText(header);
        alert.setContentText(content);

        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(IconsManager.LogisimFX);

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;

    }

    public static boolean createConfirmDialog(){

        lc.changeBundle("gui");

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("LogisimFX");
        //alert.setHeaderText(lc.get("confirmCloseTitle"));
        alert.setHeaderText(lc.get("confirmAction"));

        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(IconsManager.LogisimFX);

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;

    }

    public static int createConfirmCloseDialog(Project proj){

        lc.changeBundle("gui");

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("LogisimFX");
        alert.setHeaderText(lc.get("confirmCloseTitle"));
        alert.setContentText(lc.getFormatted("confirmDiscardMessage",  proj.getLogisimFile().getName()));

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

    public static int createFileReloadDialog(Project proj){

        lc.changeBundle("proj");

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("LogisimFX");
        alert.setHeaderText(lc.get("openAlreadyTitle"));
        alert.setContentText(lc.getFormatted("openAlreadyMessage",proj.getLogisimFile().getName()));

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


    public static String createInputDialog(LogisimFile file){

        lc.changeBundle("menu");

        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("LogisimFX");
        inputDialog.setHeaderText(lc.get("circuitNameDialogTitle"));
        inputDialog.setContentText(lc.get("circuitNamePrompt"));

        ((Stage) inputDialog.getDialogPane().getScene().getWindow()).getIcons().add(IconsManager.LogisimFX);

        Optional<String> result = inputDialog.showAndWait();

        if (result.isPresent()){

            String buff = result.get();
            buff.trim();

            if (buff.equals("")) {
                createErrorDialog("Error",lc.get("circuitNameMissingError"));
                return null;
            } else {

                if (file.getTool(buff) == null) {
                    return buff;
                } else {
                    createErrorDialog("Error",lc.get("circuitNameDuplicateError"));
                    return null;
                }

            }

        }else {
            return null;
        }

    }

    public static String createInputDialog(String title, String body){

        //lc.changeBundle("menu");

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

    public static String createInputDialog(String title, String body, String regex){

        //lc.changeBundle("menu");

        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("LogisimFX");
        inputDialog.setHeaderText(title);
        inputDialog.setContentText(body);

        final Pattern pattern = Pattern.compile(regex);
        TextFormatter<?> formatter = new TextFormatter<>(change -> {
            if (pattern.matcher(change.getControlNewText()).matches()) {
                return change; // allow this change to happen
            } else {
                return null; // prevent change
            }
        });

        inputDialog.getEditor().setTextFormatter(formatter);

        ((Stage) inputDialog.getDialogPane().getScene().getWindow()).getIcons().add(IconsManager.LogisimFX);

        Optional<String> result = inputDialog.showAndWait();

        if (result.isPresent()){
            return  result.get();
        }else {
            return null;
        }

    }


    public static void createProgressDialog(Task task){

        ProgressDialog progressDialog = new ProgressDialog(task);
        progressDialog.setTitle("LogisimFX");

        progressDialog.headerTextProperty().bind(task.messageProperty());

        ((Stage) progressDialog.getDialogPane().getScene().getWindow()).getIcons().add(IconsManager.LogisimFX);

    }


    public static Library[] createLibSelectionDialog(ArrayList<Library> libs){

        lc.changeBundle("menu");

        ListViewDialog<Library> dialog = new ListViewDialog<>(null, libs);

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

       }else {
           return null;
       }

    }

    public static String createBoardSelectionDialog(ArrayList<String> boards){

        lc.changeBundle("fpga");

        ListViewDialog<String> dialog = new ListViewDialog<>(null, boards);

        dialog.setTitle("LogisimFX");
        dialog.setHeaderText(lc.get("chooseBoard"));
        dialog.setSingleSelectionModel();

        dialog.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<>() {
                    @Override
                    public void updateItem(String board, boolean empty) {
                        super.updateItem(board, empty);
                        if (empty || board == null) {
                            setText(null);
                        } else {
                            setText(board.split("\\.")[0]);
                        }
                    }
                };
            }
        });


        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(IconsManager.LogisimFX);

        Optional<String> result = dialog.showAndWait();

        if(result.isPresent()){

            return dialog.getSelectedItem();

        }else {
            return null;
        }

    }

    public static void createCircSearchDialog(Project proj){

        lc.changeBundle("gui");

        CircSearchDialog dialog = new CircSearchDialog(proj);

        dialog.setTitle("LogisimFX");
        dialog.setHeaderText(lc.get("searchInProject"));

        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(IconsManager.LogisimFX);

        dialog.showAndWait();

    }



    public static Font createFontSelectorDialog(Font initFont){

        lc.changeBundle("gui");

        FontSelectorDialog fontSelectorDialog = new FontSelectorDialog(initFont);

        fontSelectorDialog.titleProperty().bind(lc.createStringBinding("fontSelectorTitle"));
        fontSelectorDialog.headerTextProperty().bind(lc.createStringBinding("fontSelectorHeader"));

        ((Stage) fontSelectorDialog.getDialogPane().getScene().getWindow()).getIcons().add(IconsManager.LogisimFX);

        Optional<Font> response = fontSelectorDialog.showAndWait();

        return response.orElse(null);

    }

}
