package LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor;

import LogisimFX.circuit.Circuit;
import LogisimFX.newgui.MainFrame.AttributeTable;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.LayoutCanvas;
import LogisimFX.newgui.MainFrame.CustomMenuBar;
import LogisimFX.newgui.MainFrame.EditorTabs.EditorBase;
import LogisimFX.proj.Project;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LayoutEditor extends EditorBase {

    private LayoutCanvas layoutCanvas;
    private LayoutEditorToolBar toolBar;

    private Circuit circ;

    public LayoutEditor(Project project, Circuit circ){

        super(project);

        this.circ = circ;

        AnchorPane canvasRoot = new AnchorPane();
        canvasRoot.setMinSize(0,0);

        layoutCanvas = new LayoutCanvas(canvasRoot, proj, circ);
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

    public void terminateListeners(CustomMenuBar menuBar, AttributeTable attributeTable){
        toolBar.terminateListeners();
        layoutCanvas.getSelection().removeListener(menuBar);
        layoutCanvas.getSelection().removeListener(attributeTable);
        layoutCanvas.terminateCanvas();
    }

}
