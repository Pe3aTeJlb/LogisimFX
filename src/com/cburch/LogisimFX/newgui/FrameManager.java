package com.cburch.LogisimFX.newgui;

import com.cburch.LogisimFX.instance.Instance;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/*
FrameManager controls all frames generated during program execution.
These classes implements AbstractController in purpose to postInitialization process (title binding etc.) and frame duplicating blocking.
*/

public class FrameManager {

    private static FXMLLoader loader;
    //private static AbstractController c;

    private static final HashMap<Project, Data> OpenedMainFrames = new HashMap<>();

    private static final HashMap<String, Data> OpenedProjectIndependentFrames = new HashMap<>();

    //ToDo: replace String with memory object reference
    private static final HashMap<Instance, Stage> OpenedMemoryEditors = new HashMap<>();

    private static class Data{

        public Stage stage;
        public AbstractController controller;
        public HashMap<String, Stage> projectAssociatedFrames = new HashMap<>();

        Data(Stage s, AbstractController c){
            this.stage = s;
            this.controller = c;
        }

    }


    //MainFrame

    public static void CreateMainFrame(Project proj){

        if(!OpenedMainFrames.containsKey(proj)){

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

            newStage.setOnHidden(event -> {

                event.consume();

                //Todo: possible problem on exit https://stackoverflow.com/questions/46053974/using-platform-exit-and-system-exitint-together

                if (proj.isFileDirty()){

                    int type = DialogManager.CreateConfirmCloseDialog(proj);

                    if (type == 2) {

                        ProjectActions.doSave(proj);

                        if (OpenedMainFrames.size() == 1) {

                            CloseProjectIndependentFrames();
                            CloseMemoryEditors();
                            CloseProjectAssociatedFrames(proj);
                            c.onClose();

                            Platform.exit();
                            System.exit(0);

                        } else {

                            CloseProjectAssociatedFrames(proj);
                            CloseMemoryEditors();
                            c.onClose();
                            OpenedMainFrames.remove(proj);

                        }

                    } else if (type == 1) {

                        if (OpenedMainFrames.size() == 1) {

                            CloseProjectIndependentFrames();
                            CloseMemoryEditors();
                            CloseProjectAssociatedFrames(proj);
                            c.onClose();

                            Platform.exit();
                            System.exit(0);

                        } else {

                            CloseProjectAssociatedFrames(proj);
                            CloseMemoryEditors();
                            c.onClose();
                            OpenedMainFrames.remove(proj);

                        }

                    } else if (type == 0) {
                        //todo: check out what to do
                    }

                }else{

                    if(OpenedMainFrames.size()==1){

                        CloseProjectIndependentFrames();
                        CloseMemoryEditors();
                        CloseProjectAssociatedFrames(proj);
                        c.onClose();

                        Platform.exit();
                        System.exit(0);

                    }else{

                        CloseProjectAssociatedFrames(proj);
                        CloseMemoryEditors();
                        c.onClose();
                        OpenedMainFrames.remove(proj);

                    }

                }


            });

            newStage.show();

            OpenedMainFrames.put(proj, new Data(newStage,c));

            System.out.println(OpenedMainFrames.size());
            System.out.println(proj.getLogisimFile().getName());

        }
        else{
            FocusOnFrame(OpenedMainFrames.get(proj).stage);
        }

    }



    //Project-depending frames

    public static void CreateNewFrame(String resourcePath, Project proj, Modality modality){

        if(!OpenedMainFrames.get(proj).projectAssociatedFrames.containsKey(resourcePath)){

            loader = new FXMLLoader(ClassLoader.getSystemResource("com/cburch/"+resourcePath));
            Parent root = null;

            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Stage newStage = new Stage();
            newStage.setScene(new Scene(root, 450, 350));

            AbstractController c = loader.getController();
            c.postInitialization(newStage, proj);

            newStage.setOnHidden(event -> {
                c.onClose();
                OpenedMainFrames.get(proj).projectAssociatedFrames.remove(resourcePath);
            });

            newStage.initModality(modality);
            //newStage.initOwner(OpenedMainFrames.get(proj).stage);

            newStage.show();

            OpenedMainFrames.get(proj).projectAssociatedFrames.put(resourcePath, newStage);

        }else{
            FocusOnFrame(OpenedMainFrames.get(proj).projectAssociatedFrames.get(resourcePath));
        }

    }

    public static void CreateOptionsFrame(Project proj){
        CreateNewFrame("LogisimFX/newgui/OptionsFrame/Options.fxml", proj, Modality.NONE);
    }

    public static void CreateCircLogFrame(Project proj){
        CreateNewFrame("LogisimFX/newgui/CircLogFrame/CircLog.fxml", proj, Modality.NONE);
    }

    public static void CreatePrintFrame(Project proj){
        CreateNewFrame("LogisimFX/newgui/PrintFrame/Print.fxml", proj, Modality.APPLICATION_MODAL);
    }

    public static void CreateExportImageFrame(Project proj){
        CreateNewFrame("LogisimFX/newgui/ExportImageFrame/ExportImage.fxml", proj, Modality.APPLICATION_MODAL);
    }

    public static void CreateCircuitAnalysisFrame(Project proj){
        CreateNewFrame("LogisimFX/newgui/CircuitAnalysisFrame/CircuitAnalysis.fxml", proj, Modality.NONE);
    }

    public static void CreateCircuitStatisticFrame(Project proj){
        CreateNewFrame("LogisimFX/newgui/CircuitStatisticFrame/CircuitStatistic.fxml", proj, Modality.NONE);
    }



    //Project independent frames

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

            AbstractController c = loader.getController();
            c.postInitialization(newStage);

            newStage.setOnHidden(event -> {
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
        }else{
            FocusOnFrame(OpenedProjectIndependentFrames.get(resourcepath).stage);
        }

        ((HelpController) OpenedProjectIndependentFrames.get(resourcepath).controller).openChapter(chapter);

    }

    public static void CreateAboutFrame(){
        CreateNewFrame("LogisimFX/newgui/AboutFrame/About.fxml", Modality.APPLICATION_MODAL);
    }



    //Memory based frames

    public static void CreateHexEditorFrame(){
        //ToDO: hex editor lol
    }



    //Getters

    public static Set<Project> getOpenedProjects(){
        return OpenedMainFrames.keySet();
    }



    //Tools

    public static void FocusOnFrame(Project project){

        OpenedMainFrames.get(project).stage.toFront();

        for (Stage stage: OpenedMainFrames.get(project).projectAssociatedFrames.values()) {
            stage.toFront();
        }


    }

    private static void FocusOnFrame(Stage s){
        s.toFront();
    }

    public static void CloseAllFrames(){

        ArrayList<Project> projects = new ArrayList<>(OpenedMainFrames.keySet());

        for (Project proj: projects) {
            OpenedMainFrames.get(proj).stage.close();
        }

    }

    private static void CloseProjectAssociatedFrames(Project project){

        //ConcurrentModificationException here
        //cause intellij can't understand
        //Solution:

        ArrayList<Stage> stages = new ArrayList<>(OpenedMainFrames.get(project).projectAssociatedFrames.values());

        for (Stage stage: stages) {
            stage.close();
        }

    }

    private static void CloseProjectIndependentFrames(){

        ArrayList<String> resources = new ArrayList<>(OpenedProjectIndependentFrames.keySet());

        for (String frameResource: resources) {
            OpenedProjectIndependentFrames.get(frameResource).stage.close();
        }

    }

    private static void CloseMemoryEditors(){

        //ConcurrentModificationException here

        for (Instance instance: OpenedMemoryEditors.keySet()) {
            OpenedMemoryEditors.get(instance).close();
        }

    }

}