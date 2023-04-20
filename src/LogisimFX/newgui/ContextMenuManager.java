/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui;

import LogisimFX.FileSelector;
import LogisimFX.circuit.CircuitMutation;
import LogisimFX.circuit.CircuitState;
import LogisimFX.circuit.SubcircuitFactory;
import LogisimFX.comp.Component;
import LogisimFX.newgui.WaveformFrame.WaveformController;
import LogisimFX.newgui.WaveformFrame.SelectionItem;
import LogisimFX.newgui.HexEditorFrame.HexFile;
import LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor.appearanceCanvas.AppearanceCanvas;
import LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor.appearanceCanvas.AppearanceEditHandler;
import LogisimFX.instance.Instance;
import LogisimFX.localization.*;
import LogisimFX.circuit.Circuit;
import LogisimFX.file.LogisimFileActions;
import LogisimFX.newgui.MainFrame.*;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.LayoutCanvas;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.Selection;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.layoutCanvas.SelectionActions;
import LogisimFX.proj.Project;
import LogisimFX.std.memory.Mem;
import LogisimFX.std.memory.MemState;
import LogisimFX.std.memory.RomAttributes;
import LogisimFX.tools.Library;
import LogisimFX.file.LoadedLibrary;
import LogisimFX.file.Loader;
import LogisimFX.tools.MenuExtender;

import javafx.scene.control.*;

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

            String circuitName = DialogManager.createInputDialog(proj.getLogisimFile());

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

        lc.changeBundle("menu");

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
        EditCircuit.setOnAction(event -> proj.getFrameController().addCircLayoutEditor(circ));

        MenuItem EditAppearance = new MenuItem();
        EditAppearance.textProperty().bind(lc.createStringBinding("projectEditCircuitAppearanceItem"));
        EditAppearance.setOnAction(event -> proj.getFrameController().addCircAppearanceEditor(circ));

        MenuItem EditVerilogModel = new MenuItem();
        EditVerilogModel.textProperty().bind(lc.createStringBinding("projectEditVerilogModelItem"));
        EditVerilogModel.setOnAction(event -> proj.getFrameController().addCodeEditor(circ, circ.getVerilogModel(proj)));

        MenuItem EditTopLevelShell = new MenuItem();
        EditTopLevelShell.textProperty().bind(lc.createStringBinding("projectEditTopLevelShell"));
        EditTopLevelShell.setOnAction(event -> proj.getFrameController().addCodeEditor(circ, circ.getTopLevelShell(proj)));

        MenuItem EditHLSModel = new MenuItem();
        EditHLSModel.textProperty().bind(lc.createStringBinding("projectEditHLSModel"));
        EditHLSModel.setOnAction(event -> proj.getFrameController().addCodeEditor(circ, circ.getHLS(proj)));

        MenuItem AnalyzeCircuit = new MenuItem();
        AnalyzeCircuit.textProperty().bind(lc.createStringBinding("projectAnalyzeCircuitItem"));
        AnalyzeCircuit.setOnAction(event -> FrameManager.CreateCircuitAnalysisFrame(proj));

        MenuItem GetCircuitStatistics = new MenuItem();
        GetCircuitStatistics.textProperty().bind(lc.createStringBinding("projectGetCircuitStatisticsItem"));
        GetCircuitStatistics.setOnAction(event -> FrameManager.CreateCircuitStatisticFrame(proj, circ));

        MenuItem SetAsMain = new MenuItem();
        SetAsMain.setDisable(proj.getLogisimFile().getMainCircuit() == circ);
        SetAsMain.textProperty().bind(lc.createStringBinding("projectSetAsMainItem"));
        SetAsMain.setOnAction(event -> proj.getLogisimFile().setMainCircuit(circ));

        MenuItem RemoveCirc = new MenuItem();
        RemoveCirc.disableProperty().bind(proj.getLogisimFile().obsPos.isEqualTo("first&last"));
        RemoveCirc.textProperty().bind(lc.createStringBinding("projectRemoveCircuitItem"));
        RemoveCirc.setOnAction(event -> ProjectCircuitActions.doRemoveCircuit(proj,circ));


        contextMenu.getItems().addAll(
                EditCircuit,
                EditAppearance,
                EditVerilogModel
        );
        if (proj.getLogisimFile().getMainCircuit() == circ){
            contextMenu.getItems().add(EditTopLevelShell);
        }
        contextMenu.getItems().addAll(
                EditHLSModel,
                AnalyzeCircuit,
                GetCircuitStatistics,
                SetAsMain,
                RemoveCirc
        );

        return contextMenu;

    }

    //LayoutCanvas elements menus

    public static ContextMenu ComponentDefaultContextMenu(Project project, Circuit circuit, Component component){

        lc.changeBundle("tools");

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
            proj.doAction(xn.toAction(LC_tools.getInstance().createComplexStringBinding("removeComponentAction",
                    comp.getFactory().getDisplayGetter().getValue())));

        });

        MenuItem attrs = new MenuItem("Menu Item");
        attrs.textProperty().bind(LC_tools.getInstance().createStringBinding("compShowAttrItem"));
        attrs.setOnAction(event -> {
            proj.getFrameController().setAttributeTable(circ, comp);
        });

        contextMenu.getItems().addAll(del,attrs);

        if (comp.getFactory().isHDLSupportedComponent(comp.getAttributeSet())
                && comp.getFactory().isHDLGeneratorAvailable() && !comp.getFactory().getHDLGenerator(comp.getAttributeSet()).isOnlyInlined()
        ){

            MenuItem verilog = new MenuItem("Verilog");
            verilog.textProperty().bind(LC_tools.getInstance().createStringBinding("compViewVerilogModel"));
            verilog.setOnAction(event -> proj.getFrameController().addCodeEditor(circ, comp));

            contextMenu.getItems().add(verilog);
        }

        return contextMenu;

    }

    public static ContextMenu SelectionContextMenu(LayoutCanvas c){

        lc.changeBundle("tools");

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
            lc.changeBundle("circuit");

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

            if (instance.getFactory().isHDLSupportedComponent(instance.getAttributeSet()) &&
                    !instance.getFactory().getHDLGenerator(instance.getAttributeSet()).isOnlyInlined()
            ){

                lc.changeBundle("menu");

                MenuItem EditVerilogModel = new MenuItem();
                EditVerilogModel.textProperty().bind(lc.createStringBinding("projectEditVerilogModelItem"));
                EditVerilogModel.setOnAction(event ->
                        proj.getFrameController().addCodeEditor(
                                ((SubcircuitFactory)instance.getFactory()).getSubcircuit(),
                                ((SubcircuitFactory)instance.getFactory()).getSubcircuit().getVerilogModel(proj)
                        )
                );

                menu.getItems().add(EditVerilogModel);

            }

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

            lc.changeBundle("std");
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

                int choice = DialogManager.createConfirmWarningDialog(
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
                    DialogManager.createErrorDialog(LC_std.getInstance().get("ramLoadErrorTitle"),e.getMessage());
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
                    DialogManager.createErrorDialog(LC_std.getInstance().get("ramSaveErrorTitle"),e.getMessage());
                }

            });

            menu.getItems().addAll(
                    edit,
                    clear,
                    load,
                    save
            );

            if (instance.getFactory().isHDLSupportedComponent(instance.getAttributeSet())
                    && instance.getFactory().isHDLGeneratorAvailable() && !instance.getFactory().getHDLGenerator(instance.getAttributeSet()).isOnlyInlined()
            ){
                lc.changeBundle("tools");

                MenuItem verilog = new MenuItem("Verilog");
                verilog.textProperty().bind(LC_tools.getInstance().createStringBinding("compViewVerilogModel"));
                verilog.setOnAction(event -> proj.getFrameController().addCodeEditor(proj.getCurrentCircuit(), instance.getComponent()));


                menu.getItems().add(verilog);

            }

        }

    }

    //AppearanceCanvas elements menus

    public static ContextMenu AppearanceEditContextMenu(AppearanceCanvas canvas) {

        lc.changeBundle("menu");

        ContextMenu menu = new ContextMenu();
        AppearanceEditHandler handler = canvas.getEditHandler();

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

        return menu;

    }

    //Waveform Radix

    public static ContextMenu RadixOptionsContextMenu(WaveformController.WaveformTableModel tableModel, TreeTableView table){

        ContextMenu contextMenu = new ContextMenu();

        MenuItem bin = new MenuItem();
        bin.setText("Binary");
        bin.setDisable(tableModel.getRadix() == 2);
        bin.setOnAction(event -> {
            tableModel.setRadix(2);
            table.refresh();
        });

        MenuItem dec = new MenuItem();
        dec.setText("Decimal");
        dec.setDisable(tableModel.getRadix() == 10);
        dec.setOnAction(event -> {
            tableModel.setRadix(10);
            table.refresh();
        });

        MenuItem hex = new MenuItem();
        hex.setText("Hexagonal");
        hex.setDisable(tableModel.getRadix() == 16);
        hex.setOnAction(event -> {
            tableModel.setRadix(16);
            table.refresh();
        });

        contextMenu.getItems().addAll(bin, dec, hex);

        return contextMenu;

    }

    public static ContextMenu RadixOptionsContextMenu(LogisimFX.newgui.MainFrame.SystemTabs.WaveformTab.WaveformController.WaveformTableModel tableModel, TreeTableView table){

        ContextMenu contextMenu = new ContextMenu();

        MenuItem bin = new MenuItem();
        bin.setText("Binary");
        bin.setDisable(tableModel.getRadix() == 2);
        bin.setOnAction(event -> {
            tableModel.setRadix(2);
            table.refresh();
        });

        MenuItem dec = new MenuItem();
        dec.setText("Decimal");
        dec.setDisable(tableModel.getRadix() == 10);
        dec.setOnAction(event -> {
            tableModel.setRadix(10);
            table.refresh();
        });

        MenuItem hex = new MenuItem();
        hex.setText("Hexagonal");
        hex.setDisable(tableModel.getRadix() == 16);
        hex.setOnAction(event -> {
            tableModel.setRadix(16);
            table.refresh();
        });

        contextMenu.getItems().addAll(bin, dec, hex);

        return contextMenu;

    }


    public static ContextMenu RadixOptionsContextMenu(SelectionItem tableModel, TableView table){

        ContextMenu contextMenu = new ContextMenu();

        MenuItem bin = new MenuItem();
        bin.setText("Binary");
        bin.setDisable(tableModel.getRadix() == 2);
        bin.setOnAction(event -> {
            tableModel.setRadix(2);
            table.refresh();
        });

        MenuItem dec = new MenuItem();
        dec.setText("Decimal");
        dec.setDisable(tableModel.getRadix() == 10);
        dec.setOnAction(event -> {
            tableModel.setRadix(10);
            table.refresh();
        });

        MenuItem hex = new MenuItem();
        hex.setText("Hexagonal");
        hex.setDisable(tableModel.getRadix() == 16);
        hex.setOnAction(event -> {
            tableModel.setRadix(16);
            table.refresh();
        });

        contextMenu.getItems().addAll(bin, dec, hex);

        return contextMenu;

    }


}
