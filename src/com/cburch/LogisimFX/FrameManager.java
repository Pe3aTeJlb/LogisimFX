package com.cburch.LogisimFX;

import com.cburch.LogisimFX.newgui.AbstractController;
import com.cburch.LogisimFX.newgui.HelpFrame.HelpController;
import com.cburch.LogisimFX.proj.Project;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.HashMap;

/*
FrameManager controls all frames generated during program execution.
These classes implements AbstractController in purpose to postInitialization process (title binding etc.) and frame duplicating blocking.
 */

public class FrameManager {

    private static FXMLLoader loader;
    private static AbstractController c;

    private static final HashMap<Project, Data> OpenedThreadsFrames = new HashMap<>();

    private static final HashMap<String, Data> OpenedThreadlessFrames = new HashMap<>();

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

        //ToDO: add opened file reference as hashmap key and as function parameter

        //if(!usedResources.containsKey(resourcePath)){

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

            if(OpenedThreadsFrames.size()==1){
                //OpenedOptionsFrames.re
                System.exit(0);
            }else{

            }

        });

        newStage.show();

        //usedResources.put(resourcePath, newStage);
        //primaryStage = newStage;

        //}

    }

    //Thread-depending frame
    public static void CreateMainFrame(Project proj){

        //ToDO: add opened file reference as hashmap key and as function parameter

        if(!OpenedThreadsFrames.containsKey(proj)){

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
            c.linkProjectReference(proj);

            newStage.setOnCloseRequest(event -> {

                if(OpenedThreadsFrames.size()==1){
                    //OpenedOptionsFrames.re
                    System.exit(0);
                }else{
                    OpenedThreadsFrames.remove(proj);
                }

            });

            newStage.show();

            OpenedThreadsFrames.put(proj, new Data(newStage,c));

        }
        else{
            FocusOnFrame(OpenedThreadsFrames.get(proj).stage);
        }

    }


    //Threadless frames

    public static void CreateNewFrame(String resourcePath, Modality modality){

        if(!OpenedThreadlessFrames.containsKey(resourcePath)){

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
                OpenedThreadlessFrames.remove(resourcePath);
            });

            newStage.initModality(modality);

            newStage.show();

            OpenedThreadlessFrames.put(resourcePath, new Data(newStage, c));

        }else{
            FocusOnFrame(OpenedThreadlessFrames.get(resourcePath).stage);
        }

    }

    public static void CreatePreferencesFrame(){
        CreateNewFrame("LogisimFX/newgui/PreferencesFrame/Preferences.fxml", Modality.NONE);
    }

    public static void CreateOptionsFrame(){
        CreateNewFrame("LogisimFX/newgui/OptionsFrame/Options.fxml", Modality.NONE);
    }

    public static void CreateCircLogFrame(){
        CreateNewFrame("LogisimFX/newgui/CircLogFrame/CircLog.fxml", Modality.NONE);
    }

    public static void CreateAboutFrame(){
        CreateNewFrame("LogisimFX/newgui/AboutFrame/About.fxml", Modality.APPLICATION_MODAL);
    }

    public static void CreateLoadingScreen(){
        CreateNewFrame("LogisimFX/newgui/LoadingFrame/Loading.fxml", Modality.NONE);
    }

    public static void CreatePrintFrame(Project proj){
        CreateNewFrame("LogisimFX/newgui/PrintFrame/Print.fxml", Modality.APPLICATION_MODAL);
        c.linkProjectReference(proj);
    }

    public static void CreateHelpFrame(String chapter){

        String resourcepath = "LogisimFX/newgui/HelpFrame/Help.fxml";

        if(!OpenedThreadlessFrames.containsKey(resourcepath)){

            CreateNewFrame("LogisimFX/newgui/HelpFrame/Help.fxml", Modality.NONE);
            ((HelpController) c).openChapter(chapter);

        }else{

            FocusOnFrame(OpenedThreadlessFrames.get(resourcepath).stage);
            ((HelpController)OpenedThreadlessFrames.get(resourcepath).controller).openChapter(chapter);

        }

    }


    //Memory based frames

    public static void CreateHexEditorFrame(){
        //ToDO: hex editor lol
    }

    private static void FocusOnFrame(Stage s){
        s.toFront();
    }

}
