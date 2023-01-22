package LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor;

import LogisimFX.circuit.Circuit;
import LogisimFX.newgui.MainFrame.EditorTabs.CodeEditor.CodeEditorEditMenu;
import LogisimFX.newgui.MainFrame.SystemTabs.AttributesTab.AttributeTable;
import LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor.appearanceCanvas.AppearanceCanvas;
import LogisimFX.newgui.MainFrame.CustomMenuBar;
import LogisimFX.newgui.MainFrame.EditorTabs.EditorBase;
import LogisimFX.proj.Project;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class AppearanceEditor extends EditorBase {

    private AppearanceCanvas appearanceCanvas;
    private AppearanceEditorToolBar toolBar;

    private AppearanceEditorEditMenu menu;

    private Circuit circ;

    public AppearanceEditor(Project project, Circuit circ) {

        super(project);

        this.circ = circ;

        AnchorPane canvasRoot = new AnchorPane();
        canvasRoot.setMinSize(0,0);

        appearanceCanvas = new AppearanceCanvas(canvasRoot, this);
        canvasRoot.getChildren().add(appearanceCanvas);

        toolBar = new AppearanceEditorToolBar(proj, this);

        VBox.setVgrow(canvasRoot, Priority.ALWAYS);

        menu = new AppearanceEditorEditMenu(this);
        appearanceCanvas.getSelection().addSelectionListener(menu);

        this.getChildren().addAll(toolBar, canvasRoot);

    }

    public List<MenuItem> getEditMenuItems(){
        return menu.getMenuItems();
    }

    public Circuit getCirc() {
        return circ;
    }

    public AppearanceCanvas getAppearanceCanvas(){
        return appearanceCanvas;
    }

    public AppearanceEditorToolBar getLayoutEditorToolBar(){
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
        appearanceCanvas.getSelection().removeSelectionListener(menu);
        appearanceCanvas.getSelection().removeSelectionListener(proj.getFrameController().getAttributeTable());
        appearanceCanvas.terminateCanvas();
    }

}
