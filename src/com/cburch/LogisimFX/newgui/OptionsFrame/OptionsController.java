package com.cburch.LogisimFX.newgui.OptionsFrame;

import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.circuit.SubcircuitFactory;
import com.cburch.LogisimFX.comp.ComponentFactory;
import com.cburch.LogisimFX.file.LogisimFile;
import com.cburch.LogisimFX.newgui.AbstractController;
import com.cburch.LogisimFX.newgui.ContextMenuManager;
import com.cburch.LogisimFX.proj.Project;

import com.cburch.LogisimFX.tools.AddTool;
import com.cburch.LogisimFX.tools.Library;
import com.cburch.LogisimFX.tools.Tool;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

public class OptionsController extends AbstractController {

    private Stage stage;

    private Project proj;



    @FXML
    private Tab SimulationOptsTab;

    @FXML
    private Label SimLimitLbl;

    @FXML
    private ComboBox<?> SimLimitCmbx;

    @FXML
    private Label GateUndefinedLbl;

    @FXML
    private ComboBox<?> GateUndefinedCmbx;

    @FXML
    private CheckBox SimRandomnessChbx;



    @FXML
    private Tab ToolbarOptsTab;

    @FXML
    private TreeView<Object> TreeExplorer;

    @FXML
    private ListView<Object> ToolbarItemsList;

    @FXML
    private Button AddSeparatorBtn;

    @FXML
    private Button MoveUpBtn;

    @FXML
    private Button MoveDownBtn;

    @FXML
    private Button DeleteBtn;



    @FXML
    private Tab MouseOptsTab;













    @FXML
    private Button RevertToTemplate;

    @FXML
    public void initialize(){

    }

    @Override
    public void postInitialization(Stage s, Project project) {

        stage = s;
        proj = project;

        String name = proj.getLogisimFile() == null ? "???" : proj.getLogisimFile().getDisplayName().toString();

        stage.titleProperty().bind(LC.createComplexStringBinding("optionsFrameTitle",name));

        initSimulationOptionsTab();
        initToolbarOptionsTab();
        initMouseOptionsTab();

        //RevertToTemplate.textProperty().bind();
        RevertToTemplate.setOnAction(event -> {});

    }


    private void initSimulationOptionsTab(){

        SimulationOptsTab.textProperty().bind(LC.createStringBinding("simulateTitle"));

        SimLimitLbl.textProperty().bind(LC.createStringBinding("simulateLimit"));
       // SimLimitCmbx

        GateUndefinedLbl.textProperty().bind(LC.createStringBinding("gateUndefined"));

        //GateUndefinedCmbx

        SimRandomnessChbx.textProperty().bind(LC.createStringBinding("simulateRandomness"));

    }

    private void initToolbarOptionsTab(){


        ToolbarOptsTab.textProperty().bind(LC.createStringBinding("toolbarTitle"));

        AddSeparatorBtn.textProperty().bind(LC.createStringBinding("toolbarAddSeparator"));
        AddSeparatorBtn.setOnAction(event -> {});

        MoveUpBtn.textProperty().bind(LC.createStringBinding("toolbarMoveUp"));
        MoveUpBtn.setOnAction(event -> {});

        MoveDownBtn.textProperty().bind(LC.createStringBinding("toolbarMoveDown"));
        MoveDownBtn.setOnAction(event -> {});

        DeleteBtn.textProperty().bind(LC.createStringBinding("toolbarRemove"));
        DeleteBtn.setOnAction(event -> {});

        TreeExplorer.setCellFactory(tree ->{

            TreeCell<Object> cell = new TreeCell<Object>() {

                @Override
                public void updateItem(Object item, boolean empty) {

                    super.updateItem(item, empty);

                    textProperty().unbind();

                    if(empty || item == null) {

                        setText(null);
                        setGraphic(null);
                        setTooltip(null);
                        setContextMenu(null);

                    } else {

                        if(item instanceof LogisimFile){

                            setText(proj.getLogisimFile().getName());
                            setGraphic(null);
                            setTooltip(null);

                        }
                        else if(item instanceof Library){

                            textProperty().bind(((Library) item).getDisplayName());
                            setGraphic(null);
                            setTooltip(null);

                        }
                        else if(item instanceof Tool){

                            textProperty().bind(((Tool) item).getDisplayName());

                            Tooltip tip = new Tooltip();
                            tip.textProperty().bind(((Tool)item).getDescription());
                            setTooltip(tip);

                            setGraphic(((Tool) item).getIcon());

                        }
                        else{
                            setText("you fucked up2");
                        }

                    }

                }

            };

            cell.setOnMouseClicked(event -> {

                if (!cell.isEmpty()) {

                    TreeItem<Object> treeItem = cell.getTreeItem();

                    if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2
                            && !event.isConsumed()) {

                        event.consume();

                        if(treeItem.getValue() instanceof AddTool){

                        }


                    }

                }

            });

            return cell;

        });

        updateTree(TreeExplorer);

    }

    private void initMouseOptionsTab(){

        MouseOptsTab.textProperty().bind(LC.createStringBinding("mouseTitle"));

    }

    public void updateTree(TreeView<Object> treeView){

        TreeItem<Object> root = new TreeItem<>(proj.getLogisimFile());
        treeView.setRoot(root);
        root.expandedProperty().set(true);

        //Circuits
        for (AddTool tool: proj.getLogisimFile().getTools()) {

            TreeItem<Object> l = new TreeItem<>(tool);
            root.getChildren().add(l);

        }

        //Libs and tools
        for (Library lib: proj.getLogisimFile().getLibraries()) {

            TreeItem<Object> l = new TreeItem<>(lib);
            root.getChildren().add(l);

            for (Tool tool: lib.getTools()) {

                TreeItem<Object> t = new TreeItem<>(tool);
                l.getChildren().add(t);

            }

        }

    }

    @Override
    public void onClose() {
        System.out.println("test options");
    }

}
