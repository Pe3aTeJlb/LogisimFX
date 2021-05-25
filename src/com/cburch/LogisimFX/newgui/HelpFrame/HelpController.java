package com.cburch.LogisimFX.newgui.HelpFrame;

import com.cburch.LogisimFX.newgui.AbstractController;
import com.cburch.LogisimFX.newgui.DialogManager;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.help.HelpSet;
import javax.help.JHelp;
import javax.swing.*;
import java.net.URL;

public class HelpController extends AbstractController {

    private Stage stage;

    @FXML
    private AnchorPane Root;


    private final SwingNode SwingContainer = new SwingNode();

    private HelpSet helpSet;
    private String helpSetUrl = "";
    private JHelp helpComponent;

    @FXML
    public void initialize(){
        
        String helpUrl = "com/cburch/"+LC.get("helpsetUrl");
        if (helpUrl == null) helpUrl = "com/cburch/LogisimFX/resources/doc/doc_en.hs";

        if (helpSet == null || !helpUrl.equals(helpSetUrl)) {

            ClassLoader loader = HelpController.class.getClassLoader();

            try {

                URL hsURL = HelpSet.findHelpSet(loader, helpUrl);

                if (hsURL == null) {
                    DialogManager.CreateErrorDialog("Error",LC.get("helpNotFoundError"));
                    return;
                }

                helpSetUrl = helpUrl;
                helpSet = new HelpSet(null, hsURL);
                helpComponent = new JHelp(helpSet);

            } catch (Exception e) {
                e.printStackTrace();
                DialogManager.CreateErrorDialog("Error",LC.get("helpUnavailableError"));
                return;
            }

        }

        createSwingContent(SwingContainer);

        AnchorPane.setLeftAnchor(SwingContainer,0.0);
        AnchorPane.setTopAnchor(SwingContainer,0.0);
        AnchorPane.setRightAnchor(SwingContainer,0.0);
        AnchorPane.setBottomAnchor(SwingContainer,0.0);

        Root.getChildren().add(SwingContainer);

    }

    @Override
    public void postInitialization(Stage s) {
        stage = s;
        stage.titleProperty().bind(LC.createStringBinding("helpWindowTitle"));
    }

    private void createSwingContent(final SwingNode swingNode) {
        SwingUtilities.invokeLater(() -> {
            swingNode.setContent(helpComponent);
            swingNode.requestFocus();
        });
    }

    public void openChapter(String chapter){
        helpComponent.setCurrentID(chapter);
    }


    @Override
    public void onClose() {
        System.out.println("Help closed");
    }

}
