/*
 * This file is part of LogisimFX. Copyright (c) 2023, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor;

import LogisimFX.circuit.Circuit;
import LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor.appearanceCanvas.AppearanceEditHandler;
import LogisimFX.newgui.MainFrame.EditorTabs.CodeEditor.CodeEditorEditMenu;
import LogisimFX.newgui.MainFrame.EditorTabs.EditHandler;
import LogisimFX.newgui.MainFrame.LC;
import LogisimFX.newgui.MainFrame.SystemTabs.AttributesTab.AttributeTable;
import LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor.appearanceCanvas.AppearanceCanvas;
import LogisimFX.newgui.MainFrame.CustomMenuBar;
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

public class AppearanceEditor extends EditorBase {

    private AppearanceCanvas appearanceCanvas;
    private AppearanceEditorToolBar toolBar;
    private HBox footBar;

    private AppearanceEditHandler editHandler;
    private AppearanceEditorEditMenu menu;

    private Circuit circ;

    public AppearanceEditor(Project project, Circuit circ) {

        super(project, circ);

        this.circ = circ;

        AnchorPane canvasRoot = new AnchorPane();
        canvasRoot.setMinSize(0,0);

        appearanceCanvas = new AppearanceCanvas(canvasRoot, this);
        canvasRoot.getChildren().add(appearanceCanvas);

        toolBar = new AppearanceEditorToolBar(proj, this);
        toolBar.setOnMousePressed(event -> Event.fireEvent(this, event.copyFor(event.getSource(), this)));

        VBox.setVgrow(canvasRoot, Priority.ALWAYS);

        editHandler = new AppearanceEditHandler(getAppearanceCanvas());
        menu = new AppearanceEditorEditMenu(this);
        appearanceCanvas.getSelection().addSelectionListener(menu);

        footBar = new HBox();
        footBar.setAlignment(Pos.CENTER_RIGHT);
        footBar.setSpacing(5);

        Label info = new Label();
        HBox.setHgrow(info, Priority.ALWAYS);
        info.textProperty().bind(
                Bindings.concat(
                        LC.createStringBinding("canvasX"),
                        appearanceCanvas.mouseXProperty,
                        " ",
                        LC.createStringBinding("canvasY"),
                        appearanceCanvas.mouseYProperty,
                        " ",
                        LC.createStringBinding("canvasZoom"),
                        appearanceCanvas.zoomProperty,
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

    public AppearanceCanvas getAppearanceCanvas(){
        return appearanceCanvas;
    }

    public AppearanceEditorToolBar getLayoutEditorToolBar(){
        return toolBar;
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
        appearanceCanvas.getSelection().removeSelectionListener(menu);
        appearanceCanvas.getSelection().removeSelectionListener(proj.getFrameController().getAttributeTable());
        appearanceCanvas.terminateCanvas();
    }

}
