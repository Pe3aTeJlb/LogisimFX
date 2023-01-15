package LogisimFX.newgui.MainFrame.EditorTabs.AppearanceEditor;

import LogisimFX.draw.tools.*;
import LogisimFX.file.ToolbarData;
import LogisimFX.prefs.AppPreferences;
import LogisimFX.proj.Project;
import LogisimFX.proj.ProjectEvent;
import LogisimFX.proj.ProjectListener;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Lighting;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class AppearanceEditorToolBar extends ToolBar {

    private AbstractTool currTool;

    private ObservableList<ToolButton> EditAppearanceBtnsList;

    private final int prefWidth = 15;
    private final int prefHeight = 15;

    private int toolsCounter = 1;

    private Project proj;
    private AppearanceEditor appearanceEditor;


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
                if(event.getAbstractTool() != null && appearanceEditor.isSelected()) {
                    highlightCurTool(event.getAbstractTool());
                }
            }
        }

    }

    class ToolButton extends Button {

        AbstractTool abstractTool;

        public AbstractTool getAbstractTool() {
            return abstractTool;
        }

        public ToolButton(AbstractTool tool) {

            super();

            this.abstractTool = tool;

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
            tip.setText(bindbuff);
            setTooltip(tip);

            this.setOnAction(event -> {
                if(appearanceEditor.isSelected()) {
                    currTool = abstractTool;
                    proj.setAbstractTool(tool);
                }
            });

        }

    }

    public AppearanceEditorToolBar(Project project, AppearanceEditor appearanceEditor) {

        super();

        this.proj = project;
        proj.addProjectListener(myListener);
        AppPreferences.GATE_SHAPE.addPropertyChangeListener(myListener);
        proj.getLogisimFile().getOptions().getToolbarData().addToolbarListener(myListener);

        this.appearanceEditor = appearanceEditor;

        EditAppearanceBtnsList = FXCollections.observableArrayList();
        initItems();

        proj.getFrameController().editorProperty().addListener((observableValue, editorBase, t1) -> {
            if (appearanceEditor.isSelected()){
                recalculateAccelerators();
                if (currTool != null)
                proj.setAbstractTool(currTool);
            }
        });

    }

    private void initItems(){

        toolsCounter = 1;

        EditAppearanceBtnsList.clear();

        DrawingAttributeSet attrs = new DrawingAttributeSet();

        AbstractTool[] tools = {
                new DragTool(),
                new SelectTool(),
                new TextTool(attrs),
                new LineTool(attrs),
                new CurveTool(attrs),
                new PolyTool(false, attrs),
                new RectangleTool(attrs),
                new RoundRectangleTool(attrs),
                new OvalTool(attrs),
                new PolyTool(true, attrs),
        };

        proj.setAbstractTool(tools[0]);
        highlightCurTool(tools[0]);

        for (AbstractTool tool: tools) {
            EditAppearanceBtnsList.add(new ToolButton(tool));
        }


        getItems().clear();
        getItems().addAll(EditAppearanceBtnsList);

    }

    public void highlightCurTool(AbstractTool tool){

        if (tool == null){
            return;
        }

        for(Node node: EditAppearanceBtnsList){
            if(node instanceof ToolButton){
                if(((ToolButton) node).abstractTool == tool) {
                    node.setEffect(lighting);
                } else {
                    node.setEffect(null);
                }
            }
        }

    }

    private void recalculateAccelerators(){

        int toolsCount = Math.min(EditAppearanceBtnsList.size(), 10);

        for(int i = 0; i < toolsCount; i++){
            int finalI = i;
            int index = (i) == 9 ? 0 : i + 1;
            proj.getFrameController().getStage().getScene().getAccelerators().put(
                    new KeyCodeCombination(KeyCode.valueOf("DIGIT"+ index), KeyCombination.CONTROL_DOWN),
                    new Runnable() {
                        @FXML
                        public void run() {
                            EditAppearanceBtnsList.get(finalI).fire();
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
