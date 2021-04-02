package com.cburch.LogisimFX.newgui.MainFrame;

import com.cburch.LogisimFX.proj.Project;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class ProjectExplorer extends TreeView {

    private Project proj;

    public ProjectExplorer(Project project){

        super();
        this.proj = project;

        MultipleSelectionModel<TreeItem<String>> selectionModel = this.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        


    }

    public void UpdateTreeView(){

    }

}
