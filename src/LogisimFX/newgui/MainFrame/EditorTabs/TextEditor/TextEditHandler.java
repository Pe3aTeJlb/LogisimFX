/*
 * This file is part of LogisimFX. Copyright (c) 2023, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.MainFrame.EditorTabs.TextEditor;

import LogisimFX.newgui.MainFrame.EditorTabs.EditHandler;
import javafx.scene.input.Clipboard;

public class TextEditHandler extends EditHandler {

    private TextEditor textEditor;

    public TextEditHandler(TextEditor editor){
        textEditor = editor;
    }

    @Override
    public boolean computeEnabled(String from) {

        if(from.equals("UNDO")){ return textEditor.getCodeArea().getUndoManager().isUndoAvailable();}
        if(from.equals("REDO")){ return textEditor.getCodeArea().getUndoManager().isRedoAvailable();}
        if(from.equals("CUT")){ return !textEditor.getCodeArea().getSelectedText().isEmpty();}
        if(from.equals("COPY")){ return !textEditor.getCodeArea().getSelectedText().isEmpty();}
        if(from.equals("PASTE")){ return Clipboard.getSystemClipboard().hasString();}
        if(from.equals("DELETE")){ return !textEditor.getCodeArea().getSelectedText().isEmpty();}
        if(from.equals("DUPLICATE")){ return !textEditor.getCodeArea().getSelectedText().isEmpty();}
        if(from.equals("SELECT_ALL")){ return true;}
        return false;

    }

    @Override
    public void zoomIn() {
        textEditor.zoomIn();
    }

    @Override
    public void zoomOut() {
        textEditor.zoomOut();
    }

    @Override
    public void toDefaultZoom() {
        textEditor.toDefaultZoom();
    }

    @Override
    public void undo() {
        textEditor.undo();
    }

    @Override
    public void redo() {
        textEditor.redo();
    }

    @Override
    public void cut() {
        textEditor.cut();
    }

    @Override
    public void copy() {
        textEditor.copy();
    }

    @Override
    public void paste() {
        textEditor.paste();
    }

    @Override
    public void delete() {
        textEditor.delete();
    }

    @Override
    public void duplicate() {
        textEditor.duplicate();
    }

    @Override
    public void selectAll() {
        textEditor.selectAll();
    }

    public void find(){
        textEditor.openFindBar();
    }

    public void replace(){
        textEditor.openReplaceBar();
    }

}
