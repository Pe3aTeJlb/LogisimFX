package com.cburch.LogisimFX.newgui.CircLogFrame;

import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.circuit.CircuitState;
import com.cburch.LogisimFX.circuit.SubcircuitFactory;
import com.cburch.LogisimFX.comp.Component;
import com.cburch.LogisimFX.comp.ComponentFactory;
import com.cburch.LogisimFX.newgui.AbstractController;
import com.cburch.LogisimFX.newgui.ContextMenuManager;
import com.cburch.LogisimFX.newgui.FrameManager;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.LogisimFX.tools.AddTool;
import com.cburch.LogisimFX.tools.Tool;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import java.util.ArrayList;


public class CircLogController extends AbstractController {

    private Stage stage;

    @FXML
    private Tab selectionTab;



    @FXML
    private TreeView<Object> circTrvw;

    private TreeItem<Object> treeRoot;

    @FXML
    private ListView<AddTool> selectedLst;



    @FXML
    private Button addBtn;

    @FXML
    private Button changRadixBtn;

    @FXML
    private Button moveUpBtn;

    @FXML
    private Button moveDownBtn;

    @FXML
    private Button removeBtn;



    @FXML
    private CheckBox timeSelectionChckbx;



    @FXML
    private Label clockLbl;

    @FXML
    private ComboBox<?> clockCmbbx;

    @FXML
    private Label frequencyLbl;

    @FXML
    private TextField frequencyTxtfld;

    @FXML
    private ComboBox<?> hertsCmbbx;


    @FXML
    private Button startLogBtn;

    private Project proj;

    private int currSelectedIndex = -1;

    @FXML
    public void initialize(){
    }

    @Override
    public void postInitialization(Stage s, Project proj) {

        this.proj = proj;

        stage = s;
        stage.setWidth(800);
        stage.setHeight(600);
        computeTitle();

        selectionTab.textProperty().bind(LC.createStringBinding("selectionTab"));

        circTrvw.setCellFactory(tree -> {

            TreeCell<Object> cell = new TreeCell<Object>() {

                @Override
                public void updateItem(Object item, boolean empty) {

                    super.updateItem(item, empty);

                    textProperty().unbind();

                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);

                    } else {

                        if (item instanceof AddTool) {

                            ComponentFactory fact = ((AddTool) item).getFactory(false);

                            if (fact instanceof SubcircuitFactory) {

                                Circuit circ = ((SubcircuitFactory) fact).getSubcircuit();

                                setContextMenu(ContextMenuManager.CircuitContextMenu(proj, circ));

                                textProperty().bind(((Tool) item).getDisplayName());

                            } else {

                                textProperty().bind(((Tool) item).getDisplayName());
                                setGraphic(((Tool) item).getIcon());

                            }

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

                        if (treeItem.getValue() instanceof AddTool) {

                            selectedLst.getItems().add((AddTool) treeItem.getValue());

                        }

                    }

                }

            });

            return cell;
        });

        treeRoot = new TreeItem<>(proj.getLogisimFile().getAddTool(proj.getCurrentCircuit()));
        circTrvw.setRoot(treeRoot);
        updateTree(treeRoot, proj.getCircuitState());


        //selectedLst

        System.out.println("Logging");

        addBtn.textProperty().bind(LC.createStringBinding("selectionAdd"));
        //addBtn.disableProperty().bind();
        addBtn.setOnAction(event -> {

            //selectedLst.getItems().addAll(circTrvw.getSelectionModel().getSelectedItems());

        });

        changRadixBtn.textProperty().bind(LC.createStringBinding("selectionChangeBase"));
        //changRadixBtn.disableProperty().bind();
        changRadixBtn.setOnAction(event -> {});

        moveUpBtn.textProperty().bind(LC.createStringBinding("selectionMoveUp"));
        //moveUpBtn.disableProperty().bind();
        moveUpBtn.setOnAction(event -> {
            currSelectedIndex--;
            doMove(-1);
        });

        moveDownBtn.textProperty().bind(LC.createStringBinding("selectionMoveDown"));
        //moveDownBtn.disableProperty().bind();
        moveDownBtn.setOnAction(event -> {
            currSelectedIndex++;
            doMove(1);
        });

        removeBtn.textProperty().bind(LC.createStringBinding("selectionRemove"));
        //removeBtn.disableProperty().bind();
        removeBtn.setOnAction(event -> {});



        timeSelectionChckbx.textProperty().bind(LC.createStringBinding("timeSelectionEnable"));

        clockLbl.textProperty().bind(LC.createStringBinding("timeSelectionClock"));
        clockLbl.disableProperty().bind(timeSelectionChckbx.selectedProperty().not());

        clockCmbbx.disableProperty().bind(timeSelectionChckbx.selectedProperty().not());

        frequencyLbl.textProperty().bind(LC.createStringBinding("timeSelectionFrequency"));
        frequencyLbl.disableProperty().bind(timeSelectionChckbx.selectedProperty().not());

        frequencyTxtfld.disableProperty().bind(timeSelectionChckbx.selectedProperty().not());
        //frequencyTxtfld.setTextFormatter();
        frequencyTxtfld.setText("1");


        hertsCmbbx.disableProperty().bind(timeSelectionChckbx.selectedProperty().not());



        startLogBtn.textProperty().bind(LC.createStringBinding("startLogging"));
        startLogBtn.setOnAction(event -> {
            FrameManager.CreateTimeLineFrame(proj);
            stage.close();
        });


    }

    private void computeTitle() {
        //String name = data == null ? "???" : data.getCircuitState().getCircuit().getName();
        //return StringUtil.format(Strings.get("logFrameTitle"), name,
          //      proj.getLogisimFile().getDisplayName());
    }

    public void setCircuit(Circuit circ){

        //setText(StringUtil.format(Strings.get("logFrameMenuItem"), title));
        stage.titleProperty().bind(LC.createStringBinding("logFrameTitle"));

    }

    private void doMove(int delta) {

        selectedLst.getSelectionModel().getSelectedItem()
/*
        int oldIndex = ToolbarItemsList.getSelectionModel().getSelectedIndex();
        int newIndex = oldIndex + delta;
        ToolbarData data = proj.getOptions().getToolbarData();
        if (oldIndex >= 0 && newIndex >= 0 && newIndex < data.size()) {
            proj.doAction(ToolbarActions.moveTool(data,
                    oldIndex, newIndex));
            //ToolbarItemsList.getSelectionModel().select(newIndex);
        }
        
 */


    }

    private void updateToolbarItemsList(){

        /*
        int buff = currSelectedIndex;

        toolbarItems.clear();
        toolbarItems.addAll(proj.getOptions().getToolbarData().getContents());
        ToolbarItemsList.setItems(toolbarItems);

        currSelectedIndex = buff;

        selectionModel.select(currSelectedIndex);

         */

    }

    private void updateTree(TreeItem<Object> root, CircuitState state){

        ArrayList<Component> subcircs = new ArrayList<>();

        for (Component comp : state.getCircuit().getNonWires()) {

            if (comp.getFactory() instanceof SubcircuitFactory) {
                System.out.println("subc "+((SubcircuitFactory)comp).getSubcircuit());
                subcircs.add(comp);
            } else {
                Object o = comp.getFeature(Loggable.class);
                System.out.println(o + " "+ comp.getFactory().getName());
                root.getChildren().add(new TreeItem<>(comp));
                //if (o != null) {
                  //  root.getChildren().add(new TreeItem<>(comp));
               // }
            }

        }

        for (Component comp: subcircs) {

            SubcircuitFactory factory = (SubcircuitFactory) comp.getFactory();
            CircuitState s = factory.getSubstate(state, comp);

            TreeItem<Object> subroot = new TreeItem<>(proj.getLogisimFile().getAddTool(s.getCircuit()));
            root.getChildren().add(subroot);
            updateTree(subroot, s);
        }

    }

    @Override
    public void onClose() {
        System.out.println("Circ log closed");
    }

}
