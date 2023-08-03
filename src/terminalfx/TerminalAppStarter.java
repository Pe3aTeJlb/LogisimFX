package terminalfx;

import terminalfx.helper.ThreadHelper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TerminalAppStarter extends Application {

    @Override
    public void start(Stage stage) {

        //        Dark Config
        /*
        TerminalConfig darkConfig = new TerminalConfig();
        darkConfig.setBackgroundColor(Color.rgb(16, 16, 16));
        darkConfig.setForegroundColor(Color.rgb(240, 240, 240));
        darkConfig.setCursorColor(Color.rgb(255, 0, 0, 0.5));

        TerminalBuilder terminalBuilder = new TerminalBuilder(darkConfig);
        TerminalTab terminal = terminalBuilder.newTerminal();

        TabPane tabPane = new TabPane();
        tabPane.getTabs().add(terminal);*/

        Terminal terminal = new Terminal();

        Scene scene = new Scene(terminal);
        scene.getStylesheets().add(TerminalAppStarter.class.getResource("terminalfx/resources/styles/Styles.css").toExternalForm());

        stage.setTitle("TerminalFX");
        stage.setScene(scene);
        stage.show();

    }

    @Override
    public void stop() {
        ThreadHelper.stopExecutorService();
        Platform.exit();
        System.exit(0);
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
