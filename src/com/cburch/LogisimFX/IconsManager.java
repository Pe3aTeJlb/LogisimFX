package com.cburch.LogisimFX;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class IconsManager {

    private static final String path = "com/cburch/LogisimFX/resources/icons";

    public static ImageView getIcon(String name) {

        System.out.println(path + "/" + name);
        Image img = new Image(path + "/" + name);

        if(img != null){
            return new ImageView(img);
        }else{
            return null;
        }

    }

}
