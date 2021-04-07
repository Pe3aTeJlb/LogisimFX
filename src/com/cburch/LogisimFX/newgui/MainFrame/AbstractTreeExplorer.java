package com.cburch.LogisimFX.newgui.MainFrame;

import com.cburch.LogisimFX.proj.Project;
import javafx.scene.control.TreeView;

public abstract class AbstractTreeExplorer extends TreeView {

    private Project proj;

    public abstract void updateTree();


}
