package LogisimFX.newgui.MainFrame.ProjectExplorer;

import LogisimFX.IconsManager;
import javafx.scene.control.Button;

public class CustomButton extends Button {

    public CustomButton(int prefWidth, int prefHeight, String IconName){

        super();
        setPrefSize(prefWidth,prefHeight);
        setMinSize(prefWidth,prefHeight);
        setMaxSize(prefWidth,prefHeight);
        graphicProperty().setValue(IconsManager.getIcon(IconName));
        //getStylesheets().add();

    }

}
