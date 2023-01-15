/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.MainFrame.ProjectExplorer_deprecated;

import LogisimFX.newgui.MainFrame.LC;
import LogisimFX.newgui.MainFrame.MainFrameController;
import LogisimFX.newgui.MainFrame.deprecated.MainToolBar;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.AnchorPane;

public class ExplorerToolBar extends ToolBar {

    //Check ExplorerToolbarModel of com.cburch.logisim.gui.main

    private MainFrameController FrameController;

    private LogisimFX.newgui.MainFrame.ProjectExplorer_deprecated.AdditionalToolBar AdditionalToolBar;
    private LogisimFX.newgui.MainFrame.deprecated.MainToolBar MainToolBar;
    private LogisimFX.newgui.MainFrame.ProjectExplorer_deprecated.TreeExplorerAggregation TreeExplorerAggregation;

    private int prefWidth = 15;
    private int prefHeight = 15;
    private ObservableList<Node> ControlBtnsList;

    private Lighting lighting = new Lighting();

    public SimpleBooleanProperty ShowProjectExplorer = new SimpleBooleanProperty(true);
    public SimpleBooleanProperty ShowSimulationHierarchy = new SimpleBooleanProperty(false);
    public SimpleBooleanProperty EditCircuitLayout = new SimpleBooleanProperty(true);
    public SimpleBooleanProperty EditCircuitAppearance = new SimpleBooleanProperty(false);

    private CustomButton ShowProjectExplorerBtn;
    private CustomButton ShowSimulationBtn;
    private CustomButton EditCircuitBtn;
    private CustomButton EditAppearanceBtn;

    private static class ToolTip extends Tooltip{

        public ToolTip(String text){
            super();
            textProperty().bind(LC.createStringBinding(text));
        }

    }


    public ExplorerToolBar(MainToolBar main, AdditionalToolBar additional,
                           TreeExplorerAggregation explorer, MainFrameController frameController){

        super();

        this.FrameController = frameController;

        MainToolBar = main;
        AdditionalToolBar = additional;

        TreeExplorerAggregation = explorer;

        ControlBtnsList = FXCollections.observableArrayList();

        AnchorPane.setLeftAnchor(this,0.0);
        AnchorPane.setTopAnchor(this,0.0);
        AnchorPane.setRightAnchor(this,0.0);

        prefHeight(20);

        initItems();

    }

    private void initItems(){

        ShowProjectExplorerBtn = new CustomButton(prefWidth,prefHeight,"projtool.gif");
        ShowProjectExplorerBtn.setTooltip(new ToolTip("projectViewToolboxTip"));
        ShowProjectExplorerBtn.setEffect(lighting);
        ShowProjectExplorerBtn.setOnAction(event -> {
            ShowProjectExplorer();
            ShowProjectExplorerBtn.setEffect(lighting);
            ShowSimulationBtn.setEffect(null);
        });

        ShowSimulationBtn = new CustomButton(prefWidth,prefHeight,"projsim.gif");
        ShowSimulationBtn.setTooltip(new ToolTip("projectViewSimulationTip"));
        ShowSimulationBtn.setOnAction(event -> {
            ShowSimulation();
            ShowProjectExplorerBtn.setEffect(null);
            ShowSimulationBtn.setEffect(lighting);
        });

        Separator sep = new Separator();

        EditCircuitBtn = new CustomButton(prefWidth,prefHeight,"projlayo.gif");
        EditCircuitBtn.setTooltip(new ToolTip("projectEditLayoutTip"));
        EditCircuitBtn.setEffect(lighting);
        EditCircuitBtn.setOnAction(event -> {
            EditCircuit();
            EditCircuitBtn.setEffect(lighting);
            EditAppearanceBtn.setEffect(null);
        });

        EditAppearanceBtn = new CustomButton(prefWidth,prefHeight,"projapp.gif");
        EditAppearanceBtn.setTooltip(new ToolTip("projectEditAppearanceTip"));
        EditAppearanceBtn.setOnAction(event -> {
            EditAppearance();
            EditCircuitBtn.setEffect(null);
            EditAppearanceBtn.setEffect(lighting);
        });

        ControlBtnsList.addAll(
                ShowProjectExplorerBtn,
                ShowSimulationBtn,
                sep,
                EditCircuitBtn,
                EditAppearanceBtn
        );

        getItems().addAll(ControlBtnsList);

    }

    public void ShowProjectExplorer(){

        ShowProjectExplorer.set(true);
        ShowSimulationHierarchy.set(false);

        AdditionalToolBar.SetAdditionalToolBarItems("ControlCircuitOrder");
        TreeExplorerAggregation.setProjectView();

    }

    public void ShowSimulation(){

        ShowProjectExplorer.set(false);
        ShowSimulationHierarchy.set(true);

        AdditionalToolBar.SetAdditionalToolBarItems("ControlCircuitTicks");
        TreeExplorerAggregation.setSimulationView();

    }

    public void EditCircuit(){

        EditCircuitLayout.set(true);
        EditCircuitAppearance.set(false);

        MainToolBar.SetMainToolBarItems("RedactCircuit");
        FrameController.setLayoutView();

    }

    public void EditAppearance(){

        EditCircuitLayout.set(false);
        EditCircuitAppearance.set(true);

        MainToolBar.SetMainToolBarItems("RedactAppearance");
        FrameController.setAppearanceView();

    }

}
