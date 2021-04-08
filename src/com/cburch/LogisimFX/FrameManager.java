package com.cburch.LogisimFX;

import com.cburch.LogisimFX.newgui.AbstractController;
import com.cburch.LogisimFX.newgui.DialogManager;
import com.cburch.LogisimFX.newgui.HelpFrame.HelpController;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.LogisimFX.proj.ProjectActions;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;

/*
FrameManager controls all frames generated during program execution.
These classes implements AbstractController in purpose to postInitialization process (title binding etc.) and frame duplicating blocking.
*/

public class FrameManager {

    private static FXMLLoader loader;
    private static AbstractController c;

    private static final HashMap<Project, Data> OpenedProjectAssociatedFrames = new HashMap<>();

    private static final HashMap<String, Data> OpenedProjectIndependentFrames = new HashMap<>();

    //ToDo: replace String with memory object reference
    private static final HashMap<String, Stage> OpenedMemoryEditors = new HashMap();

    private static class Data{

        public Stage stage;
        public AbstractController controller;

        Data(Stage s, AbstractController c){
            this.stage = s;
            this.controller = c;
        }

    }


    //Thread-depending frame
    public static void CreateMainFrame(){

        loader = new FXMLLoader(ClassLoader.getSystemResource(
                "com/cburch/LogisimFX/newgui/MainFrame/LogisimFx.fxml"));
        Parent root = null;

        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stage newStage = new Stage();
        newStage.setScene(new Scene(root, 800, 600));

        AbstractController c = loader.getController();
        c.postInitialization(newStage);
        //c.setProject(proj);

        newStage.setOnCloseRequest(event -> {
            event.consume();

            c.onClose();

            //Todo: possible problem on exit https://stackoverflow.com/questions/46053974/using-platform-exit-and-system-exitint-together



                int type = DialogManager.CreateConfirmCloseDialog(null);

                if (type == 2) {

                    if (OpenedProjectAssociatedFrames.size() == 1) {
                        Platform.exit();
                        System.exit(0);
                    }

                } else if (type == 1) {

                    newStage.close();

                    if (OpenedProjectAssociatedFrames.size() == 1) {
                        Platform.exit();
                        System.exit(0);
                    }

                } else if (type == 0) {

                }



        });

        newStage.show();

    }

    //Thread-depending frame
    public static void CreateMainFrame(Project proj){

        if(!OpenedProjectAssociatedFrames.containsKey(proj)){

            loader = new FXMLLoader(ClassLoader.getSystemResource(
                    "com/cburch/LogisimFX/newgui/MainFrame/LogisimFx.fxml"));
            Parent root = null;

            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Stage newStage = new Stage();
            newStage.setScene(new Scene(root, 800, 600));

            AbstractController c = loader.getController();
            c.postInitialization(newStage, proj);

            newStage.setOnCloseRequest(event -> {

                event.consume();

                c.onClose();

                //Todo: possible problem on exit https://stackoverflow.com/questions/46053974/using-platform-exit-and-system-exitint-together

                if (proj.isFileDirty()){

                    int type = DialogManager.CreateConfirmCloseDialog(proj);

                    if (type == 2) {
                        ProjectActions.doSave(proj);

                        if (OpenedProjectAssociatedFrames.size() == 1) {
                            Platform.exit();
                            System.exit(0);
                        } else {
                            OpenedProjectAssociatedFrames.remove(proj);
                        }

                    } else if (type == 1) {

                        newStage.close();

                        if (OpenedProjectAssociatedFrames.size() == 1) {
                            Platform.exit();
                            System.exit(0);
                        } else {
                            OpenedProjectAssociatedFrames.remove(proj);
                        }

                    } else if (type == 0) {

                    }
                }else{

                    if(OpenedProjectAssociatedFrames.size()==1){
                        Platform.exit();
                        System.exit(0);
                    }else{
                        OpenedProjectAssociatedFrames.get(proj).stage.close();
                        OpenedProjectAssociatedFrames.remove(proj);
                    }

                }


            });

            newStage.show();

            OpenedProjectAssociatedFrames.put(proj, new Data(newStage,c));

            System.out.println(OpenedProjectAssociatedFrames.size());
            System.out.println(proj.getLogisimFile().getName());

        }
        else{
            FocusOnFrame(OpenedProjectAssociatedFrames.get(proj).stage);
        }

    }

    public static void CloseAllFrames(){

        for (Project proj: OpenedProjectAssociatedFrames.keySet()) {
            OpenedProjectAssociatedFrames.get(proj).stage.close();
        }

    }


    //Threadless frames

    public static void CreateNewFrame(String resourcePath, Modality modality){

        if(!OpenedProjectIndependentFrames.containsKey(resourcePath)){

            loader = new FXMLLoader(ClassLoader.getSystemResource("com/cburch/"+resourcePath));
            Parent root = null;

            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Stage newStage = new Stage();
            newStage.setScene(new Scene(root, 450, 350));

            c = loader.getController();
            c.postInitialization(newStage);

            newStage.setOnCloseRequest(event -> {
                c.onClose();
                OpenedProjectIndependentFrames.remove(resourcePath);
            });

            newStage.initModality(modality);

            newStage.show();

            OpenedProjectIndependentFrames.put(resourcePath, new Data(newStage, c));

        }else{
            FocusOnFrame(OpenedProjectIndependentFrames.get(resourcePath).stage);
        }

    }

    public static void CreateNewFrame(String resourcePath, Project proj, Modality modality){

        if(!OpenedProjectIndependentFrames.containsKey(resourcePath)){

            loader = new FXMLLoader(ClassLoader.getSystemResource("com/cburch/"+resourcePath));
            Parent root = null;

            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Stage newStage = new Stage();
            newStage.setScene(new Scene(root, 450, 350));

            c = loader.getController();
            c.postInitialization(newStage, proj);

            newStage.setOnCloseRequest(event -> {
                c.onClose();
                OpenedProjectIndependentFrames.remove(resourcePath);
            });

            newStage.initModality(modality);

            newStage.show();

            OpenedProjectIndependentFrames.put(resourcePath, new Data(newStage, c));

        }else{
            FocusOnFrame(OpenedProjectIndependentFrames.get(resourcePath).stage);
        }

    }

    public static void CreateOptionsFrame(){
        CreateNewFrame("LogisimFX/newgui/OptionsFrame/Options.fxml", Modality.NONE);
    }

    public static void CreateCircLogFrame(){
        CreateNewFrame("LogisimFX/newgui/CircLogFrame/CircLog.fxml", Modality.NONE);
    }

    public static void CreatePrintFrame(Project proj){
        CreateNewFrame("LogisimFX/newgui/PrintFrame/Print.fxml", proj, Modality.APPLICATION_MODAL);
    }

    public static void CreateExportImageFrame(Project proj){
        CreateNewFrame("LogisimFX/newgui/ExportImageFrame/ExportImage.fxml", proj, Modality.APPLICATION_MODAL);
    }

    // open current proj circuit
//TODO: must be replaced with createNoProjFrame and cast controller method in purpose of
// methods unification
    public static void CreateCircuitAnalysisFrame(Project proj){
        CreateNewFrame("LogisimFX/newgui/CircuitAnalysisFrame/CircuitAnalysis.fxml", proj, Modality.NONE);
    }

    public static void CreateCircuitStatisticFrame(Project proj){
        CreateNewFrame("LogisimFX/newgui/CircuitStatisticFrame/CircuitStatistic.fxml", proj, Modality.APPLICATION_MODAL);
    }


    //the true project independent frames
    public static void CreateLoadingScreen(){
        CreateNewFrame("LogisimFX/newgui/LoadingFrame/Loading.fxml", Modality.NONE);
    }

    public static void CreatePreferencesFrame(){
        CreateNewFrame("LogisimFX/newgui/PreferencesFrame/Preferences.fxml", Modality.NONE);
    }

    public static void CreateHelpFrame(String chapter){

        String resourcepath = "LogisimFX/newgui/HelpFrame/Help.fxml";

        if(!OpenedProjectIndependentFrames.containsKey(resourcepath)){

            CreateNewFrame("LogisimFX/newgui/HelpFrame/Help.fxml", Modality.NONE);
            ((HelpController) c).openChapter(chapter);

        }else{

            FocusOnFrame(OpenedProjectIndependentFrames.get(resourcepath).stage);
            ((HelpController) OpenedProjectIndependentFrames.get(resourcepath).controller).openChapter(chapter);

        }

    }

    public static void CreateAboutFrame(){
        CreateNewFrame("LogisimFX/newgui/AboutFrame/About.fxml", Modality.APPLICATION_MODAL);
    }






    //Memory based frames

    public static void CreateHexEditorFrame(){
        //ToDO: hex editor lol
    }

    //Getters
    public static HashMap<Project, Data> getOpenedFrames(){
        return OpenedProjectAssociatedFrames;
    }

    //Tools

    private static void FocusOnFrame(Stage s){
        s.toFront();
    }

    public static void FocusOnFrame(Project project){

        OpenedProjectAssociatedFrames.get(project).stage.toFront();

    }

}