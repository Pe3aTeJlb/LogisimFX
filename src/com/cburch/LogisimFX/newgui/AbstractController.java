package com.cburch.LogisimFX.newgui;

import javafx.stage.Stage;

public abstract class AbstractController {

    public abstract void prepareFrame(Stage s);
    public abstract void setStageTitle();
    public abstract void onClose();

}
