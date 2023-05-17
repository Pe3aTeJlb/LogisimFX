/*
 * This file is part of LogisimFX. Copyright (c) 2023, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor;

import LogisimFX.localization.LC_menu;
import LogisimFX.localization.Localizer;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.LayoutEditHandler;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.Selection;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;

import java.util.List;

public class LayoutEditorEditMenu implements Selection.Listener{

    private static Localizer localizer = LC_menu.getInstance();

    private List<MenuItem> menuItems;
    private LayoutEditHandler editHandler;

    private final MenuItem Undo,
                            Redo,
                            Find,
                            Cut,
                            Copy,
                            Paste,
                            Delete,
                            Duplicate,
                            SelectAll;

    public LayoutEditorEditMenu(LayoutEditor layoutEditor){

        editHandler = (LayoutEditHandler) layoutEditor.getEditHandler();

        Undo = new MenuItem();
        Undo.setAccelerator(KeyCombination.keyCombination("Ctrl+Z"));
        Undo.textProperty().bind(localizer.createStringBinding("editCantUndoItem"));
        Undo.disableProperty().bind(layoutEditor.undoAvailableProperty().not());
        Undo.disableProperty().addListener(change ->{
            Undo.textProperty().unbind();
            Undo.textProperty().bind(
                    layoutEditor.getLastAction() == null
                            ? localizer.createStringBinding("editCantUndoItem")
                            : localizer.createComplexStringBinding("editUndoItem", layoutEditor.getLastAction().getName())
            );
        });
        Undo.setOnAction(event -> layoutEditor.undo());

        Redo = new MenuItem();
        Redo.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+Z"));
        Redo.textProperty().bind(localizer.createStringBinding("editCantRedoItem"));
        Redo.disableProperty().bind(layoutEditor.redoAvailableProperty().not());
        Redo.disableProperty().addListener(change ->{
            Redo.textProperty().unbind();
            Redo.textProperty().bind(
                    layoutEditor.getLastRedoAction() == null
                            ? localizer.createStringBinding("editCantRedoItem")
                            : localizer.createComplexStringBinding("editRedoItem", layoutEditor.getLastRedoAction().getName())
            );
        });
        Redo.setOnAction(event -> layoutEditor.redo());

        Find = new MenuItem();
        Find.setAccelerator(KeyCombination.keyCombination("Ctrl+F"));
        Find.textProperty().bind(localizer.createStringBinding("editFindItem"));
        //Find.setDisable(editHandler == null || !editHandler.computeEnabled("FIND"));
        Find.setOnAction(event -> layoutEditor.triggerFindBar());


        SeparatorMenuItem sp1 = new SeparatorMenuItem();


        Cut = new MenuItem();
        Cut.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));
        Cut.textProperty().bind(localizer.createStringBinding("editCutItem"));
        Cut.setDisable(editHandler == null || !editHandler.computeEnabled("CUT"));
        Cut.setOnAction(event -> {
            editHandler.cut();
            calculateEnabled();
        });

        Copy = new MenuItem();
        Copy.setAccelerator(KeyCombination.keyCombination("Ctrl+C"));
        Copy.textProperty().bind(localizer.createStringBinding("editCopyItem"));
        Copy.setDisable(editHandler == null || !editHandler.computeEnabled("COPY"));
        Copy.setOnAction(event -> {
            editHandler.copy();
            calculateEnabled();
        });

        Paste = new MenuItem();
        Paste.setAccelerator(KeyCombination.keyCombination("Ctrl+V"));
        Paste.textProperty().bind(localizer.createStringBinding("editPasteItem"));
        Paste.setDisable(editHandler == null || !editHandler.computeEnabled("PASTE"));
        Paste.setOnAction(event -> {
            editHandler.paste();
            calculateEnabled();
        });


        SeparatorMenuItem sp2 = new SeparatorMenuItem();


        Delete = new MenuItem();
        Delete.setAccelerator(KeyCombination.keyCombination("Delete"));
        Delete.textProperty().bind(localizer.createStringBinding("editClearItem"));
        Delete.setDisable(editHandler == null || !editHandler.computeEnabled("DELETE"));
        Delete.setOnAction(event -> {
            editHandler.delete();
            calculateEnabled();
        });

        Duplicate = new MenuItem();
        Duplicate.setAccelerator(KeyCombination.keyCombination("Ctrl+D"));
        Duplicate.textProperty().bind(localizer.createStringBinding("editDuplicateItem"));
        Duplicate.setDisable(editHandler == null || !editHandler.computeEnabled("DUPLICATE"));
        Duplicate.setOnAction(event -> {
            editHandler.duplicate();
            calculateEnabled();
        });

        SelectAll = new MenuItem();
        SelectAll.setAccelerator(KeyCombination.keyCombination("Ctrl+A"));
        SelectAll.textProperty().bind(localizer.createStringBinding("editSelectAllItem"));
        SelectAll.setDisable(editHandler == null || !editHandler.computeEnabled("SELECT_ALL"));
        SelectAll.setOnAction(event -> {
            editHandler.selectAll();
            calculateEnabled();
        });

        menuItems = List.of(
                Undo,
                Redo,
                Find,
                sp1,
                Cut,
                Copy,
                Paste,
                sp2,
                Delete,
                Duplicate,
                SelectAll
        );

    }

    private void calculateEnabled(){

        //Find.setDisable(editHandler == null || !editHandler.computeEnabled("FIND"));
        Cut.setDisable(editHandler == null || !editHandler.computeEnabled("CUT"));
        Copy.setDisable(editHandler == null || !editHandler.computeEnabled("COPY"));
        Paste.setDisable(editHandler == null || !editHandler.computeEnabled("PASTE"));
        Delete.setDisable(editHandler == null || !editHandler.computeEnabled("DELETE"));
        Duplicate.setDisable(editHandler == null || !editHandler.computeEnabled("DUPLICATE"));
        SelectAll.setDisable(editHandler == null || !editHandler.computeEnabled("SELECT_ALL"));

    }

    public List<MenuItem> getMenuItems(){
        return menuItems;
    }

    @Override
    public void selectionChanged(Selection.Event event) {
        calculateEnabled();
    }
}
