package com.cburch.LogisimFX.newgui.MainFrame;

import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.circuit.CircuitState;
import com.cburch.LogisimFX.circuit.Simulator;
import com.cburch.LogisimFX.localization.LC_menu;
import com.cburch.LogisimFX.newgui.FrameManager;
import com.cburch.LogisimFX.file.LogisimFile;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.LogisimFX.proj.ProjectActions;
import com.cburch.LogisimFX.localization.Localizer;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;

public class CustomMenuBar extends MenuBar {

    private int prefHeight = 25;

    private static Localizer localizer = LC_menu.getInstance();

    private Project proj;
    private LogisimFile logisimFile;

    private ExplorerToolBar explorerToolBar;
    private TreeExplorerAggregation treeExplorerAggregation;

    public CustomMenuBar(ExplorerToolBar etb, Project project, TreeExplorerAggregation tea){

        super();

        explorerToolBar = etb;
        treeExplorerAggregation = tea;

        proj = project;
        logisimFile = proj.getLogisimFile();

        prefHeight(prefHeight);

        AnchorPane.setLeftAnchor(this,0.0);
        AnchorPane.setTopAnchor(this,0.0);
        AnchorPane.setRightAnchor(this,0.0);

        initMenus();

    }

    private void initMenus(){

        initFileMenu();
        initEditMenu();
        initProjectMenu();
        this.getMenus().add(new SimulateMenu());
        //initSimulateMenu();
        this.getMenus().add(new WindowMenu());
       // initWindowMenu();
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
        Open.setOnAction(event -> { ProjectActions.doOpen(proj); });

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

    private void initEditMenu(){

        Menu Edit = new Menu();
        Edit.textProperty().bind(localizer.createStringBinding("editMenu"));

        MenuItem Undo = new MenuItem();
        Undo.setAccelerator(KeyCombination.keyCombination("Ctrl+T"));
        Undo.textProperty().bind(localizer.createStringBinding("editCantUndoItem"));
        Undo.setOnAction(event -> {});


        SeparatorMenuItem sp1 = new SeparatorMenuItem();


        MenuItem Cut = new MenuItem();
        Cut.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));
        Cut.textProperty().bind(localizer.createStringBinding("editCutItem"));
        Cut.setOnAction(event -> {});

        MenuItem Copy = new MenuItem();
        Copy.setAccelerator(KeyCombination.keyCombination("Ctrl+C"));
        Copy.textProperty().bind(localizer.createStringBinding("editCopyItem"));
        Copy.setOnAction(event -> {});

        MenuItem Paste = new MenuItem();
        Paste.setAccelerator(KeyCombination.keyCombination("Ctrl+V"));
        Paste.textProperty().bind(localizer.createStringBinding("editPasteItem"));
        Paste.setOnAction(event -> {});


        SeparatorMenuItem sp2 = new SeparatorMenuItem();


        MenuItem Delete = new MenuItem();
        Delete.setAccelerator(KeyCombination.keyCombination("Delete"));
        Delete.textProperty().bind(localizer.createStringBinding("editDuplicateItem"));
        Delete.setOnAction(event -> {});

        MenuItem Duplicate = new MenuItem();
        Duplicate.setAccelerator(KeyCombination.keyCombination("Ctrl+D"));
        Duplicate.textProperty().bind(localizer.createStringBinding("editClearItem"));
        Duplicate.setOnAction(event -> {});

        MenuItem SelecteAll = new MenuItem();
        SelecteAll.setAccelerator(KeyCombination.keyCombination("Ctrl+A"));
        SelecteAll.textProperty().bind(localizer.createStringBinding("editSelectAllItem"));
        SelecteAll.setOnAction(event -> {});


        SeparatorMenuItem sp3 = new SeparatorMenuItem();


        MenuItem RaiseSelection = new MenuItem();
        RaiseSelection.setAccelerator(KeyCombination.keyCombination("Ctrl+Up"));
        RaiseSelection.textProperty().bind(localizer.createStringBinding("editLowerItem"));
        RaiseSelection.setOnAction(event -> {});

        MenuItem LowerSelection = new MenuItem();
        LowerSelection.setAccelerator(KeyCombination.keyCombination("Ctrl+Down"));
        LowerSelection.textProperty().bind(localizer.createStringBinding("editRaiseItem"));
        LowerSelection.setOnAction(event -> {});

        MenuItem RiseToTop = new MenuItem();
        RiseToTop.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+Up"));
        RiseToTop.textProperty().bind(localizer.createStringBinding("editRaiseTopItem"));
        RiseToTop.setOnAction(event -> {});

        MenuItem LowerToBottom = new MenuItem();
        LowerToBottom.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+Down"));
        LowerToBottom.textProperty().bind(localizer.createStringBinding("editLowerBottomItem"));
        LowerToBottom.setOnAction(event -> {});


        SeparatorMenuItem sp4 = new SeparatorMenuItem();


        MenuItem AddVertex = new MenuItem();
        AddVertex.textProperty().bind(localizer.createStringBinding("editAddControlItem"));
        AddVertex.setOnAction(event -> {});

        MenuItem RemoveVertex = new MenuItem();
        RemoveVertex.textProperty().bind(localizer.createStringBinding("editRemoveControlItem"));
        RemoveVertex.setOnAction(event -> {});

        Edit.getItems().addAll(
                Undo,
                sp1,
                Cut,
                Copy,
                Paste,
                sp2,
                Delete,
                Duplicate,
                SelecteAll,
                sp3,
                RaiseSelection,
                LowerSelection,
                RiseToTop,
                LowerToBottom,
                sp4,
                AddVertex,
                RemoveVertex
        );
        this.getMenus().add(Edit);

    }

    private void initProjectMenu(){

        Menu Project = new Menu();
        Project.textProperty().bind(localizer.createStringBinding("projectMenu"));

        MenuItem AddCircuit = new MenuItem();
        AddCircuit.textProperty().bind(localizer.createStringBinding("projectAddCircuitItem"));
        AddCircuit.setOnAction(event -> {
            ProjectCircuitActions.doAddCircuit(proj);
            treeExplorerAggregation.updateTree();
        });


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


        SeparatorMenuItem sp1 = new SeparatorMenuItem();


        MenuItem MoveCircuitUp = new MenuItem();
        MoveCircuitUp.disableProperty().bind(
                Bindings.or(logisimFile.obsPos.isEqualTo("first"),logisimFile.obsPos.isEqualTo("first&last"))
        );
        MoveCircuitUp.textProperty().bind(localizer.createStringBinding("projectMoveCircuitUpItem"));
        MoveCircuitUp.setOnAction(event -> {
            ProjectCircuitActions.doMoveCircuit(proj,proj.getCurrentCircuit(),-1);
            treeExplorerAggregation.updateTree();
        });

        MenuItem MoveCircuitDown = new MenuItem();
        MoveCircuitDown.disableProperty().bind(
                Bindings.or(logisimFile.obsPos.isEqualTo("last"),logisimFile.obsPos.isEqualTo("first&last"))
        );
        MoveCircuitDown.textProperty().bind(localizer.createStringBinding("projectMoveCircuitDownItem"));
        MoveCircuitDown.setOnAction(event -> {
            ProjectCircuitActions.doMoveCircuit(proj,proj.getCurrentCircuit(),1);
            treeExplorerAggregation.updateTree();
        });

        MenuItem SetAsMain = new MenuItem();
        SetAsMain.disableProperty().bind(logisimFile.isMain);
        SetAsMain.textProperty().bind(localizer.createStringBinding("projectSetAsMainItem"));
        SetAsMain.setOnAction(event -> ProjectCircuitActions.doSetAsMainCircuit(proj, proj.getCurrentCircuit()));

        MenuItem RemoveCirc = new MenuItem();
        RemoveCirc.disableProperty().bind(logisimFile.obsPos.isEqualTo("first&last"));
        RemoveCirc.textProperty().bind(localizer.createStringBinding("projectRemoveCircuitItem"));
        RemoveCirc.setOnAction(event -> {
            ProjectCircuitActions.doRemoveCircuit(proj,proj.getCurrentCircuit());
            treeExplorerAggregation.updateTree();
        });

        MenuItem RevertAppearance = new MenuItem();

        RevertAppearance.textProperty().bind(localizer.createStringBinding("projectRevertAppearanceItem"));
        RevertAppearance.setOnAction(event -> {

            //proj.doAction(new RevertAppearanceAction(cur));
            //Todo:
        });


        SeparatorMenuItem sp2 = new SeparatorMenuItem();

        MenuItem ShowTools = new MenuItem();
        ShowTools.disableProperty().bind(explorerToolBar.ShowProjectExplorer);
        ShowTools.textProperty().bind(localizer.createStringBinding("projectViewToolboxItem"));
        ShowTools.setOnAction(event -> explorerToolBar.ShowProjectExplorer());

        MenuItem ViewSimulationTree = new MenuItem();
        ViewSimulationTree.disableProperty().bind(explorerToolBar.ShowSimulationHierarchy);
        ViewSimulationTree.textProperty().bind(localizer.createStringBinding("projectViewSimulationItem"));
        ViewSimulationTree.setOnAction(event -> explorerToolBar.ShowSimulation());

        MenuItem EditCircuitLayout = new MenuItem();
        EditCircuitLayout.disableProperty().bind(explorerToolBar.EditCircuitLayout);
        EditCircuitLayout.textProperty().bind(localizer.createStringBinding("projectEditCircuitLayoutItem"));
        EditCircuitLayout.setOnAction(event -> explorerToolBar.EditCircuit());

        MenuItem EditCircuitAppearance = new MenuItem();
        EditCircuitAppearance.disableProperty().bind(explorerToolBar.EditCircuitAppearance);
        EditCircuitAppearance.textProperty().bind(localizer.createStringBinding("projectEditCircuitAppearanceItem"));
        EditCircuitAppearance.setOnAction(event -> explorerToolBar.EditAppearance());


        SeparatorMenuItem sp3 = new SeparatorMenuItem();

        MenuItem AnalyzeCircuit = new MenuItem();
        AnalyzeCircuit.textProperty().bind(localizer.createStringBinding("projectAnalyzeCircuitItem"));
        AnalyzeCircuit.setOnAction(event -> FrameManager.CreateCircuitAnalysisFrame(proj));

        MenuItem GetCircuitStatistics = new MenuItem();
        GetCircuitStatistics.textProperty().bind(localizer.createStringBinding("projectGetCircuitStatisticsItem"));
        GetCircuitStatistics.setOnAction(event -> FrameManager.CreateCircuitStatisticFrame(proj, proj.getCurrentCircuit()));

        SeparatorMenuItem sp4 = new SeparatorMenuItem();

        MenuItem Options = new MenuItem();
        Options.textProperty().bind(localizer.createStringBinding("projectOptionsItem"));
        Options.setOnAction(event -> FrameManager.CreateOptionsFrame(proj));

        Project.getItems().addAll(
                AddCircuit,
                UploadLibrary,
                UnloadLibrary,
                sp1,
                MoveCircuitUp,
                MoveCircuitDown,
                SetAsMain,
                RemoveCirc,
                RevertAppearance,
                sp2,
                ShowTools,
                ViewSimulationTree,
                EditCircuitLayout,
                EditCircuitAppearance,
                sp3,
                AnalyzeCircuit,
                GetCircuitStatistics,
                sp4,
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
        private SimpleBooleanProperty running = new SimpleBooleanProperty(false);

        private CircuitState currentState;
        private CircuitState bottomState;
        private Simulator sim;

        public SimulateMenu(){

            super();
            this.textProperty().bind(localizer.createStringBinding("simulateMenu"));

            freqItems = FXCollections.observableArrayList();
            downStateItems = FXCollections.observableArrayList();
            upStateItems = FXCollections.observableArrayList();

            sim = proj.getSimulator();

            setCurrentState(proj.getSimulator(),proj.getCircuitState());

            running.set(sim.isRunning());

            init();

        }

        private void init(){

            RadioMenuItem EnableSimulation = new RadioMenuItem();
            EnableSimulation.setAccelerator(KeyCombination.keyCombination("Ctrl+E"));
            EnableSimulation.textProperty().bind(localizer.createStringBinding("simulateRunItem"));
            EnableSimulation.disableProperty().bind(Bindings.not(present));
            EnableSimulation.setSelected(present.get());
            EnableSimulation.setOnAction(event -> {

                if (sim != null) {
                    sim.setIsRunning(!sim.isRunning());
                    proj.repaintCanvas();
                }

                running.set(sim.isRunning());

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
            SimStep.disableProperty().bind(Bindings.or(Bindings.not(present),running));


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

            RadioMenuItem TicksEnable = new RadioMenuItem();
            TicksEnable.setAccelerator(KeyCombination.keyCombination("Ctrl+K"));
            TicksEnable.textProperty().bind(localizer.createStringBinding("simulateTickItem"));
            TicksEnable.setOnAction(event -> {if (sim != null) sim.setIsTicking(!sim.isTicking());});
            TicksEnable.disableProperty().bind(Bindings.or(Bindings.not(present),Bindings.not(running)));



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
        Tutorial.setOnAction(event -> FrameManager.CreateHelpFrame("tutorial"));

        MenuItem UserGuide = new MenuItem();
        UserGuide.textProperty().bind(localizer.createStringBinding("helpGuideItem"));
        UserGuide.setOnAction(event -> FrameManager.CreateHelpFrame("guide"));

        MenuItem LibraryReference = new MenuItem();
        LibraryReference.textProperty().bind(localizer.createStringBinding("helpLibraryItem"));
        LibraryReference.setOnAction(event -> FrameManager.CreateHelpFrame("libs"));


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

    //Technical methods


}


