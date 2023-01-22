package LogisimFX.newgui.MainFrame.EditorTabs.CodeEditor;

import LogisimFX.circuit.Circuit;
import LogisimFX.localization.LC_menu;
import LogisimFX.localization.Localizer;
import LogisimFX.proj.Project;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;

import java.util.List;

public class CodeEditorEditMenu {

    private static Localizer localizer = LC_menu.getInstance();

    private List<MenuItem> menuItems;
    private CodeEditor codeEditor;
    private CodeEditHandler editHandler;
    private Project proj;
    private Circuit circ;

    private final MenuItem Undo,
                            Redo,
                            Cut,
                            Copy,
                            Paste,
                            Delete,
                            Duplicate,
                            SelectAll;

    public CodeEditorEditMenu(CodeEditor codeEditor){

        this.codeEditor = codeEditor;
        this.proj = codeEditor.getProj();
        this.circ = codeEditor.getCirc();
        editHandler = new CodeEditHandler(codeEditor);

        Undo = new MenuItem();
        Undo.setAccelerator(KeyCombination.keyCombination("Ctrl+Z"));
        Undo.textProperty().bind(localizer.createStringBinding("editCantUndoItem"));
        Undo.setDisable(true);
        Undo.setOnAction(event -> {
            proj.undoAction();
            calculateEnabled();
        });

        Redo = new MenuItem();
        Redo.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+Z"));
        Redo.textProperty().bind(localizer.createStringBinding("editCantRedoItem"));
        Redo.setDisable(true);
        Redo.setOnAction(event -> {
            proj.undoAction();
            calculateEnabled();
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
            Undo.textProperty().bind(localizer.createComplexStringBinding("editUndoItem", ""));
        }

        Redo.setDisable(editHandler == null || !editHandler.computeEnabled("REDO"));
        Redo.textProperty().unbind();
        if (Redo.isDisable()){
            Redo.textProperty().bind(localizer.createStringBinding("editCantRedoItem"));
        } else {
            Redo.textProperty().bind(localizer.createComplexStringBinding("editRedoItem", ""));
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
