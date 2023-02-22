/*
 * This file is part of LogisimFX. Copyright (c) 2023, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.MainFrame.SystemTabs.ProjectExplorerTab;

import LogisimFX.file.LogisimFile;
import LogisimFX.newgui.MainFrame.LC;
import LogisimFX.newgui.MainFrame.ProjectCircuitActions;
import LogisimFX.newgui.MainFrame.SystemTabs.CustomButton;
import LogisimFX.proj.Project;
import javafx.beans.binding.Bindings;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;

public class ProjectTreeToolBar extends ToolBar {

    private Project proj;
    private LogisimFile logisimFile;

    private int prefWidth = 15;
    private int prefHeight = 15;

    private ProjectExplorerTreeView treeView;

    public ProjectTreeToolBar(Project project, ProjectExplorerTreeView treeView){

        super();

        proj = project;
        logisimFile = proj.getLogisimFile();
        this.treeView = treeView;
        //setPrefHeight(20);

        initExplorerOrderControlButtons();

    }

    private void initExplorerOrderControlButtons(){

        CustomButton AddCircuitBtn = new CustomButton(prefWidth,prefHeight,"projadd.gif");
        AddCircuitBtn.setTooltip(new ToolTip("projectAddCircuitTip"));
        AddCircuitBtn.setOnAction(event -> {
            ProjectCircuitActions.doAddCircuit(proj);
        });


        CustomButton PullCircuitUpBtn = new  CustomButton(prefWidth,prefHeight,"projup.gif");
        PullCircuitUpBtn.setTooltip(new ToolTip("projectMoveCircuitUpTip"));
        PullCircuitUpBtn.disableProperty().bind(
                Bindings.or(logisimFile.obsPos.isEqualTo("first"),logisimFile.obsPos.isEqualTo("first&last"))
        );
        PullCircuitUpBtn.setOnAction(event -> {
            ProjectCircuitActions.doMoveCircuit(proj,proj.getCurrentCircuit(),-1);
        });

        CustomButton PullCircuitDownIBtn = new CustomButton(prefWidth,prefHeight,"projdown.gif");
        PullCircuitDownIBtn.setTooltip(new ToolTip("projectMoveCircuitDownTip"));
        PullCircuitDownIBtn.disableProperty().bind(
                Bindings.or(logisimFile.obsPos.isEqualTo("last"),logisimFile.obsPos.isEqualTo("first&last"))
        );
        PullCircuitDownIBtn.setOnAction(event -> {
            ProjectCircuitActions.doMoveCircuit(proj,proj.getCurrentCircuit(),1);
        });

        CustomButton DeleteCircuitBtn = new CustomButton(prefWidth,prefHeight,"projdel.gif");
        DeleteCircuitBtn.setTooltip(new ToolTip("projectRemoveCircuitTip"));
        DeleteCircuitBtn.disableProperty().bind(
                logisimFile.obsPos.isEqualTo("first&last")
        );
        DeleteCircuitBtn.setOnAction(event -> {
            ProjectCircuitActions.doRemoveCircuit(proj,proj.getCurrentCircuit());
        });

        this.getItems().addAll(
                AddCircuitBtn,
                PullCircuitUpBtn,
                PullCircuitDownIBtn,
                DeleteCircuitBtn
        );

    }

    private static class ToolTip extends Tooltip {

        public ToolTip(String text){
            super();
            textProperty().bind(LC.createStringBinding(text));
        }

    }

}
