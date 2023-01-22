package LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor;

import LogisimFX.circuit.Circuit;
import LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor.AppearanceEditorEditMenu;
import LogisimFX.newgui.MainFrame.EditorTabs.CodeEditor.CodeEditorEditMenu;
import LogisimFX.newgui.MainFrame.SystemTabs.AttributesTab.AttributeTable;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.LayoutCanvas;
import LogisimFX.newgui.MainFrame.CustomMenuBar;
import LogisimFX.newgui.MainFrame.EditorTabs.EditorBase;
import LogisimFX.proj.Project;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class LayoutEditor extends EditorBase {

    private LayoutCanvas layoutCanvas;
    private LayoutEditorToolBar toolBar;

    private LayoutEditorEditMenu menu;

    private Circuit circ;

    public LayoutEditor(Project project, Circuit circ){

        super(project);

        this.circ = circ;

        AnchorPane canvasRoot = new AnchorPane();
        canvasRoot.setMinSize(0,0);

        layoutCanvas = new LayoutCanvas(canvasRoot, this);
        canvasRoot.getChildren().add(layoutCanvas);

        toolBar = new LayoutEditorToolBar(proj, this);

        VBox.setVgrow(canvasRoot, Priority.ALWAYS);

        menu = new LayoutEditorEditMenu(this);
        layoutCanvas.getSelection().addListener(menu);

        this.getChildren().addAll(toolBar, canvasRoot);

    }

    public List<MenuItem> getEditMenuItems(){
        return menu.getMenuItems();
    }

    public Circuit getCirc() {
        return circ;
    }

    public LayoutCanvas getLayoutCanvas(){
        return layoutCanvas;
    }

    public LayoutEditorToolBar getLayoutEditorToolBar(){
        return toolBar;
    }

    @Override
    public void copyAccelerators(){
        if (this.getScene() != proj.getFrameController().getStage().getScene()){
            this.getScene().getAccelerators().putAll(
                    proj.getFrameController().getStage().getScene().getAccelerators()
            );
        }
        toolBar.recalculateAccelerators();
    }

    @Override
    public void terminateListeners(){
        toolBar.terminateListeners();
        layoutCanvas.getSelection().removeListener(menu);
        layoutCanvas.getSelection().removeListener(proj.getFrameController().getAttributeTable());
        layoutCanvas.terminateCanvas();
    }

}
