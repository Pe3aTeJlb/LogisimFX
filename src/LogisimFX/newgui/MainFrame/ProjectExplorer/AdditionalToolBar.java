package LogisimFX.newgui.MainFrame.ProjectExplorer;

import LogisimFX.file.LogisimFile;
import LogisimFX.newgui.MainFrame.LC;
import LogisimFX.newgui.MainFrame.ProjectCircuitActions;
import LogisimFX.proj.Project;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;

public class AdditionalToolBar extends ToolBar {

    private ObservableList<Node> CircuitOrderControlBtnsList;
    private ObservableList<Node> CircuitTicksControlBtnsList;

    private int prefWidth = 15;
    private int prefHeight = 15;

    private TreeExplorerAggregation treeExplorerAggregation;

    private Project proj;
    private LogisimFile logisimFile;

    private static class ToolTip extends Tooltip{

        public ToolTip(String text){
            super();
            textProperty().bind(LC.createStringBinding(text));
        }

    }

    public AdditionalToolBar(Project project, TreeExplorerAggregation explorer){

        super();

        proj = project;
        logisimFile = proj.getLogisimFile();

        treeExplorerAggregation = explorer;

        AnchorPane.setLeftAnchor(this,0.0);
        AnchorPane.setTopAnchor(this,20.0);
        AnchorPane.setRightAnchor(this,0.0);

        prefHeight(20);

        CircuitOrderControlBtnsList = FXCollections.observableArrayList();
        CircuitTicksControlBtnsList = FXCollections.observableArrayList();

        SetCircuitOrderControlItems();
        SetCircuitTicksControlItems();

        SetAdditionalToolBarItems("ControlCircuitOrder");

    }

    private void SetCircuitOrderControlItems(){

        CustomButton AddCircuitBtn = new CustomButton(prefWidth,prefHeight,"projadd.gif");
        AddCircuitBtn.setTooltip(new ToolTip("projectAddCircuitTip"));
        AddCircuitBtn.setOnAction(event -> {
            ProjectCircuitActions.doAddCircuit(proj);
            treeExplorerAggregation.updateTree();
        });


        CustomButton PullCircuitUpBtn = new  CustomButton(prefWidth,prefHeight,"projup.gif");
        PullCircuitUpBtn.setTooltip(new ToolTip("projectMoveCircuitUpTip"));
        PullCircuitUpBtn.disableProperty().bind(
                Bindings.or(logisimFile.obsPos.isEqualTo("first"),logisimFile.obsPos.isEqualTo("first&last"))
        );
        PullCircuitUpBtn.setOnAction(event -> {
                ProjectCircuitActions.doMoveCircuit(proj,proj.getCurrentCircuit(),-1);
                treeExplorerAggregation.updateTree();
        });

        CustomButton PullCircuitDownIBtn = new CustomButton(prefWidth,prefHeight,"projdown.gif");
        PullCircuitDownIBtn.setTooltip(new ToolTip("projectMoveCircuitDownTip"));
        PullCircuitDownIBtn.disableProperty().bind(
                Bindings.or(logisimFile.obsPos.isEqualTo("last"),logisimFile.obsPos.isEqualTo("first&last"))
        );
        PullCircuitDownIBtn.setOnAction(event -> {
                ProjectCircuitActions.doMoveCircuit(proj,proj.getCurrentCircuit(),1);
                treeExplorerAggregation.updateTree();
        });

        CustomButton DeleteCircuitBtn = new CustomButton(prefWidth,prefHeight,"projdel.gif");
        DeleteCircuitBtn.setTooltip(new ToolTip("projectRemoveCircuitTip"));
        DeleteCircuitBtn.disableProperty().bind(
                logisimFile.obsPos.isEqualTo("first&last")
        );
        DeleteCircuitBtn.setOnAction(event -> {
                ProjectCircuitActions.doRemoveCircuit(proj,proj.getCurrentCircuit());
                treeExplorerAggregation.updateTree();
        });

        CircuitOrderControlBtnsList.addAll(
                AddCircuitBtn,
                PullCircuitUpBtn,
                PullCircuitDownIBtn,
                DeleteCircuitBtn
        );

    }

    private void SetCircuitTicksControlItems(){

        CustomButton SimStopBtn = new CustomButton(prefWidth,prefHeight,"simstop.png");
        SimStopBtn.setTooltip(new ToolTip("simulateEnableStepsTip"));
        SimStopBtn.setOnAction(event -> {
        });

        CustomButton SimPlayOneStepBtn = new CustomButton(prefWidth,prefHeight,"simtplay.png");
        SimPlayOneStepBtn.setTooltip(new ToolTip("simulateStepTip"));
        SimPlayOneStepBtn.setOnAction(event -> {
        });

        CustomButton SimPlayBtn = new CustomButton(prefWidth,prefHeight,"simplay.png");
        SimPlayBtn.setTooltip(new ToolTip("simulateEnableTicksTip"));
        SimPlayBtn.setOnAction(event -> {
        });

        CustomButton SimStepBtn = new CustomButton(prefWidth,prefHeight,"simstep.png");
        SimStepBtn.setTooltip(new ToolTip("simulateTickTip"));
        SimStepBtn.setOnAction(event -> {
        });

        CircuitTicksControlBtnsList.addAll(
                SimStopBtn,
                SimPlayOneStepBtn,
                SimPlayBtn,
                SimStepBtn
        );

    }

    public void SetAdditionalToolBarItems(String ToolBarType){

        if(ToolBarType.equals("ControlCircuitOrder")){
            getItems().clear();
            getItems().addAll(CircuitOrderControlBtnsList);
        }

        if(ToolBarType.equals("ControlCircuitTicks")){
            getItems().clear();
            getItems().addAll(CircuitTicksControlBtnsList);
        }

    }
}
