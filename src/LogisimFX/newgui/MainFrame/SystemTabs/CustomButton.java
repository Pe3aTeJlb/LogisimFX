/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.MainFrame.SystemTabs;

import LogisimFX.IconsManager;
import javafx.scene.control.Button;

public class CustomButton extends Button {

    public CustomButton(int prefWidth, int prefHeight, String IconName){

        super();
        setPrefSize(prefWidth,prefHeight);
        setMinSize(prefWidth,prefHeight);
        setMaxSize(prefWidth,prefHeight);
        graphicProperty().setValue(IconsManager.getIcon(IconName));

    }

}
