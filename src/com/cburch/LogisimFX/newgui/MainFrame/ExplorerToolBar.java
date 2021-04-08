package com.cburch.LogisimFX.newgui.MainFrame;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;

public class ExplorerToolBar extends ToolBar {

    //Check ExplorerToolbarModel of com.cburch.logisim.gui.main

    public AdditionalToolBar  AdditionalToolBar;
    public MainToolBar MainToolBar;
    public TreeExplorerAggregation TreeExplorerAggregation;

    private int prefWidth = 15;
    private int prefHeight = 15;
    private ObservableList<Node> ControlBtnsList;

    public SimpleBooleanProperty ShowProjectExplorer = new SimpleBooleanProperty(true);
    public SimpleBooleanProperty ShowSimulationHierarchy = new SimpleBooleanProperty(false);
    public SimpleBooleanProperty EditCircuitLayout = new SimpleBooleanProperty(true);
    public SimpleBooleanProperty EditCircuitAppearance = new SimpleBooleanProperty(false);


    public ExplorerToolBar(MainToolBar main, AdditionalToolBar additional, TreeExplorerAggregation explorer){

        super();

        MainToolBar = main;
        AdditionalToolBar = additional;
        //ProjectTreeExplorer = explorer;
        TreeExplorerAggregation = explorer;

        ControlBtnsList = FXCollections.observableArrayList();

        AnchorPane.setLeftAnchor(this,0.0);
        AnchorPane.setTopAnchor(this,0.0);
        AnchorPane.setRightAnchor(this,0.0);

        prefHeight(20);

        initItems();

    }

    private void initItems(){

        CustomButton ShowToolLibraryBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/projtool.gif");
        ShowToolLibraryBtn.setOnAction(event -> {

            ShowProjectExplorer.set(true);
            ShowSimulationHierarchy.set(false);

            AdditionalToolBar.SetAdditionalToolBarItems("ControlCircuitOrder");
            TreeExplorerAggregation.setProjectView();

        });

        CustomButton ShowCurcuitHierarchyBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/projsim.gif");
        ShowCurcuitHierarchyBtn.setOnAction(event -> {

            ShowProjectExplorer.set(false);
            ShowSimulationHierarchy.set(true);

            AdditionalToolBar.SetAdditionalToolBarItems("ControlCircuitTicks");
            TreeExplorerAggregation.setSimulationView();

        });

        Separator sep = new Separator();

        CustomButton RedactCircuitBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/projlayo.gif");
        RedactCircuitBtn.setOnAction(event -> {

            EditCircuitLayout.set(true);
            EditCircuitAppearance.set(false);

            MainToolBar.SetMainToolBarItems("RedactCircuit");
            //TODO: add controll method for canvas
        });

        CustomButton RedactBlackBoxBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/projapp.gif");
        RedactBlackBoxBtn.setOnAction(event -> {

            EditCircuitLayout.set(false);
            EditCircuitAppearance.set(true);

            MainToolBar.SetMainToolBarItems("RedactBlackBox");
            //TODO: add controll method for canvas
        });

        ControlBtnsList.addAll(
                ShowToolLibraryBtn,
                ShowCurcuitHierarchyBtn,
                sep,
                RedactCircuitBtn,
                RedactBlackBoxBtn
        );

        getItems().addAll(ControlBtnsList);

    }

}
