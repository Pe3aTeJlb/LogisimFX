/*
 * This file is part of LogisimFX. Copyright (c) 2023, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.MainFrame.EditorTabs.CodeEditor;

import LogisimFX.newgui.MainFrame.EditorTabs.EditHandler;
import javafx.scene.input.Clipboard;

public class CodeEditHandler extends EditHandler {

    private CodeEditor codeEditor;

    public CodeEditHandler(CodeEditor editor){
        codeEditor = editor;
    }

    @Override
    public boolean computeEnabled(String from) {

        if(from.equals("UNDO")){ return codeEditor.getCodeArea().getUndoManager().isUndoAvailable();}
        if(from.equals("REDO")){ return codeEditor.getCodeArea().getUndoManager().isRedoAvailable();}
        if(from.equals("CUT")){ return !codeEditor.getCodeArea().getSelectedText().isEmpty();}
        if(from.equals("COPY")){ return !codeEditor.getCodeArea().getSelectedText().isEmpty();}
        if(from.equals("PASTE")){ return Clipboard.getSystemClipboard().hasString();}
        if(from.equals("DELETE")){ return !codeEditor.getCodeArea().getSelectedText().isEmpty();}
        if(from.equals("DUPLICATE")){ return !codeEditor.getCodeArea().getSelectedText().isEmpty();}
        if(from.equals("SELECT_ALL")){ return true;}
        return false;

    }

    @Override
    public void zoomIn() {
        codeEditor.zoomIn();
    }

    @Override
    public void zoomOut() {
        codeEditor.zoomOut();
    }

    @Override
    public void toDefaultZoom() {
        codeEditor.toDefaultZoom();
    }

    @Override
    public void undo() {
        codeEditor.undo();
    }

    @Override
    public void redo() {
        codeEditor.redo();
    }

    @Override
    public void cut() {
        codeEditor.cut();
    }

    @Override
    public void copy() {
        codeEditor.copy();
    }

    @Override
    public void paste() {
        codeEditor.paste();
    }

    @Override
    public void delete() {
        codeEditor.delete();
    }

    @Override
    public void duplicate() {
        codeEditor.duplicate();
    }

    @Override
    public void selectAll() {
        codeEditor.selectAll();
    }

    public void find(){
        codeEditor.openFindBar();
    }

    public void replace(){
        codeEditor.openReplaceBar();
    }

}
