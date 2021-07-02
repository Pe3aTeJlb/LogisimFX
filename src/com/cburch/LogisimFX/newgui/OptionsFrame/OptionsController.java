package com.cburch.LogisimFX.newgui.OptionsFrame;

import com.cburch.LogisimFX.file.LogisimFile;
import com.cburch.LogisimFX.file.Options;
import com.cburch.LogisimFX.newgui.AbstractController;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.LogisimFX.tools.AddTool;
import com.cburch.LogisimFX.tools.Library;
import com.cburch.LogisimFX.tools.Tool;
import com.cburch.LogisimFX.data.AttributeSet;
import com.cburch.LogisimFX.data.AttributeOption;


import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private ComboBox<Integer> SimLimitCmbx;

    @FXML
    private Label GateUndefinedLbl;

    @FXML
    private ComboBox<ComboOption> GateUndefinedCmbx;

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

    private Options opts;

    private AttributeSet attrs;

    @FXML
    public void initialize(){

    }

    @Override
    public void postInitialization(Stage s, Project project) {

        stage = s;
        proj = project;

        opts = proj.getOptions();

        attrs = opts.getAttributeSet();

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

        ObservableList<Integer> simlimit = FXCollections.observableArrayList();

        simlimit.addAll(
                200,
                500,
                1000,
                2000,
                5000,
                10000,
                20000,
                50000
        );
        SimLimitCmbx.setItems(simlimit);
        SimLimitCmbx.setValue(attrs.getValue(Options.sim_limit_attr));
        SimLimitCmbx.setOnAction(event -> proj.doAction(OptionsActions.setAttribute(attrs,
                Options.sim_limit_attr, SimLimitCmbx.getValue())));



        GateUndefinedLbl.textProperty().bind(LC.createStringBinding("gateUndefined"));

        ObservableList<ComboOption> gateUndefined = FXCollections.observableArrayList();

        gateUndefined.addAll(
                new ComboOption(Options.GATE_UNDEFINED_IGNORE),
                new ComboOption(Options.GATE_UNDEFINED_ERROR)
        );

        GateUndefinedCmbx.setCellFactory(lv -> new TranslationCell());
        GateUndefinedCmbx.setButtonCell(new TranslationCell());

        GateUndefinedCmbx.setItems(gateUndefined);

        GateUndefinedCmbx.setOnAction(event -> {proj.doAction(OptionsActions.setAttribute(attrs,
                Options.ATTR_GATE_UNDEFINED, GateUndefinedCmbx.getValue()));});



        SimRandomnessChbx.textProperty().bind(LC.createStringBinding("simulateRandomness"));
        SimRandomnessChbx.setSelected(attrs.getValue(Options.sim_rand_attr)>0);
        SimRandomnessChbx.setOnAction(event -> {
            Object val = SimRandomnessChbx.isSelected() ? Options.sim_rand_dflt
                    : Integer.valueOf(0);
            proj.doAction(OptionsActions.setAttribute
                (attrs, Options.sim_rand_attr, val));
        });

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

        ObservableList<ComboOption> toolbarItems = FXCollections.observableArrayList();

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

class ComboOption {

    private Object value;
    private StringBinding binding;

    ComboOption(AttributeOption value) {
        this.value = value;
        this.binding = null;
    }

    public StringBinding getBinding() {
        //ToDo:
        //if (binding != null)
            return binding;
        //if (value instanceof AttributeOption) return ((AttributeOption) value).toDisplayString();
    }

    public Object getValue() {
        return value;
    }

}

class TranslationCell extends ListCell<ComboOption> {

    @Override
    protected void updateItem(ComboOption item, boolean empty) {

        super.updateItem(item, empty);
        textProperty().unbind();

        if (empty || item == null) {
            setText("");
        } else {
            textProperty().bind(item.getBinding());
        }

    }
}