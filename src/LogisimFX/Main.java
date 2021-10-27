package LogisimFX;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application{

    public static final LogisimVersion VERSION = LogisimVersion.get(1, 0, 0);
    public static final String VERSION_NAME = VERSION.toString();
    public static final int COPYRIGHT_YEAR = 2021;

    private static String[] arguments;

    public static void main(String[] args) {
        System.setProperty("quantum.multithreading", "false");
        System.setProperty("javafx.animation.fullspeed", "true");
        System.setProperty("javafx.animation.pulse", "60");
        System.setProperty("javafx.animation.framerate", "60");
        arguments = args;
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        Startup startup = Startup.parseArgs(arguments);

        if (startup == null) {
            System.exit(0);
        } else {
            startup.run();
        }

    }

}