package com.cburch.LogisimFX.newgui;

import com.cburch.LogisimFX.localization.LC_null;
import com.cburch.LogisimFX.localization.Localizer;
import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.file.LogisimFileActions;
import com.cburch.LogisimFX.newgui.MainFrame.ProjectCircuitActions;
import com.cburch.LogisimFX.newgui.MainFrame.ProjectLibraryActions;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.LogisimFX.tools.Library;
import com.cburch.LogisimFX.file.LoadedLibrary;
import com.cburch.LogisimFX.file.Loader;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class ContextMenuManager {

    private static Localizer lc = LC_null.getInstance();

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

    public static ContextMenu CircuitContextMenu(Project proj, Circuit circ){

        lc.changeBundle("menu");

        ContextMenu contextMenu = new ContextMenu();

        MenuItem EditCircuit = new MenuItem();
        EditCircuit.textProperty().bind(lc.createStringBinding("projectEditCircuitLayoutItem"));
        EditCircuit.setOnAction(event -> proj.getFrameController().setEditView(circ));

        MenuItem EditAppearance = new MenuItem();
        EditAppearance.textProperty().bind(lc.createStringBinding("projectEditCircuitAppearanceItem"));
        EditAppearance.setOnAction(event -> proj.getFrameController().setAppearanceView(circ));

        MenuItem AnalyzeCircuit = new MenuItem();
        AnalyzeCircuit.textProperty().bind(lc.createStringBinding("projectAnalyzeCircuitItem"));
        AnalyzeCircuit.setOnAction(event -> FrameManager.CreateCircuitAnalysisFrame(proj));

        MenuItem GetCircuitStatistics = new MenuItem();
        GetCircuitStatistics.textProperty().bind(lc.createStringBinding("projectGetCircuitStatisticsItem"));
        GetCircuitStatistics.setOnAction(event -> FrameManager.CreateCircuitStatisticFrame(proj, circ));

        MenuItem SetAsMain = new MenuItem();
        SetAsMain.disableProperty().bind(proj.getLogisimFile().isMain);
        SetAsMain.textProperty().bind(lc.createStringBinding("projectSetAsMainItem"));
        SetAsMain.setOnAction(event -> proj.getLogisimFile().setMainCircuit(circ));

        MenuItem RemoveCirc = new MenuItem();
        RemoveCirc.disableProperty().bind(proj.getLogisimFile().obsPos.isEqualTo("first&last"));
        RemoveCirc.textProperty().bind(lc.createStringBinding("projectRemoveCircuitItem"));
        RemoveCirc.setOnAction(event -> {
            ProjectCircuitActions.doRemoveCircuit(proj,circ);
            proj.getFrameController().manual_Explorer_Update();
        });


        contextMenu.getItems().addAll(
                EditCircuit,
                EditAppearance,
                AnalyzeCircuit,
                GetCircuitStatistics,
                SetAsMain,
                RemoveCirc
        );

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
