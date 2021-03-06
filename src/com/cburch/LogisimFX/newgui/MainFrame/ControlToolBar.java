package com.cburch.LogisimFX.newgui.MainFrame;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import com.cburch.LogisimFX.Localizer;

import java.util.Locale;

public class ControlToolBar extends ToolBar {

    private AdditionalToolBar  AdditionalToolBar;
    private MainToolBar MainToolBar;
    private int prefWidth = 15;
    private int prefHeight = 15;
    private ObservableList<Node> ControlBtnsList;

    public ControlToolBar(MainToolBar main, AdditionalToolBar additional){

        super();

        MainToolBar = main;
        AdditionalToolBar = additional;

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
            AdditionalToolBar.SetAdditionalToolBarItems("ControlCircuitOrder");
            Localizer.setLocale(Locale.forLanguageTag("ru"));
            System.out.println("ru");
            //TODO: add controll method for tree hierarcy
        });

        CustomButton ShowCurcuitHierarchyBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/projsim.gif");
        ShowCurcuitHierarchyBtn.setOnAction(event -> {
            AdditionalToolBar.SetAdditionalToolBarItems("ControlCircuitTicks");
            Localizer.setLocale(Locale.forLanguageTag("en"));
            System.out.println("en");
            //TODO: add controll method for tree hierarcy
        });

        Separator sep = new Separator();

        CustomButton RedactCircuitBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/projlayo.gif");
        RedactCircuitBtn.setOnAction(event -> {
            MainToolBar.SetMainToolBarItems("RedactCircuit");
            //TODO: add controll method for tree hierarcy
        });

        CustomButton RedactBlackBoxBtn = new CustomButton(prefWidth,prefHeight,"resources/logisim/icons/projapp.gif");
        RedactBlackBoxBtn.setOnAction(event -> {
            MainToolBar.SetMainToolBarItems("RedactBlackBox");
            //TODO: add controll method for tree hierarcy
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
