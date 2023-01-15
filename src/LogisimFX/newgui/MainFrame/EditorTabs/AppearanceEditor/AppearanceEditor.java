package LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor;

import LogisimFX.newgui.MainFrame.Canvas.appearanceCanvas.AppearanceCanvas;
import LogisimFX.newgui.MainFrame.EditorTabs.EditorBase;
import LogisimFX.proj.Project;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class AppearanceEditor extends EditorBase {

    private AppearanceCanvas appearanceCanvas;
    private AppearanceEditorToolBar toolBar;

    public AppearanceEditor(Project project) {

        super(project);

        AnchorPane canvasRoot = new AnchorPane();
        canvasRoot.setMinSize(0,0);

        appearanceCanvas = new AppearanceCanvas(canvasRoot, proj);
        canvasRoot.getChildren().add(appearanceCanvas);

        toolBar = new AppearanceEditorToolBar(proj, this);

        VBox.setVgrow(canvasRoot, Priority.ALWAYS);

        this.getChildren().addAll(toolBar, canvasRoot);

    }

    public AppearanceCanvas getAppearanceCanvas(){
        return appearanceCanvas;
    }

    public AppearanceEditorToolBar getLayoutEditorToolBar(){
        return toolBar;
    }

    @Override
    public void terminateListeners(){
        toolBar.terminateListeners();
        appearanceCanvas.terminateCanvas();
    }

}
