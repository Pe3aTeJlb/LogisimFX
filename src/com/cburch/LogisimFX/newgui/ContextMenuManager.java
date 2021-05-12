package com.cburch.LogisimFX.newgui;

import com.cburch.LogisimFX.Localizer;
import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.file.LogisimFileActions;
import com.cburch.LogisimFX.newgui.MainFrame.ProjectLibraryActions;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.LogisimFX.tools.Library;
import com.cburch.LogisimFX.file.LoadedLibrary;
import com.cburch.LogisimFX.file.Loader;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class ContextMenuManager {

    private static Localizer lc = new Localizer(null);


    public static ContextMenu ProjectContextMenu(Project proj){

        lc.changeBundle("menu");

        ContextMenu contextMenu = new ContextMenu();

        MenuItem AddCircuit = new MenuItem();
        AddCircuit.textProperty().bind(lc.createStringBinding("projectAddCircuitItem"));
        AddCircuit.setOnAction(event -> {

            String circuitName = DialogManager.CreateInputDialog(proj.getLogisimFile());

            if (circuitName != null) {
                Circuit circuit = new Circuit(circuitName);
                proj.doAction(LogisimFileActions.addCircuit(circuit));
                proj.setCurrentCircuit(circuit);
            }

        });

        Menu UploadLibrary = new Menu();
        UploadLibrary.textProperty().bind(lc.createStringBinding("projectLoadLibraryItem"));

        MenuItem BuiltInLib = new MenuItem();
        BuiltInLib.textProperty().bind(lc.createStringBinding("projectLoadBuiltinItem"));
        BuiltInLib.setOnAction(event -> ProjectLibraryActions.doLoadBuiltinLibrary(proj));

        MenuItem LogisimLib = new MenuItem();
        LogisimLib.textProperty().bind(lc.createStringBinding("projectLoadLogisimItem"));
        LogisimLib.setOnAction(event -> ProjectLibraryActions.doLoadLogisimLibrary(proj));

        MenuItem JARLib = new MenuItem();
        JARLib.textProperty().bind(lc.createStringBinding("projectLoadJarItem"));
        JARLib.setOnAction(event -> ProjectLibraryActions.doLoadJarLibrary(proj));

        UploadLibrary.getItems().addAll(
                BuiltInLib,
                LogisimLib,
                JARLib
        );

        contextMenu.getItems().addAll(
                AddCircuit,
                UploadLibrary
        );

        return contextMenu;

    }

    public static ContextMenu LibraryContextMenu(Project proj,Library lib){

        ContextMenu contextMenu = new ContextMenu();

        MenuItem UnloadLibrary = new MenuItem();
        UnloadLibrary.textProperty().bind(lc.createStringBinding("projectUnloadLibraryItem"));
        UnloadLibrary.setOnAction(event -> ProjectLibraryActions.doUnloadLibrary(proj, lib));

        MenuItem ReloadLibrary = new MenuItem();
        ReloadLibrary.textProperty().bind(lc.createStringBinding("projectUnloadLibraryItem"));
        ReloadLibrary.setDisable(!lib.isDirty());
        ReloadLibrary.setOnAction(event -> {
            Loader loader = proj.getLogisimFile().getLoader();
            loader.reload((LoadedLibrary) lib);
        });

        contextMenu.getItems().addAll(
                UnloadLibrary,
                ReloadLibrary
        );

        return contextMenu;

    }

    public static ContextMenu CircuitContextMenu(Project proj){

        ContextMenu contextMenu = new ContextMenu();

        MenuItem menuItem = new MenuItem("Menu Item");
        menuItem.setDisable(proj.getLogisimFile().getTools().size()==1);


        contextMenu.getItems().addAll(menuItem);

        return contextMenu;

    }


    public static ContextMenu ComponentDefaultContextMenu(){

        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem = new MenuItem("Menu Item");

        contextMenu.getItems().addAll(menuItem);

        return contextMenu;

    }

    public static ContextMenu CircuitComponentContextMenu(){

        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem = new MenuItem("Menu Item");

        contextMenu.getItems().addAll(menuItem);

        return contextMenu;

    }

    public static ContextMenu MemoryComponentContextMenu(){

        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem = new MenuItem("Menu Item");

        contextMenu.getItems().addAll(menuItem);

        return contextMenu;

    }

}
