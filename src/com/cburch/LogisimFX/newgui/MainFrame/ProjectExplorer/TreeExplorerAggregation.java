package com.cburch.LogisimFX.newgui.MainFrame.ProjectExplorer;

import com.cburch.LogisimFX.proj.Project;
import javafx.scene.control.TreeView;

public class TreeExplorerAggregation extends TreeView {

    private Project proj;

    private ProjectTreeExplorer projectTreeExplorer;
    private SimulationTreeExplorer simulationTreeExplorer;
    private AbstractTreeExplorer currTreeExplorer;

    public TreeExplorerAggregation(Project project){

        super();

        proj = project;

        projectTreeExplorer = new ProjectTreeExplorer(proj);
        simulationTreeExplorer = new SimulationTreeExplorer(proj);

        currTreeExplorer = projectTreeExplorer;

        setProjectView();

    }

    public void setSimulationView(){

        currTreeExplorer = simulationTreeExplorer;

        this.setCellFactory(simulationTreeExplorer.getCellFactory());
        this.setRoot(simulationTreeExplorer.getRoot());

        updateTree();

    }

    public void setProjectView(){

        currTreeExplorer = projectTreeExplorer;

        this.setCellFactory(projectTreeExplorer.getCellFactory());
        this.setRoot(projectTreeExplorer.getRoot());

        updateTree();

    }

    public void updateTree(){

        currTreeExplorer.updateTree();

        this.setRoot(null);
        this.setCellFactory(null);

        this.setCellFactory(currTreeExplorer.getCellFactory());
        this.setRoot(currTreeExplorer.getRoot());

    }

}
