package com.cburch.LogisimFX.newgui;

import com.cburch.LogisimFX.FileSelector;
import com.cburch.LogisimFX.circuit.CircuitMutation;
import com.cburch.LogisimFX.circuit.CircuitState;
import com.cburch.LogisimFX.comp.Component;
import com.cburch.LogisimFX.newgui.HexEditorFrame.HexFile;
import com.cburch.LogisimFX.newgui.MainFrame.Canvas.appearanceCanvas.AppearanceCanvas;
import com.cburch.LogisimFX.newgui.MainFrame.Canvas.appearanceCanvas.AppearanceEditHandler;
import com.cburch.LogisimFX.instance.Instance;
import com.cburch.LogisimFX.localization.*;
import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.file.LogisimFileActions;
import com.cburch.LogisimFX.newgui.MainFrame.*;
import com.cburch.LogisimFX.newgui.MainFrame.Canvas.layoutCanvas.LayoutCanvas;
import com.cburch.LogisimFX.newgui.MainFrame.Canvas.layoutCanvas.Selection;
import com.cburch.LogisimFX.newgui.MainFrame.Canvas.layoutCanvas.SelectionActions;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.LogisimFX.std.memory.Mem;
import com.cburch.LogisimFX.std.memory.MemState;
import com.cburch.LogisimFX.std.memory.RomAttributes;
import com.cburch.LogisimFX.tools.Library;
import com.cburch.LogisimFX.file.LoadedLibrary;
import com.cburch.LogisimFX.file.Loader;
import com.cburch.LogisimFX.tools.MenuExtender;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import java.io.File;
import java.io.IOException;

public class ContextMenuManager {

    private static Localizer lc = LC_null.getInstance();

    //Library menus

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
        EditCircuit.setOnAction(event -> proj.getFrameController().setLayoutView(circ));

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

    //LayoutCanvas elements menus

    public static ContextMenu ComponentDefaultContextMenu(Project project, Circuit circuit, Component component){

        Project proj = project;
        Circuit circ = circuit;
        Component comp = component;
        boolean canChange = proj.getLogisimFile().contains(circ);

        ContextMenu contextMenu = new ContextMenu();

        MenuItem del = new MenuItem("Menu Item");
        del.textProperty().bind(LC_tools.getInstance().createStringBinding("compDeleteItem"));
        del.setDisable(!canChange);
        del.setOnAction(event -> {

            Circuit c = proj.getCurrentCircuit();
            CircuitMutation xn = new CircuitMutation(c);
            xn.remove(comp);
            proj.doAction(xn.toAction(LC_tools.getInstance().createStringBinding("removeComponentAction",
                    comp.getFactory().getDisplayGetter())));

        });

        MenuItem attrs = new MenuItem("Menu Item");
        attrs.textProperty().bind(LC_tools.getInstance().createStringBinding("compShowAttrItem"));
        attrs.setOnAction(event -> {
            proj.getFrameController().setAttributeTable(comp);
        });

        contextMenu.getItems().addAll(del,attrs);

        return contextMenu;

    }

    public static ContextMenu SelectionContextMenu(LayoutCanvas c){

        Project proj = c.getProject();
        Selection sel = c.getSelection();

        ContextMenu contextMenu = new ContextMenu();

        MenuItem del = new MenuItem();
        del.textProperty().bind(LC_tools.getInstance().createStringBinding("selDeleteItem"));
        del.setDisable(!proj.getLogisimFile().contains(proj.getCurrentCircuit()));
        del.setOnAction(event -> proj.doAction(SelectionActions.clear(sel)));

        MenuItem cut = new MenuItem();
        cut.textProperty().bind(LC_tools.getInstance().createStringBinding("selCutItem"));
        cut.setDisable(!proj.getLogisimFile().contains(proj.getCurrentCircuit()));
        cut.setOnAction(event -> proj.doAction(SelectionActions.cut(sel)));

        MenuItem copy = new MenuItem();
        copy.textProperty().bind(LC_tools.getInstance().createStringBinding("selCopyItem"));
        copy.setOnAction(event -> proj.doAction(SelectionActions.copy(sel)));

        contextMenu.getItems().addAll(del,cut,copy);

        return contextMenu;

    }

    public static class CircuitComponentContextMenu implements MenuExtender {

        private ContextMenu menu;
        private Instance instance;
        private Project proj;

        public CircuitComponentContextMenu(Instance instance) {

            this.instance = instance;

        }

        @Override
        public void configureMenu(ContextMenu menu, Project proj) {

            this.menu = menu;
            this.proj = proj;

            String name = instance.getFactory().getDisplayName().getValue();

            MenuItem item = new MenuItem();
            item.textProperty().bind(LC_circuit.getInstance().createComplexStringBinding("subcircuitViewItem", name));
            item.setOnAction(event -> {

                CircuitState superState = proj.getCircuitState();
                if (superState == null) return;

                //CircuitState subState = getSubstate(superState, instance);
                CircuitState subState = proj.getCurrentCircuit().getSubcircuitFactory().getSubstate(superState, instance);
                if (subState == null) return;
                proj.setCircuitState(subState);

            });

            menu.getItems().add(item);

        }

    }

    public static class MemoryComponentContextMenu implements MenuExtender {

        private ContextMenu menu;
        private Mem factory;
        private Instance instance;
        private Project proj;
        private CircuitState circState;
        private boolean enabled;

        public MemoryComponentContextMenu(Mem factory, Instance instance){
            this.factory = factory;
            this.instance = instance;
        }

        @Override
        public void configureMenu(ContextMenu menu, Project proj) {

            this.menu = menu;
            this.proj = proj;
            circState = proj.getCircuitState();
            enabled = circState != null;

            Object attrs = instance.getAttributeSet();
            if (attrs instanceof RomAttributes) {
                ((RomAttributes) attrs).setProject(proj);
            }

            MenuItem edit = new MenuItem();
            edit.setDisable(!enabled);
            edit.textProperty().bind(LC_std.getInstance().createStringBinding("ramEditMenuItem"));
            edit.setOnAction(event -> {

                MemState s = factory.getState(instance, circState);
                if (s == null) return;
                factory.createHexFrame(proj, instance, circState);

            });

            MenuItem clear = new MenuItem();
            clear.setDisable(!enabled);
            clear.textProperty().bind(LC_std.getInstance().createStringBinding("ramClearMenuItem"));
            clear.setOnAction(event -> {


                MemState s = factory.getState(instance, circState);
                boolean isAllZero = s.getContents().isClear();
                if (isAllZero) return;

                int choice = DialogManager.CreateConfirmWarningDialog(
                        LC_std.getInstance().get("ramConfirmClearTitle"),
                        LC_std.getInstance().get("ramConfirmClearMsg")
                );

                if(choice == 1){
                    s.getContents().clear();
                }

            });

            MenuItem load = new MenuItem();
            load.setDisable(!enabled);
            load.textProperty().bind(LC_std.getInstance().createStringBinding("ramLoadMenuItem"));
            load.setOnAction(event -> {

                FileSelector fileSelector = new FileSelector(proj.getFrameController().getStage());
                File oldSelected = factory.getCurrentImage(instance);
                if (oldSelected != null)fileSelector.setSelectedFile(oldSelected);
                File f = fileSelector.showOpenDialog(LC_std.getInstance().get("ramLoadDialogTitle"));
                try {
                    if(f!=null)factory.loadImage(circState.getInstanceState(instance), f);
                } catch (IOException e) {
                    DialogManager.CreateErrorDialog(LC_std.getInstance().get("ramLoadErrorTitle"),e.getMessage());
                }


            });

            MenuItem save = new MenuItem();
            save.setDisable(!enabled);
            save.textProperty().bind(LC_std.getInstance().createStringBinding("ramSaveMenuItem"));
            save.setOnAction(event -> {

                MemState s = factory.getState(instance, circState);

                FileSelector fileSelector = new FileSelector(proj.getFrameController().getStage());
                File f = fileSelector.showSaveDialog(LC_std.getInstance().get("ramSaveDialogTitle"));

                try {

                    if(f!=null){
                        HexFile.save(f, s.getContents());
                        factory.setCurrentImage(instance, f);
                    }

                } catch (IOException e) {
                    DialogManager.CreateErrorDialog(LC_std.getInstance().get("ramSaveErrorTitle"),e.getMessage());
                }

            });

            menu.getItems().addAll(
                    edit,
                    clear,
                    load,
                    save
            );

        }

    }

    //AppearanceCanvas elements menus

    public static ContextMenu AppearanceEditContextMenu(AppearanceCanvas canvas) {

        ContextMenu menu = new ContextMenu();
        AppearanceEditHandler handler = new AppearanceEditHandler(canvas);

        MenuItem cut = new MenuItem();
        cut.textProperty().bind(LC_menu.getInstance().createStringBinding("editCutItem"));
        cut.setDisable(!handler.computeEnabled("CUT"));
        cut.setOnAction(event -> handler.cut());

        MenuItem copy = new MenuItem();
        copy.textProperty().bind(LC_menu.getInstance().createStringBinding("editCopyItem"));
        copy.setDisable(!handler.computeEnabled("COPY"));
        copy.setOnAction(event -> handler.copy());

        MenuItem delete = new MenuItem();
        delete.textProperty().bind(LC_menu.getInstance().createStringBinding("editClearItem"));
        delete.setDisable(!handler.computeEnabled("DELETE"));
        delete.setOnAction(event -> handler.delete());

        MenuItem duplicate = new MenuItem();
        duplicate.textProperty().bind(LC_menu.getInstance().createStringBinding("editDuplicateItem"));
        duplicate.setDisable(!handler.computeEnabled("DUPLICATE"));
        duplicate.setOnAction(event -> handler.duplicate());

        MenuItem raise = new MenuItem();
        raise.textProperty().bind(LC_menu.getInstance().createStringBinding("editRaiseItem"));
        raise.setDisable(!handler.computeEnabled("RAISE"));
        raise.setOnAction(event -> handler.raise());

        MenuItem lower = new MenuItem();
        lower.textProperty().bind(LC_menu.getInstance().createStringBinding("editLowerItem"));
        lower.setDisable(!handler.computeEnabled("LOWER"));
        lower.setOnAction(event -> handler.lower());

        MenuItem raiseTop = new MenuItem();
        raiseTop.textProperty().bind(LC_menu.getInstance().createStringBinding("editRaiseTopItem"));
        raiseTop.setDisable(!handler.computeEnabled("RAISE_TOP"));
        raiseTop.setOnAction(event -> handler.raiseTop());

        MenuItem lowerBottom = new MenuItem();
        lowerBottom.textProperty().bind(LC_menu.getInstance().createStringBinding("editLowerBottomItem"));
        lowerBottom.setDisable(!handler.computeEnabled("LOWER_BOTTOM"));
        lowerBottom.setOnAction(event -> handler.lowerBottom());

        MenuItem addControl = new MenuItem();
        addControl.textProperty().bind(LC_menu.getInstance().createStringBinding("editAddControlItem"));
        addControl.setDisable(!handler.computeEnabled("ADD_CONTROL"));
        addControl.setOnAction(event -> handler.addControlPoint());

        MenuItem removeControl = new MenuItem();
        removeControl.textProperty().bind(LC_menu.getInstance().createStringBinding("editRemoveControlItem"));
        removeControl.setDisable(!handler.computeEnabled("REMOVE_CONTROL"));
        removeControl.setOnAction(event -> handler.removeControlPoint());

        menu.getItems().add(cut);
        menu.getItems().add(copy);
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().add(delete);
        menu.getItems().add(duplicate);
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().add(raise);
        menu.getItems().add(lower);
        menu.getItems().add(raiseTop);
        menu.getItems().add(lowerBottom);
        if(handler.canAddCtrl || handler.canRemCtrl) {
            menu.getItems().add(new SeparatorMenuItem());
            menu.getItems().add(addControl);
            menu.getItems().add(removeControl);
        }

        /*
                /*class MenuListener check check chekc*/
        /*
        setEnabled(LogisimMenuBar.CUT, selHasRemovable && canChange);
		setEnabled(LogisimMenuBar.COPY, !selEmpty);
		setEnabled(LogisimMenuBar.PASTE, canChange && clipExists);
		setEnabled(LogisimMenuBar.DELETE, selHasRemovable && canChange);
		setEnabled(LogisimMenuBar.DUPLICATE, !selEmpty && canChange);
		setEnabled(LogisimMenuBar.SELECT_ALL, true);
		setEnabled(LogisimMenuBar.RAISE, canRaise);
		setEnabled(LogisimMenuBar.LOWER, canLower);
		setEnabled(LogisimMenuBar.RAISE_TOP, canRaise);
		setEnabled(LogisimMenuBar.LOWER_BOTTOM, canLower);
		setEnabled(LogisimMenuBar.ADD_CONTROL, canAddCtrl);
		setEnabled(LogisimMenuBar.REMOVE_CONTROL, canRemCtrl);
         */


        return menu;

    }

}
