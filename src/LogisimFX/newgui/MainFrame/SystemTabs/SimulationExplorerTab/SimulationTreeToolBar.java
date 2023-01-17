package LogisimFX.newgui.MainFrame.SystemTabs.SimulationExplorerTab;

import LogisimFX.file.LogisimFile;
import LogisimFX.newgui.MainFrame.LC;
import LogisimFX.newgui.MainFrame.SystemTabs.CustomButton;
import LogisimFX.proj.Project;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;

public class SimulationTreeToolBar extends ToolBar {

    private Project proj;
    private LogisimFile logisimFile;

    private int prefWidth = 15;
    private int prefHeight = 15;

    public SimulationTreeToolBar(Project project){

        super();

        proj = project;
        logisimFile = proj.getLogisimFile();
        
        initSimulationControlButtons();
        
    }
    
    private void initSimulationControlButtons(){
        
        CustomButton SimStopBtn = new CustomButton(prefWidth, prefHeight,"simstop.png");
        SimStopBtn.setTooltip(new ToolTip("simulateEnableStepsTip"));
        SimStopBtn.setOnAction(event -> {
            if (proj.getSimulator() != null) {
                proj.getSimulator().setIsRunning(!proj.getSimulator().isRunning().getValue());
            }
        });

        CustomButton SimPlayOneStepBtn = new CustomButton(prefWidth, prefHeight,"simtplay.png");
        SimPlayOneStepBtn.setTooltip(new ToolTip("simulateStepTip"));
        SimPlayOneStepBtn.disableProperty().bind(proj.getSimulator().isRunning());
        SimPlayOneStepBtn.setOnAction(event -> {
            if (proj.getSimulator() != null) proj.getSimulator().step();
        });

        CustomButton SimPlayBtn = new CustomButton(prefWidth, prefHeight,"simplay.png");
        SimPlayBtn.setTooltip(new ToolTip("simulateEnableTicksTip"));
        SimPlayBtn.setOnAction(event -> {
            if (proj.getSimulator() != null) proj.getSimulator().setIsTicking(!proj.getSimulator().isTicking());
        });

        CustomButton SimStepBtn = new CustomButton(prefWidth, prefHeight,"simstep.png");
        SimStepBtn.setTooltip(new ToolTip("simulateTickTip"));
        SimStepBtn.setOnAction(event -> {
            if (proj.getSimulator() != null) proj.getSimulator().tick();
        });

        this.getItems().addAll(
                SimStopBtn,
                SimPlayOneStepBtn,
                SimPlayBtn,
                SimStepBtn
        );
        
    }

    private static class ToolTip extends Tooltip {

        public ToolTip(String text){
            super();
            textProperty().bind(LC.createStringBinding(text));
        }

    }

}
