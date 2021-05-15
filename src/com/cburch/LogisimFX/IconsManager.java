package com.cburch.LogisimFX;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class IconsManager {

    public static final Image LogisimFX = new Image("com/cburch/LogisimFX/resources/logo/logisimfx_128.png");

    private static final String path = "com/cburch/LogisimFX/resources/icons";

    public static ImageView getIcon(String name) {
        return getImageView(name);
    }



    public static ImageView getImageView(String name) {

        //System.out.println(path + "/" + name);
        Image img = new Image(path + "/" + name);
        return new ImageView(img);

    }

    public static Image getImage(String name){

        Image img = new Image(path + "/" + name);
        return img;

    }

}
