package LogisimFX;

import LogisimFX.newgui.MainFrame.MainFrameController;
import docklib.dock.DockPane;
import javafx.application.Application;

import java.util.Map;

import static java.util.Map.entry;

public class StyleManager {

    public static Map<String, String> themes = Map.ofEntries(
            entry(Application.STYLESHEET_MODENA, Application.STYLESHEET_MODENA),
            entry(Application.STYLESHEET_CASPIAN, Application.STYLESHEET_CASPIAN),
            entry("Dracula", "/LogisimFX/resources/css/dracula.css"),
            entry("Monokai", "/LogisimFX/resources/css/monokai.css")
    );

    public StyleManager() {
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
    }

    public static void initializeDefaultUserAgentStylesheet(){
        //firstly init default javafx default stylesheet
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        //init additional stylesheets for every getStyleClass().add() in project
        DockPane.initializeDefaultUserAgentStylesheet();
        com.sun.javafx.css.StyleManager.getInstance().addUserAgentStylesheet(StyleManager.class.getResource("/LogisimFX/resources/css/default.css").toExternalForm());
        //Set app preference stylesheet

    }

    public static void setTheme(String theme){
        if (theme.equals(Application.STYLESHEET_MODENA) || theme.equals(Application.STYLESHEET_CASPIAN)) {
            Application.setUserAgentStylesheet(themes.get(theme));
        } else {
            com.sun.javafx.css.StyleManager.getInstance().
                    addUserAgentStylesheet(StyleManager.class.getResource(themes.get(theme)).toExternalForm());
        }
    }

}
