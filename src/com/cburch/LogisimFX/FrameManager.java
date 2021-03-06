package com.cburch.LogisimFX;

import com.cburch.LogisimFX.newgui.AbstractController;
import com.cburch.LogisimFX.newgui.HelpFrame.HelpController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.HashMap;

/*
FrameManager controls next frames: Options, Preferences, AboutFrame, LogFrame, CircAnalyzeFrame;
these classes implements AbstractController in purpose to Title binding and frame duplicating blocking
 */

public class FrameManager {

    private static Stage splashScreen;

    private static Stage helpFrame;
    private static HelpController helpController;

    private static FXMLLoader loader;

    //ToDo: replace String with project reference
    private static final HashMap<String, Stage> OpenedOptionsFrames = new HashMap<>();
    private static final HashMap<String, Stage> OpenedFiles = new HashMap<>();
    //private static HashMap<> OpenedMemoryEditors = new HashMap();

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
            c.prepareFrame(newStage);

            newStage.setOnCloseRequest(event -> {

                if(OpenedFiles.size()==1){
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

    public static void CreateNewFrame(String resourcePath){

        if(!OpenedOptionsFrames.containsKey(resourcePath)){

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
            c.prepareFrame(newStage);

            newStage.setOnCloseRequest(event -> {
                OpenedOptionsFrames.remove(resourcePath);
                c.onClose();
            });

            //newStage.initOwner(primaryStage);

            newStage.show();

            OpenedOptionsFrames.put(resourcePath, newStage);

        }else{
            FocusOnFrame(OpenedOptionsFrames.get(resourcePath));
        }

    }

    public static void CreateSplashScreen(){

        Stage newStage = new Stage();

        loader = new FXMLLoader(ClassLoader.getSystemResource("com/cburch/LogisimFX/newgui/LoadingFrame/Loading.fxml"));
        Parent root = null;

        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        newStage.setScene(new Scene(root, 450, 350));
        newStage.initStyle(StageStyle.UNDECORATED);
        newStage.setResizable(false);
        newStage.show();

        splashScreen = newStage;

    }

    public static void CreateHelpFrame(String chapter){

        if(helpFrame==null){

            loader = new FXMLLoader(ClassLoader.getSystemResource("com/cburch/LogisimFX/newgui/HelpFrame/Help.fxml"));
            Parent root = null;

            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Stage newStage = new Stage();
            newStage.setScene(new Scene(root, 800, 600));

            helpController = loader.getController();
            helpController.prepareFrame(newStage);
            helpController.openChapter(chapter);

            newStage.setOnCloseRequest(event -> {helpFrame = null; helpController.onClose();});

            //newStage.initOwner(primaryStage);

            newStage.show();

            helpFrame = newStage;

        }else{
            helpController.openChapter(chapter);
            FocusOnFrame(helpFrame);
        }

    }

    public static void CreateHexEditorFrame(){
        //ToDO: hex editor lol
    }

    public static void CloseSplashScreen(){

        splashScreen.close();

    }

    private static void FocusOnFrame(Stage s){
        s.toFront();
    }

}
