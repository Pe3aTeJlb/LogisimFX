/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.newgui.OptionsFrame;

import LogisimFX.file.*;
import LogisimFX.newgui.AbstractController;
import LogisimFX.newgui.MainFrame.SystemTabs.AttributesTab.AttributeTable;
import LogisimFX.proj.Project;
import LogisimFX.tools.AddTool;
import LogisimFX.tools.Library;
import LogisimFX.tools.Tool;
import LogisimFX.data.AttributeSet;
import LogisimFX.data.AttributeOption;
import LogisimFX.util.InputEventUtil;

import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
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
    private Label SimRandomnessLbl;

    @FXML
    private CheckBox SimRandomnessChbx;



    @FXML
    private Tab ToolbarOptsTab;

    @FXML
    private TreeView<Object> TreeExplorer;

    @FXML
    private ListView<Object> ToolbarItemsList;

    private ObservableList<Object> toolbarItems;

    @FXML
    private Button AddSeparatorBtn;

    @FXML
    private Button MoveUpBtn;

    @FXML
    private Button MoveDownBtn;

    @FXML
    private Button DeleteToolBtn;



    @FXML
    private Tab MouseOptsTab;

    @FXML
    private Button BindBtn;

    @FXML
    private TreeView<Object> AttrExplorer;

    @FXML
    private TableView<ToolBindingDataModel> BindTable;

    @FXML
    private Button AttrDeleteBtn;

    @FXML
    private ScrollPane AttrTablePane;

    private AttributeTable attrTable;

    private Tool currTool;

    private ObservableList<ToolBindingDataModel> toolBindings;

    private MultipleSelectionModel<Object> selectionModel;

    private int currSelectedIndex = -1;


    @FXML
    private Button RevertToTemplate;

    private Options opts;

    private AttributeSet attrs;

    private MyListener myListener = new MyListener();

    private class MyListener
            implements LibraryListener {

        public void libraryChanged(LibraryEvent event) {
            if (event.getAction() == LibraryEvent.SET_NAME) {
                setTitle();
            }
        }

    }

    @FXML
    public void initialize(){

    }

    @Override
    public void postInitialization(Stage s, Project project) {

        stage = s;
        proj = project;

        proj.getLogisimFile().addLibraryListener(myListener);

        stage.setHeight(450);
        stage.setWidth(500);

        opts = proj.getOptions();

        attrs = opts.getAttributeSet();

        setTitle();

        initSimulationOptionsTab();
        initToolbarOptionsTab();
        initMouseOptionsTab();

        RevertToTemplate.textProperty().bind(LC.createStringBinding("revertButton"));
        RevertToTemplate.setOnAction(event -> proj.doAction(LogisimFileActions.revertDefaults()));

    }

    private void setTitle(){

        stage.titleProperty().unbind();

        String name = proj.getLogisimFile() == null ? "???" : proj.getLogisimFile().getDisplayName().getValue();

        stage.titleProperty().bind(LC.createComplexStringBinding("optionsFrameTitle",name));

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

        //default value
        for (ComboOption opt: gateUndefined) {

            if (opt.getValue().equals(attrs.getValue(Options.ATTR_GATE_UNDEFINED))) {
                GateUndefinedCmbx.setValue(opt);
                break;
            }

        }

        GateUndefinedCmbx.setOnAction(event -> {proj.doAction(OptionsActions.setAttribute(attrs,
                Options.ATTR_GATE_UNDEFINED, GateUndefinedCmbx.getValue()));});



        SimRandomnessLbl.textProperty().bind(LC.createStringBinding("simulateRandomness"));

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
        AddSeparatorBtn.setOnAction(event -> {
            proj.getOptions().getToolbarData().addSeparator();
            updateToolbarItemsList();
        });

        MoveUpBtn.textProperty().bind(LC.createStringBinding("toolbarMoveUp"));
        MoveUpBtn.setOnAction(event -> {
            currSelectedIndex--;
            doMove(-1);
            updateToolbarItemsList();
        });

        MoveDownBtn.textProperty().bind(LC.createStringBinding("toolbarMoveDown"));
        MoveDownBtn.setOnAction(event -> {
            currSelectedIndex++;
            doMove(1);
            updateToolbarItemsList();
        });

        DeleteToolBtn.textProperty().bind(LC.createStringBinding("toolbarRemove"));
        DeleteToolBtn.setOnAction(event -> {
            if(ToolbarItemsList.getSelectionModel().getSelectedIndex()>=0) {
                proj.doAction(ToolbarActions.removeTool(proj.getOptions().getToolbarData(),
                        ToolbarItemsList.getSelectionModel().getSelectedIndex()));
                updateToolbarItemsList();
            }
        });

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

                        if(treeItem.getValue() instanceof Tool){
                            proj.doAction(ToolbarActions.addTool(proj.getOptions().getToolbarData(), (Tool)treeItem.getValue()));
                            updateToolbarItemsList();
                        }


                    }

                }

            });

            return cell;

        });

        updateTree(TreeExplorer);



        toolbarItems = FXCollections.observableArrayList();

        ToolbarItemsList.setCellFactory(list ->{

            ListCell<Object> cell = new ListCell<Object>(){

                protected void updateItem(Object item, boolean empty) {

                    super.updateItem(item, empty);
                    textProperty().unbind();

                    if (empty) {

                        setText(null);
                        setGraphic(null);

                    }
                    else if (item instanceof Tool){

                        textProperty().bind(((Tool) item).getDisplayName());

                        ImageView buff = new ImageView(((Tool) item).getIcon().getImage());
                        graphicProperty().setValue(buff);

                    }

                }

            };

            return cell;

        });

        selectionModel = ToolbarItemsList.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);
        selectionModel.selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> {
                    currSelectedIndex = selectionModel.getSelectedIndex();
                    MoveUpBtn.setDisable(selectionModel.selectedIndexProperty().getValue()==0);
                    MoveDownBtn.setDisable(selectionModel.selectedIndexProperty().getValue()==toolbarItems.size()-1);

                }
        );
        updateToolbarItemsList();

    }

    private void updateTree(TreeView<Object> treeView){

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

    private void updateToolbarItemsList(){

        int buff = currSelectedIndex;

        toolbarItems.clear();
        toolbarItems.addAll(proj.getOptions().getToolbarData().getContents());
        ToolbarItemsList.setItems(toolbarItems);

        currSelectedIndex = buff;

        selectionModel.select(currSelectedIndex);

    }

    private void doMove(int delta) {
        int oldIndex = ToolbarItemsList.getSelectionModel().getSelectedIndex();
        int newIndex = oldIndex + delta;
        ToolbarData data = proj.getOptions().getToolbarData();
        if (oldIndex >= 0 && newIndex >= 0 && newIndex < data.size()) {
            proj.doAction(ToolbarActions.moveTool(data,
                    oldIndex, newIndex));
            //ToolbarItemsList.getSelectionModel().select(newIndex);
        }
    }



    public static class ToolBindingDataModel{

        Tool tool;
        int binding;

        ToolBindingDataModel(int i,Tool t){
            tool = t;
            binding = i;
        }

        public StringBinding getTool(){
            return tool.getDisplayName();
        }

        public String getBinding(){
            return InputEventUtil.toDisplayString(binding);
        }

        public int getPureBinding(){
            return binding;
        }

    }

    private void initMouseOptionsTab(){

        MouseOptsTab.textProperty().bind(LC.createStringBinding("mouseTitle"));

        //Tree explorer

        AttrExplorer.setCellFactory(tree ->{

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

                    if (event.getButton().equals(MouseButton.PRIMARY) && !event.isConsumed()) {

                        event.consume();

                        if(treeItem.getValue() instanceof Tool){
                            currTool=(Tool)treeItem.getValue();
                            attrTable.setTool(currTool);
                            setBindBtnText();
                        }


                    }

                }

            });

            return cell;

        });

        updateTree(AttrExplorer);

        attrTable = new AttributeTable(proj);
        AttrTablePane.setContent(attrTable);


        //Binding Table

        MultipleSelectionModel<ToolBindingDataModel> selectionModel = BindTable.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        toolBindings = FXCollections.observableArrayList();

        BindTable.getColumns().clear();

        TableColumn<ToolBindingDataModel, String> binding = new TableColumn<>();
        binding.setCellValueFactory(new PropertyValueFactory<>("Binding"));

        TableColumn<ToolBindingDataModel,String> tool = new TableColumn<>();
        tool.setCellValueFactory(item -> item.getValue().getTool());

        BindTable.getColumns().add(binding);
        BindTable.getColumns().add(tool);

        updateBindTable();

        //Bind button
        setBindBtnText();


            //Works only on mouse click
        BindBtn.setOnMouseClicked(event -> {

           // event.is
            //System.out.println(event.);
            //Tool t = currTool.cloneTool();
            //Integer mods = Integer.valueOf();
           // proj.doAction(OptionsActions.setMapping(proj.getOptions().getMouseMappings(), mods, t));


        });

        //Delete button
        AttrDeleteBtn.textProperty().bind(LC.createStringBinding("mouseRemoveButton"));

        AttrDeleteBtn.setOnAction(event ->
                {
                    proj.doAction(OptionsActions.removeMapping(proj.getOptions().getMouseMappings(),
                            selectionModel.getSelectedItem().getPureBinding()));
                    updateBindTable();
                }
        );

    }

    private void updateBindTable(){

        toolBindings.clear();
        BindTable.getItems().clear();

        for (Integer i: proj.getOptions().getMouseMappings().getMappedModifiers()) {
            toolBindings.add(new ToolBindingDataModel(i,
                    proj.getOptions().getMouseMappings().getToolFor(i))
            );
        }

        BindTable.setItems(toolBindings);

    }

    private void setBindBtnText(){

        BindBtn.textProperty().unbind();

        if (currTool == null) {
            BindBtn.textProperty().bind(LC.createStringBinding("mouseMapNone"));
            BindBtn.setDisable(true);
        } else {
            BindBtn.textProperty().bind(LC.createComplexStringBinding("mouseMapText", currTool.getDisplayName().getValue()));
            BindBtn.setDisable(false);
        }

    }



    @Override
    public void onClose() {

        proj.getLogisimFile().removeLibraryListener(myListener);

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
        if (binding != null) return binding;
        if (value instanceof AttributeOption) return ((AttributeOption) value).getStringBinding();
        return null;
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