/*
 * This file is part of LogisimFX. Copyright (c) 2023, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.MainFrame.EditorTabs.TerminalTab;

import LogisimFX.newgui.MainFrame.EditorTabs.EditHandler;
import javafx.scene.input.Clipboard;

public class TerminalHandler extends EditHandler {

    private Terminal terminal;

    public TerminalHandler(Terminal terminal){
        this.terminal = terminal;
    }

    @Override
    public boolean computeEnabled(String from) {

        if(from.equals("CUT")){ return !terminal.getTerminalArea().getSelectedText().isEmpty();}
        if(from.equals("COPY")){ return !terminal.getTerminalArea().getSelectedText().isEmpty();}
        if(from.equals("PASTE")){ return Clipboard.getSystemClipboard().hasString();}

        return false;

    }

    @Override
    public void zoomIn() {
        terminal.zoomIn();
    }

    @Override
    public void zoomOut() {
        terminal.zoomOut();
    }

    @Override
    public void toDefaultZoom() {
        terminal.toDefaultZoom();
    }

    @Override
    public void undo() {
    }

    @Override
    public void redo() {
    }

    @Override
    public void cut() {
        terminal.cut();
    }

    @Override
    public void copy() {
        terminal.copy();
    }

    @Override
    public void paste() {
        terminal.paste();
    }

    @Override
    public void delete() {
    }

    @Override
    public void duplicate() {
    }

    @Override
    public void selectAll() {
    }

    public void find(){
        terminal.openFindBar();
    }

    public void replace(){
    }

}
