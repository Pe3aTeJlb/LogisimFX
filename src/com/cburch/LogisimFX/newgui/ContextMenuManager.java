package com.cburch.LogisimFX.newgui;

import com.cburch.LogisimFX.tools.Library;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class ContextMenuManager {

    public static ContextMenu getContextMenu(Object obj){

        if (obj instanceof Library) return LibraryContextMenu((Library) obj);
        //else if (obj instanceof Library) return LibraryContextMenu((Library) obj);
        else return null;

    }

    public static ContextMenu LibraryContextMenu(Library lib){

        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem = new MenuItem("Menu Item");

        contextMenu.getItems().addAll(menuItem);

        return contextMenu;

    }

}
