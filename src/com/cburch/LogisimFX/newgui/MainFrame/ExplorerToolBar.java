package com.cburch.LogisimFX.newgui.MainFrame;

import com.cburch.LogisimFX.Localizer;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;

public class ExplorerToolBar extends ToolBar {

    //Check ExplorerToolbarModel of com.cburch.logisim.gui.main

    private AdditionalToolBar  AdditionalToolBar;
    private MainToolBar MainToolBar;
    private TreeExplorerAggregation TreeExplorerAggregation;

    private int prefWidth = 15;
    private int prefHeight = 15;
    private ObservableList<Node> ControlBtnsList;

    public SimpleBooleanProperty ShowProjectExplorer = new SimpleBooleanProperty(true);
    public SimpleBooleanProperty ShowSimulationHierarchy = new SimpleBooleanProperty(false);
    public SimpleBooleanProperty EditCircuitLayout = new SimpleBooleanProperty(true);
    public SimpleBooleanProperty EditCircuitAppearance = new SimpleBooleanProperty(false);

    private static Localizer lc = new Localizer("gui");

    private static class ToolTip extends Tooltip{

        public ToolTip(String text){
            super();
            textProperty().bind(lc.createStringBinding(text));
        }

    }


    public ExplorerToolBar(MainToolBar main, AdditionalToolBar additional, TreeExplorerAggregation explorer){

        super();

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

        CustomButton ShowProjectExplorerBtn = new CustomButton(prefWidth,prefHeight,"projtool.gif");
        ShowProjectExplorerBtn.setTooltip(new ToolTip("projectViewToolboxTip"));
        ShowProjectExplorerBtn.setOnAction(event -> ShowProjectExplorer());

        CustomButton ShowSimulationBtn = new CustomButton(prefWidth,prefHeight,"projsim.gif");
        ShowSimulationBtn.setTooltip(new ToolTip("projectViewSimulationTip"));
        ShowSimulationBtn.setOnAction(event -> ShowSimulation());

        Separator sep = new Separator();

        CustomButton EditCircuitBtn = new CustomButton(prefWidth,prefHeight,"projlayo.gif");
        EditCircuitBtn.setTooltip(new ToolTip("projectEditLayoutTip"));
        EditCircuitBtn.setOnAction(event -> EditCircuit());

        CustomButton EditAppearanceBtn = new CustomButton(prefWidth,prefHeight,"projapp.gif");
        EditAppearanceBtn.setTooltip(new ToolTip("projectEditAppearanceTip"));
        EditAppearanceBtn.setOnAction(event -> EditAppearance());

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
        //TODO: add controll method for canvas

    }

    public void EditAppearance(){

        EditCircuitLayout.set(false);
        EditCircuitAppearance.set(true);

        MainToolBar.SetMainToolBarItems("RedactAppearance");
        //TODO: add controll method for canvas

    }

}
