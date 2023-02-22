/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui;

import LogisimFX.proj.Project;
import javafx.stage.Stage;

public abstract class AbstractController {

    private Stage stage;

    public void postInitialization(Stage s){
        stage = s;
    }
    public void postInitialization(Stage s,Project p){
        stage = s;
    }

    public abstract void onClose();

}
