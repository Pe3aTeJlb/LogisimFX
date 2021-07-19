package com.cburch.LogisimFX;

import com.cburch.LogisimFX.newgui.AbstractController;
import com.cburch.LogisimFX.newgui.DialogManager;
import com.cburch.LogisimFX.proj.ProjectActions;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static final LogisimVersion VERSION = LogisimVersion.get(2, 7, 1);
    public static final String VERSION_NAME = VERSION.toString();
    public static final int COPYRIGHT_YEAR = 2011;
    private static String[] arguments;

    public static void main(String[] args) {
        arguments = args;
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        FXMLLoader  loader = new FXMLLoader(ClassLoader.getSystemResource(
                "com/cburch/LogisimFX/newgui/AboutFrame/About.fxml"));
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

        newStage.getIcons().add(IconsManager.LogisimFX);

        newStage.show();


        /*
        Startup startup = Startup.parseArgs(arguments);

        if (startup == null) {
            System.exit(0);
        } else {
            startup.run();
        }

         */

    }
}