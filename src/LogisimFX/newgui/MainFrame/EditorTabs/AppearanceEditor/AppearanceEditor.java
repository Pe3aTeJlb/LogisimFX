package LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor;

import LogisimFX.circuit.Circuit;
import LogisimFX.newgui.MainFrame.SystemTabs.AttributesTab.AttributeTable;
import LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor.appearanceCanvas.AppearanceCanvas;
import LogisimFX.newgui.MainFrame.CustomMenuBar;
import LogisimFX.newgui.MainFrame.EditorTabs.EditorBase;
import LogisimFX.proj.Project;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class AppearanceEditor extends EditorBase {

    private AppearanceCanvas appearanceCanvas;
    private AppearanceEditorToolBar toolBar;

    private Circuit circ;

    public AppearanceEditor(Project project, Circuit circ) {

        super(project);

        this.circ = circ;

        AnchorPane canvasRoot = new AnchorPane();
        canvasRoot.setMinSize(0,0);

        appearanceCanvas = new AppearanceCanvas(canvasRoot, proj, circ);
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

    public void terminateListeners(CustomMenuBar menuBar, AttributeTable attributeTable){
        toolBar.terminateListeners();
        appearanceCanvas.getSelection().removeSelectionListener(menuBar);
        appearanceCanvas.getSelection().removeSelectionListener(attributeTable);
        appearanceCanvas.terminateCanvas();
    }

}
