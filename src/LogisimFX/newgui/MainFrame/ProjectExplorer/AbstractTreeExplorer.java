package LogisimFX.newgui.MainFrame.ProjectExplorer;

import LogisimFX.proj.Project;
import javafx.scene.control.TreeView;

public abstract class AbstractTreeExplorer extends TreeView {

    private Project proj;

    public abstract void updateTree();


}
