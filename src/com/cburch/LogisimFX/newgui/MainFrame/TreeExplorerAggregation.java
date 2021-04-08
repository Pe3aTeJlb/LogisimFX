package com.cburch.LogisimFX.newgui.MainFrame;

import com.cburch.LogisimFX.proj.Project;

public class TreeExplorerAggregation {

    private Project proj;

    private ProjectTreeExplorer projectTreeExplorer;
    private SimulationTreeExplorer simulationTreeExplorer;
    private AbstractTreeExplorer currTreeExplorer;

    public TreeExplorerAggregation(Project project){

        proj = project;

        projectTreeExplorer = new ProjectTreeExplorer(proj);
        simulationTreeExplorer = new SimulationTreeExplorer(proj);

        currTreeExplorer = new AbstractTreeExplorer() {
            @Override
            public void updateTree() {

            }
        };

        setSimulationView();
        //setProjectView();

    }

    public AbstractTreeExplorer getTree(){
        return currTreeExplorer;
    }

    public void setSimulationView(){
        //currTreeExplorer = simulationTreeExplorer;
        currTreeExplorer.setCellFactory(simulationTreeExplorer.getCellFactory());
        currTreeExplorer.setRoot(simulationTreeExplorer.getRoot());
        updateTree();
    }

    public void setProjectView(){
        //currTreeExplorer = projectTreeExplorer;
        currTreeExplorer.setCellFactory(projectTreeExplorer.getCellFactory());
        currTreeExplorer.setRoot(projectTreeExplorer.getRoot());
        updateTree();
    }

    public void updateTree(){
        currTreeExplorer.updateTree();
    }

}
