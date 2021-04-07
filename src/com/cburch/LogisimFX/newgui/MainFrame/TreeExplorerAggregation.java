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

        //projectTreeExplorer.setListener(new ToolboxManip(proj, projectTreeExplorer));

        currTreeExplorer = new AbstractTreeExplorer() {
            @Override
            public void updateTree() {

            }
        };

        setProjectView();

    }

    public AbstractTreeExplorer getTree(){
        return currTreeExplorer;
    }

    public void setSimulationView(){
        currTreeExplorer.setRoot(simulationTreeExplorer.getRoot());
        currTreeExplorer = simulationTreeExplorer;
        updateTree();
    }

    public void setProjectView(){
        currTreeExplorer.setRoot(projectTreeExplorer.getRoot());
        currTreeExplorer = projectTreeExplorer;
        updateTree();
    }

    public void updateTree(){
        currTreeExplorer.updateTree();
    }

}
