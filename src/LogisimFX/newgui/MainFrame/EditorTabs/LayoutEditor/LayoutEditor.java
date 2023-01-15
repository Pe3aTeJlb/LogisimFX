package LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor;

import LogisimFX.newgui.MainFrame.Canvas.layoutCanvas.LayoutCanvas;
import LogisimFX.newgui.MainFrame.EditorTabs.EditorBase;
import LogisimFX.proj.Project;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LayoutEditor extends EditorBase {

    private LayoutCanvas layoutCanvas;
    private LayoutEditorToolBar toolBar;

    public LayoutEditor(Project project){

        super(project);

        AnchorPane canvasRoot = new AnchorPane();
        canvasRoot.setMinSize(0,0);

        layoutCanvas = new LayoutCanvas(canvasRoot, proj);
        canvasRoot.getChildren().add(layoutCanvas);

        toolBar = new LayoutEditorToolBar(proj, this);

        VBox.setVgrow(canvasRoot, Priority.ALWAYS);

        this.getChildren().addAll(toolBar, canvasRoot);

    }

    public LayoutCanvas getLayoutCanvas(){
        return layoutCanvas;
    }

    public LayoutEditorToolBar getLayoutEditorToolBar(){
        return toolBar;
    }

    @Override
    public void terminateListeners(){
        toolBar.terminateListeners();
        layoutCanvas.terminateCanvas();
    }

}
