package com.cburch.LogisimFX;

import com.cburch.LogisimFX.newgui.DialogManager;
import com.cburch.LogisimFX.proj.Project;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import com.cburch.LogisimFX.Startup;

import java.io.FileNotFoundException;

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

        //FrameManager.CreateMainFrame();

        //DialogManager.CreateConfirmCloseDialog(null);

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