package com.cburch.LogisimFX.newgui.HexEditorFrame;

import com.cburch.LogisimFX.newgui.AbstractController;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.LogisimFX.std.memory.MemContents;

import javafx.stage.Stage;

public class HexEditorController extends AbstractController {

    private Stage stage;

    @Override
    public void postInitialization(Stage s, Project project) {

        stage = s;

    }

    public void openHex(MemContents memContents){



    }

    @Override
    public void onClose() {

    }

}
