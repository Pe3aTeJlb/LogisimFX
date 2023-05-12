/*
 * This file is part of LogisimFX. Copyright (c) 2023, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.MainFrame.EditorTabs.TextEditor;

import LogisimFX.localization.LC_menu;
import LogisimFX.localization.Localizer;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;

import java.util.List;

public class TextEditorEditMenu {

    private static Localizer localizer = LC_menu.getInstance();

    private List<MenuItem> menuItems;
    private TextEditHandler editHandler;

    private final MenuItem Undo,
                            Redo,
                            Find,
                            Replace,
                            Cut,
                            Copy,
                            Paste,
                            Delete,
                            Duplicate,
                            SelectAll;

    public TextEditorEditMenu(TextEditor textEditor){

        editHandler = (TextEditHandler) textEditor.getEditHandler();

        textEditor.getCodeArea().getCaretSelectionBind().selectedTextProperty().addListener(change -> calculateEnabled());

        Undo = new MenuItem();
        Undo.setAccelerator(KeyCombination.keyCombination("Ctrl+Z"));
        Undo.textProperty().bind(localizer.createStringBinding("editCantUndoItem"));
        Undo.setDisable(editHandler == null || !editHandler.computeEnabled("UNDO"));
        Undo.setOnAction(event -> {
            editHandler.undo();
            calculateEnabled();
        });

        Redo = new MenuItem();
        Redo.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+Z"));
        Redo.textProperty().bind(localizer.createStringBinding("editCantRedoItem"));
        Redo.setDisable(editHandler == null || !editHandler.computeEnabled("REDO"));
        Redo.setOnAction(event -> {
            editHandler.redo();
            calculateEnabled();
        });

        SeparatorMenuItem sp = new SeparatorMenuItem();

        Find = new MenuItem();
        Find.setAccelerator(KeyCombination.keyCombination("Ctrl+F"));
        Find.textProperty().bind(localizer.createStringBinding("editFindItem"));
        Find.setOnAction(event -> {
            editHandler.find();
        });

        Replace = new MenuItem();
        Replace.setAccelerator(KeyCombination.keyCombination("Ctrl+R"));
        Replace.textProperty().bind(localizer.createStringBinding("editReplaceItem"));
        Replace.setOnAction(event -> {
            editHandler.replace();
        });

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
                sp,
                Find,
                Replace,
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

        Undo.setDisable(editHandler == null || !editHandler.computeEnabled("UNDO"));
        Undo.textProperty().unbind();
        if (Undo.isDisable()){
            Undo.textProperty().bind(localizer.createStringBinding("editCantUndoItem"));
        } else {
            Undo.textProperty().bind(localizer.createStringBinding("editUndoItemSimple"));
        }

        Redo.setDisable(editHandler == null || !editHandler.computeEnabled("REDO"));
        Redo.textProperty().unbind();
        if (Redo.isDisable()){
            Redo.textProperty().bind(localizer.createStringBinding("editCantRedoItem"));
        } else {
            Redo.textProperty().bind(localizer.createStringBinding("editRedoItemSimple"));
        }

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

}