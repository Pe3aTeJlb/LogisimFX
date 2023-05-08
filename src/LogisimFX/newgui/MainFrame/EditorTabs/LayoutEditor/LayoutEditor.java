/*
 * This file is part of LogisimFX. Copyright (c) 2023, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor;

import LogisimFX.circuit.Circuit;
import LogisimFX.newgui.MainFrame.EditorTabs.EditHandler;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.LayoutEditHandler;
import LogisimFX.newgui.MainFrame.LC;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.LayoutCanvas;
import LogisimFX.newgui.MainFrame.EditorTabs.EditorBase;
import LogisimFX.proj.Project;
import javafx.beans.binding.Bindings;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class LayoutEditor extends EditorBase {

    private LayoutCanvas layoutCanvas;
    private LayoutEditorToolBar toolBar;
    private HBox footBar;

    private LayoutEditHandler editHandler;
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
        toolBar.setOnMousePressed(event -> Event.fireEvent(this, event.copyFor(event.getSource(), this)));

        VBox.setVgrow(canvasRoot, Priority.ALWAYS);

        editHandler = new LayoutEditHandler(getLayoutCanvas());
        menu = new LayoutEditorEditMenu(this);
        layoutCanvas.getSelection().addListener(menu);

        footBar = new HBox();
        footBar.setAlignment(Pos.CENTER_RIGHT);
        footBar.setSpacing(5);

        Label info = new Label();
        HBox.setHgrow(info, Priority.ALWAYS);
        info.textProperty().bind(
                Bindings.concat(
                        LC.createStringBinding("canvasX"),
                        layoutCanvas.mouseXProperty,
                        " ",
                        LC.createStringBinding("canvasY"),
                        layoutCanvas.mouseYProperty,
                        " ",
                        LC.createStringBinding("canvasZoom"),
                        layoutCanvas.zoomProperty,
                        "%"
                )
        );

        footBar.getChildren().add(info);

        this.getChildren().addAll(toolBar, canvasRoot, footBar);

    }

    public List<MenuItem> getEditMenuItems(){
        return menu.getMenuItems();
    }

    public EditHandler getEditHandler(){
        return editHandler;
    }

    public LayoutCanvas getLayoutCanvas(){
        return layoutCanvas;
    }

    public LayoutEditorToolBar getLayoutEditorToolBar(){
        return toolBar;
    }

    public Circuit getCirc(){
        return circ;
    }

    @Override
    public String getEditorDescriptor(){
        return circ.getName();
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
        proj.removeProjectListener(projectListener);
        toolBar.terminateListeners();
        layoutCanvas.getSelection().removeListener(menu);
        layoutCanvas.getSelection().removeListener(proj.getFrameController().getAttributeTable());
        layoutCanvas.terminateCanvas();
    }

}
