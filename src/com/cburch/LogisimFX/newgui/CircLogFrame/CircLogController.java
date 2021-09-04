package com.cburch.LogisimFX.newgui.CircLogFrame;

import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.file.ToolbarData;
import com.cburch.LogisimFX.newgui.AbstractController;
import com.cburch.LogisimFX.proj.Project;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class CircLogController extends AbstractController {

    private Stage stage;

    @FXML
    private Tab selectionTab;



    @FXML
    private ListView<?> selectLst;

    @FXML
    private ListView<?> selectedLst;



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


        //selectLst
        //selectedLst



        System.out.println("Logging");

        addBtn.textProperty().bind(LC.createStringBinding("selectionAdd"));
        //addBtn.disableProperty().bind();
        addBtn.setOnAction(event -> {});

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

        frequencyLbl.textProperty().bind(LC.createStringBinding("timeSelectionFrequency"));


        startLogBtn.textProperty().bind(LC.createStringBinding("startLogging"));
        startLogBtn.setOnAction(event -> {});


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

    @Override
    public void onClose() {
        System.out.println("Circ log closed");
    }

}
