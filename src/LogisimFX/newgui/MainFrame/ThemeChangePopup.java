package LogisimFX.newgui.MainFrame;

import LogisimFX.StyleManager;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.util.Map;

public class ThemeChangePopup extends Popup {

    public ThemeChangePopup(Stage stage){
        super();
        initPopup();
        setAutoHide(true);
        setHideOnEscape(true);
        show(stage, stage.getWidth()/2, stage.getHeight()/2);
    }

    private void initPopup(){

        VBox container = new VBox();
        container.getStyleClass().add("popup-container");

        VBox listContainer = new VBox();
        listContainer.getStyleClass().add("theme-list-container");
        for (Map.Entry<String, String> i : StyleManager.themes.entrySet()) {
            Label item = new Label(i.getKey());
            item.getStyleClass().add("theme-list-item");
            item.setOnMouseClicked(event -> {
                StyleManager.setTheme(i.getKey());
                hide();
            });
            listContainer.getChildren().add(item);
        }

        container.getChildren().add(listContainer);
        getContent().add(container);
    }

}
