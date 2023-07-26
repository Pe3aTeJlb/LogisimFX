/*
 * This file is part of LogisimFX. Copyright (c) 2023, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX;

import LogisimFX.prefs.AppPreferences;
import docklib.dock.DockPane;
import javafx.application.Application;
import javafx.scene.text.Font;

import java.util.Map;

import static java.util.Map.entry;

public class StyleManager {

	public static Map<String, String> themes = Map.ofEntries(
			entry(Application.STYLESHEET_MODENA, Application.STYLESHEET_MODENA),
			entry(Application.STYLESHEET_CASPIAN, Application.STYLESHEET_CASPIAN),
			entry("Dark", "/LogisimFX/resources/css/dark.css")
			//entry("Darcula", "/LogisimFX/resources/css/darcula.css"),
			//entry("Monokai", "/LogisimFX/resources/css/monokai.css")
	);

	public StyleManager() {
		Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
	}

	public static void initializeDefaultUserAgentStylesheet() {
		Font.loadFont(String.valueOf(StyleManager.class.getResource("/LogisimFX/resources/css/JetBrainsMono-Regular.ttf")), 10);
		//firstly init default javafx default stylesheet
        /*
        for (String theme: themes.values()){
            if (!theme.equals(Application.STYLESHEET_MODENA) && !theme.equals(Application.STYLESHEET_CASPIAN)) {
                com.sun.javafx.css.StyleManager.getInstance().
                        addUserAgentStylesheet(StyleManager.class.getResource(theme).toExternalForm());
            }
        }*/
		Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
		//Set app preference stylesheet
		setTheme(AppPreferences.WINDOW_STYLE.get());
		//init additional stylesheets for every getStyleClass().add() in project
		DockPane.initializeDefaultUserAgentStylesheet();
		com.sun.javafx.css.StyleManager.getInstance().
				addUserAgentStylesheet(StyleManager.class.getResource("/LogisimFX/resources/css/default.css").toExternalForm());
	}

	public static void setTheme(String theme) {
		if (theme.equals("Dark")) {
			Application.setUserAgentStylesheet(themes.get("MODENA"));
			com.sun.javafx.css.StyleManager.getInstance().
					addUserAgentStylesheet(StyleManager.class.getResource(themes.get(theme)).toExternalForm());
			AppPreferences.WINDOW_STYLE.set("Dark");
		} else {
			com.sun.javafx.css.StyleManager.getInstance().
					removeUserAgentStylesheet(StyleManager.class.getResource(themes.get("Dark")).toExternalForm());
			Application.setUserAgentStylesheet(themes.get(theme));
			AppPreferences.WINDOW_STYLE.set(theme);
		}
	}

}
