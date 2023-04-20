/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.MainFrame;

import LogisimFX.circuit.Circuit;
import LogisimFX.circuit.CircuitState;
import LogisimFX.circuit.Simulator;
import LogisimFX.newgui.DialogManager;
import LogisimFX.localization.LC_menu;
import LogisimFX.newgui.FrameManager;
import LogisimFX.file.LogisimFile;
import LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor.appearanceCanvas.RevertAppearanceAction;
import LogisimFX.newgui.MainFrame.EditorTabs.EditHandler;
import LogisimFX.prefs.AppPreferences;
import LogisimFX.proj.Project;
import LogisimFX.proj.ProjectActions;
import LogisimFX.localization.Localizer;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class CustomMenuBar extends MenuBar {

    private int prefHeight = 25;

    private static Localizer localizer = LC_menu.getInstance();

    private Stage stage;
    private Project proj;
    private LogisimFile logisimFile;

    private EditMenu editMenu;
    private EditHandler editHandler;

    public CustomMenuBar(Stage s, Project project){

        super();

        stage = s;
        proj = project;
        logisimFile = proj.getLogisimFile();

        proj.getFrameController().editorProperty().addListener((observableValue, handler, t1) -> {
            editMenu.getItems().clear();
            editMenu.getItems().addAll(t1.getEditMenuItems());
            editHandler = t1.getEditHandler();
        });

        prefHeight(prefHeight);

        AnchorPane.setLeftAnchor(this,0.0);
        AnchorPane.setTopAnchor(this,0.0);
        AnchorPane.setRightAnchor(this,0.0);

        initMenus();

    }

    private void initMenus(){

        initFileMenu();
        this.getMenus().add(editMenu = new EditMenu());
        initViewMenu();
        initProjectMenu();
        this.getMenus().add(new SimulateMenu());
        initFPGAMenu();
        this.getMenus().add(new WindowMenu());
        initHelpMenu();

    }

    private void initFileMenu(){

        Menu File = new Menu();
        File.textProperty().bind(localizer.createStringBinding("fileMenu"));

        MenuItem New = new MenuItem();
        New.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
        New.textProperty().bind(localizer.createStringBinding("fileNewItem"));
        New.setOnAction(event -> ProjectActions.spamNew(proj));

        MenuItem Open = new MenuItem();
        Open.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
        Open.textProperty().bind(localizer.createStringBinding("fileOpenItem"));
        Open.setOnAction(event -> ProjectActions.doOpen(proj));

        //see gui/menu/OpenRecent
        OpenRecentMenu OpenRecent = new OpenRecentMenu(proj);
        OpenRecent.textProperty().bind(localizer.createStringBinding("fileOpenRecentItem"));
        //OpenRecent.setOnShowing(event -> {OpenRecent.renewItems();});


        SeparatorMenuItem sp1 = new SeparatorMenuItem();


        MenuItem Close = new MenuItem();
        Close.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+W"));
        Close.textProperty().bind(localizer.createStringBinding("fileCloseItem"));
        Close.setOnAction(event -> proj.getFrameController().getStage().close());

        MenuItem Save = new MenuItem();
        Save.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
        Save.textProperty().bind(localizer.createStringBinding("fileSaveItem"));
        Save.setOnAction(event -> ProjectActions.doSave(proj));

        MenuItem SaveAs = new MenuItem();
        SaveAs.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+S"));
        SaveAs.textProperty().bind(localizer.createStringBinding("fileSaveAsItem"));
        SaveAs.setOnAction(event -> ProjectActions.doSaveAs(proj));


        SeparatorMenuItem sp2 = new SeparatorMenuItem();


        MenuItem ExportImage = new MenuItem();
        ExportImage.textProperty().bind(localizer.createStringBinding("fileExportImageItem"));
        ExportImage.setOnAction(event -> FrameManager.CreateExportImageFrame(proj));

        MenuItem Print = new MenuItem();
        Print.setAccelerator(KeyCombination.keyCombination("Ctrl+P"));
        Print.textProperty().bind(localizer.createStringBinding("filePrintItem"));
        Print.setOnAction(event -> FrameManager.CreatePrintFrame(proj));


        SeparatorMenuItem sp3 = new SeparatorMenuItem();


        MenuItem Preferences = new MenuItem();
        Preferences.textProperty().bind(localizer.createStringBinding("filePreferencesItem"));
        Preferences.setOnAction(event -> FrameManager.CreatePreferencesFrame());


        SeparatorMenuItem sp4 = new SeparatorMenuItem();


        MenuItem Exit = new MenuItem();
        Exit.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"));
        Exit.textProperty().bind(localizer.createStringBinding("fileQuitItem"));
        Exit.setOnAction(event -> FrameManager.ExitProgram());

        MenuItem ForceExit = new MenuItem();
        ForceExit.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+Q"));
        ForceExit.setText("Force Exit");
        ForceExit.setOnAction(event -> FrameManager.ForceExit());

        File.getItems().addAll(
                New,
                Open,
                OpenRecent,
                sp1,
                Close,
                Save,
                SaveAs,
                sp2,
                ExportImage,
                Print,
                sp3,
                Preferences,
                sp4,
                Exit,
                ForceExit
        );

        this.getMenus().add(File);

    }

    private void initViewMenu(){

        Menu View = new Menu();
        View.textProperty().bind(localizer.createStringBinding("viewMenu"));

        MenuItem zoomInItem = new MenuItem();
        zoomInItem.textProperty().bind(localizer.createStringBinding("viewZoomIn"));
        zoomInItem.setAccelerator(new KeyCodeCombination(KeyCode.ADD, KeyCombination.CONTROL_DOWN));
        zoomInItem.setOnAction(event -> editHandler.zoomIn());

        MenuItem zoomOutItem = new MenuItem();
        zoomOutItem.textProperty().bind(localizer.createStringBinding("viewZoomOut"));
        zoomOutItem.setAccelerator(new KeyCodeCombination(KeyCode.SUBTRACT, KeyCombination.CONTROL_DOWN));
        zoomOutItem.setOnAction(event -> editHandler.zoomOut());

        MenuItem zoomDefItem = new MenuItem();
        zoomDefItem.textProperty().bind(localizer.createStringBinding("viewZoomDef"));
        zoomDefItem.setAccelerator(new KeyCodeCombination(KeyCode.DIVIDE, KeyCombination.CONTROL_DOWN));
        zoomDefItem.setOnAction(event -> editHandler.toDefaultZoom());

        SeparatorMenuItem sp1 = new SeparatorMenuItem();

        Menu tabMenu = new Menu();
        tabMenu.textProperty().bind(localizer.createStringBinding("viewToolsWindow"));

        MenuItem addToolsTab = new MenuItem();
        //addToolsTab.setAccelerator(KeyCombination.keyCombination("Alt+1"));
        addToolsTab.textProperty().bind(localizer.createStringBinding("viewToolsTab"));
        addToolsTab.setOnAction(event -> proj.getFrameController().addToolsTab());

        MenuItem addSimulationTab = new MenuItem();
        //addSimulationTab.setAccelerator(KeyCombination.keyCombination("Alt+2"));
        addSimulationTab.textProperty().bind(localizer.createStringBinding("viewSimTab"));
        addSimulationTab.setOnAction(event -> proj.getFrameController().addSimulationTab());

        MenuItem addAttributesTab = new MenuItem();
        //addAttributesTab.setAccelerator(KeyCombination.keyCombination("Alt+3"));
        addAttributesTab.textProperty().bind(localizer.createStringBinding("viewAttrTab"));
        addAttributesTab.setOnAction(event -> proj.getFrameController().addAttributesTab());

        MenuItem addWaveformTab = new MenuItem();
        //addWaveformTab.setAccelerator(KeyCombination.keyCombination("Alt+4"));
        addWaveformTab.textProperty().bind(localizer.createStringBinding("viewWaveformTab"));
        addWaveformTab.setOnAction(event -> proj.getFrameController().addWaveformTab());

        tabMenu.getItems().addAll(
                addToolsTab,
                addSimulationTab,
                addAttributesTab,
                addWaveformTab
        );

        SeparatorMenuItem sp2 = new SeparatorMenuItem();

        MenuItem changeTheme = new MenuItem();
        changeTheme.textProperty().bind(localizer.createStringBinding("viewChangeTheme"));
        changeTheme.setOnAction(event -> new ThemeChangePopup(stage));

        SeparatorMenuItem sp3 = new SeparatorMenuItem();

        MenuItem systemTabsToDefault = new MenuItem();
        systemTabsToDefault.textProperty().bind(localizer.createStringBinding("viewResetSystemTabs"));
        systemTabsToDefault.setOnAction(event -> proj.getFrameController().toDefaultSystemTabs());

        MenuItem workspaceToDefault = new MenuItem();
        workspaceToDefault.textProperty().bind(localizer.createStringBinding("viewResetWorkspace"));
        workspaceToDefault.setOnAction(event -> proj.getFrameController().toDefaultWorkspace());

        MenuItem layoutToDefault = new MenuItem();
        layoutToDefault.textProperty().bind(localizer.createStringBinding("viewResetLayout"));
        layoutToDefault.setOnAction(event -> proj.getFrameController().toDefaultLayout());

        View.getItems().addAll(
                zoomInItem,
                zoomOutItem,
                zoomDefItem,
                sp1,
                tabMenu,
                sp2,
                changeTheme,
                sp3,
                systemTabsToDefault,
                workspaceToDefault,
                layoutToDefault
        );

        this.getMenus().add(View);

    }

    private class EditMenu extends Menu{

        public EditMenu() {
            super();
            this.textProperty().bind(localizer.createStringBinding("editMenu"));
        }

    }

    private void initProjectMenu(){

        Menu Project = new Menu();
        Project.textProperty().bind(localizer.createStringBinding("projectMenu"));

        MenuItem UndoLastProjectAction = new MenuItem();
        UndoLastProjectAction.textProperty().bind(localizer.createStringBinding("projectCantUndoAction"));
        UndoLastProjectAction.disableProperty().bind(proj.undoAvailableProperty().not());
        UndoLastProjectAction.disableProperty().addListener(change ->{
            UndoLastProjectAction.textProperty().unbind();
            UndoLastProjectAction.textProperty().bind(
                    proj.getLastAction() == null
                    ? localizer.createStringBinding("projectCantUndoAction")
                    : localizer.createComplexStringBinding("projectUndoAction", proj.getLastAction().getName())
            );
        });
        UndoLastProjectAction.setOnAction(event -> proj.undoAction());

        MenuItem RedoLastProjectAction = new MenuItem();
        RedoLastProjectAction.textProperty().bind(localizer.createStringBinding("projectCantRedoAction"));
        RedoLastProjectAction.disableProperty().bind(proj.redoAvailableProperty().not());
        RedoLastProjectAction.disableProperty().addListener(change ->{
            RedoLastProjectAction.textProperty().unbind();
            RedoLastProjectAction.textProperty().bind(
                    proj.getLastRedoAction() == null
                            ? localizer.createStringBinding("projectCantRedoAction")
                            : localizer.createComplexStringBinding("projectRedoAction", proj.getLastRedoAction().getName())
            );
        });
        RedoLastProjectAction.setOnAction(event -> proj.undoAction());

        SeparatorMenuItem sp1 = new SeparatorMenuItem();

        MenuItem AddCircuit = new MenuItem();
        AddCircuit.textProperty().bind(localizer.createStringBinding("projectAddCircuitItem"));
        AddCircuit.setOnAction(event -> ProjectCircuitActions.doAddCircuit(proj));


        //menuproject 20
        Menu UploadLibrary = new Menu();
        UploadLibrary.textProperty().bind(localizer.createStringBinding("projectLoadLibraryItem"));

        MenuItem BuiltInLib = new MenuItem();
        BuiltInLib.textProperty().bind(localizer.createStringBinding("projectLoadBuiltinItem"));
        BuiltInLib.setOnAction(event -> ProjectLibraryActions.doLoadBuiltinLibrary(proj));

        MenuItem LogisimLib = new MenuItem();
        LogisimLib.textProperty().bind(localizer.createStringBinding("projectLoadLogisimItem"));
        LogisimLib.setOnAction(event -> ProjectLibraryActions.doLoadLogisimLibrary(proj));

        MenuItem JARLib = new MenuItem();
        JARLib.textProperty().bind(localizer.createStringBinding("projectLoadJarItem"));
        JARLib.setOnAction(event -> ProjectLibraryActions.doLoadJarLibrary(proj));

        UploadLibrary.getItems().addAll(
                BuiltInLib,
                LogisimLib,
                JARLib
        );



        MenuItem UnloadLibrary = new MenuItem();
        UnloadLibrary.textProperty().bind(localizer.createStringBinding("projectUnloadLibraryItem"));
        UnloadLibrary.setOnAction(event -> ProjectLibraryActions.doUnloadLibraries(proj));


        SeparatorMenuItem sp2 = new SeparatorMenuItem();


        MenuItem MoveCircuitUp = new MenuItem();
        MoveCircuitUp.disableProperty().bind(
                Bindings.or(logisimFile.obsPos.isEqualTo("first"),logisimFile.obsPos.isEqualTo("first&last"))
        );
        MoveCircuitUp.textProperty().bind(localizer.createStringBinding("projectMoveCircuitUpItem"));
        MoveCircuitUp.setOnAction(event -> {
            ProjectCircuitActions.doMoveCircuit(proj,proj.getCurrentCircuit(),-1);
        });

        MenuItem MoveCircuitDown = new MenuItem();
        MoveCircuitDown.disableProperty().bind(
                Bindings.or(logisimFile.obsPos.isEqualTo("last"),logisimFile.obsPos.isEqualTo("first&last"))
        );
        MoveCircuitDown.textProperty().bind(localizer.createStringBinding("projectMoveCircuitDownItem"));
        MoveCircuitDown.setOnAction(event -> ProjectCircuitActions.doMoveCircuit(proj,proj.getCurrentCircuit(),1));

        MenuItem SetAsMain = new MenuItem();
        SetAsMain.disableProperty().bind(logisimFile.isMain);
        SetAsMain.textProperty().bind(localizer.createStringBinding("projectSetAsMainItem"));
        SetAsMain.setOnAction(event -> ProjectCircuitActions.doSetAsMainCircuit(proj, proj.getCurrentCircuit()));

        MenuItem RemoveCirc = new MenuItem();
        RemoveCirc.disableProperty().bind(logisimFile.obsPos.isEqualTo("first&last"));
        RemoveCirc.textProperty().bind(localizer.createStringBinding("projectRemoveCircuitItem"));
        RemoveCirc.setOnAction(event -> ProjectCircuitActions.doRemoveCircuit(proj,proj.getCurrentCircuit()));

        MenuItem RevertAppearance = new MenuItem();

        RevertAppearance.textProperty().bind(localizer.createStringBinding("projectRevertAppearanceItem"));
        RevertAppearance.setOnAction(event -> proj.doAction(new RevertAppearanceAction(proj.getCurrentCircuit())));


        SeparatorMenuItem sp3 = new SeparatorMenuItem();

        MenuItem EditCircuitLayout = new MenuItem();
        EditCircuitLayout.textProperty().bind(localizer.createStringBinding("projectEditCircuitLayoutItem"));
        EditCircuitLayout.setOnAction(event -> proj.getFrameController().addCircLayoutEditor(proj.getCurrentCircuit()));

        MenuItem EditCircuitAppearance = new MenuItem();
        EditCircuitAppearance.textProperty().bind(localizer.createStringBinding("projectEditCircuitAppearanceItem"));
        EditCircuitAppearance.setOnAction(event -> proj.getFrameController().addCircAppearanceEditor(proj.getCurrentCircuit()));

        MenuItem EditVerilogModel = new MenuItem();
        EditVerilogModel.textProperty().bind(localizer.createStringBinding("projectEditVerilogModelItem"));
        EditVerilogModel.setOnAction(event -> proj.getFrameController().addCodeEditor(proj.getCurrentCircuit(), proj.getCurrentCircuit().getVerilogModel(proj)));

        MenuItem EditTopLevelShell = new MenuItem();
        EditTopLevelShell.disableProperty().bind(Bindings.not(logisimFile.isMain));
        EditTopLevelShell.textProperty().bind(localizer.createStringBinding("projectEditTopLevelShell"));
        EditTopLevelShell.setOnAction(event -> proj.getFrameController().addCodeEditor(proj.getCurrentCircuit(), proj.getCurrentCircuit().getTopLevelShell(proj)));

        MenuItem EditHLSModel = new MenuItem();
        EditHLSModel.textProperty().bind(localizer.createStringBinding("projectEditHLSModel"));
        EditHLSModel.setOnAction(event -> proj.getFrameController().addCodeEditor(proj.getCurrentCircuit(), proj.getCurrentCircuit().getHLS(proj)));


        SeparatorMenuItem sp4 = new SeparatorMenuItem();

        MenuItem AnalyzeCircuit = new MenuItem();
        AnalyzeCircuit.textProperty().bind(localizer.createStringBinding("projectAnalyzeCircuitItem"));
        AnalyzeCircuit.setOnAction(event -> ProjectCircuitActions.doAnalyze(proj, proj.getCurrentCircuit()));

        MenuItem GetCircuitStatistics = new MenuItem();
        GetCircuitStatistics.textProperty().bind(localizer.createStringBinding("projectGetCircuitStatisticsItem"));
        GetCircuitStatistics.setOnAction(event -> FrameManager.CreateCircuitStatisticFrame(proj, proj.getCurrentCircuit()));

        SeparatorMenuItem sp5 = new SeparatorMenuItem();

        MenuItem Options = new MenuItem();
        Options.textProperty().bind(localizer.createStringBinding("projectOptionsItem"));
        Options.setOnAction(event -> FrameManager.CreateOptionsFrame(proj));

        Project.getItems().addAll(
                UndoLastProjectAction,
                RedoLastProjectAction,
                sp1,
                AddCircuit,
                UploadLibrary,
                UnloadLibrary,
                sp2,
                MoveCircuitUp,
                MoveCircuitDown,
                SetAsMain,
                RemoveCirc,
                RevertAppearance,
                sp3,
                EditCircuitLayout,
                EditCircuitAppearance,
                EditVerilogModel,
                EditTopLevelShell,
                EditHLSModel,
                sp4,
                AnalyzeCircuit,
                GetCircuitStatistics,
                sp5,
                Options
        );
        this.getMenus().add(Project);

    }

    private class SimulateMenu extends Menu{

        private ObservableList<TickFrequencyItem> freqItems;
        private ObservableList<CircuitStateMenuItem> downStateItems;
        private ObservableList<CircuitStateMenuItem> upStateItems;

        private Menu upStateMenu = new Menu();
        private Menu downStateMenu = new Menu();

        private SimpleBooleanProperty present = new SimpleBooleanProperty(false);

        private CircuitState currentState;
        private CircuitState bottomState;
        private Simulator sim;

        private RadioMenuItem EnableSimulation;
        private RadioMenuItem TicksEnable;

        private TickFrequencyItem lastTickFreqItem;

        public SimulateMenu(){

            super();
            this.textProperty().bind(localizer.createStringBinding("simulateMenu"));

            freqItems = FXCollections.observableArrayList();
            downStateItems = FXCollections.observableArrayList();
            upStateItems = FXCollections.observableArrayList();

            sim = proj.getSimulator();

            setCurrentState(proj.getSimulator(),proj.getCircuitState());

            this.setOnShown(event -> {
                EnableSimulation.setSelected(sim.isRunning().getValue());
                TicksEnable.setSelected(sim.isTicking());
            });

            init();

        }

        private void init(){

            EnableSimulation = new RadioMenuItem();
            EnableSimulation.setAccelerator(KeyCombination.keyCombination("Ctrl+E"));
            EnableSimulation.textProperty().bind(localizer.createStringBinding("simulateRunItem"));
            EnableSimulation.disableProperty().bind(Bindings.not(present));
            EnableSimulation.setSelected(present.get());
            EnableSimulation.setOnAction(event -> {

                if (sim != null) {
                    sim.setIsRunning(!sim.isRunning().getValue());
                }

            });

            MenuItem ResetSimulation = new MenuItem();
            ResetSimulation.setAccelerator(KeyCombination.keyCombination("Ctrl+R"));
            ResetSimulation.textProperty().bind(localizer.createStringBinding("simulateResetItem"));
            ResetSimulation.setOnAction(event -> {if (sim != null) sim.requestReset();});
            ResetSimulation.disableProperty().bind(Bindings.not(present));

            MenuItem SimStep = new MenuItem();
            SimStep.setAccelerator(KeyCombination.keyCombination("Ctrl+I"));
            SimStep.textProperty().bind(localizer.createStringBinding("simulateStepItem"));
            SimStep.setOnAction(event -> {if (sim != null) sim.step();});
            SimStep.disableProperty().bind(Bindings.or(Bindings.not(present),sim.isRunning()));


            SeparatorMenuItem sp1 = new SeparatorMenuItem();



            upStateMenu.textProperty().bind(localizer.createStringBinding("simulateUpStateMenu"));
            upStateMenu.disableProperty().bind(Bindings.not(present));



            downStateMenu.textProperty().bind(localizer.createStringBinding("simulateDownStateMenu"));
            downStateMenu.disableProperty().bind(Bindings.not(present));



            SeparatorMenuItem sp2 = new SeparatorMenuItem();


            MenuItem TickOnce = new MenuItem();
            TickOnce.setAccelerator(KeyCombination.keyCombination("Ctrl+T"));
            TickOnce.textProperty().bind(localizer.createStringBinding("simulateTickOnceItem"));
            TickOnce.setOnAction(event -> {if (sim != null) sim.tick();});
            TickOnce.disableProperty().bind(Bindings.not(present));

            TicksEnable = new RadioMenuItem();
            TicksEnable.setAccelerator(KeyCombination.keyCombination("Ctrl+K"));
            TicksEnable.textProperty().bind(localizer.createStringBinding("simulateTickItem"));
            TicksEnable.setOnAction(event -> {if (sim != null) sim.setIsTicking(!sim.isTicking());});
            TicksEnable.disableProperty().bind(Bindings.or(Bindings.not(present),Bindings.not(sim.isRunning())));

            Menu runNTicks = new Menu();
            runNTicks.textProperty().bind(localizer.createStringBinding("simulateNTicsTip"));
            runNTicks.setOnAction(event -> {


                if(event.getSource() != event.getTarget()) return;

                String ticks = DialogManager.createInputDialog(localizer.get("simulateInputTicksCountHeader"),
                        localizer.get("simulateInputTicksCountBody"),
                        "^([1-9]{0,1}[0-9]{0,2}$){0,1}");

                if(ticks == null || ticks.equals(""))return;

                int n = Integer.parseInt(ticks,10);

                if (runNTicks.getItems().size() >= 5) {
                    runNTicks.getItems().remove(0);
                }
                runNTicks.getItems().add(new RunNTicksMenuItem(n));

                sim.runTickNTimes(n);

            });
            runNTicks.disableProperty().bind(Bindings.not(present));


            Menu Frequency = new Menu();
            Frequency.textProperty().bind(localizer.createStringBinding("simulateTickFreqMenu"));
            Frequency.disableProperty().bind(Bindings.not(present));

            freqItems.addAll(
                    new TickFrequencyItem(4096),
                    new TickFrequencyItem(2048),
                    new TickFrequencyItem(1024),
                    new TickFrequencyItem(512),
                    new TickFrequencyItem(256),
                    new TickFrequencyItem(128),
                    new TickFrequencyItem(64),
                    new TickFrequencyItem(32),
                    new TickFrequencyItem(16),
                    new TickFrequencyItem(8),
                    new TickFrequencyItem(4),
                    new TickFrequencyItem(2),
                    new TickFrequencyItem(1),
                    new TickFrequencyItem(0.5),
                    new TickFrequencyItem(0.25)
            );


            Frequency.getItems().addAll(freqItems);


            SeparatorMenuItem sp3 = new SeparatorMenuItem();


            MenuItem SimLog = new MenuItem();
            SimLog.textProperty().bind(localizer.createStringBinding("simulateLogItem"));
            SimLog.setOnAction(event -> FrameManager.CreateCircLogFrame(proj));

            this.getItems().addAll(
                    EnableSimulation,
                    ResetSimulation,
                    SimStep,
                    sp1,
                    upStateMenu,
                    downStateMenu,
                    sp2,
                    TickOnce,
                    TicksEnable,
                    runNTicks,
                    Frequency,
                    sp3,
                    SimLog
            );

        }

        public void setCurrentState(Simulator newSim, CircuitState value) {

            if (currentState == value) return;

            Simulator oldSim = sim;
            CircuitState oldState = currentState;
            sim = newSim;
            currentState = value;

            if (bottomState == null) {
                bottomState = currentState;
            } else if (currentState == null) {
                bottomState = null;
            } else {
                CircuitState cur = bottomState;
                while (cur != null && cur != currentState) {
                    cur = cur.getParentState();
                }
                if (cur == null) bottomState = currentState;
            }

            boolean oldPresent = oldState != null;
            boolean pres = currentState != null;
            if (oldPresent != pres) {
                present.set(pres);
            }

            if (sim != oldSim) {
                double freq = sim == null ? 1.0 : sim.getTickFrequency();
                for (TickFrequencyItem r: freqItems) {
                    r.setSelected(Math.abs(r.getFreq() - freq) < 0.001);
                }

            }

            downStateItems.clear();

            CircuitState cur = bottomState;
            while (cur != null && cur != currentState) {
                downStateItems.add(new SimulateMenu.CircuitStateMenuItem(cur));
                cur = cur.getParentState();
            }
            if (cur != null) cur = cur.getParentState();

            upStateItems.clear();

            while (cur != null) {
                upStateItems.add(0, new SimulateMenu.CircuitStateMenuItem(cur));
                cur = cur.getParentState();
            }

            recreateStateMenus();

        }

        private void recreateStateMenus() {
            recreateStateMenu(downStateMenu, downStateItems);
            recreateStateMenu(upStateMenu, upStateItems);
        }

        private void recreateStateMenu(Menu menu, ObservableList<CircuitStateMenuItem> items) {

            menu.getItems().clear();
            menu.setDisable(items.size() == 0);

            for (CircuitStateMenuItem c: items) {
                menu.getItems().add(c);
            }

        }

        private class TickFrequencyItem extends RadioMenuItem{

            private double freq;

            TickFrequencyItem(double value){

                super();
                freq = value;

                init();

                if(value == sim.getTickFrequency()){
                    this.setSelected(true);
                    lastTickFreqItem = this;
                }

            }

            private void init() {

                if (freq < 1000) {
                    String hzStr;
                    if (Math.abs(freq - Math.round(freq)) < 0.0001) {
                        hzStr = "" + (int) Math.round(freq);
                    } else {
                        hzStr = "" + freq;
                    }
                    this.textProperty().bind(localizer.createComplexStringBinding("simulateTickFreqItem", hzStr));
                } else {
                    String kHzStr;
                    double kf = Math.round(freq / 100) / 10.0;
                    if (kf == Math.round(kf)) {
                        kHzStr = "" + (int) kf;
                    } else {
                        kHzStr = "" + kf;
                    }

                    this.textProperty().bind(localizer.createComplexStringBinding("simulateTickKFreqItem", kHzStr));
                }

                this.setOnAction(event -> {
                    if (sim != null) sim.setTickFrequency(freq);
                    AppPreferences.TICK_FREQUENCY.set(freq);
                    if(lastTickFreqItem != null)lastTickFreqItem.setSelected(false);
                    lastTickFreqItem = this;

                });

            }

            public Double getFreq(){
                return freq;
            }

        }

        private class CircuitStateMenuItem extends MenuItem {

            private CircuitState circuitState;

            public CircuitStateMenuItem(CircuitState circuitState) {

                this.circuitState = circuitState;
                Circuit circuit = circuitState.getCircuit();
                this.textProperty().setValue(circuit.getName());
                this.setOnAction(event -> setCurrentState(sim,circuitState));
            }

        }

        private class RunNTicksMenuItem extends MenuItem{

            public RunNTicksMenuItem(int n){

                super();
                this.setText(Integer.toString(n));
                this.setOnAction(event ->{

                    event.consume();

                    sim.runTickNTimes(n);

                });

            }

        }

    }

    private void initFPGAMenu(){

        Menu FPGAMenu = new Menu();
        FPGAMenu.textProperty().bind(localizer.createStringBinding("fpgaMenu"));

        MenuItem annotate = new MenuItem();
        annotate.textProperty().bind(localizer.createStringBinding("fpgaAnnotate"));
        annotate.setOnAction(event -> proj.getFpgaToolchainOrchestrator().annotate(false));

        MenuItem annotateClearExisting = new MenuItem();
        annotateClearExisting.textProperty().bind(localizer.createStringBinding("fpgaAnnotateClearExisting"));
        annotateClearExisting.setOnAction(event -> proj.getFpgaToolchainOrchestrator().annotate(true));

        SeparatorMenuItem sp1 = new SeparatorMenuItem();

        MenuItem chooseBoard = new MenuItem();
        chooseBoard.textProperty().bind(localizer.createStringBinding("fpgaChooseBoard"));
        chooseBoard.setOnAction(event -> proj.getFpgaToolchainOrchestrator().selectBoard());

        MenuItem openProjectConstrains = new MenuItem();
//        openProjectConstrains.disableProperty().bind(proj.getFpgaToolchainOrchestrator().constrainsFileProperty().isNull());
        openProjectConstrains.textProperty().bind(localizer.createStringBinding("fpgaOpenConstrainsFile"));
        openProjectConstrains.setOnAction(event -> proj.getFpgaToolchainOrchestrator().openConstrainsFile());

        SeparatorMenuItem sp2 = new SeparatorMenuItem();

        MenuItem exportFiles = new MenuItem();
        exportFiles.textProperty().bind(localizer.createStringBinding("fpgaExportFilesItem"));
        exportFiles.setOnAction(event -> proj.getFpgaToolchainOrchestrator().exportHDLFiles(0, 0));

        MenuItem generateBit = new MenuItem();
        generateBit.textProperty().bind(localizer.createStringBinding("fpgaGenerateBitFile"));
        //generateBit.setOnAction(event -> proj.getFpgaToolchainOrchestrator().exportHDLFiles(0, 0));

        SeparatorMenuItem sp3 = new SeparatorMenuItem();

        MenuItem uploadToBoard = new MenuItem();
        uploadToBoard.textProperty().bind(localizer.createStringBinding("fpgaUploadToBoard"));
        //uploadToBoard.setOnAction(event -> proj.getFpgaToolchainOrchestrator().exportHDLFiles(0, 0));

        SeparatorMenuItem sp4 = new SeparatorMenuItem();


        FPGAMenu.getItems().addAll(
                annotate,
                annotateClearExisting,
                sp1,
                chooseBoard,
                openProjectConstrains,
                sp2,
                exportFiles,
                generateBit,
                sp3,
                uploadToBoard,
                sp4
        );

        this.getMenus().add(FPGAMenu);

    }

    private class WindowMenu extends Menu{

        private ObservableList<MenuItem> defaultWindowMenuItems = FXCollections.observableArrayList();

        public WindowMenu(){

            super();
            this.textProperty().bind(localizer.createStringBinding("windowMenu"));

            init();

            this.setOnShowing(event -> {
                recalculateItems();
            });

        }

        private void init(){

            MenuItem Maximize = new MenuItem();
            Maximize.setAccelerator(KeyCombination.keyCombination("Ctrl+M"));
            Maximize.textProperty().bind(localizer.createStringBinding("windowZoomItem"));
            Maximize.setOnAction(event -> proj.getFrameController().getStage().setMaximized(true));

            MenuItem Minimize = new MenuItem();
            Minimize.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+M"));
            Minimize.textProperty().bind(localizer.createStringBinding("windowMinimizeItem"));
            Minimize.setOnAction(event -> proj.getFrameController().getStage().setIconified(true));

            MenuItem Close = new MenuItem();
            Close.setAccelerator(KeyCombination.keyCombination("Ctrl+W"));
            Close.textProperty().bind(localizer.createStringBinding("windowCloseItem"));
            Close.setOnAction(event -> proj.getFrameController().getStage().close());


            SeparatorMenuItem sp1 = new SeparatorMenuItem();


            MenuItem CombAnalyse = new MenuItem();
            CombAnalyse.textProperty().bind(localizer.createStringBinding("analyzerWindowTitle"));
            CombAnalyse.setOnAction(event -> FrameManager.CreateCircuitAnalysisFrame(proj));


            MenuItem Preferences = new MenuItem();
            Preferences.textProperty().bind(localizer.createStringBinding("preferencesFrameMenuItem"));
            Preferences.setOnAction(event -> FrameManager.CreatePreferencesFrame());

            SeparatorMenuItem sp2 = new SeparatorMenuItem();

            defaultWindowMenuItems.addAll(
                    Maximize,
                    Minimize,
                    Close,
                    sp1,
                    CombAnalyse,
                    Preferences,
                    sp2
            );

            this.getItems().addAll(defaultWindowMenuItems);

        }

        private void recalculateItems(){

            this.getItems().clear();
            this.getItems().addAll(defaultWindowMenuItems);

            for (Project project: FrameManager.getOpenedProjects()) {

                RadioMenuItem menuItem = new RadioMenuItem();
                menuItem.setText(project.getLogisimFile().getName());
                if(project == proj){menuItem.setSelected(true);}
                menuItem.setOnAction(event -> FrameManager.FocusOnFrame(project));

                this.getItems().add(menuItem);

            }

        }

    }

    private void initHelpMenu(){

        Menu Help = new Menu();
        Help.textProperty().bind(localizer.createStringBinding("helpMenu"));

        MenuItem Tutorial = new MenuItem();
        Tutorial.textProperty().bind(localizer.createStringBinding("helpTutorialItem"));
        Tutorial.setOnAction(event -> FrameManager.CreateHelpFrame("htmlHelpMainUrl"));

        MenuItem UserGuide = new MenuItem();
        UserGuide.textProperty().bind(localizer.createStringBinding("helpGuideItem"));
        UserGuide.setOnAction(event -> FrameManager.CreateHelpFrame("htmlHelpGuidUrl"));

        MenuItem LibraryReference = new MenuItem();
        LibraryReference.textProperty().bind(localizer.createStringBinding("helpLibraryItem"));
        LibraryReference.setOnAction(event -> FrameManager.CreateHelpFrame("htmlHelpLibsUrl"));


        SeparatorMenuItem sp1 = new SeparatorMenuItem();


        MenuItem About = new MenuItem();
        About.textProperty().bind(localizer.createStringBinding("helpAboutItem"));
        About.setOnAction(event -> FrameManager.CreateAboutFrame());

        Help.getItems().addAll(
                Tutorial,
                UserGuide,
                LibraryReference,
                sp1,
                About
        );
        this.getMenus().add(Help);

    }

}


