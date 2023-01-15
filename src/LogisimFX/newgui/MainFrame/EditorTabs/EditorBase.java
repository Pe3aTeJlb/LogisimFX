package LogisimFX.newgui.MainFrame.EditorTabs;

import LogisimFX.proj.Project;
import javafx.scene.layout.VBox;

public class EditorBase extends VBox {

    protected Project proj;

    public EditorBase(Project project){
        super();
        this.proj = project;
    }

    public Project getProj() {
        return proj;
    }

    public boolean isSelected() {
        return proj.getFrameController().getEditor() == this;
    }

    public void terminateListeners(){ }

}
