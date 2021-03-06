package com.cburch.LogisimFX.newgui.MainFrame;

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

public class CustomButton extends Button {

    public CustomButton(int prefWidth, int prefHeight, String Icon){
        super();
        setPrefSize(prefWidth,prefHeight);
        setMinSize(prefWidth,prefHeight);
        setMaxSize(prefWidth,prefHeight);
        graphicProperty().setValue(new ImageView(Icon));
        //getStylesheets().add();
    }

}
