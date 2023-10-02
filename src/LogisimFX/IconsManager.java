/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class IconsManager {

    private static final String path = "LogisimFX/resources/icons";

    private static final String logoPath = "LogisimFX/resources/logo";

    public static final Image LogisimFX = new Image("LogisimFX/resources/logo/logisimfx_128.png");

    public static Node getLogisimFXPrompt() {

        Pane pane = new Pane();
        pane.setMouseTransparent(true);

        ImageView img = new ImageView("LogisimFX/resources/logo/LogisimFXPrompt.png");
        img.setMouseTransparent(true);

        pane.setMinSize(0, 0);
        pane.setPrefSize(img.getImage().getWidth(), img.getImage().getHeight());
        pane.setMaxSize(img.getImage().getWidth(), img.getImage().getHeight());

        pane.getChildren().add(img);

        return pane;

    }

    public static ImageView getIcon(String name) {
        return getImageView(name);
    }

    public static ImageView getImageView(String name) {

        Image img = new Image(path + "/" + name);
        return new ImageView(img);

    }

    public static Image getImage(String name){

        Image img = new Image(path + "/" + name);
        return img;

    }

    public static Image getLogo(String name){

        Image img = new Image(logoPath + "/" + name);
        return img;

    }

    public static void initializeDefaultIcons(){
        docklib.utils.IconsManager.setStageIcon(IconsManager.LogisimFX);
    }

}
