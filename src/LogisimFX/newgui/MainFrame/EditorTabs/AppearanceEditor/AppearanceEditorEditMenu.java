package LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor;

import LogisimFX.circuit.Circuit;
import LogisimFX.localization.LC_menu;
import LogisimFX.localization.Localizer;
import LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor.appearanceCanvas.AppearanceEditHandler;
import LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor.appearanceCanvas.SelectionEvent;
import LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor.appearanceCanvas.SelectionListener;
import LogisimFX.proj.Action;
import LogisimFX.proj.Project;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;

import java.util.List;

public class AppearanceEditorEditMenu implements SelectionListener {

    private static Localizer localizer = LC_menu.getInstance();

    private List<MenuItem> menuItems;
    private AppearanceEditor appearanceEditor;
    private AppearanceEditHandler editHandler;
    private Project proj;
    private Circuit circ;

    private final MenuItem Undo,
                            Redo,
                            Cut,
                            Copy,
                            Paste,
                            Delete,
                            Duplicate,
                            SelectAll,
                            RaiseSelection,
                            LowerSelection,
                            RiseToTop,
                            LowerToBottom,
                            AddVertex,
                            RemoveVertex;

    public AppearanceEditorEditMenu(AppearanceEditor appearanceEditor){

        this.appearanceEditor = appearanceEditor;
        this.proj = appearanceEditor.getProj();
        this.circ = appearanceEditor.getCirc();
        editHandler = new AppearanceEditHandler(appearanceEditor.getAppearanceCanvas());

        Undo = new MenuItem();
        Undo.setAccelerator(KeyCombination.keyCombination("Ctrl+Z"));
        Undo.textProperty().bind(localizer.createStringBinding("editCantUndoItem"));
        Undo.disableProperty().bind(appearanceEditor.undoAvailableProperty().not());
        Undo.disableProperty().addListener(change ->{
            Undo.textProperty().unbind();
            Undo.textProperty().bind(
                    appearanceEditor.getLastAction() == null
                            ? localizer.createStringBinding("editCantUndoItem")
                            : localizer.createComplexStringBinding("editUndoItem", appearanceEditor.getLastAction().getName())
            );
        });
        Undo.setOnAction(event -> appearanceEditor.undo());

        Redo = new MenuItem();
        Redo.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+Z"));
        Redo.textProperty().bind(localizer.createStringBinding("editCantRedoItem"));
        Redo.disableProperty().bind(appearanceEditor.redoAvailableProperty().not());
        Redo.disableProperty().addListener(change ->{
            Redo.textProperty().unbind();
            Redo.textProperty().bind(
                    appearanceEditor.getLastRedoAction() == null
                            ? localizer.createStringBinding("editCantRedoItem")
                            : localizer.createComplexStringBinding("editRedoItem", appearanceEditor.getLastRedoAction().getName())
            );
        });
        Redo.setOnAction(event -> appearanceEditor.redo());


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

        SeparatorMenuItem sp3 = new SeparatorMenuItem();


        RaiseSelection = new MenuItem();
        RaiseSelection.setAccelerator(KeyCombination.keyCombination("Ctrl+Up"));
        RaiseSelection.textProperty().bind(localizer.createStringBinding("editLowerItem"));
        RaiseSelection.setDisable(editHandler == null || !editHandler.computeEnabled("RAISE"));
        RaiseSelection.setOnAction(event -> {
            editHandler.raise();
            calculateEnabled();
        });

        LowerSelection = new MenuItem();
        LowerSelection.setAccelerator(KeyCombination.keyCombination("Ctrl+Down"));
        LowerSelection.textProperty().bind(localizer.createStringBinding("editRaiseItem"));
        LowerSelection.setDisable(editHandler == null || !editHandler.computeEnabled("LOWER"));
        LowerSelection.setOnAction(event -> {
            editHandler.lower();
            calculateEnabled();
        });

        RiseToTop = new MenuItem();
        RiseToTop.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+Up"));
        RiseToTop.textProperty().bind(localizer.createStringBinding("editRaiseTopItem"));
        RiseToTop.setDisable(editHandler == null || !editHandler.computeEnabled("RAISE_TOP"));
        RiseToTop.setOnAction(event -> {
            editHandler.raiseTop();
            calculateEnabled();
        });

        LowerToBottom = new MenuItem();
        LowerToBottom.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+Down"));
        LowerToBottom.textProperty().bind(localizer.createStringBinding("editLowerBottomItem"));
        LowerToBottom.setDisable(editHandler == null || !editHandler.computeEnabled("LOWER_BOTTOM"));
        LowerToBottom.setOnAction(event -> {
            editHandler.lowerBottom();
            calculateEnabled();
        });


        SeparatorMenuItem sp4 = new SeparatorMenuItem();


        AddVertex = new MenuItem();
        AddVertex.textProperty().bind(localizer.createStringBinding("editAddControlItem"));
        AddVertex.setDisable(editHandler == null || !editHandler.computeEnabled("ADD_CONTROL"));
        AddVertex.setOnAction(event -> {
            editHandler.addControlPoint();
            calculateEnabled();
        });

        RemoveVertex = new MenuItem();
        RemoveVertex.textProperty().bind(localizer.createStringBinding("editRemoveControlItem"));
        RemoveVertex.setDisable(editHandler == null || !editHandler.computeEnabled("REMOVE_CONTROL"));
        RemoveVertex.setOnAction(event -> {
            editHandler.removeControlPoint();
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
                SelectAll,
                sp3,
                RaiseSelection,
                LowerSelection,
                RiseToTop,
                LowerToBottom,
                sp4,
                AddVertex,
                RemoveVertex
        );

    }

    private void calculateEnabled(){

        Cut.setDisable(editHandler == null || !editHandler.computeEnabled("CUT"));
        Copy.setDisable(editHandler == null || !editHandler.computeEnabled("COPY"));
        Paste.setDisable(editHandler == null || !editHandler.computeEnabled("PASTE"));
        Delete.setDisable(editHandler == null || !editHandler.computeEnabled("DELETE"));
        Duplicate.setDisable(editHandler == null || !editHandler.computeEnabled("DUPLICATE"));
        SelectAll.setDisable(editHandler == null || !editHandler.computeEnabled("SELECT_ALL"));
        RaiseSelection.setDisable(editHandler == null || !editHandler.computeEnabled("RAISE"));
        LowerSelection.setDisable(editHandler == null || !editHandler.computeEnabled("LOWER"));
        RiseToTop.setDisable(editHandler == null || !editHandler.computeEnabled("RAISE_TOP"));
        LowerToBottom.setDisable(editHandler == null || !editHandler.computeEnabled("LOWER_BOTTOM"));
        AddVertex.setDisable(editHandler == null || !editHandler.computeEnabled("ADD_CONTROL"));
        RemoveVertex.setDisable(editHandler == null || !editHandler.computeEnabled("REMOVE_CONTROL"));

    }

    public List<MenuItem> getMenuItems(){
        return menuItems;
    }

    @Override
    public void selectionChanged(SelectionEvent e) {
        calculateEnabled();
    }
}
