package LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor;

import LogisimFX.file.ToolbarData;
import LogisimFX.newgui.MainFrame.EditorTabs.EditorBase;
import LogisimFX.newgui.MainFrame.EditorTabs.LayoutEditor.LayoutEditor;
import LogisimFX.prefs.AppPreferences;
import LogisimFX.proj.Project;
import LogisimFX.proj.ProjectEvent;
import LogisimFX.proj.ProjectListener;
import LogisimFX.tools.Tool;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Lighting;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

public class LayoutEditorToolBar extends ToolBar {

    private Tool currTool;

    private ObservableList<Node> EditCircuitBtnsList;

    private final int prefWidth = 15;
    private final int prefHeight = 15;

    private int toolsCounter = 1;

    private Project proj;
    private LayoutEditor layoutEditor;

    private Lighting lighting = new Lighting();

    private MyListener myListener = new MyListener();

    private class MyListener
            implements PropertyChangeListener, ToolbarData.ToolbarListener, ProjectListener {

        //
        // PropertyChangeListener methods
        //
        public void propertyChange(PropertyChangeEvent event) {
            if (AppPreferences.GATE_SHAPE.isSource(event)) {
                Platform.runLater(() -> initItems());
            }
        }

        @Override
        public void toolbarChanged() {
            Platform.runLater(() -> initItems());
        }

        @Override
        public void projectChanged(ProjectEvent event) {
            int action = event.getAction();

            if (action == ProjectEvent.ACTION_SET_TOOL) {
                if(event.getTool() != null && layoutEditor.isSelected()) {
                    highlightCurTool(event.getTool());
                }
            }
        }

    }

    class ToolButton extends Button {

        Tool tool;

        public ToolButton(Tool tool) {

            super();

            this.tool = tool;

            setPrefSize(prefWidth,prefHeight);
            setMinSize(prefWidth,prefHeight);
            setMaxSize(prefWidth,prefHeight);

            ImageView buff = new ImageView(tool.getIcon().getImage());
            graphicProperty().setValue(buff);

            if(toolsCounter < 11){

                if(toolsCounter == 10) {
                    toolsCounter = 0;
                }

                proj.getFrameController().getStage().getScene().getAccelerators().put(
                        new KeyCodeCombination(KeyCode.valueOf("DIGIT"+ toolsCounter), KeyCombination.CONTROL_DOWN),
                        new Runnable() {
                            @FXML
                            public void run() {
                                fire();
                            }
                        }

                );

                toolsCounter++;
            }

            String bindbuff = " ("+"CTRL+"+ (toolsCounter-1)+")";

            Tooltip tip = new Tooltip();
            tip.textProperty().bind(tool.getDescription().concat(bindbuff));
            setTooltip(tip);

            this.setOnAction(event -> {
                if(layoutEditor.isSelected()) {
                    currTool = tool;
                    proj.setTool(tool);
                }
            });

        }

    }

    public LayoutEditorToolBar(Project project, LayoutEditor layoutEditor) {

        super();

        this.proj = project;
        proj.addProjectListener(myListener);
        AppPreferences.GATE_SHAPE.addPropertyChangeListener(myListener);
        proj.getLogisimFile().getOptions().getToolbarData().addToolbarListener(myListener);

        this.layoutEditor = layoutEditor;

        EditCircuitBtnsList = FXCollections.observableArrayList();
        initItems();

        proj.getFrameController().editorProperty().addListener((observableValue, editorBase, t1) -> {
            if (layoutEditor.isSelected()){
                recalculateAccelerators();
                if (currTool != null)
                proj.setTool(currTool);
            }
        });

    }

    private void initItems(){

        toolsCounter= 1;

        EditCircuitBtnsList.clear();

        ToolbarData data = proj.getLogisimFile().getOptions().getToolbarData();

        for (Tool tool : data.getContents()) {

            if (tool == null) {
                EditCircuitBtnsList.add(new Separator());
            } else {
                EditCircuitBtnsList.add(new ToolButton(tool));
            }

        }

        proj.setTool(data.getFirstTool());
        highlightCurTool(data.getFirstTool());

        getItems().clear();
        getItems().addAll(EditCircuitBtnsList);

    }

    public void highlightCurTool(Tool tool){

        if (tool == null){
            return;
        }

        for(Node node: EditCircuitBtnsList){
            if(node instanceof ToolButton){
                if(((ToolButton) node).tool == tool){
                    node.setEffect(lighting);
                } else {
                    node.setEffect(null);
                }
            }
        }

    }

    public void recalculateAccelerators(){

        if (layoutEditor.getScene() == null) return;

        int toolsCount = 0;

        ArrayList<Node> buff = new ArrayList<>();
        for(Node node: EditCircuitBtnsList){
            if(node instanceof ToolButton) {
                toolsCount++;
                buff.add(node);
            }
        }

        toolsCount = Math.min(toolsCount, 10);

        for(int i = 0; i < toolsCount; i++){
            int finalI = i;
            int index = (i) == 9 ? 0 : i + 1;
            layoutEditor.getScene().getAccelerators().put(
                    new KeyCodeCombination(KeyCode.valueOf("DIGIT"+ index), KeyCombination.CONTROL_DOWN),
                    new Runnable() {
                        @FXML
                        public void run() {
                            ((ToolButton)buff.get(finalI)).fire();
                        }
                    }

            );
        }

    }

    public void terminateListeners(){
        proj.removeProjectListener(myListener);
        proj.getLogisimFile().getOptions().getToolbarData().removeToolbarListener(myListener);
        AppPreferences.GATE_SHAPE.removePropertyChangeListener(myListener);
    }

}
