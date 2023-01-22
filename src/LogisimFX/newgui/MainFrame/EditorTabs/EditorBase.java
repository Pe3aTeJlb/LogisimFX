package LogisimFX.newgui.MainFrame.EditorTabs;

import LogisimFX.newgui.MainFrame.MainFrameController;
import LogisimFX.proj.Project;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;

import java.util.List;

public class EditorBase extends VBox {

    protected Project proj;

    public EditorBase(Project project){
        super();
        this.proj = project;
        proj.getFrameController().editorProperty().addListener((observableValue, handler, t1) -> setIsSelected());
    }

    public Project getProj() {
        return proj;
    }

    private BooleanProperty isSelected;

    private void setIsSelected() {
        editorProperty().set(proj.getFrameController().getEditor() == this);
    }

    public boolean isSelected() {
        return editorProperty().get();
    }

    public BooleanProperty editorProperty() {
        if (isSelected == null) {
            isSelected = new SimpleBooleanProperty(this, "isSelected", false);
        }
        return isSelected;
    }


    public void copyAccelerators(){

    }

    public List<MenuItem> getEditMenuItems(){
        return null;
    }

    public void terminateListeners(){

    }

}
