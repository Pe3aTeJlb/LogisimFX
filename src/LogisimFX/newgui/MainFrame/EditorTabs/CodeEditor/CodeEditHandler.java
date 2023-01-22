package LogisimFX.newgui.MainFrame.EditorTabs.CodeEditor;

import LogisimFX.newgui.MainFrame.EditorTabs.EditHandler;

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
        if(from.equals("PASTE")){ return !codeEditor.getCodeArea().getSelectedText().isEmpty();}
        if(from.equals("DELETE")){ return !codeEditor.getCodeArea().getSelectedText().isEmpty();}
        if(from.equals("DUPLICATE")){ return !codeEditor.getCodeArea().getSelectedText().isEmpty();}
        if(from.equals("SELECT_ALL")){ return true;}
        return false;

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

    @Override
    public void raise() {

    }

    @Override
    public void lower() {

    }

    @Override
    public void raiseTop() {

    }

    @Override
    public void lowerBottom() {

    }

    @Override
    public void addControlPoint() {

    }

    @Override
    public void removeControlPoint() {

    }

}
