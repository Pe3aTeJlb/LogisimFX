package com.cburch.LogisimFX.newgui;

import com.cburch.LogisimFX.proj.Project;
import javafx.stage.Stage;

public abstract class AbstractController {

    private Stage stage;

    public abstract void postInitialization(Stage s);
    public abstract void linkProjectReference(Project project);
    public abstract void onClose();

}
