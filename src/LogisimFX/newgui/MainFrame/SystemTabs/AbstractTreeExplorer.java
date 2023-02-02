/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.MainFrame.SystemTabs;

import LogisimFX.proj.Project;
import javafx.scene.control.TreeView;

public abstract class AbstractTreeExplorer<T> extends TreeView<T> {

    private Project proj;

    public abstract void updateTree();


}
