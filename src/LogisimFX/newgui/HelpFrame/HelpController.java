/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.HelpFrame;

import LogisimFX.newgui.AbstractController;
import LogisimFX.newgui.DialogManager;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class HelpController extends AbstractController {

    private Stage stage;

    @FXML
    private WebView webview;

    @FXML
    public void initialize(){
    }

    @Override
    public void postInitialization(Stage s) {

        stage = s;
        stage.titleProperty().bind(LC.createStringBinding("helpWindowTitle"));

    }


    public void openChapter(String chapter){
        webview.getEngine().load(getClass().getResource(LC.get(chapter)).toString());
    }


    @Override
    public void onClose() {
    }

}
