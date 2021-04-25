package com.cburch.LogisimFX.newgui.MainFrame;

import com.cburch.LogisimFX.file.LogisimFileActions;
import com.cburch.LogisimFX.newgui.FrameManager;
import com.cburch.LogisimFX.file.LogisimFile;
import com.cburch.LogisimFX.newgui.DialogManager;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.proj.ProjectActions;
import com.cburch.LogisimFX.FileSelector;
import com.cburch.LogisimFX.Localizer;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.File;

public class CustomMenuBar extends MenuBar {

    private int prefHeight = 25;

    private Localizer localizer
            = new Localizer("LogisimFX/resources/localization/menu");

    private Project proj;
    private LogisimFile logisimFile;

    private ExplorerToolBar explorerToolBar;


    public CustomMenuBar(ExplorerToolBar etb, Project project){

        super();

        explorerToolBar = etb;

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
        initSimulateMenu();
        initWindowMenu();
        initHelpMenu();

    }

    private void initFileMenu(){

        Menu File = new Menu();
        File.textProperty().bind(localizer.createStringBinding("fileMenu"));

        MenuItem New = new MenuItem();
        New.textProperty().bind(localizer.createStringBinding("fileNewItem"));
        New.setOnAction(event -> ProjectActions.doNew(proj));

        MenuItem Open = new MenuItem();
        Open.textProperty().bind(localizer.createStringBinding("fileOpenItem"));
        Open.setOnAction(event -> {
            ProjectActions.doOpen(proj);
        });

        //see gui/menu/OpenRecent
        OpenRecentMenu OpenRecent = new OpenRecentMenu(proj);
        OpenRecent.textProperty().bind(localizer.createStringBinding("fileOpenRecentItem"));
        //OpenRecent.setOnShowing(event -> {OpenRecent.renewItems();});


        SeparatorMenuItem sp1 = new SeparatorMenuItem();


        MenuItem Close = new MenuItem();
        Close.textProperty().bind(localizer.createStringBinding("fileCloseItem"));
        Close.setOnAction(event -> proj.getFrameController().getStage().close());

        MenuItem Save = new MenuItem();
        Save.textProperty().bind(localizer.createStringBinding("fileSaveItem"));
        Save.setOnAction(event -> ProjectActions.doSave(proj));

        MenuItem SaveAs = new MenuItem();
        SaveAs.textProperty().bind(localizer.createStringBinding("fileSaveAsItem"));
        SaveAs.setOnAction(event -> ProjectActions.doSaveAs(proj));


        SeparatorMenuItem sp2 = new SeparatorMenuItem();


        MenuItem ExportImage = new MenuItem();
        ExportImage.textProperty().bind(localizer.createStringBinding("fileExportImageItem"));
        ExportImage.setOnAction(event -> FrameManager.CreateExportImageFrame(proj));

        MenuItem Print = new MenuItem();
        Print.textProperty().bind(localizer.createStringBinding("filePrintItem"));
        Print.setOnAction(event -> FrameManager.CreatePrintFrame(proj));


        SeparatorMenuItem sp3 = new SeparatorMenuItem();


        MenuItem Preferences = new MenuItem();
        Preferences.textProperty().bind(localizer.createStringBinding("filePreferencesItem"));
        Preferences.setOnAction(event -> FrameManager.CreatePreferencesFrame());


        SeparatorMenuItem sp4 = new SeparatorMenuItem();


        MenuItem Exit = new MenuItem();
        Exit.textProperty().bind(localizer.createStringBinding("fileQuitItem"));
        Exit.setOnAction(event -> FrameManager.ExitProgram());

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
                Exit
        );

        this.getMenus().add(File);


    }

    private void initEditMenu(){

        Menu Edit = new Menu();
        Edit.textProperty().bind(localizer.createStringBinding("editMenu"));

        MenuItem Undo = new MenuItem();
        Undo.textProperty().bind(localizer.createStringBinding("editCantUndoItem"));
        Undo.setOnAction(event -> {});


        SeparatorMenuItem sp1 = new SeparatorMenuItem();


        MenuItem Cut = new MenuItem();
        Cut.textProperty().bind(localizer.createStringBinding("editCutItem"));
        Cut.setOnAction(event -> {});

        MenuItem Copy = new MenuItem();
        Copy.textProperty().bind(localizer.createStringBinding("editCopyItem"));
        Copy.setOnAction(event -> {});

        MenuItem Paste = new MenuItem();
        Paste.textProperty().bind(localizer.createStringBinding("editPasteItem"));
        Paste.setOnAction(event -> {});


        SeparatorMenuItem sp2 = new SeparatorMenuItem();


        MenuItem Delete = new MenuItem();
        Delete.textProperty().bind(localizer.createStringBinding("editDuplicateItem"));
        Delete.setOnAction(event -> {});

        MenuItem Duplicate = new MenuItem();
        Duplicate.textProperty().bind(localizer.createStringBinding("editClearItem"));
        Duplicate.setOnAction(event -> {});

        MenuItem SelecteAll = new MenuItem();
        SelecteAll.textProperty().bind(localizer.createStringBinding("editSelectAllItem"));
        SelecteAll.setOnAction(event -> {});


        SeparatorMenuItem sp3 = new SeparatorMenuItem();


        MenuItem RaiseSelection = new MenuItem();
        RaiseSelection.textProperty().bind(localizer.createStringBinding("editLowerItem"));
        RaiseSelection.setOnAction(event -> {});

        MenuItem LowerSelection = new MenuItem();
        LowerSelection.textProperty().bind(localizer.createStringBinding("editRaiseItem"));
        LowerSelection.setOnAction(event -> {});

        MenuItem RiseToTop = new MenuItem();
        RiseToTop.textProperty().bind(localizer.createStringBinding("editRaiseTopItem"));
        RiseToTop.setOnAction(event -> {});

        MenuItem LowerToBottom = new MenuItem();
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

            String circuitName = DialogManager.CreateInputDialog(proj.getLogisimFile());

            if (circuitName != null) {
                Circuit circuit = new Circuit(circuitName);
                proj.doAction(LogisimFileActions.addCircuit(circuit));
                proj.setCurrentCircuit(circuit);
            }

        });



        Menu UploadLibrary = new Menu();
        UploadLibrary.textProperty().bind(localizer.createStringBinding("projectLoadLibraryItem"));

        MenuItem BuiltInLib = new MenuItem();
        BuiltInLib.textProperty().bind(localizer.createStringBinding("projectLoadBuiltinItem"));
        BuiltInLib.setOnAction(event -> {});

        MenuItem LogisimLib = new MenuItem();
        LogisimLib.textProperty().bind(localizer.createStringBinding("projectLoadLogisimItem"));
        LogisimLib.setOnAction(event -> {});

        MenuItem JARLib = new MenuItem();
        JARLib.textProperty().bind(localizer.createStringBinding("projectLoadJarItem"));
        JARLib.setOnAction(event -> {});

        UploadLibrary.getItems().addAll(
                BuiltInLib,
                LogisimLib,
                JARLib
        );



        MenuItem UnloadLibrary = new MenuItem();
        UnloadLibrary.textProperty().bind(localizer.createStringBinding("projectUnloadLibraryItem"));
        UnloadLibrary.setOnAction(event -> {});


        SeparatorMenuItem sp1 = new SeparatorMenuItem();


        MenuItem MoveCircuitUp = new MenuItem();
        MoveCircuitUp.disableProperty().bind(
                Bindings.or(logisimFile.obsPos.isEqualTo("first"),logisimFile.obsPos.isEqualTo("first&last"))
        );
        MoveCircuitUp.textProperty().bind(localizer.createStringBinding("projectMoveCircuitUpItem"));
        MoveCircuitUp.setOnAction(event -> {});

        MenuItem MoveCircuitDown = new MenuItem();
        MoveCircuitDown.disableProperty().bind(
                Bindings.or(logisimFile.obsPos.isEqualTo("last"),logisimFile.obsPos.isEqualTo("first&last"))
        );
        MoveCircuitDown.textProperty().bind(localizer.createStringBinding("projectMoveCircuitDownItem"));
        MoveCircuitDown.setOnAction(event -> {});

        MenuItem SetAsMain = new MenuItem();
        SetAsMain.disableProperty().bind(
                logisimFile.obsPos.isEqualTo("first&last")
        );
        SetAsMain.textProperty().bind(localizer.createStringBinding("projectSetAsMainItem"));
        SetAsMain.setOnAction(event -> {});

        MenuItem RemoveCirc = new MenuItem();
        RemoveCirc.disableProperty().bind(
                logisimFile.obsPos.isEqualTo("first&last")
        );
        RemoveCirc.textProperty().bind(localizer.createStringBinding("projectRemoveCircuitItem"));
        RemoveCirc.setOnAction(event -> {});

        MenuItem RevertAppearance = new MenuItem();

        RevertAppearance.textProperty().bind(localizer.createStringBinding("projectRevertAppearanceItem"));
        RevertAppearance.setOnAction(event -> {});


        SeparatorMenuItem sp2 = new SeparatorMenuItem();

        MenuItem ShowTools = new MenuItem();
        ShowTools.disableProperty().bind(explorerToolBar.ShowProjectExplorer);
        ShowTools.textProperty().bind(localizer.createStringBinding("projectViewToolboxItem"));
        ShowTools.setOnAction(event -> {

            explorerToolBar.ShowProjectExplorer.set(true);
            explorerToolBar.ShowSimulationHierarchy.set(false);

            explorerToolBar.AdditionalToolBar.SetAdditionalToolBarItems("ControlCircuitOrder");
            explorerToolBar.TreeExplorerAggregation.setProjectView();

        });

        MenuItem ViewSimulationTree = new MenuItem();
        ViewSimulationTree.disableProperty().bind(explorerToolBar.ShowSimulationHierarchy);
        ViewSimulationTree.textProperty().bind(localizer.createStringBinding("projectViewSimulationItem"));
        ViewSimulationTree.setOnAction(event -> {

            explorerToolBar.ShowProjectExplorer.set(false);
            explorerToolBar.ShowSimulationHierarchy.set(true);

            explorerToolBar.AdditionalToolBar.SetAdditionalToolBarItems("ControlCircuitTicks");
            explorerToolBar.TreeExplorerAggregation.setSimulationView();

        });

        MenuItem EditCircuitLayout = new MenuItem();
        EditCircuitLayout.disableProperty().bind(explorerToolBar.EditCircuitLayout);
        EditCircuitLayout.textProperty().bind(localizer.createStringBinding("projectEditCircuitLayoutItem"));
        EditCircuitLayout.setOnAction(event -> {

            explorerToolBar.EditCircuitLayout.set(true);
            explorerToolBar.EditCircuitAppearance.set(false);

            explorerToolBar.MainToolBar.SetMainToolBarItems("RedactCircuit");
            //TODO: add controll method for canvas
        });

        MenuItem EditCircuitAppearance = new MenuItem();
        EditCircuitAppearance.disableProperty().bind(explorerToolBar.EditCircuitAppearance);
        EditCircuitAppearance.textProperty().bind(localizer.createStringBinding("projectEditCircuitAppearanceItem"));
        EditCircuitAppearance.setOnAction(event -> {

            explorerToolBar.EditCircuitLayout.set(false);
            explorerToolBar.EditCircuitAppearance.set(true);

            explorerToolBar.MainToolBar.SetMainToolBarItems("RedactBlackBox");
            //TODO: add controll method for canvas
        });


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

    private void initSimulateMenu(){

        Menu Simulate = new Menu();
        Simulate.textProperty().bind(localizer.createStringBinding("simulateMenu"));

        MenuItem EnableSimulation = new MenuItem();
        EnableSimulation.textProperty().bind(localizer.createStringBinding("simulateRunItem"));
        EnableSimulation.setOnAction(event -> {});

        MenuItem ResetSimulation = new MenuItem();
        ResetSimulation.textProperty().bind(localizer.createStringBinding("simulateResetItem"));
        ResetSimulation.setOnAction(event -> {});

        MenuItem SimStep = new MenuItem();
        SimStep.textProperty().bind(localizer.createStringBinding("simulateStepItem"));
        SimStep.setOnAction(event -> {});


        SeparatorMenuItem sp1 = new SeparatorMenuItem();



        Menu UpState = new Menu();
        UpState.textProperty().bind(localizer.createStringBinding("simulateUpStateMenu"));



        Menu DownState = new Menu();
        DownState.textProperty().bind(localizer.createStringBinding("simulateDownStateMenu"));



        SeparatorMenuItem sp2 = new SeparatorMenuItem();


        MenuItem TickOnce = new MenuItem();
        TickOnce.textProperty().bind(localizer.createStringBinding("simulateTickOnceItem"));
        TickOnce.setOnAction(event -> {});

        MenuItem TicksEnable = new MenuItem();
        TicksEnable.textProperty().bind(localizer.createStringBinding("simulateTickItem"));
        TicksEnable.setOnAction(event -> {});



        Menu Frequency = new Menu();
        Frequency.textProperty().bind(localizer.createStringBinding("simulateTickFreqMenu"));

        MenuItem KHZ41 = new MenuItem();
        KHZ41.textProperty().bind(localizer.createStringBinding("KHZ41"));
        KHZ41.setOnAction(event -> {});

        MenuItem KHZ2 = new MenuItem();
        KHZ2.textProperty().bind(localizer.createStringBinding("KHZ2"));
        KHZ2.setOnAction(event -> {});

        MenuItem KHZ1 = new MenuItem();
        KHZ1.textProperty().bind(localizer.createStringBinding("KHZ1"));
        KHZ1.setOnAction(event -> {});

        MenuItem HZ512 = new MenuItem();
        HZ512.textProperty().bind(localizer.createStringBinding("HZ512"));
        HZ512.setOnAction(event -> {});

        MenuItem HZ256 = new MenuItem();
        HZ256.textProperty().bind(localizer.createStringBinding("HZ256"));
        HZ256.setOnAction(event -> {});

        MenuItem HZ128 = new MenuItem();
        HZ128.textProperty().bind(localizer.createStringBinding("HZ128"));
        HZ128.setOnAction(event -> {});

        MenuItem HZ64 = new MenuItem();
        HZ64.textProperty().bind(localizer.createStringBinding("HZ64"));
        HZ64.setOnAction(event -> {});

        MenuItem HZ32 = new MenuItem();
        HZ32.textProperty().bind(localizer.createStringBinding("HZ32"));
        HZ32.setOnAction(event -> {});

        MenuItem HZ16 = new MenuItem();
        HZ16.textProperty().bind(localizer.createStringBinding("HZ16"));
        HZ16.setOnAction(event -> {});

        MenuItem HZ8 = new MenuItem();
        HZ8.textProperty().bind(localizer.createStringBinding("HZ8"));
        HZ8.setOnAction(event -> {});

        MenuItem HZ4 = new MenuItem();
        HZ4.textProperty().bind(localizer.createStringBinding("HZ4"));
        HZ4.setOnAction(event -> {});

        MenuItem HZ2 = new MenuItem();
        HZ2.textProperty().bind(localizer.createStringBinding("HZ2"));
        HZ2.setOnAction(event -> {});

        MenuItem HZ1 = new MenuItem();
        HZ1.textProperty().bind(localizer.createStringBinding("HZ1"));
        HZ1.setOnAction(event -> {});

        MenuItem HZ05 = new MenuItem();
        HZ05.textProperty().bind(localizer.createStringBinding("HZ05"));
        HZ05.setOnAction(event -> {});

        MenuItem HZ025 = new MenuItem();
        HZ025.textProperty().bind(localizer.createStringBinding("HZ025"));
        HZ025.setOnAction(event -> {});

        Frequency.getItems().addAll(
                KHZ41,
                KHZ2,
                KHZ1,
                HZ512,
                HZ256,
                HZ128,
                HZ64,
                HZ32,
                HZ16,
                HZ8,
                HZ4,
                HZ2,
                HZ1,
                HZ05,
                HZ025
        );


        SeparatorMenuItem sp3 = new SeparatorMenuItem();


        MenuItem SimLog = new MenuItem();
        SimLog.textProperty().bind(localizer.createStringBinding("simulateLogItem"));
        SimLog.setOnAction(event -> FrameManager.CreateCircLogFrame(proj));

        Simulate.getItems().addAll(
                EnableSimulation,
                ResetSimulation,
                SimStep,
                sp1,
                UpState,
                DownState,
                sp2,
                TickOnce,
                TicksEnable,
                Frequency,
                sp3,
                SimLog
        );
        this.getMenus().add(Simulate);
    }

    private void initWindowMenu(){

        Menu Window = new WindowMenu();
        Window.textProperty().bind(localizer.createStringBinding("windowMenu"));

        this.getMenus().add(Window);

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


    private class WindowMenu extends Menu{

        private ObservableList<MenuItem> defaultWindowMenuItems = FXCollections.observableArrayList();

        public WindowMenu(){

            super();

            init();

            this.setOnShowing(event -> {
                recalculateItems();
            });

        }

        private void init(){

            MenuItem Maximize = new MenuItem();
            Maximize.textProperty().bind(localizer.createStringBinding("windowZoomItem"));
            Maximize.setOnAction(event -> proj.getFrameController().getStage().setMaximized(true));

            MenuItem Minimize = new MenuItem();
            Minimize.textProperty().bind(localizer.createStringBinding("windowMinimizeItem"));
            Minimize.setOnAction(event -> proj.getFrameController().getStage().setIconified(true));

            MenuItem Close = new MenuItem();
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

}


