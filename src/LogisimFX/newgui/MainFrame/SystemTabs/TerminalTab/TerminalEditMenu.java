/*
 * This file is part of LogisimFX. Copyright (c) 2023, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.MainFrame.SystemTabs.TerminalTab;

import LogisimFX.localization.LC_menu;
import LogisimFX.localization.Localizer;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;

import java.util.List;

public class TerminalEditMenu {

    private static Localizer localizer = LC_menu.getInstance();

    private List<MenuItem> menuItems;
    private TerminalHandler editHandler;

    private final MenuItem  Find,
                            Cut,
                            Copy,
                            Paste;

    public TerminalEditMenu(Terminal terminal){

        editHandler = (TerminalHandler) terminal.getEditHandler();

        terminal.getTerminalArea().getCaretSelectionBind().selectedTextProperty().addListener(change -> calculateEnabled());

        Find = new MenuItem();
        Find.setAccelerator(KeyCombination.keyCombination("Ctrl+F"));
        Find.textProperty().bind(localizer.createStringBinding("editFindItem"));
        Find.setOnAction(event -> {
            editHandler.find();
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

        menuItems = List.of(
                Find
               // sp1,
                //Cut,
               // Copy,
               // Paste
        );

    }

    private void calculateEnabled(){

        Cut.setDisable(editHandler == null || !editHandler.computeEnabled("CUT"));
        Copy.setDisable(editHandler == null || !editHandler.computeEnabled("COPY"));
        Paste.setDisable(editHandler == null || !editHandler.computeEnabled("PASTE"));

    }

    public List<MenuItem> getMenuItems(){
        return menuItems;
    }

}
